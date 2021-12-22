package org.camunda.webapptranslation.operation;

import org.camunda.webapptranslation.SynchroParams;
import org.camunda.webapptranslation.app.AppDictionary;
import org.camunda.webapptranslation.app.AppPilot;
import org.camunda.webapptranslation.report.ReportInt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DictionaryCompletion extends Operation {

    /**
     * Do the completion on each dictionary
     *
     * @param synchroParams parameter object
     * @param report report object
     */
    public void completion(Set<String> expectedLanguages, File folder,
                           AppDictionary referenceDictionary, SynchroParams synchroParams, ReportInt report) {

        // check each dictionary
        report.info(AppPilot.class, "----- Folder " + folder);

        for (String language : expectedLanguages) {
            if (synchroParams.getOnlyCompleteOneLanguage() != null && !synchroParams.getOnlyCompleteOneLanguage().equals(language))
                continue;

            if (language.equals(referenceDictionary.getLanguage())) {
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
            DicoStatus dictionaryStatus = checkKeys(appDictionary, referenceDictionary);
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

}
