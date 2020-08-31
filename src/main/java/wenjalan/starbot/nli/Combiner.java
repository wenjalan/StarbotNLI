package wenjalan.starbot.nli;

import java.util.List;
import java.util.Map;

// combines the results of Predictors into one prediction
public interface Combiner {

    // returns a map of next words to their likelihoods
    Map<String, Double> combine(List<Map<String, Double>> maps, double[] weights);

}
