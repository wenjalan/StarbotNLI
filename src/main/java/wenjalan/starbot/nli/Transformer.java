package wenjalan.starbot.nli;

import java.util.List;
import java.util.Map;

public interface Transformer {

    // modifies a map given a sentence and weight to apply
    void transformPrediction(Map<String, Long> prediction, List<String> sentence, double weight);

}
