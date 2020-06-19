package wenjalan.starbot.nli.model.factory;

import wenjalan.starbot.nli.model.Edge;
import wenjalan.starbot.nli.model.Model;
import wenjalan.starbot.nli.model.Vertex;

import java.util.LinkedList;
import java.util.List;

// creates a Model given a SentenceCorpus
public class SentenceModelFactory {

    // the corpi to use in creating the model
    List<SentenceCorpus> corpi;

    // constructor
    public SentenceModelFactory() {
        this.corpi = new LinkedList<>();
    }

    // constructor: with a corpus
    public SentenceModelFactory(SentenceCorpus corpus) {
        this();
        corpi.add(corpus);
    }

    // constructor: with many corpi
    public SentenceModelFactory(List<SentenceCorpus> corpi) {
        this.corpi = corpi;
    }

    // adds a corpus to the factory
    public void addCorpus(SentenceCorpus corpus) {
        this.corpi.add(corpus);
    }

    // removes a corpus from the factory
    public boolean removeCorpus(SentenceCorpus corpus) {
        return this.corpi.remove(corpus);
    }

    // creates the model from the corpi provided to the factory
    public Model buildModel() {
        // check that there's at least one corpus to read from
        if (corpi.size() == 0) {
            throw new IllegalStateException("No corpi have been added to the factory");
        }

        // create the model
        Model model = new Model();
        // read each corpus and add vertices for each word
        for (SentenceCorpus corpus : corpi) {
            // for each sentence in this corpus
            for (String sentence : corpus.sentences()) {
                // get the words in this sentence
                String[] words = sentence.trim().split("\\s+");

                // attach the first word to the end of the model's starting vertex
                // if the model hasn't seen this word before, add it
                if (!model.containsVertex(words[0])) {
                    // add it
                    model.addVertex(new Vertex(words[0]));
                }

                // get the Vertex for the first word
                Vertex first = model.getVertex(words[0]);

                // if the start Vertex has an edge to this word, increase that edge's weight
                if (!model.startVertex().hasEdgeTo(first)) {
                    model.startVertex().addEdge(first, 0.0);
                }
                Edge edge = model.startVertex().getEdgeTo(first);
                edge.updateWeight(edge.weight() + 1.0);

                // perform a similar process with the rest of the words in the sentence
                for (int i = 0; i < words.length - 1; i++) {
                    String fromWord = words[i];
                    String toWord = words[i + 1];

                    // check if the toWord is already in the model, add it if not
                    // fromWord should already be in the model if everything goes right
                    if (!model.containsVertex(toWord)) {
                        model.addVertex(new Vertex(toWord));
                    }

                    // get the from and to vertices
                    Vertex from = model.getVertex(fromWord);
                    Vertex to = model.getVertex(toWord);

                    // update their relationship
                    if (!from.hasEdgeTo(to)) {
                        from.addEdge(to, 0.0);
                    }
                    Edge e = from.getEdgeTo(to);
                    e.updateWeight(e.weight() + 1.0);
                }

                // have an edge of the last node point to the end node
                Vertex last = model.getVertex(words[words.length - 1]);
                if (!last.hasEdgeTo(model.endVertex())) {
                    last.addEdge(model.endVertex(), 0.0);
                }
                Edge edge_ = last.getEdgeTo(model.endVertex());
                edge_.updateWeight(edge_.weight() + 1.0);
            }
        }
        // return the finished model
        return model;
    }

}
