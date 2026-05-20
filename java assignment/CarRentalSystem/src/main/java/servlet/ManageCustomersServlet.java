package servlet;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CSRFUtil;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageCustomersServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ManageCustomersServlet.class.getName());
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (!SessionUtil.hasRole(request, "ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String action = request.getParameter("action");
            String customerId = request.getParameter("customerId");
            CSRFUtil.ensureToken(request.getSession());

            if ("view".equals(action) && customerId != null) {
                viewCustomerDetails(request, response, customerId);
            } else if ("bookings".equals(action) && customerId != null) {
                viewCustomerBookings(request, response, customerId);
            } else {
                listAllCustomers(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ManageCustomersServlet doGet", e);
            request.setAttribute("errorMessage", "An error occurred while managing customers");
            try {
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                LOGGER.log(Level.SEVERE, "Error forwarding to error page", ex);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (!SessionUtil.hasRole(request, "ADMIN")) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            if (!CSRFUtil.isValid(request)) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid security token");
                return;
            }

            String action = request.getParameter("action");

            if ("edit".equals(action)) {
                editCustomer(request, response);
            } else if ("delete".equals(action)) {
                deleteCustomer(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid action");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ManageCustomersServlet doPost", e);
            try {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=An error occurred");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error sending redirect", ex);
            }
        }
    }

    private void listAllCustomers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("pageTitle", "Customer Management");
        request.setAttribute("pageDescription", "Manage and view all registered customers in the system");
        List<User> customers = userDAO.getAllCustomers();
        request.setAttribute("allCustomers", customers);
        request.getRequestDispatcher("/manageCustomers.jsp").forward(request, response);
    }

    private void viewCustomerDetails(HttpServletRequest request, HttpServletResponse response, String customerId) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(customerId);
            User customer = userDAO.getUserById(id);

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer not found");
                return;
            }

            request.setAttribute("pageTitle", "Customer Details");
            request.setAttribute("pageDescription", "View customer information");
            request.setAttribute("customer", customer);
            request.getRequestDispatcher("/customerDetails.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid customer ID format", e);
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid customer ID");
        }
    }

    private void viewCustomerBookings(HttpServletRequest request, HttpServletResponse response, String customerId) throws IOException {
        try {
            int id = Integer.parseInt(customerId);
            User customer = userDAO.getUserById(id);

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer not found");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/bookings?customerId=" + id);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid customer ID format", e);
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid customer ID");
        }
    }

    private void editCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String customerIdStr = request.getParameter("customerId");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");

            if (customerIdStr == null || customerIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer ID is required");
                return;
            }

            if (fullName == null || fullName.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Full name cannot be empty");
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Email cannot be empty");
                return;
            }

            int customerId = Integer.parseInt(customerIdStr);
            User customer = userDAO.getUserById(customerId);

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer not found");
                return;
            }

            customer.setFullName(fullName.trim());
            customer.setEmail(email.trim());
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                customer.setPhoneNumber(phoneNumber.trim());
            }

            if (userDAO.updateUser(customer)) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?success=Customer updated successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Failed to update customer");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid customer ID format", e);
            try {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid customer ID");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error sending redirect", ex);
            }
        }
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String customerIdStr = request.getParameter("customerId");

            if (customerIdStr == null || customerIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer ID is required");
                return;
            }

            int customerId = Integer.parseInt(customerIdStr);
            User customer = userDAO.getUserById(customerId);

            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer not found");
                return;
            }

            if (userDAO.deleteUser(customerId)) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?success=Customer deleted successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Failed to delete customer");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid customer ID format", e);
            try {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid customer ID");
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error sending redirect", ex);
            }
        }
    }
}
