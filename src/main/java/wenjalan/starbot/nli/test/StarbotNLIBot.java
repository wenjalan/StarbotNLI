package wenjalan.starbot.nli.test;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import wenjalan.starbot.nli.LanguageModel;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

// a test JDA bot for the language modeling
public class StarbotNLIBot extends ListenerAdapter {

    // the JDA instance
    private JDA jda;

    // the LanguageModel
    private LanguageModel model = null;

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
            learn(event);
        }

        // sample command
        if (message.getContentRaw().startsWith("!sample")) {
            sample(event);
        }

        if (message.getContentRaw().startsWith("!clear")) {
            clear(event);
        }
    }

    // clear command
    private void clear(GuildMessageReceivedEvent event) {
        try {
            // get the arguments
            Scanner tokenScanner = new Scanner(event.getMessage().getContentRaw());
            int amount = 10;
            while (tokenScanner.hasNext()) {
                String token = tokenScanner.next();
                if (token.equalsIgnoreCase("-a")) {
                    amount = tokenScanner.nextInt();
                }
            }
            final int fAmount = amount;
            TextChannel channel = event.getChannel();
            List<Message> retrieved = new ArrayList<>();
            channel.getIterableHistory().forEachAsync((message) -> {
                if (message.getContentRaw().startsWith("!") || message.getAuthor().getIdLong() == event.getJDA().getSelfUser().getIdLong()) {
                    retrieved.add(message);
                }
                return retrieved.size() <= fAmount;
            }).thenRun(() -> {
                channel.deleteMessages(retrieved).queue();
            });
        } catch (Exception e) {
            event.getChannel().sendMessage("Error: " + e.getMessage()).queue();
        }
    }

    // sample command
    private void sample(GuildMessageReceivedEvent event) {
        try {
            // get the arguments
            Scanner tokenScanner = new Scanner(event.getMessage().getContentRaw());
            int amount = 10;
            int originality = 5;
            while (tokenScanner.hasNext()) {
                String token = tokenScanner.next();
                if (token.equalsIgnoreCase("-a")) {
                    amount = tokenScanner.nextInt();
                }
                else if (token.equalsIgnoreCase("-o")) {
                    originality = tokenScanner.nextInt();
                }
            }

            // build the message
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 0; i < amount; i++) {
                messageBuilder.append(model.nextSequence(originality));
                messageBuilder.append("\n");
            }

            // send
            event.getChannel().sendMessage(messageBuilder.toString()).queue();
        } catch (Exception e) {
            event.getChannel().sendMessage("Error: " + e.getMessage()).queue();
        }
    }

    // learn command
    private void learn(GuildMessageReceivedEvent event) {
        // the user that requested the learn command
        User author = event.getAuthor();
        // the guild we're learning off of
        Guild guild = event.getGuild();
        // the channel to send feedback to
        PrivateChannel dm = author.openPrivateChannel().complete();

        // find each channel in this guild
        List<TextChannel> textChannels = event.getGuild().getTextChannels();
        dm.sendMessage("found " + textChannels.size() + " text channels in guild " + guild.getName()).complete();

        // for each channel, scrape all the messages
        List<String> messages = new ArrayList<>();
        AtomicInteger channelsLearned = new AtomicInteger();
        textChannels.forEach((channel) -> {
            dm.sendMessage("learning from channel " + channel.getName() + "...").queue();
            AtomicInteger messageCount = new AtomicInteger();
            // read the messages
            try {
                channel.getIterableHistory().forEachAsync((message) -> {
                    // filters: bot author
                    if (message.getAuthor().isBot()) {
                        return true;
                    }

                    // filters: empty message, command, and single-user ping
                    String contentDisplay = message.getContentDisplay();
                    if (contentDisplay.isEmpty() || contentDisplay.startsWith("!") || (contentDisplay.startsWith("@") && !contentDisplay.contains(" "))) {
                        return true;
                    }

                    // add the content of the message to our list
                    messages.add(contentDisplay);
                    messageCount.getAndIncrement();

                    // continue
                    return true;
                }).thenRun(() -> {
                    // announce we're done
                    dm.sendMessage("finished learning from channel " + channel.getName() + ", found " + messageCount.get() + " messages").queue();
                    channelsLearned.getAndIncrement();

                    // if this was the last channel, make a model
                    if (channelsLearned.get() == textChannels.size()) {
                        dm.sendMessage("creating language model...").queue();
                        model = new LanguageModel.Builder()
                                .addCorpus(messages)
                                .build();
                        dm.sendMessage("language model created successfully").queue();
                    }
                });
            } catch (Exception e) {
                dm.sendMessage("error reading channel " + channel.getName() + ": " + e.getMessage()).queue();
                channelsLearned.getAndIncrement();
            }
        });
    }

}
