package servlet;

import dao.BookingDAO;
import dao.CarDAO;
import model.Booking;
import model.Car;
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
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BookingServlet.class);
    private BookingDAO bookingDAO;
    private CarDAO carDAO;
    private dao.ReviewDAO reviewDAO;

    @Override
    public void init() {
        bookingDAO = new BookingDAO();
        carDAO = new CarDAO();
        reviewDAO = new dao.ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int carId;
        try {
            carId = Integer.parseInt(request.getParameter("carId"));
        } catch (NumberFormatException e) {
            logger.error("Invalid carId in GET: {}", request.getParameter("carId"));
            response.sendRedirect(request.getContextPath() + "/customer/dashboard?error=Invalid+car+ID");
            return;
        }

        Car car = carDAO.getCarById(carId);
        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/customer/dashboard?error=Car+not+found");
            return;
        }

        java.util.List<model.Review> reviews = reviewDAO.getApprovedReviewsByCarId(carId);

        CSRFUtil.ensureToken(request.getSession());
        request.setAttribute("car", car);
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("/booking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── 1. CSRF check ──
        if (!CSRFUtil.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/customer/dashboard?error=Invalid+security+token");
            return;
        }

        // ── 2. Session check ──
        User user = SessionUtil.getLoggedUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=Session+expired.+Please+log+in+again");
            return;
        }

        // ── 3. Parse + validate carId ──
        int carId;
        try {
            String carIdParam = request.getParameter("carId");
            if (carIdParam == null || carIdParam.isBlank()) throw new IllegalArgumentException("carId missing");
            carId = Integer.parseInt(carIdParam.trim());
        } catch (Exception e) {
            logger.error("Invalid carId param: {}", request.getParameter("carId"), e);
            response.sendRedirect(request.getContextPath() + "/customer/dashboard?error=Invalid+car+ID");
            return;
        }

        // ── 4. Parse + validate dates ──
        LocalDate pickupLocal;
        LocalDate returnLocal;
        Date pickupDate;
        Date returnDate;
        try {
            String pdStr = request.getParameter("pickupDate");
            String rdStr = request.getParameter("returnDate");

            if (pdStr == null || pdStr.isBlank()) throw new IllegalArgumentException("Pickup date is required");
            if (rdStr == null || rdStr.isBlank()) throw new IllegalArgumentException("Return date is required");

            pickupLocal = LocalDate.parse(pdStr.trim());
            returnLocal = LocalDate.parse(rdStr.trim());
            pickupDate  = Date.valueOf(pickupLocal);
            returnDate  = Date.valueOf(returnLocal);
        } catch (java.time.format.DateTimeParseException e) {
            logger.error("Date parse failure — pickup='{}' return='{}'",
                    request.getParameter("pickupDate"), request.getParameter("returnDate"), e);
            response.sendRedirect(request.getContextPath() +
                    "/customer/bookCar?carId=" + carId + "&error=Invalid+date+format.+Use+YYYY-MM-DD");
            return;
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() +
                    "/customer/bookCar?carId=" + carId + "&error=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
            return;
        }

        // ── 5. Parse locations ──
        String pickupLocation = request.getParameter("pickupLocation");
        String dropLocation   = request.getParameter("dropLocation");

        // ── 6. Load car + availability check ──
        Car car = carDAO.getCarById(carId);
        if (car == null) {
            response.sendRedirect(request.getContextPath() + "/customer/dashboard?error=Car+not+found");
            return;
        }

        // Re-populate form state for error returns
        request.setAttribute("car", car);
        request.setAttribute("pickupDate", pickupDate);
        request.setAttribute("returnDate", returnDate);
        request.setAttribute("pickupLocation", pickupLocation);
        request.setAttribute("dropLocation", dropLocation);

        if (!"AVAILABLE".equals(car.getStatus())) {
            request.setAttribute("errorMessage", "This car is currently unavailable for booking.");
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
            return;
        }

        // ── 7. Location validation ──
        if (pickupLocation == null || pickupLocation.isBlank()) {
            request.setAttribute("errorMessage", "Please select a pickup location.");
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
            return;
        }
        if (dropLocation == null || dropLocation.isBlank()) {
            request.setAttribute("errorMessage", "Please select a drop-off location.");
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
            return;
        }

        // ── 8. Date logic validation ──
        if (!pickupLocal.isBefore(LocalDate.now())) {
            // Pickup must be today or future (allow same-day)
        }
        long daysBetween = ChronoUnit.DAYS.between(pickupLocal, returnLocal);
        if (daysBetween <= 0) {
            request.setAttribute("errorMessage",
                    "Return date must be at least 1 day after pickup date. You selected: " +
                    pickupLocal + " → " + returnLocal + " (" + daysBetween + " days).");
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
            return;
        }

        // ── 9. Date conflict check ──
        if (!bookingDAO.isCarAvailableForDates(carId, pickupDate, returnDate)) {
            request.setAttribute("errorMessage",
                    "This car is already booked for the selected dates (" + pickupLocal + " – " + returnLocal + "). " +
                    "Please choose different dates.");
            request.getRequestDispatcher("/booking.jsp").forward(request, response);
            return;
        }

        // ── 10. Calculate amounts ──
        double rentAmount  = daysBetween * car.getPricePerDay();
        double gstAmount   = rentAmount * 0.18;
        double totalAmount = rentAmount + gstAmount;
        String invoiceId   = "INV-" + System.currentTimeMillis();

        logger.info("Booking calculation — carId={}, days={}, rent={}, gst={}, total={}",
                carId, daysBetween, rentAmount, gstAmount, totalAmount);

        // ── 11. Build Booking object ──
        Booking booking = new Booking();
        booking.setCustomerId(user.getId());
        booking.setCarId(carId);
        booking.setPickupDate(pickupDate);
        booking.setReturnDate(returnDate);
        booking.setPickupLocation(pickupLocation);
        booking.setDropLocation(dropLocation);
        booking.setTotalAmount(totalAmount);
        booking.setGstAmount(gstAmount);
        booking.setInvoiceId(invoiceId);
        booking.setStatus("PENDING_APPROVAL");

        // ── 12. Transactional insert ──
        try (Connection conn = utils.DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int bookingId = bookingDAO.addBookingWithConnection(conn, booking);
                if (bookingId <= 0) {
                    conn.rollback();
                    throw new Exception("Booking INSERT returned no generated ID");
                }

                conn.commit();
                logger.info("Booking request #{} submitted successfully for user #{}", bookingId, user.getId());

                // Try email notification (non-critical)
                try {
                    EmailUtil.sendEmail(
                            user.getEmail(),
                            "Booking Request Received — #" + bookingId,
                            "Hi " + user.getFullName() + ",\n\n" +
                            "Your booking request has been received and is pending approval.\n" +
                            "Booking ID: " + bookingId + "\n" +
                            "Car: " + car.getBrand() + " " + car.getModel() + "\n" +
                            "Pickup: " + pickupLocal + " | Return: " + returnLocal + "\n" +
                            "Total: ₹" + String.format("%.2f", totalAmount) + " (incl. GST)\n\n" +
                            "You will be notified once the admin approves your request, after which you can proceed to payment."
                    );
                } catch (Exception emailEx) {
                    logger.warn("Email failed for booking #{}: {}", bookingId, emailEx.getMessage());
                }

                response.sendRedirect(request.getContextPath() +
                        "/customer/bookings?success=Booking+request+%23" + bookingId + "+submitted+and+is+pending+approval.");

            } catch (Exception e) {
                try { conn.rollback(); } catch (Exception re) { /* ignore rollback error */ }
                logger.error("Booking transaction failed — carId={}, userId={}", carId, user.getId(), e);

                // Show the REAL error message to the user
                String userMessage = resolveUserFriendlyError(e);
                request.setAttribute("errorMessage", userMessage);
                request.getRequestDispatcher("/booking.jsp").forward(request, response);
            }

        } catch (java.sql.SQLException sqlEx) {
            logger.error("DB connection error during booking", sqlEx);
            response.sendRedirect(request.getContextPath() +
                    "/customer/dashboard?error=Database+connection+error.+Please+try+again.");
        }
    }

    /**
     * Converts raw exceptions into user-readable messages without exposing
     * SQL internals, while still being specific enough to be actionable.
     */
    private String resolveUserFriendlyError(Exception e) {
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        if (msg.contains("duplicate") && msg.contains("invoice")) {
            return "A duplicate booking was detected. Please try again.";
        }
        if (msg.contains("cannot add or update a child row") || msg.contains("foreign key")) {
            return "Data integrity error — the selected car or account may have been removed. Please refresh and try again.";
        }
        if (msg.contains("check constraint") || msg.contains("chk_booking_dates")) {
            return "Invalid date range — return date must be strictly after the pickup date.";
        }
        if (msg.contains("enum") || msg.contains("data truncated")) {
            return "An internal data format error occurred. Please contact support. (Code: DB-ENUM)";
        }
        if (msg.contains("connection") || msg.contains("communications link")) {
            return "Database connection lost. Please try again in a moment.";
        }
        if (msg.contains("payment insert failed")) {
            return "Booking was created but payment record failed. Please contact support.";
        }
        if (msg.contains("booking insert returned no generated id")) {
            return "Booking could not be saved. Please try again.";
        }
        // Generic but NOT silent
        return "Booking failed: " + (e.getMessage() != null
                ? e.getMessage().replaceAll("(?i)(password|token|secret)", "***")
                : "Unknown error") +
               ". Please try again or contact support.";
    }
}
