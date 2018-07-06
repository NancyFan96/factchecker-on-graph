package benchmark;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * tag, triple confident, subject/object match/synony score, path score
 */
public class FeaturedScore {
    public static int  tag;                 // -1, 1
    public static double conf;
    public static double subjectMatchScores;
    public static double objectMatchScores;
    public static double subjectSynonyScores;
    public static double objectSynonyScores;
    public static double pathHitScores;

    FeaturedScore() {
        tag = -2; conf = -2;
        subjectMatchScores = -2; objectMatchScores = -2;
        subjectSynonyScores = -2; objectSynonyScores = -2;
        pathHitScores = -2;
    }


    public static void write2csvheader (String csvPath) throws IOException {
        FileWriter fw = new FileWriter(csvPath);

        String addCSV = "tag" + "," + "conf_triple" + ","
                + "subject_match" + ","
                + "object_match" + ","
                + "subject_synony" + ","
                + "object_synony" + ","
                + "path_hit" + "\n";
        fw.write(addCSV);
        fw.close();

    }

    public static void write2csv (String csvPath) throws IOException {
        FileWriter aw = new FileWriter(csvPath,true);
        String addCSV = tag + "," + conf + ","
                + subjectMatchScores + ","
                + objectMatchScores + ","
                + subjectSynonyScores + ","
                + objectSynonyScores + ","
                + pathHitScores + "\n";

        aw.write(addCSV);
        aw.close();

    }

}
