package servlet;

import dao.BookingDAO;
import model.Booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;

public class ManagePaymentsServlet extends HttpServlet {
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.hasRole(request, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("pageTitle", "Payment Management");
        // For simplicity, we use confirmed bookings as a proxy for payment history, 
        // or we could use PaymentDAO if we want to list each transaction.
        // Let's assume we list confirmed/completed bookings which imply payments.
        List<Booking> payments = bookingDAO.getAllBookings(); 
        request.setAttribute("allPayments", payments);
        request.getRequestDispatcher("/managePayments.jsp").forward(request, response);
    }
}
