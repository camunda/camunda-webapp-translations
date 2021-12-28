package org.camunda.webapptranslation.operation;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.camunda.webapptranslation.app.AppDictionary;
import org.camunda.webapptranslation.report.ReportInt;

public class ProposalGoogleTranslate implements Proposal {

    private final String googleAPIKey;
    private final int limitNumberOfTranslations;
    private Translate translate;
    private AppDictionary appDictionary;
    private AppDictionary referenceDictionary;
    private int numberOfTranslations;
    private int numberOfTranslationsRequested;

    private long cumulTranslationTimeInMs = 0;

    public ProposalGoogleTranslate(String googleAPIKey, int limitNumberOfTranslations) {
        this.googleAPIKey = googleAPIKey;
        this.limitNumberOfTranslations = limitNumberOfTranslations;
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
    public boolean begin(ReportInt report) {
        System.setProperty("GOOGLE_API_KEY", googleAPIKey);
        translate = TranslateOptions.newBuilder().setApiKey(googleAPIKey).build().getService();
        numberOfTranslations = 0;
        numberOfTranslationsRequested = 0;
        return true;
    }

    @Override
    public void end(ReportInt report) {
        report.info(Proposal.class, "GoogleTranslation: " + numberOfTranslationsRequested + " requested,  " + numberOfTranslations + " done in " + cumulTranslationTimeInMs + " ms ");
    }

    @Override
    public String calculateProposition(String key, ReportInt report) {
        if (this.googleAPIKey == null)
            return null;
        numberOfTranslationsRequested++;

        if (numberOfTranslations >= limitNumberOfTranslations)
            return null;

        try {
            TranslateOption sourceLanguageOption = Translate.TranslateOption.sourceLanguage(referenceDictionary.getLanguage());
            TranslateOption targetLanguageOption = Translate.TranslateOption.targetLanguage(appDictionary.getLanguage());

            long currentTime = System.currentTimeMillis();
            Translation translation = translate.translate(
                    (String) referenceDictionary.getDictionary().get(key),
                    sourceLanguageOption,
                    targetLanguageOption);
            cumulTranslationTimeInMs += System.currentTimeMillis() - currentTime;

            numberOfTranslations++;
            return translation.getTranslatedText().replace("&#39;", "'");
        } catch (Exception e) {
            report.severe(ProposalGoogleTranslate.class, "Can't translate : " + e);
            return null;
        }
    }
}
