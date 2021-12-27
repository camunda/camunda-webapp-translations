package org.camunda.webapptranslation.operation;

import org.camunda.webapptranslation.app.AppDictionary;

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

    /*
     *   The translation may exist in another [language] dictionary.
     *       Example:
     *          taskList: ABORT = 'annuler'
     *          cockpit:  ABORT: ?
     *   ==> we can propose 'annuler' as the translation.
     *
     */
    public String calculateProposition(String key) {
        Encyclopedia encyclopedia = encyclopediaUniversal.getByLanguage(appDictionary.getLanguage());
        // key is something like labels.ABORT. Just  get the real key
        if (encyclopedia == null)
            return null;
        List<String> listPropositions = encyclopedia.getTranslationsByKey(key).stream().distinct().collect(Collectors.toList());

        return listPropositions.isEmpty() ? null : String.join("<##>", listPropositions);
    }
}
