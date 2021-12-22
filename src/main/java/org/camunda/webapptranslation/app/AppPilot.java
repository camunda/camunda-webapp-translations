package org.camunda.webapptranslation.app;


/* -------------------------------------------------------------------- */
/*                                                                      */
/* Manage an application (a directory)                                  */
/*                                                                      */
/* The class detects  all languages present (and missing), compare each  */
/* dictionary with the referential  language. It can just play to detect */
/* missing sentences, or complete it                                    */
/*                                                                      */
/* -------------------------------------------------------------------- */

import org.camunda.webapptranslation.SynchroParams;
import org.camunda.webapptranslation.org.camunda.webapptranslation.ReportInt;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class AppPilot {

    File folder;
    String referenceLanguage;
    Set<String> languages = new HashSet<>();
    Set<String> expectedLanguages;

    public AppPilot(File folder, String referenceLanguage) {
        this.folder = folder;
        this.referenceLanguage = referenceLanguage;
        for (File file : new File(folder.getAbsolutePath()).listFiles()) {
            if (file.getName().endsWith(".json"))
                languages.add(file.getName().substring(0, file.getName().length() - 5));
        }
    }

    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * set the expected language for the application
     *
     * @param expectedLanguages list of expected languages
     */
    public void setExpectedLanguage(Set<String> expectedLanguages) {
        this.expectedLanguages = expectedLanguages;
    }

    private static final String INDENTATION = "   ";

    /**
     * Detection
     * Check that
     * 1/ all dictionary exists
     * 2/ the language is complete
     *
     * @param synchroParams access to parameters
     * @param report        report the status
     */
    public void detection(SynchroParams synchroParams, ReportInt report) {
        AppDictionary referenceDictionary = new AppDictionary(folder, referenceLanguage);
        referenceDictionary.read(report);

        // check each dictionary
        report.info(AppPilot.class, "----- Folder " + folder);
        for (String language : expectedLanguages) {
            if (synchroParams.getOnlyCompleteOneLanguage() != null && !synchroParams.getOnlyCompleteOneLanguage().equals(language))
                continue;

            if (language.equals(referenceLanguage)) {
                report.info(AppPilot.class, headerLanguage(language) + "Referentiel");
                continue;
            }
            AppDictionary dico = new AppDictionary(folder, language);
            if (!dico.existFile()) {
                report.info(AppPilot.class, headerLanguage(language) + "Not exist (" + referenceDictionary.getDictionary().size() + " missing keys)");
                continue;
            }
            // read the dictionary
            if (!dico.read(report)) {
                // error already reported
                continue;
            } else {
                AppDictionary.DicoStatus dictionaryStatus = dico.checkKeys(referenceDictionary);
                List<String> listReports = new ArrayList<>();
                if (dictionaryStatus.nbMissingKeys > 0)
                    listReports.add("Missing " + dictionaryStatus.nbMissingKeys + " keys");
                if (dictionaryStatus.nbTooMuchKeys > 0)
                    listReports.add("Too much " + dictionaryStatus.nbTooMuchKeys + " keys");
                if (dictionaryStatus.nbIncorrectKeyClass > 0)
                    listReports.add("Incorrect class " + dictionaryStatus.nbIncorrectKeyClass + " keys");

                if (listReports.isEmpty())
                    report.info(AppPilot.class, headerLanguage(language) + "OK");
                else {
                    // report errors
                    report.info(AppPilot.class,
                            headerLanguage(language)
                                    + String.join(",", listReports));
                }
            }
        }
    }

    /**
     * Do the completion on each dictionary
     *
     * @param synchroParams parameter object
     * @param report report object
     */
    public void completion(SynchroParams synchroParams, ReportInt report) {
        AppDictionary referenceDictionary = new AppDictionary(folder, referenceLanguage);
        referenceDictionary.read(report);

        // check each dictionary
        report.info(AppPilot.class, "----- Folder " + folder);

        for (String language : expectedLanguages) {
            if (synchroParams.getOnlyCompleteOneLanguage() != null && !synchroParams.getOnlyCompleteOneLanguage().equals(language))
                continue;

            if (language.equals(referenceLanguage)) {
                report.info(AppPilot.class, headerLanguage(language) + "Referential");
                continue;
            }

            AppDictionary appDictionary = new AppDictionary(folder, language);


            //----------------  Read and complete

            // read the dictionary
            if (appDictionary.existFile() && !appDictionary.read(report)) {
                // file exist, but not possible to read: better to have a look here
                report.severe(AppPilot.class, "File [" + appDictionary.getFile().getAbsolutePath() + "] exist, but impossible to read it: check it");
                continue;
            }
            // purge all TRANSLATE key
            appDictionary.getDictionary()
                    .entrySet()
                    .removeIf(entry -> (entry.getKey().endsWith(SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE_REFERENCE)
                            || entry.getKey().endsWith(SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE)));

            // check and complete
            AppDictionary.DicoStatus dictionaryStatus = appDictionary.checkKeys(referenceDictionary);
            List<String> listReports = new ArrayList<>();

            if (dictionaryStatus.nbMissingKeys > 0) {
                listReports.add("Add " + dictionaryStatus.nbMissingKeys + " keys");

                dictionaryStatus.missingKeys.stream()
                        .forEach(key -> manageAddKey(appDictionary, referenceDictionary, key)  );
            }

            if (dictionaryStatus.nbTooMuchKeys > 0) {
                listReports.add("Remove " + dictionaryStatus.nbMissingKeys + " keys");
                dictionaryStatus.tooMuchKeys.stream()
                        .forEach(key -> appDictionary.removeKey(key));
            }
            if (dictionaryStatus.nbIncorrectKeyClass > 0) {
                listReports.add("Replace " + dictionaryStatus.nbMissingKeys + " keys");
                dictionaryStatus.incorrectClass
                        .forEach((key -> {
                            appDictionary.removeKey(key);
                            manageAddKey(appDictionary, referenceDictionary, key);
                        }));
            }
            if (listReports.isEmpty())
                listReports.add("Nothing done.");
            report.info(AppPilot.class,
                    headerLanguage(language)
                            + String.join(",", listReports));


            // -----------Write it
            if (appDictionary.isModified()) {
                boolean statusWrite = appDictionary.write(report);
                if (statusWrite)
                    report.info(AppPilot.class, INDENTATION + "   " + "Dictionary wrote with success.");
                else
                    report.severe(AppPilot.class, INDENTATION + "   " + "Error writing dictionary.");
            }
        }
    }

    /**
     * Manage the way to add a key in the dictionary.
     * @param appDictionary dictionary to add the key
     * @param referenceDictionary referential dictionary, then the value can be accessed
     * @param key key to add
     */
    private void manageAddKey(AppDictionary appDictionary, AppDictionary referenceDictionary, String key) {
        Object valueReference = referenceDictionary.getDictionary().get(key);

        if (valueReference instanceof Long || valueReference instanceof Integer|| valueReference instanceof  Double) {
            // we add the key as it
            if (valueReference instanceof Double) {
                Double valueReferenceDouble = (Double) valueReference;
                if (Math.round(valueReferenceDouble) == valueReferenceDouble)
                    valueReference = valueReferenceDouble.intValue();
            }
            appDictionary.addKey(key, valueReference);
        } else {
            appDictionary.addKey(key + SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE, referenceDictionary.getDictionary().get(key));
            appDictionary.addKey(key + SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE_REFERENCE, referenceDictionary.getDictionary().get(key));
        }
    }

    /**
     * From the language, return a standard header string
     * @param language language
     * @return the standard header string
     */
    private String headerLanguage(String language) {
        String label = INDENTATION + "[" + language + "]          ";
        return label.substring(0, INDENTATION.length() + 10) + " ... ";
    }
}
