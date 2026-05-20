package utils;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ServletUtil {
    
    private ServletUtil() {}

    public static void redirectWithError(HttpServletResponse response, String redirectUrl, String message) throws IOException {
        String separator = redirectUrl.contains("?") ? "&" : "?";
        response.sendRedirect(redirectUrl + separator + "error=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    public static void redirectWithSuccess(HttpServletResponse response, String redirectUrl, String message) throws IOException {
        String separator = redirectUrl.contains("?") ? "&" : "?";
        response.sendRedirect(redirectUrl + separator + "success=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}
