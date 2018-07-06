package query;

import nlp.Ultility;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;


public class QueryDBpedia {

    private static final String DBpediaResourcePrefix = "http://dbpedia.org/resource/";
    private static String queryStr;
    private static Query query;
    public static final String RESULT_COMMON_DIR = "./src/main/resources/queryResults/";
//    private static String RESULT_DIR = "./src/main/resources/queryResults/born-new/";
//    private static String RESULT_FILE;



    private static final String myquery_template = "SELECT ?begin ?midI ?p ?midJ (count(?counter) as ?step) ?end where {\n" +
            "  VALUES (?begin ?end) {(@@@@@@)}\n" +
            "  ?begin (<>|!<>){0,1} ?counter .\n" +
            "  ?counter (<>|!<>){0,1} ?midI .\n" +
            "  #FILTER NOT EXISTS { [] (<>|!<>) ?begin }\n" +
            "\n" +
            "  ?midI ?p ?midJ .\n" +
            "\n" +
            "  ?midJ (<>|!<>){0,1} ?end .\n" +
            "  #FILTER NOT EXISTS { ?end (<>|!<>) [] }\n" +
            "}\n" +
            "group by ?begin ?end ?midI ?p ?midJ \n" +
            "ORDER BY ?begin ?end ?step\n" +
            "LIMIT 10";
    private static final String union_query_template = "SELECT * WHERE { \n" +
            "  VALUES (?begin ?end) {(@@@@@@)}\n" +
            "  { \n" +
            "      select * where {      \n" +
            "        ?begin ?p21 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p31 ?mid31.\n" +
            "        ?mid31 ?p32  ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p41 ?mid41. \n" +
            "         ?mid41 ?p42 ?mid42. \n" +
            "         ?mid42 ?p43 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p51 ?mid51. \n" +
            "         ?mid51 ?p52 ?mid52. \n" +
            "         ?mid52 ?p53 ?mid53. \n" +
            "         ?mid53 ?p54 ?end.\n" +
            "      }\n" +
            "  }\n" +
            "}";
    private static final String new_union_query_template = "SELECT ?begin ?p51 ?mid51 ?p52 ?mid52 ?p53 ?mid53 ?p54 ?end WHERE { \n" +
            "  VALUES (?begin ?end) {(@@@@@@)}\n" +
            "  { \n" +
            "      select * where {      \n" +
            "        ?begin ?p51 ?end.\n" +
            "        FILTER (\n" +
            "         \t?p51 != <>)\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p51 ?mid51.\n" +
            "        ?mid51 ?p52  ?end.\n" +
            "\n" +
            "        FILTER (\n" +
            "         \t?p51 != <> &&\n" +
            "         \t?p52 != <>)\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p51 ?mid51. \n" +
            "         ?mid51 ?p52 ?mid52. \n" +
            "         ?mid52 ?p53 ?end.\n" +
            "         FILTER (\n" +
            "         \t?p51 != <> &&\n" +
            "         \t?p52 != <> &&\n" +
            "         \t?p53 != <>)\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p51 ?mid51. \n" +
            "         ?mid51 ?p52 ?mid52. \n" +
            "         ?mid52 ?p53 ?mid53. \n" +
            "         ?mid53 ?p54 ?end.\n" +
            "         FILTER (\n" +
            "         \t?p51 != <> &&\n" +
            "         \t?p52 != <> &&\n" +
            "         \t?p53 != <> &&\n" +
            "         \t?p54 != <>)\n" +
            "      }\n" +
            "  }\n" +
            "}";
    private static final String long_union_query_template = "SELECT ?begin ?p61 ?mid61 ?p62 ?mid62 ?p63 ?mid63 ?p64 ?end WHERE { \n" +
            "  VALUES (?begin ?end) {(@@@@@@)}\n" +
            "  {   select * where {      \n" +
            "        ?begin ?p61 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p61 ?mid61.\n" +
            "        ?mid61 ?p62  ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?mid64. \n" +
            "         ?mid64 ?p65 ?end.\n" +
            "      }\n" +
            "  }\n" +
            "}";
    private static final String semibi_long_query_template = "SELECT * WHERE { \n" +
            "  VALUES (?begin ?end) {(@@@@@@)}\n" +
            "  {   select * where {      \n" +
            "        ?begin ?p61 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p61 ?mid61.\n" +
            "        ?mid61 ?p62  ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?mid64. \n" +
            "         ?mid64 ?p65 ?end.\n" +
            "      }\n" +
            "  }\n" +
            "\n" +
            "  UNION {\n" +
            "      select * where {      \n" +
            "        ?end ?p61 ?begin.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?mid61 ?p61 ?begin.\n" +
            "        ?mid61 ?p62  ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?mid61 ?p61 ?begin. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?mid61 ?p61 ?begin. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?mid61 ?p61 ?begin. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?mid64.\n" +
            "         ?mid64 ?p65 ?end.\n" +
            "      }\n" +
            "  }\n" +
            "\n" +
            "  UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p61 ?mid61.\n" +
            "        ?end ?p62  ?mid61.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?end ?p63 ?mid62.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?end ?p64 ?mid63.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?mid64. \n" +
            "         ?end ?p65 ?mid64.\n" +
            "      }\n" +
            "  }\n" +
            "\n" +
            "\n" +
            "  }";

    private static final String bi_end_long_query_template = "SELECT * WHERE { \n" +
            "  VALUES (?begin ?end) {(@@@@@@)}\n" +
            "  {   select * where {      \n" +
            "        ?begin ?p61 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p61 ?mid61.\n" +
            "        ?mid61 ?p62  ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?end.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?mid64. \n" +
            "         ?mid64 ?p65 ?end.\n" +
            "      }\n" +
            "  }\n" +
            "\n" +
            "  UNION {\n" +
            "      select * where {      \n" +
            "        ?end ?p61 ?begin.\n" +
            "      }\n" +
            "  }\n" +
            "\n" +
            "  UNION {\n" +
            "      select * where {\n" +
            "        \n" +
            "        ?begin ?p61 ?mid61.\n" +
            "        ?end ?p62  ?mid61.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?end ?p63 ?mid62.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?end ?p64 ?mid63.\n" +
            "      }\n" +
            "  }UNION {\n" +
            "      SELECT * where {\n" +
            "         ?begin ?p61 ?mid61. \n" +
            "         ?mid61 ?p62 ?mid62. \n" +
            "         ?mid62 ?p63 ?mid63. \n" +
            "         ?mid63 ?p64 ?mid64. \n" +
            "         ?end ?p65 ?mid64.\n" +
            "      }\n" +
            "  }\n" +
            "\n" +
            "\n" +
            "  }";


//    private static String union_query_template = "SELECT * WHERE { \n" +
//            "  {\n" +
//            "      select * where {\n" +
//            "        VALUES (?begin ?end) { (@@@@@@) }\n" +
//            "        \n" +
//            "        ?begin ?p21 ?end.\n" +
//            "      }\n" +
//            "  }UNION {\n" +
//            "      select * where {\n" +
//            "        VALUES (?begin ?end) { (@@@@@@) }\n" +
//            "        \n" +
//            "        ?begin ?p31 ?mid31.\n" +
//            "        ?mid31 ?p32  ?end.\n" +
//            "      }\n" +
//            "  }UNION {\n" +
//            "      SELECT * where {\n" +
//            "        VALUES (?begin ?end) { (@@@@@@) }\n" +
//            "         ?begin ?p41 ?mid41. \n" +
//            "         ?mid41 ?p42 ?mid42. \n" +
//            "         ?mid42 ?p43 ?end.\n" +
//            "      }\n" +
//            "  }UNION {\n" +
//            "      SELECT * where {\n" +
//            "        VALUES (?begin ?end) { (@@@@@@) }\n" +
//            "         ?begin ?p51 ?mid51. \n" +
//            "         ?mid51 ?p52 ?mid52. \n" +
//            "         ?mid52 ?p53 ?mid53. \n" +
//            "         ?mid53 ?p54 ?end.\n" +
//            "      }\n" +
//            "  }\n" +
//            "  }";

    public static String runQuery(String queryStr_, String resultFile) {
        queryStr = queryStr_;
        query = QueryFactory.create(queryStr);
        String jsonStrResult = null;

//        System.out.println(queryStr);

        ResultSet rs = null;
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query) ) {
            ((QueryEngineHTTP)qexec).addParam("timeout", "50000") ;

            rs = qexec.execSelect();

            FileOutputStream fileOutputStream = new FileOutputStream(new File(resultFile));
            ResultSetFormatter.outputAsJSON(fileOutputStream, rs);

            JSONParser parser = new JSONParser();
            jsonStrResult = parser.parse(new FileReader(resultFile)).toString();

//           ResultSetFormatter.out(System.out, rs);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonStrResult;
    }


    public static String queryPath(String subjectURI, String objectURI) throws IOException {
        String queryStr_ = prepareQuery(bi_end_long_query_template, subjectURI, objectURI); //binary direction at ending only

        String resultFile = Ultility.URI2KeyString(subjectURI) + "@" + Ultility.URI2KeyString(objectURI)+".json";
        String resultDir = RESULT_COMMON_DIR + "";
        resultFile = resultDir + resultFile;
        new File(RESULT_COMMON_DIR).mkdir();
        new File(resultDir).mkdir();

        System.out.println("\t\t\t[query path]");
        String jsonStrResult = runQuery(queryStr_, resultFile);


        return jsonStrResult;
    }

    public static  String queryLabel(String URI) throws ParseException {
        String querytemp =
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "select * where {\n" +
                "        <@@@@@@> rdfs:label ?label.\n" +
                "       filter((lang(?label) = 'en'))\n" +
                "      }";
        String queryStr_ = querytemp.replace("@@@@@@", URI);
//        System.out.println(queryStr_);

        String jsonStrResult = runQuery(queryStr_, RESULT_COMMON_DIR+"test.json");
        JSONParser parser = new JSONParser();
        JSONObject ResultJson = (JSONObject) parser.parse(jsonStrResult);
        ResultJson = (JSONObject) ResultJson.get("results");
        JSONArray ResultPathArrayJson = (JSONArray) ResultJson.get("bindings");

        if (ResultPathArrayJson.isEmpty()) return "";

        ResultJson = (JSONObject)ResultPathArrayJson.get(0);
        String label = (String)((JSONObject)ResultJson.get("label")).get("value");
        return label;
    }

    public static ResultSet queryTriple(String s, String o, String p) {
        ResultSet rs = null;
        return rs;
    }

//Huxley was born in Godalming, Surrey, England, in 1894.
//
//1.0,Huxley,be,bear in Godalming in 1894
//1.0,Huxley,be bear in,1894
//1.0,Huxley,be bear in,England
//1.0,Huxley,be bear in,Godalming
//1.0,Huxley,be,bear


    public static void main(String[] args) throws Exception {
//        String reStr = queryLabel("http://dbpedia.org/ontology/birthPlace");
//        System.out.println(reStr);
///       "SELECT ?s ?p ?o WHERE" + "\n" +
//                "{ ?s ?p ?o;" + "\n" +
//                ""+phrase;
//        String queryStr = "SELECT ?prop ?place WHERE { <http://dbpedia.org/resource/%C3%84lvdalen> ?prop ?place .}";
        String s = "Wellesley_(disambiguation)";
        String o = "Dublin_(disambiguation)";//"Aristocratic_family";
//        String o = "Dublin_(disambiguation)";//"Category:Dublin";
        String beginNode = "<" + DBpediaResourcePrefix + s + ">";
        String endNode = "<" + DBpediaResourcePrefix + o + ">";
        String endpoint_values = beginNode + " " + endNode;
        System.out.println(endpoint_values);
        String queryStr_ = bi_end_long_query_template.replace("@@@@@@", endpoint_values);
        String result = runQuery(queryStr_,RESULT_COMMON_DIR+"t1-both-disam-true-semi-directionlong.json");
        System.out.println(result);


//        String queryStr = "SELECT ?prop ?place WHERE { <http://dbpedia.org/resource/%C3%84lvdalen> ?prop ?place .}";
//        Query query = QueryFactory.create(queryStr);
//
//        // Remote execution.
//        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
//            // Set the DBpedia specific timeout.
//            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;
//
//            // Execute.
//            ResultSet rs = qexec.execSelect();
//            ResultSetFormatter.out(System.out, rs, query);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String queryString1 = "select ?p ?o where { <http://dbpedia.org/resource/China> ?p ?o .}";
//        String queryString2 = "select ?s where { ?s a <http://www.w3.org/2002/07/owl#Thing> } LIMIT 100";

// now creating query object
//        Query query = QueryFactory.create(queryString2);
// initializing queryExecution factory with remote service.
// **this actually was the main problem I couldn't figure out.**
//        QueryExecution qexec = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);

//after it goes standard query execution and result processing which can
// be found in almost any Jena/SPARQL tutorial.
//        try {
//            ResultSet results = qexec.execSelect();
//            for (; results.hasNext();) {
//
//                // Result processing is done here.
//            }
//        }
//        finally {
//            qexec.close();
//        }

    }

    private static String prepareQuery(String template, String subjectURI, String objectURI) {
        String beginNode = "<" + subjectURI + ">";
        String endNode = "<" + objectURI + ">";
        String endpoint_values = beginNode + " " + endNode;
        String queryStr_ = template.replace("@@@@@@", endpoint_values);

        System.out.println("\t" + endpoint_values);

        return queryStr_;
    }

}
