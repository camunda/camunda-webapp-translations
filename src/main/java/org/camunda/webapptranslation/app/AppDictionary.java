package org.camunda.webapptranslation.app;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Manage, for an application (a directory) a language */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.io.*;
import java.util.*;

import org.camunda.webapptranslation.SynchroParams;
import org.camunda.webapptranslation.org.camunda.webapptranslation.ReportInt;


public class AppDictionary {


    private File path;
    private String language;
    /**
     * Dictionary, JSON format is a hierarchy collection of String or list
     * Something like
     * {
     * "labels": {
     * "APP_VENDOR": "Camunda",
     * },
     * "monthsShort": [
     * "Jan",
     * "Feb",
     * "Mar"
     * ],
     * "week": {
     * "dow": 1,
     * "doy": 4
     * }
     * }
     */
    private Map<String, Object> dictionary;
    /**
     * marker to know if the dictionary is modified or not
     */
    private boolean dictionaryIsModified = false;

    public final static String PREFIX_PLEASE_TRANSLATE = "";

    public AppDictionary(File path, String language) {
        this.path = path;
        this.language = language;
        this.dictionary = new HashMap<>();
    }


    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Dictionary to file                                                   */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    /**
     * Check if the file relative to the dictionary exists
     *
     * @return true if the file exists
     */
    public boolean existFile() {
        File file = getFile();
        return file.exists();
    }


    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* Read/Write */
    /*                                                                      */
    /* -------------------------------------------------------------------- */
    public boolean read(ReportInt report) {
        AppDictionarySerialize serialize = new AppDictionarySerialize(this);
        dictionary = new HashMap<>();
        boolean status = serialize.read(report);
        dictionaryIsModified = false;
        return status;
    }

    /**
     * Write the dictionary
     *
     * @param report report used to report any error
     * @return true if the writing correctly took place
     */
    public boolean write(ReportInt report) {
        AppDictionarySerialize serialize = new AppDictionarySerialize(this);
        boolean status = serialize.write(report);
        if (status)
            dictionaryIsModified = false;
        return status;
    }

    /**
     * isModified
     * @return true if the dictionary change (keys added, removed)
     */
    public boolean isModified() {
        return dictionaryIsModified;
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
    DicoStatus checkKeys(AppDictionary referenceDictionary) {
        DicoStatus dicoStatus = new DicoStatus();

        checkMissingKeys(referenceDictionary, dicoStatus);
        checkIncorrectsObject(referenceDictionary, dicoStatus);
        checkTooMuchKeys(referenceDictionary, dicoStatus);

        return dicoStatus;
    }

    /**
     * run the reference dictionary. Verify that the key exist in the local dictionary.
     *
     * @param referenceDictionary reference dictionary
     * @param dicoStatus          complete the status
     */
    private void checkMissingKeys(AppDictionary referenceDictionary, DicoStatus dicoStatus) {
        // Key in the ref
        for (String key : referenceDictionary.getDictionary().keySet()) {
            if (!dictionary.containsKey(key)) {
                dicoStatus.nbMissingKeys++;
                dicoStatus.missingKeys.add(key);
            }
        }
    }

    /**
     * run the dictionary and compare if the value on the dictionary and the value in the reference are the same type
     * @param referenceDictionary reference dictionary
     * @param dicoStatus   complete the status
     */
    private void checkIncorrectsObject(AppDictionary referenceDictionary, DicoStatus dicoStatus) {
        // Key in the ref
        for (String key : referenceDictionary.getDictionary().keySet()) {
            if ( dictionary.containsKey(key)) {
                // check: the two keys must be identical (String <-> String or List<->List)
                Object localValue = dictionary.get(key);
                Object referenceValue = referenceDictionary.getDictionary().get(key);
                if (localValue != null && referenceValue != null && !localValue.getClass().equals(referenceValue.getClass())) {
                    dicoStatus.nbIncorrectKeyClass++;
                    dicoStatus.incorrectClass.add(key + ":" + referenceValue.getClass() + " expected, " + localValue.getClass() + " found");
                }
            }
        }
    }
    private void checkTooMuchKeys(AppDictionary referenceDictionary, DicoStatus dicoStatus) {
        for (String key : dictionary.keySet()) {
            if (key.endsWith(SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE_REFERENCE))
                continue; // ignore it
            if (key.endsWith(SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE))
                key = key.substring(0, key.length() - SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE.length());
            if (!referenceDictionary.getDictionary().containsKey(key)) {
                dicoStatus.nbTooMuchKeys++;
                dicoStatus.tooMuchKeys.add(key);
            }
        }
    }

    /**
     * Status of the comparison between a dictionary and the reference
     */
    public class DicoStatus {
        /**
         * Key is missing in the dictionary. A key, present in the reference dictionary, does not exist in the local
         */
        public int nbMissingKeys = 0;
        public Set<String> missingKeys = new HashSet<>();
        /**
         * Key define in the dictionary, but not exist in the reference dictionary
         */
        public int nbTooMuchKeys = 0;
        public Set<String> tooMuchKeys = new HashSet<>();
        /**
         * Key may be a String or a List of Strings. Class are not identical between the reference dictionary an the local
         */
        public int nbIncorrectKeyClass = 0;
        public Set<String> incorrectClass = new HashSet<>();
    }


    /**
     * Add a Key in the dictionary
     *
     * @param key   key to add
     * @param value value to add
     */
    public void addKey(String key, Object value) {
        dictionary.put(key, value);
        dictionaryIsModified = true;
    }

    /**
     * Remove the key
     *
     * @param key key to remove
     */
    public void removeKey(String key) {
        dictionary.remove(key);
        dictionaryIsModified = true;
    }

    /* -------------------------------------------------------------------- */
    /*                                                                      */
    /* access function */
    /*                                                                      */
    /* -------------------------------------------------------------------- */

    public boolean exist(String key) {
        return (dictionary != null && dictionary.containsKey(key));
    }

    public Map<String, Object> getDictionary() {
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
        return new File(path +File.separator+ language + ".json");
    }

    /**
     * Return the language managed by this dictionary
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

}
