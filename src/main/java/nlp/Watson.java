package nlp;

import com.ibm.watson.developer_cloud.http.ServiceCall;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;


public class Watson {
    private static NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
            "2018-03-16",
            "7c1e901b-eedb-4c9b-bea7-f272bcb04fde",
            "5Y2JGACfc5id"
    );

    private static String testUrl = "https://en.wikipedia.org/wiki/Aldous_Huxley";
//    private static String testUrl = "www.ibm.com";
//    private static String testText = "Huxley was born in Godalming, Surrey, England, in 1894.";
    private static String testText = "Huxley";
//    private static String testText = "1894";
//    private static String testText = "England";
//    private static String testText = "Godalming";
//    private static String testText = "bear";




    private static EntitiesOptions entitiesOptions;
    private static KeywordsOptions keywordsOptions;
    private static ConceptsOptions conceptsOptions;
    private static Features features;
    private static AnalyzeOptions parameters;

    public Watson(String text, boolean isURL) {
        conceptsOptions= new ConceptsOptions.Builder()
                .limit(3)
                .build();
//        entitiesOptions = new EntitiesOptions.Builder()
//                .limit(1)
//                .build();
//        keywordsOptions = new KeywordsOptions.Builder()
//                .limit(1)
//                .build();
        features = new Features.Builder()
                .concepts(conceptsOptions)
//                .entities(entitiesOptions)
//                .keywords(keywordsOptions)
                .build();
        if (isURL) {
            parameters = new AnalyzeOptions.Builder()
                    .url(text)
                    .features(features)
                    .build();
        } else {
            parameters = new AnalyzeOptions.Builder()
                    .text(text)
                    .features(features)
                    .build();
        }
    }

    public Watson(EntitiesOptions entitiesOptions_, KeywordsOptions keywordsOptions_, String text) {
        entitiesOptions = entitiesOptions_;
        keywordsOptions = keywordsOptions_;
        features = new Features.Builder()
                .entities(entitiesOptions)
                .keywords(keywordsOptions)
                .build();
        parameters = new AnalyzeOptions.Builder()
                .text(text)
                .features(features)
                .build();
    }

    public static void main(String[] args) {
//        new Watson(testText,false);
        new Watson(testUrl, true);
        ServiceCall call = service.analyze(parameters);
        AnalysisResults response = (AnalysisResults) call.execute();
        System.out.println(response);
    }



}
