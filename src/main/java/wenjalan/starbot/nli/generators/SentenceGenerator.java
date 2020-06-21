package wenjalan.starbot.nli.generators;

import wenjalan.starbot.nli.model.Edge;
import wenjalan.starbot.nli.model.Model;
import wenjalan.starbot.nli.model.Vertex;

import javax.annotation.Nullable;
import java.util.*;

// generates sentence given a Model
public class SentenceGenerator {

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
        Map<String, Double> histogram = null;

        // the last word we added to the sentence
        String lastWord = "";

        // keep looping while the lastWord we got isn't null, which indicates the end of the sentence
        while (lastWord != null) {
            // add the lastWord to the builder
            sb.append(lastWord).append(" ");

            // get a prediction for the next word
            histogram = predictNextWord(histogram, lastWord);

            // choose a word from that histogram
            lastWord = getWordFromHistogram(histogram, originality);
        }

        // return the sentence
        return sb.toString().trim();
    }

    // randomly selects a word from a given histogram
    // randomness: the degree to which to randomize the word chosen, weighted by their likelihood
    private String getWordFromHistogram(Map<String,Double> histogram, double randomness) {
        // trim the histogram based on randomness
        Map<String, Double> weightedHistogram = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
        int selectionSpace = (int) (histogram.size() * randomness);
        // bound between 1 and the size of the original histogram
        selectionSpace = Math.max(selectionSpace, 1);
        selectionSpace = Math.min(selectionSpace, histogram.size());

        // create a copy of the histogram to destroy
        Map<String, Double> copy = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
        copy.putAll(histogram);

        // from greatest to least weight, add selectionSpace items to the weighted histogram
        for (int i = 0; i < selectionSpace; i++) {
            Map.Entry<String, Double> max = copy.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();
            copy.remove(max.getKey());
            weightedHistogram.put(max.getKey(), max.getValue());
        }

        // select a word from the weighted histogram
        double totalWeight = weightedHistogram.values().stream().mapToDouble(x -> x).sum();
        double roll = new Random().nextDouble() * totalWeight;
        double originalRoll = roll;
        for (Map.Entry<String, Double> e : weightedHistogram.entrySet()) {
            roll -= e.getValue();
            if (roll <= 0) {
                return e.getKey();
            }
        }
        throw new IllegalStateException("Encountered an error while choosing a word from the histogram (histogram size:" + histogram.size() + ", weight:" + totalWeight + ", roll:" + originalRoll + ")");
    }

    // predicts the next word given the previous guess and the true last word
    private Map<String, Double> predictNextWord(@Nullable Map<String, Double> lastGuessHistogram, String lastWord) {
        // if the last guess is null, return the histogram for the lastWord
        if (lastGuessHistogram == null) {
            return nextWordsHistogram(null);
        }

        // otherwise, combine the results of the best predicted word in the lastGuestHistogram and the lastWord histogram
        String lastBestGuess = lastGuessHistogram.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getKey();
        Map<String, Double> guessHistogram = nextWordsHistogram(lastBestGuess);
        Map<String, Double> lastWordHistogram = nextWordsHistogram(lastWord);

        // combine the maps
        for (Map.Entry<String, Double> e : guessHistogram.entrySet()) {
            String word = e.getKey();
            if (!lastWordHistogram.containsKey(word)) {
                lastWordHistogram.put(word, e.getValue());
            }
            else {
                if (word != null) {
                    lastWordHistogram.put(word, (lastWordHistogram.get(word) * 0.5 + guessHistogram.get(word)));
                }
            }
        }

        // return our prediction
        return lastWordHistogram;
    }

    // provides a histogram of possible next words given a word
    // if word is null, assumes word is the start of a sentence sentinel
    private Map<String, Double> nextWordsHistogram(@Nullable String word) {
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
        Map<String, Double> histogram = new TreeMap<>(Comparator.nullsFirst(Comparator.naturalOrder()));
        double totalWeights = v.totalEdgeWeights();
        for (Edge edge : v.edges()) {
            double relativeWeight = edge.weight() / totalWeights;
            histogram.put(edge.to().string(), relativeWeight);
        }

        // return the histogram
        return histogram;
    }

}
