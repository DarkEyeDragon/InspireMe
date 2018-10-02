package me.misterfix.commands;

import me.misterfix.Main;
import me.misterfix.MessageFactory;
import me.misterfix.WebUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SubmitCommand extends Command {
    public SubmitCommand() {
        super(".submit");
    }

    @Override
    protected void action(MessageReceivedEvent event, String[] args) {
        String msgRaw = event.getMessage().getContentRaw();
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        if (args.length > 1) {
        	msgRaw = msgRaw.substring(".submit ".length());
            if (msgRaw.length() > 250) {
                MessageFactory.createStandardMessage(member, "U w0t m8")
                    .setDescription("Why are you trying to write an essay? A quote shouldn't be longer than 250 characters.")
                    .queue(channel);
                return;
            }
            String name;
            try {
                name = URLEncoder.encode(member.getUser().getName() + "#" + member.getUser().getDiscriminator(), StandardCharsets.UTF_8.displayName());
                String quote = URLEncoder.encode(msgRaw, StandardCharsets.UTF_8.displayName());
                String json = WebUtil.getQuoteAPi(member, channel, "submit.php?user_id=" + name + "&quote=" + quote + "&responseType=json");
                if (json == null) return;
                JSONObject response = new JSONObject(json);
                if (response.getInt("response") == 200) {
                    MessageFactory.createStandardMessage(member, ":white_check_mark: Success")
                        .setDescription("Quote submitted for review.")
                        .queue(channel);
                    System.out.println(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " Submitted a quote for review.");
                } else {
                    MessageFactory.createStandardMessage(member, Main.getEmote("robloxhead") + " Oof")
                        .setDescription("Something went wrong while trying to submit the quote #DarkEyeDragon")
                        .queue(channel);
                    System.out.println(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " tried to submit a quote but something went wrong in the request.");
                    System.out.println("DEBUG: " + json);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            MessageFactory.createStandardMessage(member, ":x: Derp")
                .setDescription("Usage: .submit <quote to submit>.")
                .queue(channel);
        }
    }
}
