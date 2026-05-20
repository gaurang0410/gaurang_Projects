package servlet;

import dao.BookingDAO;
import dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Booking;
import model.Review;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSRFUtil;
import utils.SessionUtil;

import java.io.IOException;

public class ReviewServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReviewServlet.class);
    private ReviewDAO reviewDAO;
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        reviewDAO = new ReviewDAO();
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);
        if (!CSRFUtil.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid security token");
            return;
        }

        int bookingId;
        int rating;
        try {
            bookingId = Integer.parseInt(request.getParameter("bookingId"));
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (NumberFormatException e) {
            logger.error("Invalid review input. bookingId: {}, rating: {}", 
                    request.getParameter("bookingId"), request.getParameter("rating"), e);
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Invalid review input");
            return;
        }

        if (rating < 1 || rating > 5) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Rating must be between 1 and 5");
            return;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || booking.getCustomerId() != user.getId()) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Booking not found");
            return;
        }
        if (reviewDAO.hasReviewForBooking(bookingId)) {
            response.sendRedirect(request.getContextPath() + "/customer/bookings?error=Review already submitted for this booking");
            return;
        }

        Review review = new Review();
        review.setBookingId(bookingId);
        review.setCustomerId(user.getId());
        review.setCarId(booking.getCarId());
        review.setRating(rating);
        review.setComment(request.getParameter("comment"));

        boolean created = reviewDAO.addReview(review);
        response.sendRedirect(request.getContextPath() + (created ? "/customer/bookings?success=Review submitted for moderation" : "/customer/bookings?error=Unable to submit review"));
    }
}
