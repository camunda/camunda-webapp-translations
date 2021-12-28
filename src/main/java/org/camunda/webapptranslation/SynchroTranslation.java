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
import java.util.*;

public class SynchroTranslation {


    public static void main(String[] args) {

        SynchroParams synchroParams = new SynchroParams();
        synchroParams.explore(args);
        if (synchroParams.isUsage()) {
            synchroParams.printUsage();
            return;
        }

        synchroParams.printOptions();

        // get the report
        final ReportInt report;
        switch (synchroParams.getReport()) {
            case LOGGER:
                report = new ReportLogger();
                break;
            case STDOUT:
                report = new ReportStdout();
                break;
            default:
                report = new ReportStdout();
        }
        // Get all dictionary folders
        List<File> listFolders = getDictionaryFolder(synchroParams, report);
        if (listFolders.isEmpty()) {
            report.severe(SynchroTranslation.class, "No folder detected containing a file for the language [" + synchroParams.getReferenceLanguage() + "]");
            return;
        }
        // Build Application Pilot per folder
        List<AppPilot> listAppPilot = new ArrayList<>();
        listFolders.forEach(folder -> listAppPilot.add(new AppPilot(folder, synchroParams.getReferenceLanguage())));

        // Collect the list of expected languages
        Set<String> expectedLanguage = new HashSet<>();
        listAppPilot.forEach(pilot -> expectedLanguage.addAll(pilot.getLanguages()));
        listAppPilot.forEach(pilot -> pilot.setExpectedLanguage(expectedLanguage));

        // ---------- Detection
        if (synchroParams.getDetection() != SynchroParams.DETECTION.NO) {
            report.info(SynchroTranslation.class, "=================================== Detection ===================================");
            listAppPilot.forEach(pilot -> pilot.detection(synchroParams, report));
        }

        // ---------- Completion
        if (synchroParams.getCompletion() != SynchroParams.COMPLETION.NO) {
            report.info(SynchroTranslation.class, "=================================== Completion ===================================");
            EncyclopediaUniversal encyclopediaUniversal = new EncyclopediaUniversal(synchroParams.getReferenceLanguage());

            // Build the list of proposal objects
            List<Proposal> listProposals = new ArrayList<>();
            if (synchroParams.getCompletion() == SynchroParams.COMPLETION.TRANSLATION) {
                List<Proposal> listAllProposal = Arrays.asList(new ProposalSameKey(),
                        new ProposalSameTranslation(),
                        new ProposalGoogleTranslate(synchroParams.getGoogleAPIKey(), synchroParams.getLimitNumberGoogleTranslation()));

                listAllProposal.forEach(proposal -> {
                    if (proposal.begin(report)) {
                        listProposals.add(proposal);
                    }
                });

            }

            listAppPilot.forEach(pilot -> pilot.completeEncyclopedia(encyclopediaUniversal, synchroParams, report));
            // Do the completion now
            listAppPilot.forEach(pilot -> pilot.completion(encyclopediaUniversal, listProposals, synchroParams, report));

            listProposals.forEach(proposal -> proposal.end(report));
        }

        System.out.println("The end");
    }

    /**
     * Check the disk and retrieve all the different folder
     *
     * @param synchroParams parameters to access the root folder
     * @param report        to report any error
     * @return the list of all folder where a dictionary is detected, based on the reference language
     */
    public static List<File> getDictionaryFolder(SynchroParams synchroParams, ReportInt report) {
        report.info(SynchroTranslation.class, "Explore from rootFolder [" + synchroParams.getRootFolder() + "]");
        return completeRecursiveFolder(synchroParams.getRootFolder(), synchroParams.getReferenceLanguage(), report);
    }

    private static List<File> completeRecursiveFolder(String folder, String referenceLanguage, ReportInt report) {
        // check the current folder and it's child
        List<File> listFolders = new ArrayList<>();
        try {
            for (File file : Objects.requireNonNull(new File(folder).listFiles())) {
                if (file.isDirectory()) {
                    listFolders.addAll(completeRecursiveFolder(file.getAbsolutePath(), referenceLanguage, report));
                }
                if (file.getName().contains(referenceLanguage + ".json")) {
                    listFolders.add(file.getParentFile());
                    report.info(SynchroTranslation.class, "Detect [" + file.getParentFile().getAbsolutePath() + "]");
                }
            }
        } catch (Exception e) {
            report.severe(SynchroTranslation.class, "Error parsing folder [" + folder + "]", e);
        }
        return listFolders;
    }
}
