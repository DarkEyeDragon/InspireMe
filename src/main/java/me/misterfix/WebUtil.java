package me.misterfix;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebUtil {

    public static String getWebPage(String url) throws IOException {
        URL searchURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) searchURL.openConnection();
        return getWebPage(conn);
    }

    public static String getWebPage(HttpURLConnection conn) throws IOException {
        StringBuilder sb = new StringBuilder();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        conn.setRequestProperty("Referer", "http://www.google.com");

        int response = conn.getResponseCode();
        if (response == 403) {
            System.out.println("DEBUG: response code: 403");
        } else if (response != 200) {
            System.out.println("DEBUG: Response code: " + response);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            sb.append(line).append("\n");
        }
        in.close();

        return sb.toString();
    }
    public static String getApi(Member member, TextChannel channel, String url){
        String json;
        try{
            json = WebUtil.getWebPage(url);
        }
        catch (IOException e){
            MessageFactory.createStandardMessage(member, "#BlameDarkEyeDragon")
                    .setDescription("Could not contact DarkEyeDragon's API.")
                    .queue(channel);
            return null;
        }
        return json;
    }
    public static String urlenc(String str){
        try {
            str = URLEncoder.encode(str, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
}