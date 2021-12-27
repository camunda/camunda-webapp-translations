package org.camunda.webapptranslation.operation;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Proposition sentence                                  */
/*                                                                      */
/* calculate a proposition to translate the sentence.*/
/*                                                                      */
/* -------------------------------------------------------------------- */

import org.camunda.webapptranslation.app.AppDictionary;

public interface Proposal {

    String getName();

    /**Set all parameters
     *
     * @param appDictionary dictionary in progress
     * @param referenceDictionary reference dictionary in the same application
     * @param encyclopediaUniversal encyclopedia
     */
        void setDictionaries(AppDictionary appDictionary, AppDictionary referenceDictionary, EncyclopediaUniversal encyclopediaUniversal);

     String calculateProposition(String key);

    }
