package wenjalan.starbot.nli;

import java.util.*;

// contains a sequence of words paired to their possible next words
public class SequenceNode {

    // Random object to generate random ints with
    private static Random rand = null;

    // precursor sequence leading up to the next word
    private String precursorSequence;

    // the number of words in the precursor sequence
    private int precursorSequenceTokenCount;

    // the list of possible next words given the precursor sequence, mapped to their occurrences in the corpus
    private Map<String, Long> nextWords;

    // constructor
    // precursorSequence: the sequence of words leading up to this node
    // nextWords: the possible words that follow the precursor sequence, which can contain duplicates
    public SequenceNode(String precursorSequence, List<String> nextWords) {
        this.precursorSequence = precursorSequence;
        this.precursorSequenceTokenCount = new StringTokenizer(precursorSequence).countTokens();
        // map the nextWords to their occurrences
        this.nextWords = new TreeMap<>();
        nextWords.forEach((word) -> {
            this.nextWords.putIfAbsent(word, 0L);
            this.nextWords.put(word, this.nextWords.get(word) + 1);
        });
    }

    // constructor
    // precursorSequence: the sequence of words leading up to this node
    // nextWords: the map of next words to their occurrences
    public SequenceNode(String precursorSequence, Map<String, Long> nextWords) {
        this.precursorSequence = precursorSequence;
        this.nextWords = nextWords;
    }

    // returns a random word of the next sequence, weighted based on occurrences
    // requires O(n) time where n is the size of nextWords
    public String getNextWord() {
        // ensure that there are next words to choose from
        int space = 0; // the "selection space" of words to choose from
        for (long i : this.nextWords.values()) space += i;
        if (space <= 0) throw new IllegalStateException("no next words have been added to this node");

        // roll a die
        if (rand == null) rand = new Random();
        int roll = rand.nextInt(space) + 1;

        // return the word that corresponds to the roll
        long i = 0;
        for (String word : this.nextWords.keySet()) {
            long occurrences = this.nextWords.get(word);
            for (int j = 0; j < occurrences; j++) {
                i++;
                if (i >= roll) return word;
            }
        }

        // if we got through the loop we messed up
        throw new IllegalStateException("error getting next word with selection space " + space + " and roll " + roll);
    }

    // returns the precursor sequence to this node
    public String getPrecursorSequence() {
        return this.precursorSequence;
    }

    // returns the number of tokens in the precursor sequence
    public int getPrecursorSequenceTokenCount() {
        return this.precursorSequenceTokenCount;
    }

    // returns the map of next possible words to their occurrences
    public Map<String, Long> getNextWords() {
        return this.nextWords;
    }

    // toString
    @Override
    public String toString() {
        return "SequenceNode{" +
                "precursorSequence='" + precursorSequence + '\'' +
                ", nextWords=" + nextWords +
                '}';
    }
}
