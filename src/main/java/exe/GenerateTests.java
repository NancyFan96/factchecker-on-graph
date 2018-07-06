package exe;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Not finished, manually now
 */
public class GenerateTests {
    private static final String DATA_DIR = "./src/main/resources/";
    private static final String FILE_RELATIVE_PATH = "naive/born-test-50.txt";
    private static final List<String> NAME_ENTITY_LIST = Arrays.asList(
            "PERSON", "LOCATION", "ORGANIZATION", "MISC",
                    "MONEY", "NUMBER", "ORDINAL", "PERCENT",
                        "DATE", "TIME", "DURATION", "SET");

    public static void main(String[] args) throws Exception {
        // Create the Stanford CoreNLP pipeline
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        System.out.println("OpenIE engine is running...\n");

        File file = new File(DATA_DIR+FILE_RELATIVE_PATH);
        List<String> content = Files.readAllLines(Paths.get(file.toURI()));
        for (String sentence : content) {
            if (sentence.isEmpty() || sentence.charAt(0) == '<') continue;
            System.out.println(sentence);
            Annotation doc = new Annotation(sentence);
            pipeline.annotate(doc);

            for (CoreMap  sent: doc.get(CoreAnnotations.SentencesAnnotation.class)) {
                System.out.println(sent.toString());

                for (CoreLabel token: sent.get(CoreAnnotations.TokensAnnotation.class)) {
                    // this is the text of the token
                    String word = token.get(CoreAnnotations.TextAnnotation.class);
                    // this is the POS tag of the token
                    String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                    // this is the NER label of the token
                    String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    if (NAME_ENTITY_LIST.contains(ne)) {
                        System.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
                    }
                }

                System.out.println();
            }


        }
    }
}
