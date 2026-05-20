package servlet;

import dao.BookingDAO;
import dao.PaymentDAO;
import model.Booking;
import model.Payment;
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
import java.util.UUID;

public class PaymentServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServlet.class);
    private PaymentDAO paymentDAO;
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        paymentDAO = new PaymentDAO();
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);

        int bookingId;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
        } catch (NumberFormatException e) {
            logger.error("Invalid booking id in doGet: {}", request.getParameter("bookingId"), e);
            response.sendRedirect(request.getContextPath() + "/customer/dashboard?error=Invalid booking id");
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || booking.getCustomerId() != user.getId()) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Booking not found");
            return;
        }

        CSRFUtil.ensureToken(request.getSession());
        request.setAttribute("booking", booking);
        request.getRequestDispatcher("/payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);
        if (!CSRFUtil.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid security token");
            return;
        }

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
        if ("CONFIRMED".equals(booking.getStatus()) || "COMPLETED".equals(booking.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Payment already completed for this booking");
            return;
        }

        double amount = booking.getTotalAmount();
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/payment?bookingId=" + bookingId + "&error=Please select payment method.");
            return;
        }

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod.trim());
        payment.setStatus("SUCCESS");
        payment.setTransactionId("TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 14).toUpperCase());

        if (paymentDAO.addPayment(payment)) {
            bookingDAO.updateBookingStatus(bookingId, "CONFIRMED");
            EmailUtil.sendEmail(
                    user.getEmail(),
                    "Payment Success - Booking Confirmed",
                    "Hi " + user.getFullName() + ",\n\nYour payment was successful.\nBooking ID: " + booking.getId() +
                            "\nInvoice ID: " + booking.getInvoiceId() +
                            "\nTransaction ID: " + payment.getTransactionId() +
                            "\nTotal Paid: ₹" + String.format("%.2f", booking.getTotalAmount()) +
                            "\n\nThank you for choosing DriveEase."
            );
            response.sendRedirect(request.getContextPath() + "/customer/bookings?success=Payment successful. Booking confirmed. Transaction ID: " + payment.getTransactionId());
        } else {
            response.sendRedirect(request.getContextPath() + "/customer/payment?bookingId=" + bookingId + "&error=Payment failed. Please try again.");
        }
    }
}
