package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {
    public static final String CSRF_SESSION_KEY = "csrf_token";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private CSRFUtil() {
    }

    public static String ensureToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null || token.isBlank()) {
            token = generateToken();
            session.setAttribute(CSRF_SESSION_KEY, token);
        }
        return token;
    }

    public static boolean isValid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_SESSION_KEY);
        String requestToken = request.getParameter(CSRF_SESSION_KEY);
        return sessionToken != null && requestToken != null && sessionToken.equals(requestToken);
    }

    private static String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
