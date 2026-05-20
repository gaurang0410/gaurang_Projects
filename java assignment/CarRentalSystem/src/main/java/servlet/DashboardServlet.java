package servlet;

import dao.BookingDAO;
import dao.CarDAO;
import dao.PaymentDAO;
import dao.ReviewDAO;
import dao.UserDAO;
import model.Booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DashboardServlet extends HttpServlet {
    private CarDAO carDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    private ReviewDAO reviewDAO;
    private PaymentDAO paymentDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
        bookingDAO = new BookingDAO();
        userDAO = new UserDAO();
        reviewDAO = new ReviewDAO();
        paymentDAO = new PaymentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.hasRole(request, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("pageTitle", "Dashboard Overview");

        int totalCars = carDAO.getTotalCars();
        int availableCars = carDAO.getAvailableCarsCount();
        int bookedCars = carDAO.getBookedCarsCount();
        double totalRevenue = bookingDAO.getTotalRevenue();
        int totalCustomers = userDAO.getTotalCustomers();
        int activeRentals = bookingDAO.getActiveRentalsCount();
        int pendingReviews = reviewDAO.getPendingReviewsCount();
        
        Map<String, Integer> categoryDist = carDAO.getCategoryDistribution();
        Map<String, Integer> bookingStatusDist = bookingDAO.getBookingStatusDistribution();
        Map<String, Double> monthlyRevenue = bookingDAO.getMonthlyRevenue();
        Map<String, Integer> monthlyBookings = bookingDAO.getMonthlyBookingCounts();
        Map<String, Integer> monthlyNewCustomers = userDAO.getMonthlyCustomerRegistrations();
        Map<String, Integer> paymentMethodDist = paymentDAO.getPaymentMethodDistribution();
        Map<Integer, Integer> reviewRatingDist = reviewDAO.getRatingDistribution();
        Map<String, Integer> monthlyReviews = reviewDAO.getMonthlyReviewCounts();
        int totalReviews = reviewDAO.getTotalReviewsCount();

        double revenueGrowth = roundOneDecimal(calculateGrowthPercent(monthlyRevenue));
        double bookingGrowth = roundOneDecimal(calculateGrowthPercent(monthlyBookings));
        double customerGrowth = roundOneDecimal(calculateGrowthPercent(monthlyNewCustomers));
        double reviewGrowth = roundOneDecimal(calculateGrowthPercent(monthlyReviews));

        double fleetUtilization = totalCars == 0 ? 0 : roundOneDecimal((bookedCars * 100.0) / totalCars);
        double availableShare = totalCars == 0 ? 0 : roundOneDecimal((availableCars * 100.0) / totalCars);

        request.setAttribute("totalCars", totalCars);
        request.setAttribute("availableCars", availableCars);
        request.setAttribute("bookedCars", bookedCars);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("activeRentals", activeRentals);
        request.setAttribute("pendingReviews", pendingReviews);
        request.setAttribute("totalReviews", totalReviews);
        
        request.setAttribute("categoryDist", categoryDist);
        request.setAttribute("bookingStatusDist", bookingStatusDist);
        request.setAttribute("monthlyRevenue", monthlyRevenue);
        request.setAttribute("monthlyBookings", monthlyBookings);
        request.setAttribute("monthlyNewCustomers", monthlyNewCustomers);
        request.setAttribute("paymentMethodDist", paymentMethodDist);
        request.setAttribute("reviewRatingDist", reviewRatingDist);

        request.setAttribute("mostBookedCars", carDAO.getMostBookedCars(5));
        request.setAttribute("mostActiveCustomers", bookingDAO.getMostActiveCustomers(5));
        request.setAttribute("recentBookings", bookingDAO.getRecentBookings(5));

        request.setAttribute("revenueGrowth", revenueGrowth);
        request.setAttribute("bookingGrowth", bookingGrowth);
        request.setAttribute("customerGrowth", customerGrowth);
        request.setAttribute("reviewGrowth", reviewGrowth);
        request.setAttribute("revenueGrowthAbs", Math.abs(revenueGrowth));
        request.setAttribute("bookingGrowthAbs", Math.abs(bookingGrowth));
        request.setAttribute("customerGrowthAbs", Math.abs(customerGrowth));
        request.setAttribute("reviewGrowthAbs", Math.abs(reviewGrowth));
        request.setAttribute("revenueTrend", trendDirection(revenueGrowth));
        request.setAttribute("bookingTrend", trendDirection(bookingGrowth));
        request.setAttribute("customerTrend", trendDirection(customerGrowth));
        request.setAttribute("reviewTrend", trendDirection(reviewGrowth));
        request.setAttribute("revenueTrendIcon", trendIcon(revenueGrowth));
        request.setAttribute("bookingTrendIcon", trendIcon(bookingGrowth));
        request.setAttribute("customerTrendIcon", trendIcon(customerGrowth));
        request.setAttribute("reviewTrendIcon", trendIcon(reviewGrowth));
        request.setAttribute("fleetUtilization", fleetUtilization);
        request.setAttribute("availableShare", availableShare);
        request.setAttribute("bookedShare", fleetUtilization);
        
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    private double calculateGrowthPercent(Map<String, ? extends Number> values) {
        if (values == null || values.size() < 2) {
            return 0;
        }
        double previous = 0;
        double current = 0;
        int index = 0;
        int size = values.size();
        for (Number value : values.values()) {
            if (index == size - 2) {
                previous = value.doubleValue();
            }
            if (index == size - 1) {
                current = value.doubleValue();
            }
            index++;
        }
        if (previous == 0) {
            return current > 0 ? 100 : 0;
        }
        return ((current - previous) / previous) * 100;
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String trendDirection(double value) {
        if (value > 0.2) {
            return "up";
        }
        if (value < -0.2) {
            return "down";
        }
        return "flat";
    }

    private String trendIcon(double value) {
        if (value > 0.2) {
            return "fa-arrow-up";
        }
        if (value < -0.2) {
            return "fa-arrow-down";
        }
        return "fa-minus";
    }
}
