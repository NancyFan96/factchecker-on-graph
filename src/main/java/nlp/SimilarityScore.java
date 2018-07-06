package nlp;

import com.google.common.collect.Sets;
import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.ws4j.util.MatrixCalculator;


import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

import java.util.*;
import java.util.regex.Pattern;


public class SimilarityScore {
    // Tried Lucene
//    private static Version matchVersion = Version.LATEST; // Substitute desired Lucene version for XY
//    private static StandardAnalyzer analyzer = new StandardAnalyzer();

    private static final String REGEX = "(\\s|.|,)";
    private static final Pattern p = Pattern.compile(REGEX);

    // Using lib ws4j now
    private static ILexicalDatabase db = new NictWordNet();
    private static RelatednessCalculator lin = new Lin(db);
    private static RelatednessCalculator wup = new WuPalmer(db);
    private static RelatednessCalculator hso = new HirstStOnge(db);
    private static RelatednessCalculator lch = new LeacockChodorow(db);
    private static RelatednessCalculator jcn = new JiangConrath(db);
    private static RelatednessCalculator lesk = new Lesk(db);
    private static RelatednessCalculator path = new Path(db);
    private static RelatednessCalculator res = new Resnik(db);




    private static String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
    private static String[] not_stopwords ={"a", "in", "on", "at", "from"};
    private static Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
    private static Set<String> not_stopWordSet = new HashSet<String>(Arrays.asList(not_stopwords));


    /*
    //available options of metrics
    private static RelatednessCalculator[] rcs = { new HirstStOnge(db),
            new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
            new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
    */
    private static double compute(String word1, String word2) {
//        WS4JConfiguration.getInstance().setMFS(true);
//        double s = hso.calcRelatednessOfWords(word1, word2);
//        System.out.println("true: " + word1 + " <==> " + word2 + " : " + s);


        Set<String> synonyms1 = new LinkedHashSet<String>();
        for ( POS pos : POS.values() ) {
            synonyms1.addAll( JAWJAW.findSynonyms( word1, pos ) );
        }



        Set<String> synonyms2 = new LinkedHashSet<String>();
        for ( POS pos : POS.values() ) {
            synonyms2.addAll( JAWJAW.findSynonyms( word2, pos ) );
        }

        double sim = (synonyms1.contains(word2) || synonyms2.contains(word1))?1:0;


//        WS4JConfiguration.getInstance().setMFS(false);
//        double sim = wup.calcRelatednessOfWords(word1, word2);
        System.out.println("false: " + word1 + " <==> " + word2 + " : " + sim);

        return sim;
    }

    /**
     * get stemmed words similarity
     * @param word1
     * @param word2
     * @return
     */
    public static double getWordSimilarity(String word1, String word2) {
        double distance = compute(word1, word2);
        return distance;
    }

    public static double getPhraseSimilarity(String phrase1, String phrase2) throws Exception {
        phrase1 = OpenIE.getStemmed(phrase1);
        phrase2 = OpenIE.getStemmed(phrase2);
//        String[] kwList1 = phrase1.split(" ");
//        String[] kwList2  = phrase2.split(" ");
        String[] kwList1 = removeStopWords(phrase1).split(" ");
        String[] kwList2  = removeStopWords(phrase2).split(" ");

        int n = Math.min(kwList1.length, kwList2.length);
        if (n < kwList1.length) {
            String[] tmpList = kwList1;
            kwList1 = kwList2;
            kwList2 = tmpList;
        }
        if (n == 0) return 0;

        double[][] matrix = MatrixCalculator.getSynonymyMatrix( kwList1, kwList2 );
//        Arrays.stream(matrix)
//                .forEach(
//                        (row) -> {
//                            System.out.print("[");
//                            Arrays.stream(row)
//                                    .forEach((el) -> System.out.print(" " + el + " "));
//                            System.out.println("]");
//                        }
//                );

//
//        tryMatrixSimilarity(kwList1, kwList2, wup);
//        tryMatrixSimilarity(kwList1, kwList2, lch);
//        tryMatrixSimilarity(kwList1, kwList2, path);
//        tryMatrixSimilarity(kwList1, kwList2, res);
//        tryMatrixSimilarity(kwList1, kwList2, jcn);
//        tryMatrixSimilarity(kwList1, kwList2, lin);
//


//        ArrayList<Double> distancesList=new ArrayList<Double>();
//        List<Double> distancesCount =  new ArrayList<Double>();
//
//        for (String word1:kwList1) {
//            for (String word2:kwList2) {
//                double dist = getWordSimilarity(word1,word2);
//                System.out.println("\t\t\t"+word1+'@'+word2+':'+dist+"\n");
//                distancesList.add(dist);
//            }
//            distancesList.sort((a, b) -> Double.compare(b, a));
//            distancesCount.add(distancesList.get(0));
//        }

        double highestmean  = Arrays.stream(matrix).mapToDouble(
                (row) -> Arrays.stream(row).max().getAsDouble()).average().getAsDouble();
        System.out.printf("\t%s <==> %s: %f\n", phrase1, phrase2, highestmean);

        return highestmean;

    }

    private static void tryMatrixSimilarity(String[] kwList1, String[] kwList2, RelatednessCalculator lch) {
        for (String word1:kwList1) {
            for (String word2:kwList2) {
                double sim = lch.calcRelatednessOfWords(word1, word2);
                System.out.printf(sim + "\t");
            }
            System.out.printf("\n");
        }
        System.out.println();
    }

    public static double getCommonRatio(String str1, String str2)  {
        String[] kwList1 = removeStopWords(str1).split("[ _]");
        String[] kwList2  = removeStopWords(str2).split("[ _]");
        Set<String> keyWords1 = new HashSet<>(Arrays.asList(kwList1));
        Set<String> keyWords2 = new HashSet<>(Arrays.asList(kwList2));
        Set<String> denominator = Sets.union(keyWords1,keyWords2);
        Set<String> numerator = Sets.intersection(keyWords1,keyWords2);

        double score = denominator.size() > 0 ? (double)numerator.size()/(double)denominator.size() : 0;
//        System.out.println(str1+"@"+str2+"@"+score);
        return score;
    }

//Wellesley was born in Dublin, into the Protestant Ascendancy in Ireland
//
//1.0,Wellesley,be bear into,Protestant Ascendancy in Ireland
//[warning] cannot find entity
//1.0,Wellesley,be,bear
//1.0,Wellesley,be,bear in Dublin into Protestant Ascendancy
//[warning] cannot find entity
//1.0,Wellesley,be bear into,Protestant Ascendancy
//<http://dbpedia.org/resource/Wellesley> <http://dbpedia.org/resource/Protestant_Ascendancy>
//1.0,Wellesley,be,bear in Dublin into Protestant Ascendancy in Ireland
//[warning] cannot find entity
//1.0,Wellesley,be bear in,Dublin
//<http://dbpedia.org/resource/Wellesley> <http://www.wikidata.org/entity/Q1761>
//1.0,Protestant Ascendancy,be in,Ireland

    public static void main(String[] args) throws Exception {
//        String[] tripleList = new String[]{
//         "Wellesley be bear into Protestant Ascendancy in Ireland",
//         "Wellesley be bear",
//         "Wellesley be bear in Dublin into Protestant Ascendancy",
//         "Wellesley be bear into Protestant Ascendancy",
//         "Wellesley be bear in Dublin into Protestant Ascendancy in Ireland",
//         "Wellesley be bear in Dublin",
//         "Protestant Ascendancy be in Ireland"};
//
//        for (int i = 0; i < tripleList.length-1; i++){
////            double sim = getSimilarity(tripleList[i],tripleList[i+1]);
//            double sim = getPhraseSimilarity(tripleList[i],tripleList[i+1]);
//            System.out.println(tripleList[i] + "\n\t" + tripleList[i+1] + "\n\t" + sim);
//        }
//        Scanner scanner = new Scanner(new InputStreamReader(System.in));
//
//        String string1 = scanner.nextLine();
//        String string2 = scanner.nextLine();

        String string1 = "be born into";
        String string2 = "Link from a Wikipage to another Wikipage";
        String string3 = "birth place";
        new OpenIE();

//        ArrayList<Double> sims = new ArrayList<>();
//        sims.add(compute("bear", "born"));
//        sims.add(compute("bear", "place"));
//        sims.add(compute("born", "place"));
//        sims.add(compute("bear", "link"));
//        sims.add(compute("bear", "birth"));
//        System.out.print(sims);

        double sim = getPhraseSimilarity(string1, string3);
        System.out.println(sim);
        sim = getPhraseSimilarity(string1, string2);
        System.out.println(sim);



    }

    public static boolean isStopword(String word) {
        if(word.length() < 2) return true;
//        if(word.charAt(0) >= '0' && word.charAt(0) <= '9') return true; //remove numbers, "25th", etc
        if(stopWordSet.contains(word)) return true;
        else return false;
    }

    public static String removeStopWords(String string) {

        String result = "";
        String[] words = string.split(" ");
        for(String word : words) {
            if(word.isEmpty()) continue;
            if(isStopword(word) && !not_stopWordSet.contains(word)) continue; //remove stopwords
            result += (word+" ");
        }
        return result;
    }

}
