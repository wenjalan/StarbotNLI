package wenjalan.starbot.nli.test;

import wenjalan.starbot.nli.NaturalLanguageEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NaturalLanguageEngineTests {

//    static File[] testFiles = {new File("test.txt"), new File("test2.txt")};
    static File[] testFiles = {new File("general.txt")};

    static NaturalLanguageEngine engine;

    public static void main(String[] args) {
        initTest();
        predictSentenceTest();
    }

    private static void predictSentenceTest() {
        List<String> sentences = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sentences.add(engine.generateSentence());
        }
        for (int j = 0; j < sentences.size(); j++) {
            System.out.println(j + 1 + ": " + sentences.get(j));
        }
    }

    private static void initTest() {
        engine = new NaturalLanguageEngine(testFiles);
    }

}
