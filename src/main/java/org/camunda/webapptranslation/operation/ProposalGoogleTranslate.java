package org.camunda.webapptranslation.operation;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.camunda.webapptranslation.app.AppDictionary;

public class ProposalGoogleTranslate implements Proposal {

    private String googleAPIKey;

    private Translate translate;
    private AppDictionary appDictionary;
    private AppDictionary referenceDictionary;

    private int numberOfTranslations;
    private int limitNumberOfTranslation;


    public ProposalGoogleTranslate(String googleAPIKey, int limitNumberOfTranslation) {
        this.googleAPIKey = googleAPIKey;
        System.setProperty("GOOGLE_API_KEY", googleAPIKey);
        translate = TranslateOptions.newBuilder().setApiKey(googleAPIKey).build().getService();
        numberOfTranslations = 0;
        this.limitNumberOfTranslation = limitNumberOfTranslation;
    }

    @Override
    public String getName() {
        return "GoogleTranslation";
    }

    @Override
    public void setDictionaries(AppDictionary appDictionary, AppDictionary referenceDictionary, EncyclopediaUniversal encyclopediaUniversal) {
        this.appDictionary = appDictionary;
        this.referenceDictionary = referenceDictionary;
    }

    @Override
    public String calculateProposition(String key) {
        if (this.googleAPIKey == null)
            return null;
        if (numberOfTranslations > limitNumberOfTranslation)
            return null;

        numberOfTranslations++;
        TranslateOption sourceLanguageOption = Translate.TranslateOption.sourceLanguage(referenceDictionary.getLanguage());
        TranslateOption targetLanguageOption = Translate.TranslateOption.targetLanguage(appDictionary.getLanguage());

        Translation translation = translate.translate(
                (String) referenceDictionary.getDictionary().get(key),
                sourceLanguageOption,
                targetLanguageOption);
        // Use "base" for standard edition, "nmt" for the premium model.
        // Translate.TranslateOption.model("base"));
        return translation.getTranslatedText().replace("&#39;", "'");
    }
}
