import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main extends ListenerAdapter
{
    public static void main(String[] args)
            throws LoginException, InterruptedException
    {
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken("")
                .build()
                .awaitReady();
        jda.addEventListener(new Main());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String msgRaw = event.getMessage().getContentRaw();
        Message message = event.getMessage();
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
                        MessageFactory.createStandardMessage(member, "Whut :feelsChromosomeMan:")
                                .setDescription("What are you trying to search for?\nUsage .quote search <pending> [search string]")
                                .queue(channel);
                    }
                }
                else if(command.length >= 3){
                    String json;
                    boolean pending = false;
                    //yeah ik ik
                    String search = WebUtil.urlenc(msgRaw.replace(".quote ", "").replace("search ", "")).trim();
                    if(command[2].equalsIgnoreCase("pending")){
                        if(command.length == 3){
                            MessageFactory.createStandardMessage(member, "Wait whut :robloxhead:")
                                    .setDescription("What are you trying to search for?\nUsage .quote search <pending> [search string]")
                                    .queue(channel);
                        }
                        //Don't worry i'm going to chemotherapy after this code
                        search = search.replace("pending", "").replace("pending ", "").trim();
                        json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getQuote.php?search="+search+"&filter=pending");
                        pending = true;
                    }
                    else {
                        json = WebUtil.getApi(member, channel, "https://api.darkeyedragon.me/quotes/getQuote.php?search="+search);
                    }
                    System.out.println(search);
                    if(json==null) return;
                    JSONArray response = new JSONArray(json);
                    StringBuilder quotes = new StringBuilder();
                    for (int i = 0; i < response.length(); i++){
                        JSONObject object = response.getJSONObject(i);
                        quotes.append("`"+object.getInt("id")+"`. `\""+object.getString("quote")+"\"` - "+object.getString("user_id")+"\n");
                    }
                    MessageFactory.createStandardMessage(member, "Quote search results"+(pending?" (Pending)":" (Accepted)"))
                            .setDescription(quotes.toString())
                            .queue(channel);

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
                            MessageFactory.createStandardMessage(member, ":robloxhead: Oof")
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
        }
    }
}