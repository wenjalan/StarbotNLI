package wenjalan.starbot.nli.model;

// represents a relationship between two Vertices
// undirected, weighted
public class Edge {

    // the Vertex this edge comes from and goes to
    final private Vertex from;
    final private Vertex to;

    // the weight of this edge
    // higher weight means stronger connection
    private double weight;

    // constructor
    public Edge(Vertex from, Vertex to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Vertex from() {
        return from;
    }

    public Vertex to() {
        return to;
    }

    public double weight() {
        return weight;
    }

    public void updateWeight(double weight) {
        this.weight = weight;
    }

}
