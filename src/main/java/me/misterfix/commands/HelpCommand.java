package me.misterfix.commands;

import me.misterfix.MessageFactory;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand extends Command {
    public HelpCommand() {
        super(".help");
    }

    @Override
    protected void action(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        if (channel.getName().equalsIgnoreCase("bot-commands")) {
            MessageFactory.createStandardMessage(member, "InspireMe: Help")
                .setDescription("`.quote` - Get a random quote.\n" +
                    "`.quote <id>` - Get a quote by its ID.\n" +
                    "`.submit <text>` - Submit a quote for review.\n" +
                    "`.quote search <author>` - Search quotes by author name (name must not be exact)\n" +
                    "`.quote search pending <author>` - Search pending quotes by author\n" +
                    "`quotes` - List all available quotes\n" +
                    "`.quotes pending` - List all pending quotes\n" +
                    "`.help` - Display this help page\n\n" +
                    "Made by Mister_Fix with <3")
                .queue(channel);
        } else {
            MessageFactory.createStandardMessage(member, "Please no")
                .setDescription("Spare us the spam, move to #bot-commands")
                .queue(channel);
        }
    }
}
