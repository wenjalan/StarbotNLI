package wenjalan.starbot.nli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wenjalan.starbot.nli.wrapper.LanguageModelJsonWrapper;
import wenjalan.starbot.nli.wrapper.SequenceNodeJsonWrapper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

    // constructor
    // modelWrapper: a LanguageModelJsonWrapper imported from a file to create a model from
    private LanguageModel(LanguageModelJsonWrapper modelWrapper) {
        this.nodes = new TreeMap<>();
        Map<String, SequenceNodeJsonWrapper> wrapperNodes = modelWrapper.getNodes();
        wrapperNodes.forEach((sequence, node) -> {
            this.nodes.put(sequence, new SequenceNode(node.getPrecursorSequence(), node.getNextWords()));
        });
    }

    // constructor method: from a corpus of sentences
    private static LanguageModel fromSentences(List<String> sentences) {
        // map of sequences to possible next words
        Map<String, List<String>> sequences = new TreeMap<>();

        // create a node for every permutation of the sentence
        for (String sentence : sentences) {
            // add the beginning of sentence escape sequence and end of sentence escape sequence to the sentence
            sentence = STX + " " + sentence + " " + ETX;
            // get the tokens
            String[] tokens = sentence.split("\\s+");

            // for each word in the sentence, find each sequence that can precurse it
            // Ex: I am (free)
            // [I am (free)]
            // [am (free)]
            for (int i = 1; i < tokens.length; i++) {
                // the next word
                String word = tokens[i];
                // the list of sequences that precurse this word
                List<String> precursorSequences = new ArrayList<>();

                // create all possible precursor sentences
                for (int j = 0; j < i; j++) {
                    // create the sequence
                    StringBuilder sequenceBuilder = new StringBuilder();
                    for (int k = j; k < i; k++) {
                        sequenceBuilder.append(tokens[k]);
                        sequenceBuilder.append(" ");
                    }
                    // add the sequence
                    precursorSequences.add(sequenceBuilder.toString());
                }

                // put each sequence into the map with the next word as a prediction
                precursorSequences.forEach((seq) -> {
                    sequences.putIfAbsent(seq, new ArrayList<>());
                    sequences.get(seq).add(word);
                });
            }
        }

        // create a list of SequenceNodes from the data
        Map<String, SequenceNode> nodes = new TreeMap<>();
        sequences.forEach((sequence, nextWords) -> nodes.put(sequence, new SequenceNode(sequence, nextWords)));

        // return a new LanguageModel with those nodes
        return new LanguageModel(nodes);
    }

    // generates a sequence of words up to a limit of words
    // originality: the measure of the maximum number of words to look back at when predicting the next word
    public String nextSequence(int originality) {
        return nextSequence(STX, originality);
    }

    // recursive method to generate sequences
    private String nextSequence(String precursorSequence, int originality) {
        // if the ETX char is already present, return the sentence with the ETX char cut off
        if (precursorSequence.endsWith(ETX + " ")) return precursorSequence.substring(0, precursorSequence.length() - 3);
        else {
            // find our precursor sequence with up to "originality" tokens
            String[] tokens = precursorSequence.split("\\s+");
            StringBuilder sequenceBuilder = new StringBuilder();
            int start = Math.max(0, tokens.length - originality);
            for (int i = start; i < tokens.length; i++) {
                sequenceBuilder.append(tokens[i]);
                sequenceBuilder.append(" ");
            }
            String limitedSequence = sequenceBuilder.toString();

            // find the next word based on the limited sequence
            SequenceNode node = nodes.get(limitedSequence);
            String nextWord = node.getNextWord();
            String newSequence = (precursorSequence.equals(STX) ? nextWord : precursorSequence + nextWord) + " ";
            return nextSequence(newSequence, originality);
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
        LanguageModelJsonWrapper modelWrapper = new LanguageModelJsonWrapper(this);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(modelWrapper);
        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.flush();
    }

    // imports the model from a file
    public static LanguageModel importFromFile(File file) throws IOException {
        Gson gson = new GsonBuilder().create();
        FileReader reader = new FileReader(file);
        LanguageModelJsonWrapper modelWrapper = gson.fromJson(reader, LanguageModelJsonWrapper.class);
        return new LanguageModel(modelWrapper);
    }

    // returns the nodes of this LanguageModel
    public Map<String, SequenceNode> getNodes() {
        return this.nodes;
    }

}
