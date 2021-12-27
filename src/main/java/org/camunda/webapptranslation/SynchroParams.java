package org.camunda.webapptranslation;

public class SynchroParams {

    private boolean usage = false;
    private String referenceLanguage = "en";
    private String rootFolder = ".";

    /**
     * If not null, then only this language is completed (the -c option must be set too)
     */
    private String onlyCompleteOneLanguage = null;
    public static final String PLEASE_TRANSLATE_THE_SENTENCE = "_PLEASETRANSLATETHESENTENCE";
    public static final String PLEASE_VERIFY_THE_SENTENCE = "_PLEASEVERIFYTHESENTENCE";
    public static final String PLEASE_TRANSLATE_THE_SENTENCE_REFERENCE = "_PLEASETRANSLATETHESENTENCE_REFERENCE";
    public static final String PLEASE_VERIFY_THE_SENTENCE_REFERENCE = "_PLEASEVERIFYTHESENTENCE_REFERENCE";

    public enum DETECTION {NO, SYNTHETIC, FULL}

    private DETECTION detection = DETECTION.SYNTHETIC;

    public enum COMPLETION {NO, KEYS, TRANSLATION}

    private COMPLETION completion = COMPLETION.NO;

    public enum REPORT {STDOUT, LOGGER}

    private REPORT report = REPORT.STDOUT;

    private String googleAPIKey;
    private int limitNumberGoogleTranslation = 100;

    /**
     * Explore the arguments to fulfil parameters
     *
     * @param args arguments
     */
    public void explore(String[] args) {
        int i = 0;
        try {
            rootFolder = new java.io.File(".").getCanonicalPath();
        } catch (Exception e) {
            System.out.println("Can't access current Path");
        }
        while (i < args.length) {
            if (("-d".equals(args[i]) || "--detect".equals(args[i])) && i < args.length - 1) {
                try {
                    detection = DETECTION.valueOf(args[i + 1]);
                } catch (Exception e) {
                    System.out.println("-d <" + DETECTION.NO + "|" + DETECTION.SYNTHETIC + "|" + DETECTION.FULL + "> detection and comparaison. Default is " + DETECTION.SYNTHETIC);
                }
                i += 2;
            } else if (("-l".equals(args[i]) || "--language".equals(args[i])) && i < args.length - 1) {
                onlyCompleteOneLanguage = args[i + 1];
                i += 2;
            } else if (("-c".equals(args[i]) || "--completion".equals(args[i])) && i < args.length - 1) {
                try {
                    completion = COMPLETION.valueOf(args[i + 1]);
                } catch (Exception e) {
                    System.out.println("-d <" + COMPLETION.NO + "|" + COMPLETION.KEYS + "|" + COMPLETION.TRANSLATION + "> Complete each dictionary. Default is " + COMPLETION.NO);
                }
                i += 2;
            } else if (("-g".equals(args[i]) || "--googleAPIKey".equals(args[i])) && i < args.length - 1) {
                googleAPIKey = args[i + 1];
                i += 2;
            } else if (("--limiteGoogleAPIKey".equals(args[i])) && i < args.length - 1) {
                try {
                    limitNumberGoogleTranslation = Integer.valueOf(args[i + 1]);
                } catch (Exception e) {
                    System.out.println("-limiteGoogleAPIKey <number>");

                }
                i += 2;
            } else if ("-u".equals(args[i]) || "--usage".equals(args[i])) {
                usage = true;
                i++;
            } else if (("-f".equals(args[i]) || "--folder".equals(args[i])) && i < args.length - 1) {
                rootFolder = args[i + 1];
                i += 2;
            } else if (("-r".equals(args[i]) || "--report".equals(args[i])) && i < args.length - 1) {
                try {
                    report = REPORT.valueOf(args[i + 1]);
                } catch (Exception e) {
                    System.out.println("-r <" + REPORT.STDOUT + "|" + REPORT.LOGGER + "> accepted");
                }
                i += 2;
            } else {
                referenceLanguage = args[i];
                i++;
            }
        }
        System.out.println(" Folder to study: " + getRootFolder());
        System.out.println(" Reference language: " + getReferenceLanguage());
        System.out.println(" Detection: " + getDetection());
        System.out.println(" Completion: " + getCompletion());
        if (getGoogleAPIKey() != null) {
            System.out.println(" GoogleAPIKey: " + getGoogleAPIKey());
            System.out.println(" Maximum number of Google  translation: " + getLimitNumberGoogleTranslation());
        }
        if (getOnlyCompleteOneLanguage() != null)
            System.out.println(" Only one language: " + getOnlyCompleteOneLanguage());

        System.out.println(" Report: " + getReport());
    }


    public DETECTION getDetection() {
        return detection;
    }

    public boolean isUsage() {
        return usage;
    }

    public COMPLETION getCompletion() {
        return completion;
    }

    public String getReferenceLanguage() {
        return referenceLanguage;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public REPORT getReport() {
        return report;
    }

    public String getOnlyCompleteOneLanguage() {
        return onlyCompleteOneLanguage;
    }

    public String getGoogleAPIKey() {
        return googleAPIKey;
    }

    public int getLimitNumberGoogleTranslation() {
        return limitNumberGoogleTranslation;
    }

    public void printUsage() {
        System.out.println("Usage: SynchroTranslation [-d|--detect] [-c|--complete] [-u|--usage] <ReferenceLanguage> <Folder>");
        System.out.println(" Subfolders contains a list of .json files. Each file is a language (de.json). The reference language contains all references. Each language is controlled from this reference to detect the missing keys.");
        System.out.println(" -d|--detect <" + DETECTION.NO + "|" + DETECTION.SYNTHETIC + "|" + DETECTION.FULL + "> detection and comparaison. Default is " + DETECTION.SYNTHETIC);

        System.out.println(" -c|--complete: <" + COMPLETION.NO + "|" + COMPLETION.KEYS + "|" + COMPLETION.TRANSLATION + "> missing keys are created in each the dictionary. Current file are saved with <language>_<date>.txt and a new file is created. Missing keys are suffixed with '" + PLEASE_TRANSLATE_THE_SENTENCE
                + "'. With " + COMPLETION.TRANSLATION + ", dictionary are exploded to get a good translation. Default is " + COMPLETION.NO);
        System.out.println(" -g|--googleAPIKey <GoogleAPIKey>: Give a Google API Key to translate the missing keys");
        System.out.println(" --limiteGoogleAPIKey <Number of Translation>: Set the limit. Default is 100");
        System.out.println(" -l|--language <language>: if set, only this language is analysed / completed");

        System.out.println(" -f|--folder <folder>: dictionary are under this folder. Else, default folder is ../..");
        System.out.println(" -r|--report  <" + REPORT.STDOUT + "|" + REPORT.LOGGER + ">");

    }
}
