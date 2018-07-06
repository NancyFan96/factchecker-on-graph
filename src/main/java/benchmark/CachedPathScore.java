package benchmark;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * This cach is for Evaluate.evaluatePath
 *  member:     pathScoreCache
 *  method:     getValue
 *              putValue
 *              clearAll
 */
public class CachedPathScore {
    private static Map<String, Map<String,Double>> pathScoreCache = new HashMap<>();

    public static double getValue(String relation, String pathLable) {
        double val = pathScoreCache.get(relation).get(pathLable);
        return val;
    }

    public static void putValue(String relation, String pathLable, Double score) {
        if (!pathScoreCache.containsKey(relation)){
            Map<String, Double> subMapEntry = new HashMap<>();
            subMapEntry.put(pathLable, score);
            pathScoreCache.put(relation, subMapEntry);
        } else {
            pathScoreCache.get(relation).put(pathLable, score);
        }

    }

    public static boolean contains(String relation, String pathLable) {
        if (pathScoreCache.containsKey(relation) && pathScoreCache.get(relation).containsKey(pathLable))
            return true;
        return false;
    }

    public static void clearAll() {
        pathScoreCache.clear();
    }
}
