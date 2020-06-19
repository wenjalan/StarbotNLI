package wenjalan.starbot.nli.model.factory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

// holds sentences to act as model data for a ModelFactory
// handles the cleaning and scrubbing of data
public class SentenceCorpus {

    // the list of sentences
    private List<String> sentences;

    // the Function to use as a cleaner of a sentence
    private Function<String, String> cleaner;

    // constructor
    // cleaner: a Function to use to clean Strings of a sentence
    public SentenceCorpus(@Nullable Function<String, String> cleaner) {
        this.sentences = new ArrayList<>();
        this.cleaner = cleaner;
    }

    // adds a sentence to the corpus
    public void add(String sentence) {
        // check that the sentence has at least one valid word in it
        if (sentence.trim().length() == 0) {
            throw new IllegalArgumentException("Sentence must contain at least one proper word: " + sentence);
        }
        // split the sentence, clean each word with the cleaner, then rejoin them
        sentence = clean(sentence);
        sentences.add(sentence);
    }

    // adds multiple sentences to the corpus
    public void addAll(List<String> sentences) {
        for (String sentence : sentences) {
            add(sentence);
        }
    }

    // returns the sentences currently held in the corpus
    public List<String> sentences() {
        return List.copyOf(this.sentences);
    }

    // removes a sentence from the corpus, and all its copies
    // an expensive operation for sure
    // returns: whether or not the operation was successful
    public void remove(String sentence) {
        sentence = clean(sentence);
        while (sentences.contains(sentence)) {
            sentences.remove(sentence);
        }
    }

    // cleans a sentence using the cleaner
    private String clean(String sentence) {
        if (cleaner != null) {
            return Arrays.stream(sentence.trim().split("\\s+"))
                    .map(cleaner)
                    .collect(Collectors.joining(" "));
        }
        return sentence;
    }

}
