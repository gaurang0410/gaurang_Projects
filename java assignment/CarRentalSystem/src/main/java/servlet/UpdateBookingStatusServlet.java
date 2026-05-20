package servlet;

import dao.BookingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSRFUtil;
import utils.SessionUtil;

import java.io.IOException;

public class UpdateBookingStatusServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UpdateBookingStatusServlet.class);
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!CSRFUtil.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Invalid security token");
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (NumberFormatException e) {
            logger.error("Invalid booking id: {}", request.getParameter("bookingId"), e);
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Invalid booking id");
            return;
        }
        String status = request.getParameter("status");
        java.util.List<String> validStatuses = java.util.Arrays.asList(
            "PENDING", "PENDING_APPROVAL", "APPROVED", "REJECTED", "PAYMENT_PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"
        );
        if (!validStatuses.contains(status)) {
            response.sendRedirect(request.getContextPath() + "/admin/bookings?error=Invalid status");
            return;
        }

        boolean updated = bookingDAO.updateBookingStatus(bookingId, status);
        response.sendRedirect(request.getContextPath() + (updated ? "/admin/bookings?success=Booking status updated" : "/admin/bookings?error=Unable to update booking status"));
    }
}
