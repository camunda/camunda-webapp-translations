package org.camunda.webapptranslation.operation;

import org.camunda.webapptranslation.SynchroParams;
import org.camunda.webapptranslation.app.AppDictionary;
import org.camunda.webapptranslation.app.AppPilot;
import org.camunda.webapptranslation.report.ReportInt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Detection                              */
/*                                                                      */
/* The operation detect any issues in the dictionary, and do not change them*/
/*                                                                      */
/* -------------------------------------------------------------------- */

public class DictionaryDetection extends Operation {



    /**
     * Detection
     * Check that
     * 1/ all dictionary exists
     * 2/ the language is complete
     *
     * @param synchroParams access to parameters
     * @param report        report the status
     */
    public void detection(Set<String> expectedLanguages,
                          File folder,
                          AppDictionary referenceDictionary,
                          SynchroParams synchroParams, ReportInt report) {

        report.info(AppPilot.class, "----- Folder " + folder);

        // check each dictionary
        for (String language : expectedLanguages) {
            if (synchroParams.getOnlyCompleteOneLanguage() != null && !synchroParams.getOnlyCompleteOneLanguage().equals(language))
                continue;

            if (language.equals(referenceDictionary.getLanguage())) {
                report.info(AppPilot.class, headerLanguage(language) + "Referentiel");
                continue;
            }
            AppDictionary dictionary = new AppDictionary(folder, language);
            if (!dictionary.existFile()) {
                report.info(AppPilot.class, headerLanguage(language) + "Not exist (" + referenceDictionary.getDictionary().size() + " missing keys)");
                continue;
            }
            // read the dictionary
            if (!dictionary.read(report)) {
                // error already reported
                continue;
            } else {
                DictionaryStatus dictionaryStatus = checkKeys(dictionary, referenceDictionary);
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


}
