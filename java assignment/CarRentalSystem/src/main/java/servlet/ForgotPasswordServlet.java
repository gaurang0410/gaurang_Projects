package servlet;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.EmailUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

public class ForgotPasswordServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordServlet.class);
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email == null || email.isBlank()) {
            request.setAttribute("errorMessage", "Email is required");
            request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
            return;
        }

        if (userDAO.emailExists(email)) {
            String token = UUID.randomUUID().toString();
            Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600000); // 1 hour
            
            if (userDAO.saveResetToken(email, token, expiry)) {
                String resetLink = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() 
                                 + request.getContextPath() + "/resetPassword?token=" + token;
                
                boolean sent = EmailUtil.sendEmail(email, "Password Reset Request", 
                    "Hi,\n\nYou requested a password reset. Click the link below to set a new password:\n" + resetLink + 
                    "\n\nThis link will expire in 1 hour.");
                
                if (sent) {
                    request.setAttribute("successMessage", "Password reset link has been sent to your email.");
                } else {
                    logger.warn("Password reset email failed to send to {}", email);
                    request.setAttribute("errorMessage", "Unable to send email. Please try again later.");
                }
            } else {
                request.setAttribute("errorMessage", "A system error occurred. Please try again.");
            }
        } else {
            // Security best practice: don't reveal if email exists
            request.setAttribute("successMessage", "If this email exists in our system, a reset link has been sent.");
        }
        request.getRequestDispatcher("/forgotPassword.jsp").forward(request, response);
    }
}
