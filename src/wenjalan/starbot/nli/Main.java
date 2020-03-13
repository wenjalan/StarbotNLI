package wenjalan.starbot.nli;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        List<String> corpus = new ArrayList<>();
        corpus.add("A 1 2 3 4 5 A");
        corpus.add("B 1 2 3 4 5 B");
        corpus.add("C 1 2 3 4 5 C");
        LanguageModel model = new LanguageModel.Builder().addCorpus(corpus).build();
        for (int i = 0; i < 1000; i++) {
            System.out.println("generated sequence: " + model.nextSequence());
        }
    }
}
