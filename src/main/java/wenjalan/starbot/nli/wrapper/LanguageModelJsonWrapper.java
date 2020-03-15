package wenjalan.starbot.nli.wrapper;

import wenjalan.starbot.nli.LanguageModel;
import wenjalan.starbot.nli.SequenceNode;

import java.util.Map;
import java.util.TreeMap;

// a wrapper for the LanguageModel class for Gson exporting and importing
public class LanguageModelJsonWrapper {

    // the map of sequences to next words
    private final Map<String, SequenceNodeJsonWrapper> nodes;

    // constructor
    // model: the LanguageModel to mimic
    public LanguageModelJsonWrapper(LanguageModel model) {
        this.nodes = new TreeMap<>();
        Map<String, SequenceNode> modelNodes = model.getNodes();
        modelNodes.forEach((sequence, node) -> {
            this.nodes.put(sequence, new SequenceNodeJsonWrapper(node));
        });
    }

    // getters
    public Map<String, SequenceNodeJsonWrapper> getNodes() {
        return nodes;
    }
}
