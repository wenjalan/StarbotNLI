package wenjalan.starbot.nli.model;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

// represents a single term
public class Vertex {

    // the String of characters this Vertex represents
    final private String string;

    // the List of outgoing Edges this Vertex has
    final private List<Edge> edges;

    // constructor
    // string: the string this Vertex represents
    public Vertex(String string) {
        this.string = string;
        this.edges = new LinkedList<>();
    }

    // adds a new edge to this Vertex
    public void addEdge(@Nonnull Vertex to, double weight) {
        this.edges.add(new Edge(this, to, weight));
    }

    // removes an edge from this Vertex
    // returns: whether or not the edge was successfully removed
    public boolean removeEdge(@Nonnull Edge e) {
        return this.edges.remove(e);
    }

    // returns the list of edges outgoing from this vertex
    public List<Edge> edges() {
        return List.copyOf(edges);
    }

    // returns the String associated with this vertex
    public String string() {
        return this.string;
    }

    // toString
    @Override
    public String toString() {
        return "Vertex{" +
                "string='" + string + '\'' +
                ", edges=" + edges +
                '}';
    }
}
