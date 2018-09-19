package me.misterfix;

import java.util.List;
import javax.security.auth.login.LoginException;
import me.misterfix.commands.HelpCommand;
import me.misterfix.commands.InfoCommand;
import me.misterfix.commands.QuoteCommand;
import me.misterfix.commands.QuotesCommand;
import me.misterfix.commands.SubmitCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;

public class Main {
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
        jda.addEventListener(new HelpCommand());
        jda.addEventListener(new InfoCommand());
        jda.addEventListener(new QuoteCommand());
        jda.addEventListener(new QuotesCommand());
        jda.addEventListener(new SubmitCommand());
    }

    public static String getEmote(String name) {
        Guild guild = jda.getGuildsByName("Codevision", false).get(0);
        List<Emote> emotes = guild.getEmotesByName(name, false);
        return emotes.get(0).getAsMention();
    }
}