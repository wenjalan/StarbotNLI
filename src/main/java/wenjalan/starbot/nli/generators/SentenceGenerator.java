package wenjalan.starbot.nli.generators;

import wenjalan.starbot.nli.model.Edge;
import wenjalan.starbot.nli.model.Model;
import wenjalan.starbot.nli.model.Vertex;

import javax.annotation.Nullable;
import java.util.*;

// generates sentence given a Model
public class SentenceGenerator {

    // the weight of recurrence histograms
    public static final double RECURRENCE_WEIGHT = 0.25;

    // the Model to use in generating sentences
    private final Model model;

    // constructor
    public SentenceGenerator(Model model) {
        this.model = model;
    }

    // generates a new sentence
    // originality: a value from 0 (low) to 1 (high) that determines the strictness of adherence to the corpus
    public String generateSentence(double originality) {
        // StringBuilder to build the sentence
        StringBuilder sb = new StringBuilder();

        // the last Histogram we used
        Histogram histogram = null;

        // the last word we added to the sentence
        String lastWord = "";

        // keep looping while the lastWord we got isn't null, which indicates the end of the sentence
        while (lastWord != null) {
            // add the lastWord to the builder
            sb.append(lastWord).append(" ");
            System.out.print(lastWord + " ");

            // get a prediction for the next word
            histogram = predictNextWord(histogram, lastWord, originality);

            // choose a word from that histogram
            lastWord = getWordFromHistogram(histogram, originality);
        }

        // return the sentence
        System.out.print("\n\n");
        return sb.toString().trim();
    }

    // randomly selects a word from a given histogram
    // randomness: the degree to which to randomize the word chosen, weighted by their likelihood
    // post: histogram is pruned
    private String getWordFromHistogram(Histogram histogram, double randomness) {
        int selectionSpace = (int) (histogram.size() * randomness);
        // bound between 1 and the size of the original histogram
        selectionSpace = Math.max(selectionSpace, 1);
        selectionSpace = Math.min(selectionSpace, histogram.size());

        // prune
        pruneHistogram(histogram, selectionSpace);

        // select a word from the weighted histogram
        double totalWeight = histogram.totalWeight();
        double roll = new Random().nextDouble() * totalWeight;
        double originalRoll = roll;
        for (String word : histogram) {
            roll -= histogram.weight(word);
            if (roll <= 0) {
                return word;
            }
        }
        throw new IllegalStateException("Encountered an error while choosing a word from the histogram (histogram size:" + histogram.size() + ", weight:" + totalWeight + ", roll:" + originalRoll + ")");
    }

    // returns a histogram choosing containing only the top n elements
    // also removes all entries with a value of 0
    private void pruneHistogram(Histogram histogram, int n) {
        for (int i = 0; i < n; i++) {
            histogram.removeMin();
        }
    }

    // predicts the next word given the previous guess and the true last word
    private Histogram predictNextWord(@Nullable Histogram lastGuessHistogram, String lastWord, double randomness) {
        // if the last guess is null, return the histogram for the lastWord
        if (lastGuessHistogram == null) {
            return nextWordsHistogram(null);
        }

        // trim the lastGuessHistogram down based on randomness
        int selectionSpace = (int) (lastGuessHistogram.size() * randomness);
        // bound between 1 and the size of the original histogram
        selectionSpace = Math.max(selectionSpace, 1);
        selectionSpace = Math.min(selectionSpace, lastGuessHistogram.size());

        // prune
        pruneHistogram(lastGuessHistogram, selectionSpace);

        // combine the results of the best predicted words in the lastGuestHistogram and the lastWord histogram
        List<Histogram> lastHistograms = new LinkedList<>();
        for (String word : lastGuessHistogram) {
            lastHistograms.add(nextWordsHistogram(word));
        }
        Histogram lastWordHistogram = nextWordsHistogram(lastWord);

        // weight of all lastHistograms
        double lastHistogramsWeight = (1.0 / lastHistograms.size()) * RECURRENCE_WEIGHT;

        // combine the maps
        for (Histogram lastHistogram : lastHistograms) {
            for (String word : lastHistogram) {
                if (!lastWordHistogram.contains(word)) {
                    lastWordHistogram.addWord(word, lastHistogram.weight(word));
                }
                else {
                    // if (word != null) {
                        lastWordHistogram.addWord(word, lastWordHistogram.weight(word) + lastHistogram.weight(word) * lastHistogramsWeight);
                    // }
                }
            }
        }

        // return our prediction
        return lastWordHistogram;
    }

    // provides a histogram of possible next words given a word
    // if word is null, assumes word is the start of a sentence sentinel
    private Histogram nextWordsHistogram(@Nullable String word) {
        // get the vertex associated with this word
        Vertex v;
        if (word == null) {
            v = model.startVertex();
        }
        else {
            if (!model.containsVertex(word)) {
                throw new IllegalArgumentException("Model does not contain a vertex for word " + word);
            }
            v = model.getVertex(word);
        }

        // create a histogram
        // Map<String, Double> histogram = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
        Histogram histogram = new Histogram();
        double totalWeights = v.totalEdgeWeights();
        for (Edge edge : v.edges()) {
            double relativeWeight = edge.weight() / totalWeights;
            histogram.addWord(edge.to().string(), relativeWeight);
        }

        // return the histogram
        return histogram;
    }

}
