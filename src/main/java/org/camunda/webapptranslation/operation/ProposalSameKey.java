package org.camunda.webapptranslation.operation;

import org.camunda.webapptranslation.app.AppDictionary;
import org.camunda.webapptranslation.report.ReportInt;

import java.util.List;
import java.util.stream.Collectors;


/* -------------------------------------------------------------------- */
/*                                                                                                              */
/* Calculate a proposition based on the same key                            */
/* If a key/translation exist for the same language , then propose it */
/* The encyclopedia keep all keys / translation for all dictionaries    */
/*                                                                                                              */
/* -------------------------------------------------------------------- */

public class ProposalSameKey implements Proposal {

    private AppDictionary appDictionary;
    private EncyclopediaUniversal encyclopediaUniversal;

    private int numberOfPropositions = 0;

    /**
     * name of this proposal object
     *
     * @return the name
     */
    @Override
    public String getName() {
        return "SameKey";
    }

    @Override
    public void setDictionaries(AppDictionary appDictionary, AppDictionary referenceDictionary, EncyclopediaUniversal encyclopediaUniversal) {
        this.appDictionary = appDictionary;
        this.encyclopediaUniversal = encyclopediaUniversal;
    }

    @Override
    public boolean begin(ReportInt report) {
        numberOfPropositions = 0;
        return true;
    }

    @Override
    public void end(ReportInt report) {
        report.info(ProposalSameKey.class, "SameKey: " + numberOfPropositions + " propositions");
    }

    /*
     *   The translation may exist in another [language] dictionary.
     *       Example:
     *          taskList: ABORT = 'annuler'
     *          cockpit:  ABORT: ?
     *   ==> we can propose 'annuler' as the translation.
     *
     */
    @Override
    public String calculateProposition(String key, ReportInt report) {
        Encyclopedia encyclopedia = encyclopediaUniversal.getByLanguage(appDictionary.getLanguage());
        // key is something like labels.ABORT. Just  get the real key
        if (encyclopedia == null)
            return null;
        List<String> listPropositions = encyclopedia.getTranslationsByKey(key).stream().distinct().collect(Collectors.toList());
        if (!listPropositions.isEmpty())
            numberOfPropositions++;
        return listPropositions.isEmpty() ? null : String.join(DELIMITER_BETWEEN_PROPOSITION, listPropositions);
    }
}
