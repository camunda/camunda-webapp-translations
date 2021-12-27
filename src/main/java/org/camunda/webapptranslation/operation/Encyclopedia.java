package org.camunda.webapptranslation.operation;


/* -------------------------------------------------------------------- */
/*                                                                      */
/* Manage, for one langage, all key/value for all dictionaries                               */
/*                                                                      */
/*                                                                      */
/* -------------------------------------------------------------------- */

import org.camunda.webapptranslation.app.AppDictionary;

import java.util.*;
import java.util.stream.Collectors;

public class Encyclopedia {

    private String language;

    public Encyclopedia(String language) {
        this.language = language;
    }

    Map<String, List<Registration>> registrationKeys = new HashMap<>();
    Map<String, List<Registration>> registrationTranslations = new HashMap<>();

    public void registerDictionary(AppDictionary dictionary) {
        for (Map.Entry<String, Object> entry : dictionary.getDictionary().entrySet()) {
            String shortKey = getShortKey(entry.getKey());
            List<Registration> listPropositions = registrationKeys.getOrDefault(getShortKey(shortKey), new ArrayList<>());
            List<Registration> listTranslations = registrationTranslations.getOrDefault(entry.getValue(), new ArrayList<>());
            Registration registration = new Registration();
            registration.key = entry.getKey();
            registration.value = entry.getValue();
            registration.dictionary = dictionary;

            listPropositions.add(registration);
            listTranslations.add(registration);
            registrationKeys.put(shortKey, listPropositions);
            registrationTranslations.put(entry.getValue().toString(), listTranslations);
        }
    }

    /**
     * Return the language use by this encyclopedia
     *
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * For a key (labels.ABORT), retrieve all propositions (i.e. ABORT ) in all directory
     *
     * @param key key to search
     * @return list of proposition
     */
    public List<String> getTranslationsByKey(String key) {
        List<Registration> listPropositions = registrationKeys.getOrDefault(getShortKey(key), Collections.emptyList());
        return listPropositions.stream()
                .filter(registration -> registration.value instanceof String)
                .map(registration -> (String) registration.value)
                .collect(Collectors.toList());
    }

    List<String> getKeysByTranslation(String translation) {
        List<Registration> listPropositions = registrationTranslations.getOrDefault(translation, Collections.emptyList());
        return listPropositions.stream()
                .filter(registration -> registration.value instanceof String)
                .map(registration -> registration.key)
                .collect(Collectors.toList());
    }


    /**
     * When the key is labels.ABORT, just return ABORT
     *
     * @param key complete key
     * @return the short key
     */
    private String getShortKey(String key) {
        if (key.lastIndexOf(".") == -1)
            return key;
        return key.substring(key.lastIndexOf(".") + 1);
    }

    /**
     * Register a cle / value and the dictionary
     */
    private class Registration {
        /**
         * Key (complete one, like labels.ABORT)
         */
        public String key;
        /**
         * Value of the key
         */
        public Object value;
        /**
         * Dictionary where the association is made
         */
        public AppDictionary dictionary;

    }
}
