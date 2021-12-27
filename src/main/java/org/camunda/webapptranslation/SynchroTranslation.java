package org.camunda.webapptranslation;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* SynchroTranslation                                                    */
/*                                                                      */
/*                                                                      */
/* -------------------------------------------------------------------- */

import org.camunda.webapptranslation.app.AppPilot;
import org.camunda.webapptranslation.operation.*;
import org.camunda.webapptranslation.report.ReportInt;
import org.camunda.webapptranslation.report.ReportLogger;
import org.camunda.webapptranslation.report.ReportStdout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SynchroTranslation {

    public static void main(String[] args) {

        SynchroParams synchroParams = new SynchroParams();
        synchroParams.explore(args);
        if (synchroParams.isUsage()) {
            synchroParams.printUsage();
            return;
        }

        // get the report
        final ReportInt report;
        switch(synchroParams.getReport()) {
            case LOGGER:
                report = new ReportLogger();
                break;
            case STDOUT:
                report = new ReportStdout();
                break;
            default:
                report = new ReportStdout();
        }
        // get all dictionary folder
        List<File> listFolders = getDictionaryFolder(synchroParams, report);
        if (listFolders.isEmpty()) {
            report.severe(SynchroTranslation.class, "No folder detected containing a file for the language ["+synchroParams.getReferenceLanguage()+"]");
            return;
        }
        // build all Application Pilot
        List<AppPilot> listAppPilot = new ArrayList<>();
        listFolders.forEach(folder -> listAppPilot.add(new AppPilot(folder, synchroParams.getReferenceLanguage())));

        // collect the list of expected languages
        Set<String> expectedLanguage = new HashSet<>();
        listAppPilot.forEach(pilot -> expectedLanguage.addAll(pilot.getLanguages()));
        listAppPilot.forEach(pilot->pilot.setExpectedLanguage(expectedLanguage));

        // ---------- detection
        if (synchroParams.getDetection() != SynchroParams.DETECTION.NO) {
            report.info(SynchroTranslation.class, "=================================== Detection ===================================");
            listAppPilot.forEach(pilot -> pilot.detection(synchroParams, report));
        }
        if (synchroParams.getCompletion() != SynchroParams.COMPLETION.NO) {
            report.info(SynchroTranslation.class, "=================================== Completion ===================================");
            EncyclopediaUniversal encyclopediaUniversal = new EncyclopediaUniversal( synchroParams.getReferenceLanguage());

            List<Proposal> listProposals = new ArrayList<>();
            if (synchroParams.getCompletion() == SynchroParams.COMPLETION.TRANSLATION) {
                listProposals.add(new ProposalSameKey());
                listProposals.add(new ProposalSameTranslation());
                listProposals.add(new ProposalGoogleTranslate( synchroParams.getGoogleAPIKey(), synchroParams.getLimitNumberGoogleTranslation()));
            }



            listAppPilot.forEach(pilot->pilot.completeEncyclopedia(encyclopediaUniversal, synchroParams, report));
            listAppPilot.forEach(pilot -> pilot.completion(encyclopediaUniversal, listProposals, synchroParams, report));
        }

        System.out.println("The end");
    }

    /**
     * Check the disk and retrieve all the different folder
     *
     * @param synchroParams parameters to access the root folder
     * @param report        to report any error
     * @return the list of all folder where a dictionary is dectected, based on the reference language
     */
    public static List<File> getDictionaryFolder(SynchroParams synchroParams, ReportInt report) {
        report.info(SynchroTranslation.class,"Explore from rootFolder ["+synchroParams.getRootFolder()+"]");
        return completeRecursiveFolder(synchroParams.getRootFolder(), synchroParams.getReferenceLanguage(), report);
    }

    private static List<File> completeRecursiveFolder(String folder, String referenceLanguage, ReportInt report) {
        // check the current folder and it's child
        List<File> listFolders = new ArrayList<>();
        try {
            for (File file : new File(folder).listFiles()) {
                if (file.isDirectory()) {
                    listFolders.addAll(completeRecursiveFolder(file.getAbsolutePath(), referenceLanguage, report));
                }
                if (file.getName().contains(referenceLanguage + ".json")) {
                    listFolders.add(file.getParentFile());
                    report.info(SynchroTranslation.class, "Detect ["+file.getParentFile().getAbsolutePath()+"]");
                }
            }
        } catch (Exception e) {
            report.severe(SynchroTranslation.class, "Error parsing folder [" + folder + "]", e);
        }
        return listFolders;
    }
}
