package wenjalan.starbot.nli.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;

// a test JDA bot for the language modeling
public class StarbotNLIBot extends ListenerAdapter {

    // the JDA instance
    private JDA jda;

    // the LanguageModel
    // private LanguageModel model = null;

    // constructor
    public StarbotNLIBot(String token) throws LoginException {
        // create new JDA instance
        this.jda = new JDABuilder()
                .setToken(token)
                .addEventListeners(this)
                .build();
    }

    // onGuildMessageReceived
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        // message filters
        User author = event.getAuthor();
        Message message = event.getMessage();

        // if bot, return
        if (author.isBot()) {
            return;
        }

        // learn command
        if (message.getContentRaw().startsWith("!learn")) {
            // learn(event);
        }

        // sample command
        if (message.getContentRaw().startsWith("!sample")) {
            // sample(event);
        }

        if (message.getContentRaw().startsWith("!clear")) {
            // clear(event);
        }
    }

}
