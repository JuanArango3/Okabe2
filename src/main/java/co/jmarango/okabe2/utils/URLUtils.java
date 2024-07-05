package co.jmarango.okabe2.utils;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtils {
    public static Optional<String> getURLParam(String url, String param) {
        final String regex = param+"=([^&]*)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();

    }
}
