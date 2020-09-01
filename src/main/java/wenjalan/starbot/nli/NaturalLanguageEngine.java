package wenjalan.starbot.nli;

import edu.stanford.nlp.simple.*;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// main class
public class NaturalLanguageEngine {

    public static final String SENTENCE_END = "</s>";// + (char) 3;
    public static final String SENTENCE_START = "<s>";// + (char) 2;

    // a list of predictors used to create sentences
    List<Predictor> predictors;

    // a list of transformers used to modify predictions
    List<Transformer> transformers;

//    // args: a list of files to act as corpi
//    public static void main(String[] args) {
//        // load corpi
//        File[] corpi = new File[args.length];
//        for (int i = 0; i < args.length; i++) {
//            corpi[i] = new File(args[i]);
//            if (corpi[i].exists()) {
//                System.out.println("Loaded corpus " + corpi[i].getName());
//            }
//            else {
//                System.err.println("Couldn't load corpus " + args[i]);
//            }
//        }
//
//        // create an engine
//        NaturalLanguageEngine e = new NaturalLanguageEngine(corpi);
//        for (int i = 0; i < 10; i++) {
//            System.out.println(e.generateSentence());
//        }
//    }

    // constructor
    public NaturalLanguageEngine(File[] corpi) {
        // initialize nodes
        try {
            predictors = new LinkedList<>();
            predictors.add(new BiGramPredictor(corpi));
            // predictors.add(new PartOfSpeechPredictor(corpi));
            // todo: add more predictors here

            // todo: add more transformers here
            transformers = new LinkedList<>();
            transformers.add(new PartOfSpeechPredictor(corpi));

        } catch (IOException e) {
            System.err.println("Error while initializing predictors");
            e.printStackTrace();
        }
    }

    // generates a sentence
    public String generateSentence() {
        // the sentence being generated
        ArrayList<String> sentence = new ArrayList<>();

        // add the sentence start sentinel
        sentence.add(SENTENCE_START);

        // while the last word isn't the end sentence sentinel
        while (!sentence.get(sentence.size() - 1).equalsIgnoreCase(SENTENCE_END)) {
            // predict the next word
            String nextWord = predictNextWord(sentence);

            // add it to the sentence
            sentence.add(nextWord);
        }

        // return the sentence
        return formSentence(sentence);
    }

    // forms a sentence from a list of strings, placing spaces where necessary
    private String formSentence(ArrayList<String> sentence) {
        List<String> sublist = sentence.subList(1, sentence.size() - 1);
        // TODO: accomodate for things like punctuation, etc.
        return String.join(" ", sublist);
    }

    // predicts the next word in a sentence
    // sentence: a list of words (strings) representing the sentence thus far
    // pre: sentence has at least 1 word in it (at least the starting sentinel)
    // returns: a word that comes after the words in the sentence
    private String predictNextWord(ArrayList<String> sentence) {
        // have all the predictors predict
        List<Map<String, Long>> predictions = new ArrayList<>();
        for (Predictor predictor : predictors) {
            predictions.add(predictor.predictNextWord(sentence));
        }

        // todo: temp combination algorithm
        Map<String, Long> finalPrediction = new TreeMap<>();
        for (Map<String, Long> prediction : predictions) {
            for (Map.Entry<String, Long> entry : prediction.entrySet()) {
                finalPrediction.putIfAbsent(entry.getKey(), 0L);
                finalPrediction.put(entry.getKey(), finalPrediction.get(entry.getKey()) + entry.getValue());
            }
        }

        // todo: temp transformer algorithm
        for (Transformer transformer : transformers) {
            transformer.transformPrediction(finalPrediction, sentence, 1000.0);
        }

        // pick the value with the highest
//        String highest = null;
//        for (String s : finalPrediction.keySet()) {
//            if (highest == null || finalPrediction.get(s) > finalPrediction.get(highest)) {
//                highest = s;
//            }
//        }
//        return highest;
        return chooseWeightedRandom(finalPrediction);
    }

    // returns an element E, randomized weighted on its value
    // greater values greater chances
    public static <E> E chooseWeightedRandom(Map<E, Long> map) {
        int weightSum = 0;
        for (Long value : map.values()) {
            weightSum += value;
        }
        int ceiling = new Random().nextInt(weightSum) + 1;
        for (E e : map.keySet()) {
            ceiling -= map.get(e);
            if (ceiling <= 0) {
                return e;
            }
        }
        throw new IllegalStateException("Out of bounds roll");
    }

}
