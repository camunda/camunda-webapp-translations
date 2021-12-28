package org.camunda.webapptranslation.operation;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* Proposition sentence                                  */
/*                                                                      */
/* calculate a proposition to translate the sentence.*/
/*                                                                      */
/* -------------------------------------------------------------------- */

import org.camunda.webapptranslation.app.AppDictionary;
import org.camunda.webapptranslation.report.ReportInt;

public interface Proposal {

    String DELIMITER_BETWEEN_PROPOSITION = " ##;## ";

    String getName();

    /**
     * Set all parameters
     *
     * @param appDictionary         dictionary in progress
     * @param referenceDictionary   reference dictionary in the same application
     * @param encyclopediaUniversal encyclopedia
     */
    void setDictionaries(AppDictionary appDictionary, AppDictionary referenceDictionary, EncyclopediaUniversal encyclopediaUniversal);

    /**
     * Start. This method is call before the completion will start. if the object answer false, then the proposal object is invalided.
     *
     * @param report to report anything
     */
    boolean begin(ReportInt report);

    /**
     * This method is call at the end of the completion.
     *
     * @param report to report anything
     */
    void end(ReportInt report);

    String calculateProposition(String key, ReportInt report);

}
