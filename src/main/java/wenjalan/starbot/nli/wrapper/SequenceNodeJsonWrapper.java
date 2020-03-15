package wenjalan.starbot.nli.wrapper;

import wenjalan.starbot.nli.SequenceNode;

import java.util.Map;

// JSON wrapper for the SequenceNode class, used in conjunction with the LanguageModelJsonWrapper class
public class SequenceNodeJsonWrapper {

    // the precursor sequence this node leads up to
    private final String precursorSequence;

    // the list of next words this node could have, mapped to their occurrences
    private final Map<String, Long> nextWords;

    // constructor
    public SequenceNodeJsonWrapper(SequenceNode node) {
        this.precursorSequence = node.getPrecursorSequence();
        this.nextWords = node.getNextWords();
    }

    // getters
    public String getPrecursorSequence() {
        return precursorSequence;
    }

    public Map<String, Long> getNextWords() {
        return nextWords;
    }
}
