package wenjalan.starbot.nli;

import wenjalan.starbot.nli.model.Edge;
import wenjalan.starbot.nli.model.Model;
import wenjalan.starbot.nli.model.SentenceGenerator;
import wenjalan.starbot.nli.model.Vertex;
import wenjalan.starbot.nli.test.StarbotNLIBot;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws LoginException {
        // new StarbotNLIBot(args[0]);
        Model model = new Model();
        Vertex a = new Vertex("a");
        Vertex b = new Vertex("b");
        Vertex c = new Vertex("c");
        model.startVertex().addEdge(a, 1.0);
        a.addEdge(a, 10.0);
        a.addEdge(b, 1.0);
        a.addEdge(c, 1.0);
        b.addEdge(model.endVertex(), 1.0);
        c.addEdge(model.endVertex(), 1.0);

        SentenceGenerator generator = new SentenceGenerator(model);
        for (int i = 0; i < 50; i++) {
            System.out.println(generator.generateSentence());
        }
    }
}
