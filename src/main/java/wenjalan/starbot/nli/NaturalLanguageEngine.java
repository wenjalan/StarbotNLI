package wenjalan.starbot.nli;

import edu.stanford.nlp.simple.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

// main class
public class NaturalLanguageEngine {

    private static final String SENTENCE_END = "" + (char) 3;
    private static final String SENTENCE_START = "" + (char) 2;

    // args: a list of files to act as corpi
    public static void main(String[] args) {
        // load corpi
        File[] corpi = new File[args.length];
        for (int i = 0; i < args.length; i++) {
            corpi[i] = new File(args[i]);
            if (corpi[i].exists()) {
                System.out.println("Loaded corpus " + corpi[i].getName());
            }
            else {
                System.err.println("Couldn't load corpus " + args[i]);
            }
        }

        // create an engine
        NaturalLanguageEngine e = new NaturalLanguageEngine(corpi);
        for (int i = 0; i < 10; i++) {
            System.out.println(e.generateSentence());
        }
    }

    // constructor
    public NaturalLanguageEngine(File[] corpi) {
        // initialize nodes

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
        if (sentence.size() > 3) {
            return SENTENCE_END;
        } else {
            return "RUSHIA";
        }
    }

}
