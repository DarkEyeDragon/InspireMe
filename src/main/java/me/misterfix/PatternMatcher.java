package me.misterfix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcher {

    private static final String matchUserString = "-[\\\\s]*@[^ ]+$";
    private static final Pattern matchUserPattern = Pattern.compile(matchUserString);


    public static boolean isMatchUser(String text){
        Matcher matcher = matchUserPattern.matcher(text);
        return matcher.matches();
    }

    public static String getMatchUser(String text){
        Matcher matcher = matchUserPattern.matcher(text);
        return matcher.group(0);
    }
}
