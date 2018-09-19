package me.misterfix.commands;

import me.misterfix.MessageFactory;
import me.misterfix.WebUtil;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuotesCommand extends Command {
    public QuotesCommand() {
        super(".quotes");
    }

    @Override
    protected void action(MessageReceivedEvent event, String[] args) {
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        if (channel.getName().equalsIgnoreCase("bot-commands")) {
            String json = null;
            boolean pending = false;
            if (args.length == 2) {
                if (args[1].equalsIgnoreCase("pending")) {
                    json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getAll.php?filter=pending");
                    pending = true;
                }
            } else {
                json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getAll.php?filter=accepted");
            }
            if (json == null) return;
            JSONArray response = new JSONArray(json);
            StringBuilder quotes = new StringBuilder();
            for (int i = 0; i < response.length(); i++) {
                JSONObject object = response.getJSONObject(i);
                quotes.append("`" + object.getInt("id") + "`. `\"" + object.getString("quote") + "\"` - " + object.getString("user_id") + "\n");
            }
            MessageFactory.createStandardMessage(member, "Codevision quotes" + (pending ? " (Pending)" : " (Accepted)"))
                .setDescription(quotes.toString())
                .queue(channel);
        } else {
            MessageFactory.createStandardMessage(member, "Please no")
                .setDescription("Spare us the spam, move to #bot-commands")
                .queue(channel);
        }
    }
}
