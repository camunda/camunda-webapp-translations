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

import org.camunda.webapptranslation.org.camunda.webapptranslation.ReportInt;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AppPilot {

    File folder = null;
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
     * @param report report the status
     */
    public void detection(ReportInt report) {
        AppDictionary referenceDictionary = new AppDictionary(folder, referenceLanguage);
        referenceDictionary.read(report);

        // check each dictionary
        report.info(AppPilot.class, "----- Folder " + folder);
        for (String language : expectedLanguages) {
            if (language.equals(referenceLanguage))
                continue;
            AppDictionary dico = new AppDictionary(folder, language);
            if (!dico.existFile()) {
                report.info(AppPilot.class, INDENTATION + language + " does not exist");
            } else if (!dico.read(report)) {
                // error already reported
                continue;
            } else {
                AppDictionary.DicoStatus dicoStatus = dico.checkKeys(referenceDictionary);
                if (dicoStatus.missingKeys > 0 || dicoStatus.tooMuchKeys > 0) {
                    report.info(AppPilot.class, INDENTATION + language + ": "
                            + (dicoStatus.missingKeys > 0 ? "missing " + dicoStatus.missingKeys + " keys, " : "")
                            + (dicoStatus.tooMuchKeys > 0 ? "too much " + dicoStatus.tooMuchKeys + " keys, " : "")
                            + (dicoStatus.incorrectKeyClass > 0 ? "incorrect class " + dicoStatus.incorrectKeyClass + " keys, " : ""));
                } else
                    report.info(AppPilot.class, INDENTATION + language + " .. ok ");
            }
        }
    }

    public void complete() {
    // to be implemented
    }
}
