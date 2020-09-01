package wenjalan.starbot.nli;

import edu.stanford.nlp.simple.Sentence;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

import static wenjalan.starbot.nli.NaturalLanguageEngine.SENTENCE_END;
import static wenjalan.starbot.nli.NaturalLanguageEngine.SENTENCE_START;

// uses bi-grams to predict next words
public class BiGramPredictor implements Predictor {

    // a map of words to the words that come after them
    // the pair is the following word with its occurrences
    Map<String, Map<String, Long>> bigrams;

    public BiGramPredictor(File[] corpi) throws IOException {
        init(corpi);
    }

    // init
    @Override
    public void init(File[] corpi) throws IOException {
        // init word map
        bigrams = new TreeMap<>();

        // read all the corpi
        for (File file : corpi) {
            // read the file
            BufferedReader reader = new BufferedReader(new FileReader(file.getName()));
            long lineNo = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                try {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    // make a sentence of the line
                    Sentence sentence = new Sentence(line);

                    // add each of the words to the bigrams pile
                    String lastWord = SENTENCE_START;
                    for (String nextWord : sentence.words()) {
                        bigrams.putIfAbsent(lastWord, new TreeMap<>());
                        Map<String, Long> followingWords = bigrams.get(lastWord);
                        followingWords.putIfAbsent(nextWord, 0L);
                        followingWords.put(nextWord, followingWords.get(nextWord) + 1);
                        lastWord = nextWord;
                    }

                    // add an ending sentence sentinel occurrence
                    bigrams.putIfAbsent(lastWord, new TreeMap<>());
                    Map<String, Long> followingWords = bigrams.get(lastWord);
                    followingWords.putIfAbsent(SENTENCE_END, 0L);
                    followingWords.put(SENTENCE_END, followingWords.get(SENTENCE_END) + 1);

                } catch (IllegalStateException e) {
                    System.err.println("Encountered an error while reading line " + lineNo + " of corpus " + file.getName());
                    System.err.println("\t" + e.getMessage());
                    // e.printStackTrace();
                }
                lineNo++;
            }
        }
    }

    // predict
    // returns: a map of words that could come after the words provided in the sentence,
    // weighted by probability
    @Override
    public Map<String, Long> predictNextWord(List<String> sentence) {
        // get the word at the end of the sentence
        String lastWord = sentence.get(sentence.size() - 1);

        // get the next words of that word
        Map<String, Long> followingWords = bigrams.get(lastWord);

        // return prediction
        return new TreeMap<>(followingWords);
    }

    // returns a copy of the bigrams model
    public Map<String, Map<String, Long>> bigrams() {
        return new TreeMap<>(bigrams);
    }

}
