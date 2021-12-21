package org.camunda.webapptranslation;

public class SynchroParams {

    private boolean detection=true;
    private boolean usage=false;
    private boolean complete=true;
    private String referenceLanguage="en";
    private String rootFolder=".";

    public void explore(String[] args) {
        int i=0;
        try {
            rootFolder = new java.io.File(".").getCanonicalPath();
        } catch(Exception e) {
            System.out.println("Can't access current Path");
        }
        while (i<args.length) {
            if ("-d".equals(args[i]) || "--detect".equals(args[i])) {
                detection=true;
                i++;
            }
            else if ("-u".equals(args[i]) || "--usage".equals(args[i])) {
                usage=true;
                i++;
            }
            else if ("-c".equals(args[i]) || "--complete".equals(args[i])) {
                complete=true;
                i++;
            }
            else if (("-f".equals(args[i]) || "--folder".equals(args[i])) && i<args.length-1) {
                rootFolder=args[i+1];
                i++;
            }
            else {
                referenceLanguage=args[ i ];
            }

        }
    }


    public boolean isDetection() {
        return detection;
    }

    public boolean isUsage() {
        return usage;
    }

    public boolean isComplete() {
        return complete;
    }

    public String getReferenceLanguage() {
        return referenceLanguage;
    }

    public String getRootFolder() {
        return rootFolder;
    }

    public void printUsage() {
        System.out.println("Usage: SynchroTranslation [-d|--detect] [-c|--complete] [-u|--usage] <ReferenceLanguage> <Folder>");
        System.out.println(" Subfolders contains a list of .json files. Each file is a language (de.json). The reference language contains all references. Each language is controlled from this reference to detect the missing keys.");
        System.out.println(" -d|--detect : missing keys are reported");
        System.out.println(" -c|-complete: missing keys are created in the dictionary. Current file are saved with <language>_<date>.txt and a new file is created. Missing key are suffixed with '_PLEASETRANSLATETHESENTENCE' key");
        System.out.println(" -f|--folder <folder>: dictionary are under this folder. Else, default folder is ../..");

    }
}
