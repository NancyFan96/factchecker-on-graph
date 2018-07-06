package benchmark;

import static benchmark.Evaluation.*;

import edu.stanford.nlp.ie.util.RelationTriple;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nlp.OpenIE;
import nlp.SimilarityScore;
import nlp.Ultility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class FactVerification {

    private static String Sentence;
    private static JSONArray TripleJsonArray;
    private static ArrayList<Integer> GroupIdList = new ArrayList<>();
    private static int totalFact = 0;


    // features per fact
    private static ArrayList<Integer> TagList = new ArrayList<>(); // pos: +1 / neg: -1
    private static ArrayList<Double> SubjectMatchScores = new ArrayList<>();
    private static ArrayList<Double> ObjectMatchScores = new ArrayList<>();
    private static ArrayList<Double> PathMatchScores = new ArrayList<>();
    private static ArrayList<Double> SubjectSynonyScores = new ArrayList<>();
    private static ArrayList<Double> ObjectSynonyScores = new ArrayList<>();
    private static ArrayList<Double> TripleCondifence = new ArrayList<>();



    /**
     * Add all the features of a fact in sentence
     * @param tag
     * @param ss
     * @param os
     * @param ps
     * @param sss
     * @param oss
     * @param con
     */
    public static void addAllFeature(int tag, double ss, double os, double ps, double sss, double oss, double con) {
        TagList.add(tag);
        SubjectMatchScores.add(ss);
        ObjectMatchScores.add(os);
        PathMatchScores.add(ps);
        SubjectSynonyScores.add(sss);
        ObjectSynonyScores.add(oss);
        TripleCondifence.add(con);
    }

    public static void appendWriteCSV(String csvPath) throws IOException {
        FileWriter aw = new FileWriter(csvPath,true);
        for (int i = 1; i <= totalFact ; i++) {
            String addCSV = Sentence + "," + i + "," + TagList.get(i) + ","
                    + SubjectMatchScores.get(i) + ","
                    + ObjectMatchScores.get(i) + ","
                    + PathMatchScores.get(i) + "\n";
            aw.write(addCSV);
        }
        aw.close();
    }


    FactVerification(String sentence_, JSONObject sentenceWrapper) {
        Sentence = sentence_;
        TripleJsonArray = (JSONArray) sentenceWrapper.get("triple list");
        groupFacts();
    }


    FactVerification(String sentence_) {
        Sentence = sentence_;
        System.out.println("Start checking");
        System.out.println("Test case: " + Sentence);

        ArrayList<RelationTriple> TripleList = new ArrayList<>();
        try {
            OpenIE.getTriple(Sentence, TripleList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TripleJsonArray = OpenIE.triple2json(TripleList);
        groupFacts();
    }

    private static void groupFacts() {
        GroupIdList.clear();

        if(TripleJsonArray.size() == 0) {
            totalFact = 0;
            return;
        }
        int groupcnt = 1;
        GroupIdList.add(1);
        String prev = Ultility.JsonTriple2String((JSONObject) TripleJsonArray.get(0));
        String cur;
        for (int i = 1; i < TripleJsonArray.size(); i++) {
           cur = Ultility.JsonTriple2String((JSONObject) TripleJsonArray.get(i));
           double sim = SimilarityScore.getCommonRatio(prev, cur);
           if (sim <= 2.0/3) {// threshold!, a new fact
                groupcnt++;
           }
           GroupIdList.add(groupcnt);
           prev = cur;
        }
        System.out.println(Arrays.toString(GroupIdList.toArray()));
        totalFact = groupcnt;
    }

    public static void checkSentence() throws Exception {
        for (int i = 1; i <= totalFact ; i++) {
            List<Object> lo;
            lo = TripleJsonArray.subList(GroupIdList.indexOf(i), GroupIdList.lastIndexOf(i) + 1);
            JSONArray triples_json_wrapper = new JSONArray();
            triples_json_wrapper.addAll(lo);
            boolean flag = verifyFact(triples_json_wrapper);

        }
    }

    public static void checkSentence(String test_) throws Exception {
        new FactVerification(test_);
        checkSentence();

    }

    public static void checkSentence(String sentence_, JSONObject sentenceWrapper) throws Exception {
        new FactVerification(sentence_, sentenceWrapper);
        checkSentence();

    }
    
    public static boolean verifyFact(JSONArray triples_json_wrapper) throws Exception{
        double confidence = evaluateFact(triples_json_wrapper);
        System.out.printf("\t\tconfidence: %f\n", confidence);
        return confidence > 0.5; // threshold!
    }


    public static void main(String[] args) throws Exception {
        new OpenIE();
//        checkSentence("Huxley was born in Godalming, Surrey, England, in 1894.");
       checkSentence("Wellesley was born in Dublin, into the Protestant Ascendancy in Ireland.");
       checkSentence("Wellesley was born in Madrid, into the Protestant Ascendancy in Ireland.");

//        verifyJsonFile("src/main/resources/born-new/born-new_triples.json");
//        System.out.println("\n\nRun true test...");
//        verifyJsonFile("src/main/resources/naive/born-test-small-true/born-test-small-true_triples.json");
//
//        System.out.println("\n\nRun fale test...");
//        verifyJsonFile("src/main/resources/naive/born-test-small-false/born-test-small-false_triples.json");
    }


}
