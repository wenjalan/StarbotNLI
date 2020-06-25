package wenjalan.starbot.nli.generators;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Histogram implements Iterable<String> {

    private static final String NULL_STRING = "" + (char) 255;

    private PriorityQueue<String> priorityQueue;

    private Map<String, Double> weightMap;

    public Histogram() {
        this.weightMap = new HashMap<>();
        this.priorityQueue = new PriorityQueue<>((o1, o2) -> {
            try {
                double weight1 = o1.equals(NULL_STRING) ? weightMap.get(null) : weightMap.get(o1);
                double weight2 = o2.equals(NULL_STRING) ? weightMap.get(null) : weightMap.get(o2);
                return (int) Math.signum(weight1 - weight2);
            } catch (Exception e) {
                System.err.println("NullPointer on o1:" + o1 + " and o2:" + o2);
                System.err.println("weightMap.contains(o1):" + weightMap.containsKey(o1));
                System.err.println("weightMap.contains(o2):" + weightMap.containsKey(o2));
                e.printStackTrace();
                System.exit(1);
                return 0;
            }
        });
    }

    public void addWord(String string, double weight) {
        weightMap.put(string, weight);
        string = string == null ? NULL_STRING : string;
        priorityQueue.remove(string);
        priorityQueue.add(string);
    }

    public void removeWord(String string) {
        priorityQueue.remove(string);
        if (string == null) {
            priorityQueue.remove(NULL_STRING);
        }
        else {
            weightMap.remove(string);
        }
    }

    public boolean contains(String string) {
        return weightMap.containsKey(string);
    }

    public Map.Entry<String, Double> removeMin() {
        String minWord = priorityQueue.remove();
        double minWeight = weightMap.remove(minWord);
        return new Map.Entry<String, Double>() {
            @Override
            public String getKey() {
                return minWord;
            }

            @Override
            public Double getValue() {
                return minWeight;
            }

            @Override
            public Double setValue(Double value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int size() {
        return weightMap.size();
    }

    public double weight(String word) {
        return weightMap.get(word);
    }

    public double totalWeight() {
        return weightMap.values().stream().mapToDouble(x -> x).sum();
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return weightMap.keySet().iterator();
    }

}
