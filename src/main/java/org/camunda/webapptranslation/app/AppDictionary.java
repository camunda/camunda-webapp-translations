package org.camunda.webapptranslation.app;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Manage, for an application (a directory) a language */
/*                                                                      */
/* -------------------------------------------------------------------- */

import java.io.*;
import java.util.*;

import org.camunda.webapptranslation.SynchroParams;
import org.camunda.webapptranslation.report.ReportInt;


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
    public File getFile() {
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
