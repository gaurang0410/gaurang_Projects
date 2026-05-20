package servlet;

import dao.BookingDAO;
import model.Booking;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSRFUtil;
import utils.EmailUtil;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;

public class MyBookingsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MyBookingsServlet.class);
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);
        List<Booking> bookings = bookingDAO.getBookingsByCustomerId(user.getId());

        CSRFUtil.ensureToken(request.getSession());
        request.setAttribute("userBookings", bookings);
        request.getRequestDispatcher("/myBookings.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);
        if (!CSRFUtil.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid security token");
            return;
        }

        String action = request.getParameter("action");
        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (NumberFormatException e) {
            logger.error("Invalid booking id in doPost: {}", request.getParameter("bookingId"), e);
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid booking id");
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || booking.getCustomerId() != user.getId()) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Booking not found");
            return;
        }

        if ("cancel".equalsIgnoreCase(action) && ("PENDING".equals(booking.getStatus()) || "CONFIRMED".equals(booking.getStatus()))) {
            boolean cancelled = bookingDAO.updateBookingStatus(bookingId, "CANCELLED");
            if (cancelled) {
                EmailUtil.sendEmail(
                        user.getEmail(),
                        "Booking Cancelled - " + booking.getInvoiceId(),
                        "Hi " + user.getFullName() + ",\n\nYour booking #" + booking.getId() + " has been cancelled.\n\nRegards,\nDriveEase Team"
                );
            }
            response.sendRedirect(request.getContextPath() + (cancelled ? "/customer/bookings?success=Booking cancelled successfully" : "/customer/bookings?error=Unable to cancel booking"));
            return;
        }

        response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid action");
    }
}
