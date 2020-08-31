package wenjalan.starbot.nli.test;

import wenjalan.starbot.nli.BiGramPredictor;

import java.io.File;
import java.io.IOException;

import static wenjalan.starbot.nli.test.NaturalLanguageEngineTests.testFiles;

public class BiGramPredictorTest {

    public static void main(String[] args) throws IOException {
        initTest();
    }

    private static void initTest() throws IOException {
        BiGramPredictor predictor = new BiGramPredictor(testFiles);
        System.out.println(predictor.bigrams());
    }

}
