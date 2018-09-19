package me.misterfix.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {
    private final String trigger;

    public Command(String trigger) {
        this.trigger = trigger;
    }

    protected abstract void action(MessageReceivedEvent event, String[] args);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msgRaw = event.getMessage().getContentRaw();
        if (msgRaw.startsWith(trigger)) {
            action(event, msgRaw.split(" "));
        }
    }
}
