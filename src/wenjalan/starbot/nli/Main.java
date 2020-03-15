package wenjalan.starbot.nli;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> corpus = new ArrayList<>();
        corpus.add("I typed using my fingers");
        corpus.add("I wrote using my hands");
        corpus.add("My hands typed on my keyboard");
        LanguageModel model = new LanguageModel.Builder().addCorpus(corpus).build();
        for (int i = 0; i < 1000; i++) {
            System.out.println(model.nextSequence(3));
        }
    }
}
