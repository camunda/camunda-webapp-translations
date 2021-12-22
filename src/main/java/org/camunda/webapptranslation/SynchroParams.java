package org.camunda.webapptranslation;

public class SynchroParams {

    private boolean usage=false;
    private String referenceLanguage="en";
    private String rootFolder=".";

    public static final String PLEASE_TRANSLATE_THE_SENTENCE="_PLEASETRANSLATETHESENTENCE";
    public enum DETECTION { NO, SYNTHETIC, FULL}
    private DETECTION detection=DETECTION.SYNTHETIC;

    public enum COMPLETION { NO, KEYS, TRANSLATION}
    private COMPLETION completion= COMPLETION.NO;

    public enum REPORT { STDOUT, LOGGER}
    private REPORT report= REPORT.STDOUT;



    /**
     * Explore the arguments to fulfil parameters
     * @param args
     */
    public void explore(String[] args) {
        int i=0;
        try {
            rootFolder = new java.io.File(".").getCanonicalPath();
        } catch(Exception e) {
            System.out.println("Can't access current Path");
        }
        while (i<args.length) {
            if ("-d".equals(args[i]) || "--detect".equals(args[i])) {
                try {
                    detection = DETECTION.valueOf(args[i + 1]);
                }catch(Exception e) {
                    System.out.println("-d <"+DETECTION.NO.toString()+"|"+DETECTION.SYNTHETIC.toString()+"|"+DETECTION.FULL+"> detection and comparaison. Default is "+DETECTION.SYNTHETIC);
                }
                i+=2;
            }
            else if ("-c".equals(args[i]) || "--completion".equals(args[i])) {
                try {
                    completion = COMPLETION.valueOf(args[i + 1]);
                }catch(Exception e) {
                    System.out.println("-d <"+COMPLETION.NO.toString()+"|"+COMPLETION.KEYS.toString()+"|"+COMPLETION.TRANSLATION+"> Complete each dictionary. Default is "+COMPLETION.NO);
                }
                i+=2;
            }
            else if ("-u".equals(args[i]) || "--usage".equals(args[i])) {
                usage=true;
                i++;
            }
            else if (("-f".equals(args[i]) || "--folder".equals(args[i])) && i<args.length-1) {
                rootFolder=args[i+1];
                i+=2;
            }
            else if (("-r".equals(args[i]) || "--report".equals(args[i])) && i<args.length-1) {
                try {
                    report = REPORT.valueOf(args[i + 1]);
                }catch(Exception e) {
                  System.out.println("-r <"+REPORT.STDOUT.toString()+"|"+REPORT.LOGGER.toString()+"> accepted");
                }
                i+=2;
            }
            else {
                referenceLanguage=args[ i ];
                i++;

            }
        }
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

    public void printUsage() {
        System.out.println("Usage: SynchroTranslation [-d|--detect] [-c|--complete] [-u|--usage] <ReferenceLanguage> <Folder>");
        System.out.println(" Subfolders contains a list of .json files. Each file is a language (de.json). The reference language contains all references. Each language is controlled from this reference to detect the missing keys.");
        System.out.println(" -d|--detect <"+DETECTION.NO.toString()+"|"+DETECTION.SYNTHETIC.toString()+"|"+DETECTION.FULL+"> detection and comparaison. Default is SYNTHETIC ");

        System.out.println(" -c|--complete: <"+COMPLETION.NO.toString()+"|"+COMPLETION.KEYS.toString()+"|"+COMPLETION.TRANSLATION+"> missing keys are created in each the dictionary. Current file are saved with <language>_<date>.txt and a new file is created. Missing keys are suffixed with '"+PLEASE_TRANSLATE_THE_SENTENCE+"'");

        System.out.println(" -f|--folder <folder>: dictionary are under this folder. Else, default folder is ../..");
        System.out.println(" -r|--report  <"+REPORT.STDOUT.toString()+"|"+REPORT.LOGGER.toString()+">");

    }
}
