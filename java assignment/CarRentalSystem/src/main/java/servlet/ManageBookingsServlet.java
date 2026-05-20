package servlet;

import dao.BookingDAO;
import model.Booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CSRFUtil;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;

public class ManageBookingsServlet extends HttpServlet {
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

        String searchTerm = request.getParameter("search");
        String statusFilter = request.getParameter("status");
        CSRFUtil.ensureToken(request.getSession());

        request.setAttribute("pageTitle", "Booking Management");
        request.setAttribute("pageDescription", "View and manage all customer bookings with advanced filtering options");
        
        List<Booking> bookings;
        if ((searchTerm != null && !searchTerm.isEmpty()) || (statusFilter != null && !statusFilter.isEmpty())) {
            bookings = bookingDAO.searchBookings(searchTerm, statusFilter);
        } else {
            bookings = bookingDAO.getAllBookings();
        }
        
        request.setAttribute("allBookings", bookings);
        request.getRequestDispatcher("/manageBookings.jsp").forward(request, response);
    }
}
