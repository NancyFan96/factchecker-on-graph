package exe;

import benchmark.FactVerification;
import nlp.OpenIE;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;

import static benchmark.Evaluation.evaluateFact;
import static benchmark.FactVerification.checkSentence;

public class RunTest {
    private static final String DATA_DIR = "./src/main/resources/";
    private static String INPUT_DIR = "";
    private static String FATHER_DIR = "";
    private static String JsonFileName = "";

    /**
     * This function is to run test case
     * Test case has been paresed into JsonFile with
     *    - sentence =
     *        - tag = true/false
     *        - triple list:
     *            - predicate =
     *            - subject =
     *            - confident =
     *            - object =
     *
     * @param JsonFile
     * @return
     * @throws Exception
     */
    public static void verifyJsonFile(String JsonFile)  throws Exception{
        JSONArray SentenceJson = new JSONArray();
        try{
            BufferedReader in = new BufferedReader(
                    new FileReader(JsonFile));
            String inputLine;
            StringBuffer jsonbuffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                jsonbuffer.append(inputLine);
            }
            in.close();

            String resultStr = jsonbuffer.toString();
            JSONParser parser = new JSONParser();
            SentenceJson = (JSONArray) parser.parse(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // verify each sentence

        for (Object so: SentenceJson) {
            JSONObject sentenceWrapper =(JSONObject) so;
            String sentence = sentenceWrapper.get("sentence").toString();
            System.out.println(sentence);

            checkSentence(sentence, sentenceWrapper);
//            FactVerification.appendWriteCSV(FATHER_DIR + JsonFileName + "_features.csv");

        }
      }

    public static void main(String[] args) throws Exception {
        new OpenIE();
        INPUT_DIR = "naive/";
        JsonFileName = "born-test-small-true";
        FATHER_DIR = DATA_DIR + INPUT_DIR + JsonFileName + "/";
        String jsonFilePath = FATHER_DIR + JsonFileName + "_triples.json";
        System.out.println(jsonFilePath);
        verifyJsonFile(jsonFilePath);
    }



}
