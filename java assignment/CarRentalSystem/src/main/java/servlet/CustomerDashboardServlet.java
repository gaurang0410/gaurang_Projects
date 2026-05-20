package servlet;

import dao.BookingDAO;
import dao.CarDAO;
import model.Booking;
import model.Car;
import model.User;
import utils.FilterOptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;


public class CustomerDashboardServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDashboardServlet.class);
    private CarDAO carDAO;
    private BookingDAO bookingDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
        bookingDAO = new BookingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);

        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String brand = request.getParameter("brand");
        String category = request.getParameter("category");
        String fuelType = request.getParameter("fuelType");
        String location = request.getParameter("location");

        Double minPrice = parsePrice(minPriceStr);
        Double maxPrice = parsePrice(maxPriceStr);

        List<Car> availableCars = carDAO.getAvailableCars(brand, category, fuelType, location, minPrice, maxPrice);

        List<Booking> userBookings = bookingDAO.getBookingsByCustomerId(user.getId());
        int totalBookings = userBookings.size();
        long activeRentals = userBookings.stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()) || "PENDING".equalsIgnoreCase(b.getStatus()))
                .count();
        int loyaltyPoints = totalBookings * 100;

        request.setAttribute("availableCars", availableCars);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("activeRentals", (int) activeRentals);
        request.setAttribute("loyaltyPoints", loyaltyPoints);
        
        // Use predefined filter options (always show all options)
        request.setAttribute("categories", FilterOptions.getCategories());
        request.setAttribute("fuelTypes", FilterOptions.getFuelTypes());
        request.setAttribute("locations", FilterOptions.getLocations());
        
        // Also keep brand from DB for now (might not be predefined)
        request.setAttribute("brands", carDAO.getDistinctBrands());

        request.getRequestDispatcher("/customerDashboard.jsp").forward(request, response);
    }

    private Double parsePrice(String price) {
        if (price == null || price.isBlank()) {
            return null;
        }
        try {
            double value = Double.parseDouble(price);
            return value < 0 ? null : value;
        } catch (NumberFormatException e) {
            logger.error("Error parsing price: {}", price, e);
            return null;
        }
    }
}