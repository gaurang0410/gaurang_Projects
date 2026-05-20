package servlet;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import utils.CSRFUtil;
import utils.EmailUtil;
import utils.ValidationUtil;
import utils.ServletUtil;

public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!CSRFUtil.isValid(request)) {
            ServletUtil.redirectWithError(response, request.getContextPath() + "/register", "Invalid security token");
            return;
        }

        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phoneNumber = request.getParameter("phoneNumber");

        request.setAttribute("fullName", fullName);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("phoneNumber", phoneNumber);

        if (!ValidationUtil.isNotEmpty(fullName) || !ValidationUtil.isNotEmpty(username) || 
            !ValidationUtil.isNotEmpty(email) || !ValidationUtil.isNotEmpty(password) || 
            !ValidationUtil.isNotEmpty(phoneNumber)) {
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (username.length() < 3 || username.length() > 30 || !username.matches("^[a-zA-Z0-9_]+$")) {
            request.setAttribute("errorMessage", "Username must be 3-30 characters and contain only letters, numbers, and underscores.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.isUsernameTaken(username)) {
            request.setAttribute("errorMessage", "Username already exists.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Invalid email format.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPhone(phoneNumber)) {
            request.setAttribute("errorMessage", "Phone number must be exactly 10 digits.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        User user = new User(0, username, fullName, email, password, phoneNumber, "CUSTOMER");

        if (userDAO.registerUser(user)) {
            EmailUtil.sendEmail(
                    email,
                    "Welcome to DriveEase Car Rentals",
                    "Hello " + fullName + ",\n\nYour registration was successful.\n\nRegards,\nDriveEase Team"
            );
            ServletUtil.redirectWithSuccess(response, request.getContextPath() + "/login", "Registration successful. Please login.");
        } else {
            request.setAttribute("errorMessage", "Registration failed. Email might already exist.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CSRFUtil.ensureToken(request.getSession());
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}
