package org.camunda.webapptranslation.app;


/* -------------------------------------------------------------------- */
/*                                                                      */
/* Manage an application (a directory)                                  */
/*                                                                      */
/* The class dectect all languages present (and missing), compare each  */
/* dictionary with the referentiel language. It can just play to detect */
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
                AppDictionary.DicoStatus dicoStatus = dico.checkKeys(referenceDictionary, synchroParams.getDetection() == SynchroParams.DETECTION.FULL);
                List<String> listReports = new ArrayList<>();
                if (dicoStatus.nbMissingKeys > 0)
                    listReports.add("Missing " + dicoStatus.nbMissingKeys + " keys");
                if (dicoStatus.nbTooMuchKeys > 0)
                    listReports.add("Too much " + dicoStatus.nbTooMuchKeys + " keys");
                if (dicoStatus.nbIncorrectKeyClass > 0)
                    listReports.add("Incorrect class " + dicoStatus.nbIncorrectKeyClass + " keys");

                if (listReports.isEmpty())
                    report.info(AppPilot.class, headerLanguage(language) + "OK");
                else {
                    // report errors
                    report.info(AppPilot.class,
                            headerLanguage(language)
                                    + listReports.stream().collect(Collectors.joining(",")));
                    if (synchroParams.getDetection() == SynchroParams.DETECTION.FULL) {
                        if (!dicoStatus.missingKeys.isEmpty())
                            report.info(AppPilot.class,
                                    INDENTATION + "  MISSING:" + dicoStatus.missingKeys.stream().collect(Collectors.joining(",")));
                        if (!dicoStatus.tooMuchKeys.isEmpty())
                            report.info(AppPilot.class,
                                    INDENTATION + "  TOO MUCH:" + dicoStatus.tooMuchKeys.stream().collect(Collectors.joining(",")));
                        if (!dicoStatus.incorrectClass.isEmpty())
                            report.info(AppPilot.class,
                                    INDENTATION + "  INCORRECT CLASS:" + dicoStatus.incorrectClass.stream().collect(Collectors.joining(",")));
                    }

                }
            }
        }
    }

    /**
     * Do the completion on each dictionary
     *
     * @param synchroParams
     * @param report
     */
    public void completion(SynchroParams synchroParams, ReportInt report) {
        AppDictionary referenceDictionary = new AppDictionary(folder, referenceLanguage);
        referenceDictionary.read(report);

        // check each dictionary
        report.info(AppPilot.class, "----- Folder " + folder);
        if (! folder.getAbsolutePath().endsWith("welcome"))
            return;

        for (String language : expectedLanguages) {
            if (! language.equals("fr"))
                continue;

            if (language.equals(referenceLanguage)) {
                report.info(AppPilot.class, headerLanguage(language) + "Referentiel");
                continue;
            }

            AppDictionary dico = new AppDictionary(folder, language);
            if (!dico.existFile()) {
                report.info(AppPilot.class, headerLanguage(language) + "Not exist (" + referenceDictionary.getDictionary().size() + " missing keys)");
                // create all keys
                for (Map.Entry<String, Object> entry : referenceDictionary.getDictionary().entrySet()) {
                    if (entry.getValue() instanceof Long || entry.getValue() instanceof Integer) {
                        dico.addKey(entry.getKey(), entry.getValue());
                    } else {
                        dico.addKey(entry.getKey() + SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE, entry.getValue());
                    }
                }
                continue;
            }

            // read the dictionary
            if (!dico.read(report)) {
                // file exist, but not possible to read: better to have a look here
                report.severe(AppPilot.class, "File [" + dico.getFile().getAbsolutePath() + "] exist, but impossible to read it: check it");
                continue;
            }
                // check and complete
                AppDictionary.DicoStatus dicoStatus = dico.checkKeys(referenceDictionary, true);
                dicoStatus.missingKeys.stream()
                        .forEach(key -> dico.addKey(key + SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE, referenceDictionary.getDictionary().get(key)));

                dicoStatus.tooMuchKeys.stream()
                        .forEach(key -> dico.removeKey(key));

                dicoStatus.incorrectClass.stream()
                        .forEach((key -> {
                            dico.removeKey(key);
                            dico.addKey(key + SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE, referenceDictionary.getDictionary().get(key));
                            ;
                        }));


            if (dico.isModified())
                dico.write(report);
        }
    }

    private String headerLanguage(String langage) {
        String label = INDENTATION + "[" + langage + "]          ";
        return label.substring(0, INDENTATION.length() + 10) + " ... ";
    }
}
