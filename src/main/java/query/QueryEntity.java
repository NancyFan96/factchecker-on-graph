package query;

import nlp.SimilarityScore;
import nlp.Ultility;

//import org.apache.http.client.utils.URIBuilder;
//import java.net.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static com.sun.tools.classfile.Attribute.Exceptions;


public class QueryEntity {
    //Huxley was born in Godalming, Surrey, England, in 1894.
    //1.0,Huxley,be,bear in Godalming in 1894
    //1.0,Huxley,be bear in,1894
    //1.0,Huxley,be bear in,England
    //1.0,Huxley,be bear in,Godalming
    //1.0,Huxley,be,bear

    /*
    *  example
    *  http://dbpedia.org/services/rdf/iriautocomplete.get?lbl=1894
    */

    private static String URL;
    private static final String HOST = "dbpedia.org";
    private static final String PATH = "/services/rdf/iriautocomplete.get";

    public static String pickEntityResource(String phrase) throws Exception {
        JSONObject response = entityLabelLookup(phrase);
        ArrayList<String> resultList = (ArrayList<String>) response.get("results");
        Pattern p = Pattern.compile("http:\\/\\/dbpedia\\.org\\/resource\\/[A-Z,a-z]*_\\(disambiguation\\)");
                            //compile("http://dbpedia.org/resource/*");
        String picked = null;

//        System.out.println(Arrays.toString(resultList.toArray()));
//        for (int i = 0; i < resultList.size(); i+=2) {
//            System.out.println(resultList.get(i)+": <"+resultList.get(i+1)+">");
//        }

        picked = getString(phrase, resultList, p);

        if (picked == null) {
            p = Pattern.compile("http://dbpedia.org/resource/*");
            picked = getString(phrase, resultList, p);
        }


        return picked;
    }

    private static String getString(String phrase, ArrayList<String> resultList, Pattern p) {
        String picked = null;
        String entityKey = null;
        double sim = -1;

        for (String s:resultList){
            if (p.matcher(s).find()) {
                picked = s;
                // exclude pattern like http://dbpedia.org/resource/Category:*
                if(!Ultility.URI2KeyString(s).contains(":")){
                    entityKey = Ultility.URI2KeyString(picked);
                    sim = SimilarityScore.getCommonRatio(phrase, entityKey);
                    if (sim == 0) {
                        picked = null;
                        continue;
                    }
                    break;
                }

            }
        }
        System.out.println("\t\t\t[entity lookup] "+phrase+ " -->" + entityKey + " w/ csim " + sim);
        return picked;
    }

    private static JSONObject entityLabelLookup(String phrase) throws Exception {
        JSONObject resultJson = new JSONObject();
        URIBuilder builder = new URIBuilder()
                .setScheme("http")
                .setHost(HOST)
                .setPath(PATH)
                .addParameter("lbl", phrase);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        URL = builder.build().toURL().toString();
        // The underlying HTTP connection is still held by the response object
// to allow the response content to be streamed directly from the network socket.
// In order to ensure correct deallocation of system resources
// the user MUST call CloseableHttpResponse#close() from a finally clause.
// Please note that if response content is not fully consumed the underlying
// connection cannot be safely re-used and will be shut down and discarded
// by the connection manager.
        try {
            HttpGet httpGet = new HttpGet(URL);

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }

            };

//            CloseableHttpResponse response = httpclient.execute(httpGet);
//            System.out.println(response.getStatusLine());
//            HttpEntity entity1 = response.getEntity();
//            // do something useful with the response body
//            // and ensure it is fully consumed
//            EntityUtils.consume(entity1);

            String resultStr = httpclient.execute(httpGet, responseHandler);//response.toString();
            JSONParser parser = new JSONParser();
            resultJson = (JSONObject) parser.parse(resultStr);
        } finally {
            httpclient.close();
        }

//        URIBuilder builder = new URIBuilder()
//                .setScheme("http")
//                .setHost(HOST)
//                .setPath(PATH)
//                .addParameter("lbl", phrase);
//        JSONObject resultJson = new JSONObject();
//        try {
//            URL = builder.build().toURL();
//            HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
//            conn.setRequestMethod("GET");
//            int responseCode = conn.getResponseCode();
//            System.out.println("\nSending 'GET' request to URL : " + URL);
//            System.out.println("Response Code : " + responseCode);
//
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(conn.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//
//            String resultStr = response.toString();
//            JSONParser parser = new JSONParser();
//            resultJson = (JSONObject) parser.parse(resultStr);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return resultJson;
    }


    public static void main(String[] args) throws  Exception{
        String resultString = pickEntityResource("Dublin");
        resultString = pickEntityResource("Wellesley");
        resultString = pickEntityResource("Aristocratic_family");
//        System.out.println(resultString);
    }

}
