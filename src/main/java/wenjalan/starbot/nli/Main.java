package wenjalan.starbot.nli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException  {
        LanguageModel model = LanguageModel.importFromFile(new File("model.json"));
        for (int i = 0; i < 50; i++) {
            System.out.println(model.nextSequence(1));
        }
    }
}
