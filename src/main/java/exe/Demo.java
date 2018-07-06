package exe;

import nlp.OpenIE;

import static benchmark.FactVerification.checkSentence;

/**
 * Demo class can test single sentence
 *      is used for final webserver demo
 *      All sentences test share one OpenIE Engine
 */
public class Demo {

    public static void main(String[] args) throws Exception {
        new OpenIE();
        checkSentence("Wellesley was born in Madrid, into the Protestant Ascendancy in Ireland.");
        checkSentence("Wellesley was born in Dublin, into the Protestant Ascendancy in Ireland.");
    }
}
