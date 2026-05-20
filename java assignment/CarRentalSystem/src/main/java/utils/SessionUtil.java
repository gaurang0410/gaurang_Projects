package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

public class SessionUtil {

    private SessionUtil() {
    }

    public static User getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object userObj = session.getAttribute("loggedUser");
        return (userObj instanceof User) ? (User) userObj : null;
    }

    public static boolean hasRole(HttpServletRequest request, String role) {
        User user = getLoggedUser(request);
        return user != null && role.equals(user.getRole());
    }
}
