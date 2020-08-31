package wenjalan.starbot.nli;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// a predictor node, uses a series of corpi to predict next words
public interface Predictor {

    // initializes this Predictor with a series of corpi
    // corpi: the corpi to make predictions with
    void init(File[] corpi) throws IOException;

    // predict the next word given a List of words
    // pre: at least one word (the start sentinel)
    // returns: a map containing next words to their likelihoods
    Map<String, Long> predictNextWord(List<String> sentence);

}
