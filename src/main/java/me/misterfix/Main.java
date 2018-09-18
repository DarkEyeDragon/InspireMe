package me.misterfix;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main extends ListenerAdapter {
    private static JDA jda;
    public static void main(String[] args) throws LoginException, InterruptedException {
        jda = new JDABuilder(AccountType.BOT)
                .setGame(Game.of(Game.GameType.WATCHING, "kids argue over their favorite programming language on the internet"))
                .setToken("")
                .setAutoReconnect(true)
                .setAudioEnabled(false)
                .setEnableShutdownHook(false)
                .setStatus(OnlineStatus.ONLINE)
                .build()
                .awaitReady();
        jda.addEventListener(new Main());
    }

    private static String getEmote(String name) {
        Guild guild = jda.getGuildsByName("Codevision", false).get(0);
        List<Emote> emotes = guild.getEmotesByName(name, false);
        return emotes.get(0).getAsMention();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msgRaw = event.getMessage().getContentRaw();
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        if (msgRaw.startsWith(".")){
            String[] command = msgRaw.split(" ");
            if(command[0].equalsIgnoreCase(".quote")){
                if(command.length == 1){
                    String json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getQuote.php");
                    if(json == null) return;
                    JSONObject jsonObject = new JSONObject(json);
                    MessageFactory.createStandardMessage(member, jsonObject.getString("user_id")+" once said")
                            .setDescription("\""+jsonObject.getString("quote")+"\"")
                            .queue(channel);
                }
                else if(command.length == 2){
                    if(!command[1].equalsIgnoreCase("search")) {
                        String json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getQuote.php?id=" + command[1]);
                        if (json == null) return;
                        JSONObject jsonObject = new JSONObject(json);
                        MessageFactory.createStandardMessage(member, jsonObject.getString("user_id") + " once said")
                                .setDescription("\"" + jsonObject.getString("quote") + "\"")
                                .queue(channel);
                    } else {
                        MessageFactory.createStandardMessage(member, "Whut " + getEmote("feelsChromosomeMan"))
                                .setDescription("What are you trying to search for?\nUsage .quote search <pending> [search string]")
                                .queue(channel);
                    }
                }
                else if(command.length >= 3){
                    if (channel.getName().equalsIgnoreCase("bot-commands")) {
                        String json;
                        boolean pending = false;
                        //yeah ik ik
                        String search = msgRaw.replace(".quote ", "").replace("search ", "").trim();
                        if (command[2].equalsIgnoreCase("pending")) {
                            if (command.length == 3) {
                                MessageFactory.createStandardMessage(member, "Wait whut " + getEmote("robloxhead"))
                                        .setDescription("What are you trying to search for?\nUsage .quote search <pending> [search string]")
                                        .queue(channel);
                            }
                            //Don't worry i'm going to chemotherapy after this code
                            search = search.replace("pending", "").replace("pending ", "").trim();
                            json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getQuote.php?search=" + WebUtil.urlenc(search) + "&filter=pending");
                            pending = true;
                        } else {
                            json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getQuote.php?search=" + WebUtil.urlenc(search));
                        }
                        if (json == null) return;
                        if (json.contains("No quotes")) {
                            MessageFactory.createStandardMessage(member, getEmote("oof") + " nothing found")
                                    .setDescription("No search results" + (pending ? " in pending quotes." : "."))
                                    .queue(channel);
                            return;
                        }
                        JSONArray response = new JSONArray(json);
                        StringBuilder quotes = new StringBuilder();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            quotes.append("`" + object.getInt("id") + "`. `\"" + object.getString("quote") + "\"` - " + object.getString("user_id") + "\n");
                        }
                        MessageFactory.createStandardMessage(member, "Quote search results" + (pending ? " (Pending)" : ""))
                                .setDescription(quotes.toString())
                                .queue(channel);
                    }
                    else{
                        MessageFactory.createStandardMessage(member,"Please no")
                                .setDescription("Spare us the spam, move to #bot-commands")
                                .queue(channel);
                    }
                }
            }
            else if (command[0].equalsIgnoreCase(".submit")){
                if(command.length > 1) {
                    if (msgRaw.replaceFirst(".submit ", "").length() > 250) {
                        MessageFactory.createStandardMessage(member, "U w0t m8")
                                .setDescription("Why are you trying to write an essay? a quote shouldn't be longer than 250 characters.")
                                .queue(channel);
                        return;
                    }
                    String name;
                    try {
                        name = URLEncoder.encode(member.getUser().getName()+"#"+member.getUser().getDiscriminator(), StandardCharsets.UTF_8.displayName());
                        String quote = URLEncoder.encode(msgRaw.replaceFirst(".submit ", ""), StandardCharsets.UTF_8.displayName());
                        String json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/submit.php?user_id="+name+"&quote="+quote+"&responseType=json");
                        if(json == null) return;
                        JSONObject response = new JSONObject(json);
                        if(response.getInt("response") == 200){
                            MessageFactory.createStandardMessage(member, ":white_check_mark: Success")
                                    .setDescription("Quote submitted for review.")
                                    .queue(channel);
                            System.out.println(member.getUser().getName()+"#"+member.getUser().getDiscriminator()+" Submitted a quote for review.");
                        }
                        else{
                            MessageFactory.createStandardMessage(member, getEmote("robloxhead") + " Oof")
                                    .setDescription("Something went wrong while trying to submit the quote #DarkEyeDragon")
                                    .queue(channel);
                            System.out.println(member.getUser().getName()+"#"+member.getUser().getDiscriminator()+" tried to submit a quote but something went wrong in the request.");
                            System.out.println("DEBUG: "+json);
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
            else if(command[0].equalsIgnoreCase(".quotes")){
                if (channel.getName().equalsIgnoreCase("bot-commands")){
                    String json = null;
                    boolean pending = false;
                    if (command.length == 2){
                        if(command[1].equalsIgnoreCase("pending")){
                            json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getAll.php?filter=pending");
                            pending = true;
                        }
                    } else {
                        json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getAll.php?filter=accepted");
                    }
                    if(json==null) return;
                    JSONArray response = new JSONArray(json);
                    StringBuilder quotes = new StringBuilder();
                    for (int i = 0; i < response.length(); i++){
                        JSONObject object = response.getJSONObject(i);
                        quotes.append("`"+object.getInt("id")+"`. `\""+object.getString("quote")+"\"` - "+object.getString("user_id")+"\n");
                    }
                    MessageFactory.createStandardMessage(member, "Codevision quotes"+(pending?" (Pending)":" (Accepted)"))
                            .setDescription(quotes.toString())
                            .queue(channel);
                }
                else{
                    MessageFactory.createStandardMessage(member,"Please no")
                            .setDescription("Spare us the spam, move to #bot-commands")
                            .queue(channel);
                }
            }
            else if(command[0].equalsIgnoreCase(".help")){
                if (channel.getName().equalsIgnoreCase("bot-commands")){
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
                }
                else{
                    MessageFactory.createStandardMessage(member,"Please no")
                            .setDescription("Spare us the spam, move to #bot-commands")
                            .queue(channel);
                }
            } else if (command[0].equalsIgnoreCase(".info")) {
                MessageFactory.createStandardMessage(member, "InspireMe: info")
                        .setDescription("Follow the development of this garbage at https://github.com/MuteVision/InspireMe\n" +
                                "i dunno what else to put here.")
                        .queue(channel);
            }
        }
    }
}