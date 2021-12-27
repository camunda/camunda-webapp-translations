package org.camunda.webapptranslation.operation;


/* -------------------------------------------------------------------- */
/*                                                                      */
/* AppOperation                              */
/*                                                                      */
/* Basic class for all operations. Provide tool. */
/*                                                                      */
/* -------------------------------------------------------------------- */

import org.camunda.webapptranslation.SynchroParams;
import org.camunda.webapptranslation.app.AppDictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Operation {

    protected static final String INDENTATION = "   ";


    /**
     * CheckKeys
     * Verify that this dictionary is complete in regard of the reference dictionary
     */
    protected DictionaryStatus checkKeys(AppDictionary dictionary, AppDictionary referenceDictionary) {
        DictionaryStatus dictionaryStatus = new DictionaryStatus();

        checkMissingKeys(dictionary, referenceDictionary, dictionaryStatus);
        checkIncorrectObject(dictionary, referenceDictionary, dictionaryStatus);
        checkTooMuchKeys(dictionary, referenceDictionary, dictionaryStatus);

        return dictionaryStatus;
    }

    /**
     * run the reference dictionary. Verify that the key exist in the local dictionary.
     *
     * @param referenceDictionary reference dictionary
     * @param dictionaryStatus          complete the status
     */
    private void checkMissingKeys(AppDictionary dictionary, AppDictionary referenceDictionary, DictionaryStatus dictionaryStatus) {
        // Key in the ref
        for (String key : referenceDictionary.getDictionary().keySet()) {
            if (!dictionary.getDictionary().containsKey(key)) {
                dictionaryStatus.nbMissingKeys++;
                dictionaryStatus.missingKeys.add(key);
            }
        }
    }

    /**
     * run the dictionary and compare if the value on the dictionary and the value in the reference are the same type
     * @param dictionary the dictionary to check
     * @param referenceDictionary reference dictionary
     * @param dictionaryStatus   complete the status
     */
    private void checkIncorrectObject(AppDictionary dictionary, AppDictionary referenceDictionary, DictionaryStatus dictionaryStatus) {
        // Key in the ref
        for (String key : referenceDictionary.getDictionary().keySet()) {
            if ( dictionary.getDictionary().containsKey(key)) {
                // check: the two keys must be identical (String <-> String or List<->List)
                Object localValue = dictionary.getDictionary().get(key);
                Object referenceValue = referenceDictionary.getDictionary().get(key);
                if (localValue != null && referenceValue != null && !localValue.getClass().equals(referenceValue.getClass())) {
                    dictionaryStatus.nbIncorrectKeyClass++;
                    dictionaryStatus.incorrectClass.add(key + ":" + referenceValue.getClass() + " expected, " + localValue.getClass() + " found");
                }
            }
        }
    }

    /**
     * Too much keys
     * @param dictionary dictionary to work on
     * @param referenceDictionary reference dictionary
     * @param dictionaryStatus status fullfill
     */
    private void checkTooMuchKeys(AppDictionary dictionary, AppDictionary referenceDictionary, DictionaryStatus dictionaryStatus) {
        for (String key : dictionary.getDictionary().keySet()) {
            if (key.endsWith(SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE_REFERENCE))
                continue; // ignore it
            if (key.endsWith(SynchroParams.PLEASE_VERIFY_THE_SENTENCE_REFERENCE))
                continue; // ignore it
            if (key.endsWith(SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE))
                key = key.substring(0, key.length() - SynchroParams.PLEASE_TRANSLATE_THE_SENTENCE.length());
            if (key.endsWith(SynchroParams.PLEASE_VERIFY_THE_SENTENCE))
                key = key.substring(0, key.length() - SynchroParams.PLEASE_VERIFY_THE_SENTENCE.length());
            if (!referenceDictionary.getDictionary().containsKey(key)) {
                dictionaryStatus.nbTooMuchKeys++;
                dictionaryStatus.tooMuchKeys.add(key);
            }
        }
    }

    /**
     * Status of the comparison between a dictionary and the reference
     */
    public class DictionaryStatus {
        /**
         * Key is missing in the dictionary. A key, present in the reference dictionary, does not exist in the local
         */
        public int nbMissingKeys = 0;
        public Set<String> missingKeys = new HashSet<>();

        public Map<String, Integer> statisticPerProposer = new HashMap<>();
        public Map<String, Integer> statisticPerKeyAdditions = new HashMap<>();

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

        public void addProposition(String proposerName) {
            int statistic = statisticPerProposer.getOrDefault(proposerName,0);
            statisticPerProposer.put(proposerName, statistic+1);
        }
        public void addKey(String suffixKey) {
            int statistic = statisticPerKeyAdditions.getOrDefault(suffixKey,0);
            statisticPerKeyAdditions.put(suffixKey, statistic+1);
        }
    }

    /**
     * From the language, return a standard header string
     * @param language language
     * @return the standard header string
     */
    protected String headerLanguage(String language) {
        String label = INDENTATION + "[" + language + "]          ";
        return label.substring(0, INDENTATION.length() + 10) + " ... ";
    }
}
