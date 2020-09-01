package wenjalan.starbot.nli;

import edu.stanford.nlp.simple.Sentence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static wenjalan.starbot.nli.NaturalLanguageEngine.*;

// predicts based on POS, giving words with recognized POS patterns advantage
// uses bigrams
public class PartOfSpeechPredictor implements Predictor, Transformer {

    // POS mapped to next POS and their occurrences
    Map<String, Map<String, Long>> bigrams;

    // POS mapped to set of words that belong to that POS
    Map<String, Set<String>> vocab;

    public PartOfSpeechPredictor(File[] corpi) throws IOException {
        init(corpi);
    }

    @Override
    public void init(File[] corpi) throws IOException {
        // init pos map
        bigrams = new TreeMap<>();

        // init vocab
        vocab = new TreeMap<>();

        // read all the corpi
        for (File file : corpi) {
            // read the file
            BufferedReader reader = new BufferedReader(new FileReader(file.getName()));
            long lineNo = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                try {
                    // make a sentence of the line
                    Sentence sentence = new Sentence(line);

                    // add each of the words to the bigrams pile
                    String lastPos = SENTENCE_START;
                    for (int i = 0; i < sentence.length(); i++) {
                        // get the POS and the word
                        String pos = sentence.posTag(i);
                        String word = sentence.word(i);

                        // record the word
                        vocab.putIfAbsent(pos, new TreeSet<>());
                        vocab.get(pos).add(word);

                        // record the pattern of the POS
                        bigrams.putIfAbsent(lastPos, new TreeMap<>());
                        Map<String, Long> followingWords = bigrams.get(lastPos);
                        followingWords.putIfAbsent(pos, 0L);
                        followingWords.put(pos, followingWords.get(pos) + 1);
                        lastPos = pos;
                    }

                    // add an ending sentence sentinel occurrence
                    bigrams.putIfAbsent(lastPos, new TreeMap<>());
                    Map<String, Long> followingWords = bigrams.get(lastPos);
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

    @Override
    public Map<String, Long> predictNextWord(List<String> sentence) {
        // get the last POS
        String lastPos = SENTENCE_START;
        if (sentence.size() != 1) {
            lastPos = new Sentence(sentence).posTag(sentence.size() - 1);
        }

        // choose a POS that comes after that POS
        if (bigrams.get(lastPos) == null) {
            return Collections.EMPTY_MAP;
        }
        String nextPos = chooseWeightedRandom(bigrams.get(lastPos));

        // return all the words in that POS
        Map<String, Long> ret = new TreeMap<>();
        if (vocab.get(nextPos) == null) return Collections.EMPTY_MAP;
        for (String word : vocab.get(nextPos)) {
            ret.put(word, 1L);
        }
        return ret;
    }

    // applies this predictor's predictions on top of another predictor's prediction
    // post: prediction is modified with this predictor's weights
    @Override
    public void transformPrediction(Map<String, Long> prediction, List<String> sentence, double weight) {
        Map<String, Long> selfPrediction = predictNextWord(sentence);
        for (String s : selfPrediction.keySet()) {
            if (prediction.containsKey(s)) {
                prediction.put(s, (long) (prediction.get(s) * weight));
            }
        }
    }

}
