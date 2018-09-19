package me.misterfix.commands;

import me.misterfix.MessageFactory;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InfoCommand extends Command {
    public InfoCommand() {
        super(".info");
    }

    @Override
    protected void action(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        MessageFactory.createStandardMessage(member, "InspireMe: info")
            .setDescription("Follow the development of this garbage at https://github.com/MuteVision/InspireMe\n" +
                "i dunno what else to put here.")
            .queue(channel);
    }
}
