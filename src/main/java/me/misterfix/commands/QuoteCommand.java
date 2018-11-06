package me.misterfix.commands;

import me.misterfix.Main;
import me.misterfix.MessageFactory;
import me.misterfix.WebUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuoteCommand extends Command {
    public QuoteCommand() {
        super(".quote");
    }

    @Override
    protected void action(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        if (args.length == 1) {
            String json = WebUtil.getQuoteAPi(member, channel, "getQuote.php");
            if (json == null) return;
            JSONObject jsonObject = new JSONObject(json);
            MessageFactory.createStandardMessage(member, jsonObject.getString("user_id") + " once said")
                .setDescription("\"" + jsonObject.getString("quote") + "\"")
                .queue(channel);
        } else if (args.length == 2) {
            if (!args[1].equalsIgnoreCase("search")) {
                String json = WebUtil.getQuoteAPi(member, channel, "getQuote.php?id=" + args[1]);
                if (json == null) return;
                JSONObject jsonObject = new JSONObject(json);
                MessageFactory.createStandardMessage(member, jsonObject.getString("user_id") + " once said")
                    .setDescription("\"" + jsonObject.getString("quote") + "\"")
                    .queue(channel);
            } else {
                MessageFactory.createStandardMessage(member, "Whut " + Main.getEmote("feelsChromosomeMan"))
                    .setDescription("What are you trying to search for?\nUsage .quote search <pending> [search string]")
                    .queue(channel);
            }
        } else {
            if (channel.getName().equalsIgnoreCase("bot-commands")) {
                String json;
                boolean pending = false;
                //yeah ik ik
                String search = event.getMessage().getContentRaw().substring(".submit ".length()).replace("search ", "").trim();
                if (args[2].equalsIgnoreCase("pending")) {
                    if (args.length == 3) {
                        MessageFactory.createStandardMessage(member, "Wait whut " + Main.getEmote("robloxhead"))
                            .setDescription("What are you trying to search for?\nUsage .quote search <pending> [search string]")
                            .queue(channel);
                    }
                    //Don't worry i'm going to chemotherapy after this code
                    search = search.replace("pending", "").replace("pending ", "").trim();
                    json = WebUtil.getQuoteAPi(member, channel, "getQuote.php?filter=pending&search=" + WebUtil.urlenc(search));
                    pending = true;
                } else {
                    json = WebUtil.getQuoteAPi(member, channel, "getQuote.php?search=" + WebUtil.urlenc(search));
                }
                if (json == null) return;
                if (json.contains("No quotes")) {
                    MessageFactory.createStandardMessage(member, Main.getEmote("oof") + " nothing found")
                        .setDescription("No search results" + (pending ? " in pending quotes." : "."))
                        .queue(channel);
                    return;
                }
                JSONArray response = new JSONArray(json);
                StringBuilder quotes = new StringBuilder();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject object = response.getJSONObject(i);
                    quotes.append("`").append(object.getInt("id")).append("`. `\"").append(object.getString("quote"))
                            .append("\"` - ").append(object.getString("user_id")).append("\n");
                }
                MessageFactory.createStandardMessage(member, "Quote search results" + (pending ? " (Pending)" : ""))
                    .setDescription(quotes.toString())
                    .queue(channel);
            } else {
                MessageFactory.createStandardMessage(member, "Please no")
                    .setDescription("Spare us the spam, move to #bot-commands")
                    .queue(channel);
            }
        }
    }
}
