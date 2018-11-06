package me.misterfix;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class MessageFactory {
    private Message message;

    private final EmbedBuilder builder = new EmbedBuilder();
    private int selfDestruct = 0; //TODO never read - probable bug?

    public MessageFactory(String description) {
        setDescription(description);
    }

    public MessageFactory() { }

    public MessageFactory setTitle(String title, String url) {
        builder.setTitle(title, url);
        return this;
    }

    public MessageFactory setDescription(String description) {
        builder.setDescription(description);
        return this;
    }

    public MessageFactory setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public MessageFactory setImage(String image) {
        builder.setImage(image);
        return this;
    }

    public MessageFactory setColor(Color color) {
        builder.setColor(color);
        return this;
    }

    public MessageFactory addField(String name, String value, boolean inLine) {
        builder.addField(name, value, inLine);
        return this;
    }

    public MessageFactory addThumbnail(String url) {
        builder.setThumbnail(url);
        return this;
    }

    public MessageFactory setAuthor(String name, String url, String iconURL) {
        builder.setAuthor(name, url, iconURL);
        return this;
    }

    public MessageFactory setTimestamp() {
        builder.setTimestamp(OffsetDateTime.now());
        return this;
    }

    public MessageFactory setAuthor(String name, String iconURL) {
        builder.setAuthor(name, iconURL, iconURL);
        return this;
    }

    public MessageFactory queue(TextChannel channel, Consumer<Message> success) {
        try {
            success.andThen(successMessage -> this.message = successMessage);
            channel.sendMessage(builder.build()).queue(success);
        } catch (PermissionException ex) {
            System.out.println("I do not have permission: " + ex.getPermission().getName() + " on server " + channel.getGuild().getName() + " in channel: " + channel.getName());
        }
        return this;
    }

    public void queue(TextChannel channel) {
        try {
            channel.sendMessage(builder.build()).queue(successMessage -> this.message = successMessage);
        } catch (PermissionException ex) {
            System.out.println("I do not have permission: " + ex.getPermission().getName() + " on server " + channel.getGuild().getName() + " in channel: " + channel.getName());
        }
    }

    public Message complete(TextChannel channel) {
        try {
            message = channel.sendMessage(builder.build()).complete();
            return message;
        } catch (PermissionException ex) {
            System.out.println("I do not have permission: " + ex.getPermission().getName() + " on server " + channel.getGuild().getName() + " in channel: " + channel.getName());
            return null;
        }
    }

    public void send(User member) {
        member.openPrivateChannel().complete().sendMessage(builder.build()).queue();
    }

    public void send(User member, Consumer<Message> success) {
        success.andThen(msg -> this.message = msg);
        member.openPrivateChannel().queue(channel -> channel.sendMessage(builder.build()).queue(success));
    }

    public MessageFactory sendUser(User member) {
        member.openPrivateChannel().complete().sendMessage(builder.build()).queue(message1 -> this.message = message1);
        return this;
    }

    public MessageFactory addReaction(String reaction) {
        if (message == null) {
            throw new NullPointerException("Message must not be null!");
        }
        message.addReaction(reaction).queue();
        return this;
    }

    public MessageFactory selfDestruct(int time) {
        this.selfDestruct = time;
        return this;
    }

    public MessageEmbed build() {
        return builder.build();
    }

    public MessageFactory setThumbnail(String thumbnail) {
        builder.setThumbnail(thumbnail);
        return this;
    }

    public MessageFactory footer(String s, String iconURL) {
        builder.setFooter(s, iconURL);
        return this;
    }

    public static MessageFactory create() {
        return new MessageFactory();
    }

    public static MessageFactory create(String description) {
        return new MessageFactory(description);
    }

    public static void sendPlainMessage(String message, TextChannel channel) {
        if (channel.canTalk()) {
            channel.sendMessage(message).queue();
        } else {
            System.out.println("No permission to speak in " + channel.getName() + " channel on " + channel.getGuild().getName() + " guild.");
        }
    }

    public static MessageFactory createStandardMessage(Member member) {
        return create()
            .footer("Requested by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), member.getUser().getEffectiveAvatarUrl())
            .setTimestamp()
            .setColor(member.getColor());
    }

    public static MessageFactory createStandardMessage(Member member, String title) {
        return create()
            .setTitle(title)
            .footer("Requested by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), member.getUser().getEffectiveAvatarUrl())
            .setTimestamp()
            .setColor(member.getColor());
    }

    public static void createStandardMessage(Member member, TextChannel channel) {
        create()
            .footer("Requested by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), member.getUser().getEffectiveAvatarUrl())
            .setTimestamp()
            .setColor(member.getColor())
            .queue(channel);
    }

    public static void createStandardMessage(Member member, String title, TextChannel channel) {
        create()
            .setTitle(title)
            .footer("Requested by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator(), member.getUser().getEffectiveAvatarUrl())
            .setTimestamp()
            .setColor(member.getColor())
            .queue(channel);
    }
}