package servlet;

import dao.UserDAO;
import model.User;
import utils.PasswordUtil;
import utils.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSRFUtil;

import java.io.IOException;

public class SettingsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SettingsServlet.class);
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            User loggedUser = SessionUtil.getLoggedUser(request);
            
            // Re-fetch user from DB to ensure fresh data
            User freshUser = userDAO.getUserById(loggedUser.getId());
            if (freshUser != null) {
                request.getSession().setAttribute("loggedUser", freshUser);
                loggedUser = freshUser;
            }

            request.setAttribute("pageTitle", "Settings");
            request.setAttribute("currentUser", loggedUser);
            CSRFUtil.ensureToken(request.getSession());

            request.getRequestDispatcher("/settings.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in SettingsServlet doGet", e);
            String target = SessionUtil.hasRole(request, "ADMIN") ? "/admin/dashboard" : "/customer/dashboard";
            response.sendRedirect(request.getContextPath() + target + "?error=Error loading settings");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (!CSRFUtil.isValid(request)) {
                response.sendRedirect(request.getContextPath() + "/settings?error=Invalid security token");
                return;
            }

            User loggedUser = SessionUtil.getLoggedUser(request);
            String action = request.getParameter("action");

            if (action == null) {
                response.sendRedirect(request.getContextPath() + "/settings");
                return;
            }

            HttpSession session = request.getSession();
            switch (action) {
                case "updateProfile":
                    handleUpdateProfile(request, response, session, loggedUser);
                    break;
                case "changePassword":
                    handleChangePassword(request, response, session, loggedUser);
                    break;
                case "updatePreferences":
                    handleUpdatePreferences(request, response, session);
                    break;
                case "updateTheme":
                    handleUpdateTheme(request, response, session);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/settings?error=Invalid action");
            }

        } catch (Exception e) {
            logger.error("Error in SettingsServlet doPost", e);
            response.sendRedirect(request.getContextPath() + "/settings?error=Internal server error");
        }
    }

    private void handleUpdateTheme(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String theme = request.getParameter("theme");
        if (theme != null && (theme.equals("light") || theme.equals("dark"))) {
            session.setAttribute("theme", theme);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Theme updated");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, HttpSession session, User loggedUser) throws IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");

        if (fullName == null || fullName.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/settings?error=Name and Email are required");
            return;
        }

        loggedUser.setFullName(fullName.trim());
        loggedUser.setEmail(email.trim());
        loggedUser.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : "");

        if (userDAO.updateUser(loggedUser)) {
            session.setAttribute("loggedUser", loggedUser);
            response.sendRedirect(request.getContextPath() + "/settings?success=Profile updated successfully");
        } else {
            response.sendRedirect(request.getContextPath() + "/settings?error=Failed to update profile");
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session, User loggedUser) throws IOException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null || confirmPassword == null) {
            response.sendRedirect(request.getContextPath() + "/settings?error=All password fields are required#password");
            return;
        }

        if (!PasswordUtil.checkPassword(currentPassword, loggedUser.getPassword())) {
            response.sendRedirect(request.getContextPath() + "/settings?error=Current password is incorrect#password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.sendRedirect(request.getContextPath() + "/settings?error=Passwords do not match#password");
            return;
        }

        if (newPassword.length() < 6) {
            response.sendRedirect(request.getContextPath() + "/settings?error=Password too short#password");
            return;
        }

        loggedUser.setPassword(PasswordUtil.hashPassword(newPassword));

        if (userDAO.updateUser(loggedUser)) {
            session.setAttribute("loggedUser", loggedUser);
            response.sendRedirect(request.getContextPath() + "/settings?success=Password changed successfully#password");
        } else {
            response.sendRedirect(request.getContextPath() + "/settings?error=Failed to change password#password");
        }
    }

    private void handleUpdatePreferences(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        String theme = request.getParameter("theme");
        if (theme != null) {
            session.setAttribute("theme", theme);
        }
        response.sendRedirect(request.getContextPath() + "/settings?success=Preferences saved#preferences");
    }
}
