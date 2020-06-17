package wenjalan.starbot.nli.model;

import java.util.HashMap;
import java.util.Map;

// the "Graph" object of the model, holds all the vertices and edges
public class Model {

    // the starting vertex, from which all sentences are started
    private final Vertex start;

    // the ending vertex, from which all sentences end
    private final Vertex end;

    // a map of Strings to their associated vertices, not including the terminal vertices
    private final Map<String, Vertex> vertices;

    // constructor
    public Model() {
        this.start = new Vertex(null);
        this.end = new Vertex(null);
        this.vertices = new HashMap<>();
    }

    // returns whether or not the graph contains a vertex given its String
    public boolean containsVertex(String string) {
        return vertices.containsKey(string);
    }

    // adds a Vertex to the graph
    public void addVertex(Vertex v) {
        String string = v.string();
        if (containsVertex(string)) {
            throw new IllegalArgumentException("Model already contains a Vertex for String " + string);
        }
        vertices.put(string, v);
    }

    // returns a Vertex given a String
    public Vertex getVertex(String string) {
        return vertices.get(string);
    }

    // returns the starting node
    public Vertex startVertex() {
        return start;
    }

    // returns the end vertex
    public Vertex endVertex() {
        return end;
    }

}
