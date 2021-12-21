package org.camunda.webapptranslation.app;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Manage, for an application (a directory) a language */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.camunda.webapptranslation.org.camunda.webapptranslation.ReportInt;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class AppDictionary {
    public final static String PREFIX_PLEASE_TRANSLATE="";

    private File path;
    private String language;
    /**
     * Dictionary, JSON format is a hierarchy collection of String or list
     * Something like
     * {
     *   "labels": {
     *     "APP_VENDOR": "Camunda",
     *   },
     *    "monthsShort": [
     *       "Jan",
     *       "Feb",
     *       "Mar"
     *       ],
     *   "week": {
     *       "dow": 1,
     *       "doy": 4
     *     }
     * }
     */
    private Map<String,Object> dictionary;
    /**
     * marker to know if the dictionary is modified or not
     */
    private boolean dictionaryIsModified = false;
    public AppDictionary(File path, String language) {
        this.path = path;
        this.language = language;
    }


    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Dictionary to file                                                   */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Check if the file relative to the dictionary exists
     * @return true if the file exists
     */
    public boolean existFile() {
        File file = getFile();
        return file.exists();
    }
    private static final String UTF8_BOM = "\uFEFF";

    /**
     * @return true if the dictionary was read without error
     */
    public boolean read(ReportInt report ){
        // clear the dictionary
        dictionary=new HashMap<>();
        File file = getFile();
        JSONParser jsonParser = new JSONParser();
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)
        ) {

            // Some dictionary are BOM UTF8, which is not correct.
            String str;
            StringBuilder readerStr = new StringBuilder();
            while ((str = reader.readLine()) != null) {
                readerStr.append(str);
            }

            String jsonComplete = readerStr.toString();
            if (jsonComplete.startsWith(UTF8_BOM))
                jsonComplete = jsonComplete.substring(1);

            JSONObject dictionaryJson = (JSONObject) jsonParser.parse(jsonComplete);
            convertToFlatList(dictionaryJson, "");

            dictionaryIsModified = false;
            return true;
        } catch (Exception e) {
            report.severe(AppDictionary.class, String.format(" Error during reading dictionary [%s] file[%s]", language, file.getAbsolutePath()), e);
            return false;
        }
    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Read/Write */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Write the dictionary
     *
     * @return true if the dictionary has been successfully written
     */
    public boolean write(ReportInt report) {
        File file = getFile();
        try {
            // file exist before ? Rename it to .bak

            if (file.exists()) {
                File backupDirectory = new File(file.getParentFile().getAbsolutePath());
                String fileName = file.getName();
                fileName = fileName.replace(".json", ".bak");
                File destFile = new File(backupDirectory.getAbsolutePath() + "/" + fileName);

                file.renameTo(destFile);
            }
        } catch(Exception e) {
            report.severe(AppDictionary.class,String.format(" Error during writing dictionary [%s] file[%s]", language, file.getName()), e);
            return false;
        }



            try (FileWriter writer = new FileWriter(file.getAbsolutePath())) {
                //We can write any JSONArray or JSONObject instance to the file
                JSONObject dicoJson = convertFromFlatList();
                writer.write(dicoJson.toJSONString());
                writer.flush();

        } catch (Exception e) {
                report.severe(AppDictionary.class, String.format(" Error during writing dictionary [%s] file[%s]", language, file.getName()), e);
                return false;
            }
        return true;
    }
    public boolean isModified() {
        return dictionaryIsModified;
    }


    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* internal : transform the hierarchy JSON to a FLAT structure,         */
    /*  and inverse                                                         */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Get all keys. The dictionary is a hierarchy, like "{labels { APPVENDOR = ""}}. Returns this value as "labels.APPVENDOR"
     */
    private void convertToFlatList(JSONObject jsonDico, String hierarchy) {

        for (Map.Entry<String,Object> entry: (Set<Map.Entry>) jsonDico.entrySet()) {
            if (entry.getValue() instanceof Map) {
                convertToFlatList((JSONObject) entry.getValue(), (hierarchy.length()>0 ? hierarchy+".":"")+entry.getKey());
            } else if (entry.getValue() instanceof List) {
                dictionary.put(hierarchy+entry.getKey(), entry.getValue());
            } else if (entry.getValue() instanceof Long || entry.getValue() instanceof Integer) {
                dictionary.put(hierarchy + entry.getKey(), entry.getValue());
            } else {
                dictionary.put(hierarchy+entry.getKey(), entry.getValue());
            }
        }
    }
    private JSONObject convertFromFlatList() {
        JSONObject dicoObject = new JSONObject();
        for (Map.Entry<String,Object> entry : dictionary.entrySet()) {
            StringTokenizer st = new StringTokenizer(entry.getKey(),".");
            List<String> tokens= new ArrayList();
            while (st.hasMoreTokens()) {
                tokens.add(st.nextToken()); // use already extracted value
            }
            JSONObject container = getContainer( tokens, dicoObject);
            container.put(tokens.get( tokens.size()-1), entry.getValue());
        }
        return dicoObject;
    }

    /**
     * Get the container according the list of Tokens. Each token is a key in the map
     * @param tokens list of tokens to navigate inside containers
     * @param dicoObject the root object to navigate in
     * @return the local container according the tokens list
     */
    private JSONObject getContainer(List<String> tokens, JSONObject dicoObject) {
        JSONObject currentContainer = dicoObject;
        for (int i=0;i<tokens.size()-1;i++) {
            if (currentContainer.containsKey( tokens.get( i )))
                currentContainer = (JSONObject) currentContainer.get(tokens.get(i));
            else {
                JSONObject subContainer = new JSONObject();
                currentContainer.put(tokens.get(i), subContainer);
                currentContainer = subContainer;
            }
        }
        return currentContainer;
    }



    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Operation */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * CheckKeys
     * Verify that this dictionary is complete in regard of the reference dictionary
     */
    DicoStatus checkKeys(AppDictionary referenceDictionary)
    {
        DicoStatus dicoStatus = new DicoStatus();
        for (String key:referenceDictionary.getDictionary().keySet()) {
            if (! dictionary.containsKey(key))
                dicoStatus.missingKeys++;
            else {
                // check the two keys must be indentifical (String <-> String or List<->List)
                Object localValue = dictionary.get(key);
                Object referenceValue = referenceDictionary.getDictionary().get(key);
                if (localValue!=null && referenceValue!=null && ! localValue.getClass().equals(referenceValue.getClass()))
                    dicoStatus.incorrectKeyClass++;
            }
        }
        for (String key: dictionary.keySet()) {
            if (! referenceDictionary.getDictionary().containsKey(key))
                dicoStatus.tooMuchKeys++;
        }
        return dicoStatus;
    }






    public boolean exist(String key) {
        return (dictionary != null && dictionary.containsKey(key));
    }

    public Map<String,Object> getDictionary() {
        if (dictionary == null)
            return Collections.emptyMap();
        return dictionary;
    }
    /**
     * Return the file
     *
     * @return the file, path + language
     */
    protected File getFile() {
        return new File(path + "/" + language + ".json");
    }

    /**
     * Status of the comparison between a dictionary and the reference
     */
    public class DicoStatus {
        /**
         * Key is missing in the dictionary. A key, present in the reference dictionary, does not exist in the local
         */
        public int missingKeys=0;
        /**
         * Key define in the dictionary, but not exist in the reference dictionary
         */
        public int tooMuchKeys=0;
        /**
         * Key may be a String or a List of Strings. Class are not identical between the reference dictionary an the local
         */
        public int incorrectKeyClass=0;
    }

}
