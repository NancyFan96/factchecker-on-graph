package benchmark;

import nlp.SimilarityScore;
import org.apache.jena.base.Sys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import query.QueryDBpedia;
import query.QueryEntity;

import java.util.ArrayList;

public class Evaluation {
    private static int pathCacheCnt;
    private static int tripleCacheCnt;
    private static int tripleMissCnt;

    public static double evaluateFact(JSONArray tripleArrary) throws Exception{
        ArrayList<Double> ResultList = new ArrayList<>();
        if (tripleArrary.isEmpty()) return 0;
        new CachedPathScore();


        for (Object to : tripleArrary) {
            JSONObject triple = (JSONObject) to;
            double score = evaluateTriple(triple);
            ResultList.add(score);
        }
        double count = ResultList.stream().mapToDouble(a ->  (a>0? 1:0)).sum();
        double sum = ResultList.stream().mapToDouble(a -> a).sum();
        double max = ResultList.stream().mapToDouble(a -> a).max().getAsDouble();
        System.out.println("covered path, count valid, binary result:" +count+"/"+tripleArrary.size());
        System.out.println("sum: "+sum);
        System.out.println("max: "+max);
        System.out.println();

        int maxIndex = ResultList.indexOf(max);
        JSONObject triple = (JSONObject) tripleArrary.get(maxIndex);
        double confidence = Double.parseDouble(triple.get("confidence").toString());
        String subject = triple.get("subject").toString();
        String object = triple.get("object").toString();

        int tag = 1; // run true test cases
//        double ss = SimilarityScore.getCommonRatio()


        CachedPathScore.clearAll();

        return sum;
    }

//    public static double verifyTriple(JSONObject triple, String TEST_JSON_FILE) throws Exception{
//        QueryDBpedia.RESULT_DIR += Ultility.Path2Name(TEST_JSON_FILE) + "/";
//        return verifyTriple(triple);
//    }

    public static double evaluateTriple(JSONObject triple) throws Exception {
        double confidence = Double.parseDouble(triple.get("confidence").toString());
        String subject = triple.get("subject").toString();
        String relation = triple.get("predicate").toString();
        String object = triple.get("object").toString();
        String PathResultJsonStr;
        JSONObject PathResultJson;
        FeaturedScore scores;

        System.out.println(confidence + "," +
                subject + "," +
                relation + "," +
                object);


        try {
            String subjectURI = QueryEntity.pickEntityResource(subject);
            String objectURI = QueryEntity.pickEntityResource(object);

            if (subjectURI == null || objectURI == null)
                return 0;

            PathResultJsonStr = QueryDBpedia.queryPath(subjectURI, objectURI);
            JSONParser parser = new JSONParser();
            PathResultJson = (JSONObject) parser.parse(PathResultJsonStr);
            PathResultJson = (JSONObject) PathResultJson.get("results");
        } catch (Exception e) {
            return 0;
        }


        JSONArray ResultPathArrayJson = (JSONArray) PathResultJson.get("bindings");
        if(ResultPathArrayJson.isEmpty())
            return 0;

        int pathcnt = ResultPathArrayJson.size();
        if (pathcnt > 100) {
            System.out.println("\t\t\t[Triple Eval] Find more than 100 paths. Only Evaluate first 100");
        }
        ArrayList<Double> scoreList = new ArrayList<Double>();
        int i = 0;
        tripleCacheCnt = 0; tripleMissCnt = 0;
        for (Object po:ResultPathArrayJson) {
            if (i >= 100){
                System.out.println("\t\t\t[Triple Eval] Find more than 100 paths. Have Evaluated first 100");
                break;
            }
            JSONObject jsonPath = (JSONObject) po;
            pathCacheCnt = 0;
            Double s = evaluatePath(relation, jsonPath);
            tripleCacheCnt += pathCacheCnt;
            scoreList.add(s);
            if (s >= 1.0) {
                System.out.println("\t[Triple Eval] One Path match! return~");
                break;
            }
            i++;
        }
//        System.out.println("\t[Triple Eval] Cached " + tripleCacheCnt + " times for one triple");
        System.out.println("\t$$$$$$$$$[Triple Eval] Cached " + tripleCacheCnt + " times for one triple");
        System.out.println("\t$$$$$$$$$[Triple Eval] Missed " + tripleMissCnt + " times for one triple");

        double score = scoreList.stream().mapToDouble(a->a).max().getAsDouble();

        System.out.println("\tEvalue "+ pathcnt + " path, max path score: " + score);

        return score;

    }

    public static double evaluatePath(String relation, JSONObject jsonPath) throws Exception {
        double maxSim = 0;
        pathCacheCnt = 0;
        for (Object key : jsonPath.keySet()) {
            //based on you key types
            String keyStr = (String)key;
            Object keyvalue = jsonPath.get(keyStr);

            if(!keyStr.contains("p")) {
                continue;
            }
            String pathURI = (String)((JSONObject)keyvalue).get("value");
            String pathLabel = QueryDBpedia.queryLabel(pathURI);

            //Print key and value
            double sim = -1;
            if(CachedPathScore.contains(relation, pathLabel)) {
                sim = CachedPathScore.getValue(relation, pathLabel);
                pathCacheCnt++;
            } else {
                sim = SimilarityScore.getPhraseSimilarity(relation, pathLabel);
                CachedPathScore.putValue(relation, pathLabel, sim);
                tripleMissCnt++;
            }

            maxSim = Double.max(sim, maxSim);
            if (maxSim >= 1.0) {
                System.out.println("\t\t\t\t[Path Eval] match! return~");
                break; // prune
            }
//            System.out.printf("\tkey: %s pathLabel: %s, score: %f \n", keyStr, pathLabel, sim);

        }
//        System.out.println("\t\t[Path Eval] Cached " + pathCacheCnt + " times in one path evaluation");
//        System.out.println("\t\tget path score: "+maxSim);
        return maxSim;
    }



}
