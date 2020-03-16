package wenjalan.starbot.nli;

import wenjalan.starbot.nli.test.StarbotNLIBot;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws LoginException {
        new StarbotNLIBot(args[0]);
    }
}
