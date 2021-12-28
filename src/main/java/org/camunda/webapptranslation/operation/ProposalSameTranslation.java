package org.camunda.webapptranslation.operation;

import org.camunda.webapptranslation.app.AppDictionary;
import org.camunda.webapptranslation.report.ReportInt;

import java.util.List;
import java.util.stream.Collectors;

public class ProposalSameTranslation implements Proposal {


    private AppDictionary referenceDictionary;
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
        return "SameTranslation";
    }

    @Override
    public void setDictionaries(AppDictionary appDictionary, AppDictionary referenceDictionary, EncyclopediaUniversal encyclopediaUniversal) {
        this.referenceDictionary = referenceDictionary;
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
        report.info(ProposalSameKey.class, "SameTranslation: " + numberOfPropositions + " propositions");

    }


    /**
     * The content already exist. Example, to translate to French, using the English dictionary as reference:
     * We search:
     * ===>  cockpit[fr]: "BULK_OVERRIDE_SUCCESSFUL":
     * In the reference dictionary, the value is
     * ===>  cockpit[en]:  "BULK_OVERRIDE_SUCCESSFUL": "Successful",
     * <p>
     * We search if the same CONTENT exists in the reference dictionary.
     * We found in the
     * ===>  taskList[en] : "SUCCESSFUL": "Successful",
     * ===>  taskList[en] : "REMOVED_OK": "Successful",
     * ===>  taskList[en] : "CREATED_SUCCESS": "Successful",
     * and a translation exists for both with:
     * ===>  taskList[fr] :  "SUCCESSFUL": "Avec succès",
     * ===>  taskList[fr] :  "REMOVED_OK": "opération réussie",
     * But no translation for "CREATED_SUCCESS in fr.
     * So, we have two propositions, and we do not choose
     * We propose the 2 translations
     * cockpit[fr]: "BULK_OVERRIDE_SUCCESSFUL": = "Avec succès ; opération réussie"
     */
    @Override
    public String calculateProposition(String key, ReportInt report) {
        // get the sentence in the reference dictionary
        Object referenceTranslation = referenceDictionary.getDictionary().get(key);
        if (!(referenceTranslation instanceof String))
            return null;
        // get in the reference dictionary all keys with the same content
        Encyclopedia referenceEncyclopedia = encyclopediaUniversal.getByLanguage(referenceDictionary.getLanguage());
        List<String> listKeysSameSentence = referenceEncyclopedia.getKeysByTranslation(referenceTranslation.toString());
        // now, search the same key in the current language
        Encyclopedia encyclopedia = encyclopediaUniversal.getByLanguage(appDictionary.getLanguage());
        List<String> listPropositions =
                listKeysSameSentence.stream()
                        .map(keyFound -> encyclopedia.getTranslationsByKey(keyFound))
                        .flatMap(List::stream)
                        .distinct()
                        .collect(Collectors.toList());
        if (!listPropositions.isEmpty())
            numberOfPropositions++;

        return listPropositions.isEmpty() ? null : String.join(DELIMITER_BETWEEN_PROPOSITION, listPropositions);
    }
}
