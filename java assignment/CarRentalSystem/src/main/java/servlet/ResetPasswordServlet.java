package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ResetPasswordServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServlet.class);
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.isBlank() || userDAO.getEmailByResetToken(token) == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=Invalid or expired reset token");
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password == null || password.isBlank() || !password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match or are invalid");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
            return;
        }

        String email = userDAO.getEmailByResetToken(token);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=Invalid or expired reset token");
            return;
        }

        if (userDAO.updatePasswordByEmail(email, password)) {
            response.sendRedirect(request.getContextPath() + "/login?success=Password updated successfully. Please login.");
        } else {
            request.setAttribute("errorMessage", "Failed to update password. Please try again.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/resetPassword.jsp").forward(request, response);
        }
    }
}
