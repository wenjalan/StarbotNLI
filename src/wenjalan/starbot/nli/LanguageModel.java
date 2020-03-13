package wenjalan.starbot.nli;

import java.io.File;
import java.io.IOException;
import java.util.*;

// the model class, used to generate sentences
public class LanguageModel {

    // the STX escape character, used to denote the beginning of a sentence
    public static final String STX = "" + (char) 2;

    // the ETX escape character, used to denote the end of a sentence
    public static final String ETX = "" + (char) 3;

    // builder class, used in the creation of the model
    public static class Builder {

        // a list of corpuses to learn from
        private List<List<String>> corpi;

        // constructor
        public Builder() {
            this.corpi = new ArrayList<>();
        }

        // adds a corpus to the model to learn
        // corpus: a list of sentences (strings of words) to add to the model, e.g. "I like to party", "Who's with me?"
        public Builder addCorpus(List<String> corpus) {
            corpi.add(corpus);
            return this;
        }

        // build method, combines all corpi added with addCorpus() into one LanguageModel
        public LanguageModel build() {
            // combine all corpi and return a new Langauge Model
            List<String> masterCorpus = new ArrayList<>();
            corpi.forEach(masterCorpus::addAll);
            return LanguageModel.fromSentences(masterCorpus);
        }

    }

    // the map of precursor sequences to their nodes
    private Map<String, SequenceNode> nodes;

    // constructor
    // nodes: a Map of precursor sequences to Sequence Nodes
    private LanguageModel(Map<String, SequenceNode> nodes) {
        this.nodes = nodes;
    }

    // constructor method: from a corpus of sentences
    private static LanguageModel fromSentences(List<String> sentences) {
        // map of sequences to possible next words
        Map<String, List<String>> sequences = new TreeMap<>();
        // create a node for every permutation of the sentence
        for (String sentence : sentences) {
            // get the tokens
            StringTokenizer tokenizer = new StringTokenizer(sentence);
            int tokenCount = tokenizer.countTokens();
            // permutate the tokens
            for (int i = 0; i < tokenCount; i++) {
                // regenerate the tokenizer
                tokenizer = new StringTokenizer(sentence);
                // find the precursor sequence
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < i; j++) {
                    stringBuilder.append(tokenizer.nextToken());
                    stringBuilder.append(" ");
                }
                // get the precursor sequence (STX if beginning of sentence)
                String precursorSequence = stringBuilder.length() != 0 ? stringBuilder.toString() : STX;

                // get the next word (ETX if end of sentence)
                String nextWord = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : ETX;

                // put into map
                sequences.putIfAbsent(precursorSequence, new ArrayList<>());
                sequences.get(precursorSequence).add(nextWord);
            }

            // add the entire sentence plus the ETX escape sequence
            sequences.putIfAbsent(sentence + " ", new ArrayList<>());
            sequences.get(sentence + " ").add(ETX);
        }

        // create a list of SequenceNodes from the data
        Map<String, SequenceNode> nodes = new TreeMap<>();
        sequences.forEach((sequence, nextWords) -> nodes.put(sequence, new SequenceNode(sequence, nextWords)));

        // return a new LanguageModel with those nodes
        return new LanguageModel(nodes);
    }

    // generates a sequence of words up to a limit of words
    public String nextSequence(/* int safety */) {
        return nextSequence(STX/* , safety */);
    }

    // recursive method to generate sequences
    private String nextSequence(String precursorSequence/* , int safety */) {
        // if the ETX char is already present, return the sentence with the ETX char cut off
        if (precursorSequence.endsWith(ETX + " ")) return precursorSequence.substring(0, precursorSequence.length() - 3);
        else {
            // find our precursor sequence up to #safety tokens
            // String trimmedPrecursorSequence = trimSequence(precursorSequence, safety);
            SequenceNode node = nodes.get(precursorSequence);
            String nextWord = node.getNextWord();
            String newSequence = (precursorSequence.equals(STX) ? nextWord : precursorSequence + nextWord) + " ";
            return nextSequence(newSequence/* , safety */);
        }
    }

    // trims a precursor sequence, leaving the last n tokens
    private String trimSequence(String sequence, int n) {
        String[] tokens = sequence.split(" ");
        if (tokens.length - 1 < n) return sequence;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = tokens.length - 1 - n; i < tokens.length; i++) {
            stringBuilder.append(tokens[i]);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    // exports the model to a file
    public void exportToFile(File file) throws IOException {
        return;
    }

    // imports the model from a file
    public static LanguageModel importFromFile(File file) throws IOException {
        return null;
    }

}
