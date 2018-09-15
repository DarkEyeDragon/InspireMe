import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.IOException;
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
                if(command.length == 2){
                    String json;
                    try{
                        json = WebUtil.getWebPage("https://api.darkeyedragon.me/quotes/getQuote.php?id="+command[1]);
                    }
                    catch (IOException e){
                        MessageFactory.createStandardMessage(member, "#BlameDarkEyeDragon")
                                .setDescription("Could not contact DarkEyeDragon's API.")
                                .queue(channel);
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(json);
                    MessageFactory.createStandardMessage(member, jsonObject.getString("user_id")+" once said")
                            .setDescription("\""+jsonObject.getString("quote")+"\"")
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
                    String json;
                    try {
                        String name = URLEncoder.encode(member.getUser().getName()+"#"+member.getUser().getDiscriminator(), StandardCharsets.UTF_8.displayName());
                        String quote = URLEncoder.encode(msgRaw.replaceFirst(".submit ", ""), StandardCharsets.UTF_8.displayName());
                        json = WebUtil.getWebPage("https://api.darkeyedragon.me/quotes/submit.php?user_id="+name+"&quote="+quote+"&responseType=json");
                    } catch (IOException e) {
                        MessageFactory.createStandardMessage(member, "#BlameDarkEyeDragon")
                                .setDescription("Could not contact DarkEyeDragon's API.")
                                .queue(channel);
                        return;
                    }
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
                } else {
                    MessageFactory.createStandardMessage(member, ":x: Derp")
                            .setDescription("Usage: .submit <quote to submit>.")
                            .queue(channel);
                }
            }
        }
    }
}