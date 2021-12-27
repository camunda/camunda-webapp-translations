package org.camunda.webapptranslation.operation;

import org.camunda.webapptranslation.app.AppDictionary;

import java.util.HashMap;
import java.util.Map;

/* -------------------------------------------------------------------- */
/*                                                                      */
/* EncyclopediaUniversali                                */
/*                                                                      */
/* Save all encyclodia for all language.*/
/*                                                                      */
/* -------------------------------------------------------------------- */

public class EncyclopediaUniversal {

    private final  Map<String, Encyclopedia> allEncyclopedia = new HashMap<>();
    private final String referenceLanguage;


    public EncyclopediaUniversal(String referenceLanguage) {
        this.referenceLanguage  = referenceLanguage;
        allEncyclopedia.put(referenceLanguage, new Encyclopedia(referenceLanguage));
    }

    /**
     * get the reference encyclopedia
     * @return the encyclopedia for the reference language
     */
    public Encyclopedia getReferenceEncyclopedia() {
        return  allEncyclopedia.get(referenceLanguage);
    }
    /**
     * register the dictionary in the correct encyclopedia
     * @param dictionary dictionary to reference
     */
    public void registerDictionary(AppDictionary dictionary) {
        Encyclopedia encyclopedia = allEncyclopedia.getOrDefault(dictionary.getLanguage(), new Encyclopedia(dictionary.getLanguage()));
        encyclopedia.registerDictionary( dictionary);
        allEncyclopedia.put(dictionary.getLanguage(), encyclopedia);
    }
    public Encyclopedia getByLanguage(String language) {
        return allEncyclopedia.get(language);
    }

}
