package wenjalan.starbot.nli.model;

import java.util.Random;

// generates sentence given a Model
public class SentenceGenerator {

    // the Model to use in generating sentences
    private Model model;

    // constructor
    public SentenceGenerator(Model model) {
        this.model = model;
    }

    // generates a new sentence
    public String generateSentence() {
        // StringBuilder to build the sentence
        StringBuilder sb = new StringBuilder();

        // run through the model
        Vertex currentVertex = model.startVertex();
        Random r = new Random();
        while (currentVertex != model.endVertex()) {
            // append this vertex's string to the sentence
            if (currentVertex != model.startVertex()) {
                sb.append(currentVertex.string()).append(" ");
            }

            // check: if there are no next edges, throw an exception
            if (currentVertex.edges().size() == 0) throw new IllegalStateException("Found vertex with no outgoing edges: " + currentVertex);

            // select the next vertex to traverse to
            double totalWeights = 0.0;
            for (Edge edge : currentVertex.edges()) {
                totalWeights += edge.weight();
            }
            // roll die
            double roll = totalWeights * r.nextDouble();
            for (Edge edge : currentVertex.edges()) {
                roll -= edge.weight();
                if (roll <= 0.0) {
                    currentVertex = edge.to();
                    break;
                }
            }
        }

        // return the sentence
        return sb.toString();
    }

}
