package me.misterfix;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.IOUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtil {

    private static final Pattern CARRIAGE_RETURN_NEWLINE_PATTERN = Pattern.compile("\r\n", Pattern.LITERAL);

    public static String getWebPage(String url) throws IOException {
        URL searchURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) searchURL.openConnection();
        return getWebPage(conn);
    }

    public static String getWebPage(HttpURLConnection conn) throws IOException {
        conn.setRequestProperty("User-Agent", "InspireMe/1.3");

        int response = conn.getResponseCode();
        if (response != 200) {
            System.out.println("DEBUG: Response code: " + response);
        }

        return CARRIAGE_RETURN_NEWLINE_PATTERN.matcher(new String(IOUtil.readFully(conn.getInputStream())))
                .replaceAll(Matcher.quoteReplacement("\n"))
                .replace('\r', '\n');
    }

    public static String getQuoteAPi(Member member, TextChannel channel, String url) {
        return getApi(member, channel, "https://api.darkeyedragon.me/quotes/" + url);
    }

    public static String getApi(Member member, TextChannel channel, String url) {
        String json;
        try {
            json = WebUtil.getWebPage(url);
        } catch (IOException ignored) {
            MessageFactory.createStandardMessage(member, "#BlameDarkEyeDragon")
                .setDescription("Could not contact DarkEyeDragon's API.")
                .queue(channel);
            return null;
        }
        return json;
    }

    public static String urlenc(String str) {
        try {
            str = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}