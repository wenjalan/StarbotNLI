package wenjalan.starbot.nli.model;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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

    // returns whether or not this Vertex has an outgoing edge to another Vertex
    public boolean hasEdgeTo(Vertex other) {
        for (Edge edge : edges) {
            if (edge.to().equals(other)) {
                return true;
            }
        }
        return false;
    }

    // returns the Edge from this Vertex to another Vertex, null if none
    public Edge getEdgeTo(Vertex other) {
        for (Edge edge : edges) {
            if (edge.to().equals(other)) {
                return edge;
            }
        }
        return null;
    }

    // returns the String associated with this vertex
    public String string() {
        return this.string;
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        if (string == null || vertex.string == null) return false; // special case: start and end vertices
        return string.equals(vertex.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
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
