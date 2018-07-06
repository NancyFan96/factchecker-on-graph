package nlp;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class OpenIE {
    private static String DATA_DIR = "./src/main/resources/";
    private static final String FILE_TAG = "false"; //"true"
    private static Properties props = new Properties();
    public static StanfordCoreNLP pipeline;

    public OpenIE() {
        props.setProperty("annotators", "tokenize, ssplit, pos,lemma, depparse, natlog, openie");
//        props.setProperty("openie.triple.strict", "false");

        pipeline = new StanfordCoreNLP(props);
    }

    public static String getStemmed(String phrase /*, StanfordCoreNLP pipeline*/) throws Exception {
        Annotation doc = new Annotation(phrase);
        pipeline.annotate(doc);

//        ArrayList<String> wordsList = new ArrayList<>();
        String stemmedPhrase = "";

        for (CoreMap  sent: doc.get(SentencesAnnotation.class)) {
            for (CoreLabel token: sent.get(TokensAnnotation.class)) {

                String word = token.get(TextAnnotation.class);            // 获取分词
                String pos = token.get(PartOfSpeechAnnotation.class);     // 获取词性标注
                String ne = token.get(NamedEntityTagAnnotation.class);    // 获取命名实体识别结果
                String lemma = token.get(LemmaAnnotation.class);          // 获取词形还原结果

//                System.out.println(word+"\t"+pos+"\t"+lemma+"\t"+ne);
//                wordsList.add(lemma);
                if (stemmedPhrase.isEmpty()){
                    stemmedPhrase += lemma;
                } else {
                    stemmedPhrase += " "+lemma;
                }

            }
        }

//        String stemmedPhrase = wordsList.toString();

        return stemmedPhrase;
    }

    public static void getTriple(String dirname /*, StanfordCoreNLP pipeline*/) throws Exception {
        DATA_DIR = DATA_DIR + dirname + '/';
        File[] files = new File(DATA_DIR).listFiles();
        System.out.println("start analyse a directory");

        for (File file : files) {
            if (!file.toString().contains(".txt"))
                continue;

            String outFile = mk_targetfile(file.toString());

            List<String> content = Files.readAllLines(Paths.get(file.toURI()));
            for (String sentence : content) {
                if (sentence.isEmpty() || sentence.charAt(0) == '<') continue;

                getTriple(sentence, FILE_TAG, outFile);
            }
        }
    }



    public static void getTriple(String dirname, String filename /*, StanfordCoreNLP pipeline*/) throws Exception {
        DATA_DIR = DATA_DIR + dirname + '/';
        File file = new File(DATA_DIR + filename + ".txt");
        String outFile = mk_targetfile(file.toString());

        List<String> content = Files.readAllLines(Paths.get(file.toURI()));
        for (String sentence : content) {
            if (sentence.isEmpty() || sentence.charAt(0) == '<') continue;

            getTriple(sentence, FILE_TAG, outFile);
        }


    }

    public static void getTriple(String sentence, String tag, String outFile) throws Exception {
        Annotation doc = new Annotation(sentence);
        pipeline.annotate(doc);

        for (CoreMap  sent: doc.get(SentencesAnnotation.class)) {
//            System.out.println(sent.toString());

            Collection<RelationTriple> triples = sent.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);

            JSONArray triple_wrapper = triple2json(triples);
            JSONObject triple_sentence_object = new JSONObject();
            triple_sentence_object.put("sentence", sentence);
            triple_sentence_object.put("triple list", triple_wrapper);
            triple_sentence_object.put("tag", tag); // true or false

            try (RandomAccessFile outfile = new RandomAccessFile(new File(outFile), "rw")) {
                outfile.seek(outfile.length()-1);
                String appendStr = triple_sentence_object.toJSONString() + ']';
                if (outfile.length() != 2)
                    appendStr = ',' + appendStr;
                outfile.write(appendStr.getBytes());
            }
        }
    }


    public static void getTriple(String sentenceStr, ArrayList<RelationTriple> relation_wrappers) throws Exception {
        Annotation doc = new Annotation(sentenceStr);
        pipeline.annotate(doc);

        for (CoreMap sentence : doc.get(SentencesAnnotation.class)) {
            System.out.println(sentence.toString());
            // Get the OpenIE triples for the sentence
            Collection<RelationTriple> triples =
                    sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
            // Print the triples
            for (RelationTriple triple : triples) {
                relation_wrappers.add(triple);
                double confidence = triple.confidence;
                String subject = triple.subjectLemmaGloss();
                String relation = triple.relationLemmaGloss();
                String objective = triple.objectLemmaGloss();
                System.out.println(confidence + ", " + subject + ", " + relation + ", " + objective);
            }

            System.out.println();
        }
    }


    public static JSONArray triple2json(Collection<RelationTriple> triples) {
        JSONArray triple_wrapper = new JSONArray();

        for (RelationTriple triple : triples) {
            double confidence = triple.confidence;
            String subject = triple.subjectLemmaGloss();
            String relation = triple.relationLemmaGloss();
            String object = triple.objectLemmaGloss();
            JSONObject triple_json = new JSONObject();
            triple_json.put("subject", subject);
            triple_json.put("predicate", relation);
            triple_json.put("object", object);
            triple_json.put("confidence", confidence);
            triple_wrapper.add(triple_json);

        }

        return triple_wrapper;
    }

    private static String mk_targetfile(String filepath) throws IOException {
        String inputfileName = Ultility.Path2Name(filepath);
        File DIR = new File(DATA_DIR + inputfileName + '/');
        DIR.mkdir();

        String outputFileName = inputfileName + "_triples.json";
        String TARGET_FILE = DIR.toString() + '/' + outputFileName;
        File newfile = new File(TARGET_FILE);

        boolean docreate = newfile.createNewFile();
        if (docreate) {
            System.out.println("TargeFile" +  TARGET_FILE + " created");
        }

        try (FileWriter outfile = new FileWriter(TARGET_FILE, false)) {
            outfile.write("[]");
        }

        return TARGET_FILE;
    }

    public static void main(String[] args) throws Exception {
        // Create the Stanford CoreNLP pipeline
        new OpenIE();
        System.out.println("OpenIE engine is running...\n");

//        String test = "Huxley was born in Godalming, Surrey, England, in 1894.";
//        ArrayList<RelationTriple> triples_wrapper = getTriple(test);
//        String[] item_list = new String[]{
//                "NYC",
//                "AlphaGo", "Bud_Luckey", "Conservation_of_energy",
//                "Earth", "Gone_with_the_Wind_(film)", "Le_Samyn",
//                "Malaysia_Airlines_Flight_370"};
//                "born-test-50"};
//        for (String item : item_list) {
//            getTriple("naive", item, pipeline);
//        }
//        getTriple("naive", "born-test-small-true", pipeline);
//        getTriple("naive", "born-test-small-false");
        ArrayList<RelationTriple> triples =  new ArrayList<RelationTriple>();
        getTriple("Wellesley was born in Dublin, into the Protestant Ascendancy in Ireland.", triples);
    }
}
