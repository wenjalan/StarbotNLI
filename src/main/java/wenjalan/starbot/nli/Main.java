package wenjalan.starbot.nli;

import wenjalan.starbot.nli.generators.SentenceGenerator;
import wenjalan.starbot.nli.model.Model;
import wenjalan.starbot.nli.model.factory.SentenceCorpus;
import wenjalan.starbot.nli.model.factory.SentenceModelFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        SentenceCorpus corpus = new SentenceCorpus(word -> {
            word = word.toLowerCase().trim();
            if (word.charAt(word.length() - 1) == ',' ||
                    word.charAt(word.length() - 1) == '.' ||
                    word.charAt(word.length() - 1) == '?' ||
                    word.charAt(word.length() - 1) == ';' ||
                    word.charAt(word.length() - 1) == ':' ||
                    word.charAt(word.length() - 1) == '\n') {
                word = word.substring(0, word.length() - 1);
            }
            return word;
        });

        try (FileReader reader = new FileReader(new File("hamlet.txt"))) {
            BufferedReader br = new BufferedReader(reader);
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.length() > 0 && line.split(" ").length > 1) {
                    corpus.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Model model = new SentenceModelFactory(corpus).buildModel();
        SentenceGenerator generator = new SentenceGenerator(model);
        for (int i = 0; i < 50; i++) {
            // System.out.println(generator.generateSentence(0.30));
            generator.generateSentence(0.10);
        }
    }
}
