package wenjalan.starbot.nli.generators;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// represents a String in a histogram
public class Word implements Comparable<Word> {

    private final String string;

    private double weight;

    public Word(String string, double weight) {
        this.string = string;
        this.weight = weight;
    }

    public String getString() {
        return string;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(@NotNull Word o) {
        return (int) Math.signum(this.weight - o.weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(string, word.string);
    }

}
