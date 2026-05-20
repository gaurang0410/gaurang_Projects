package servlet;

import dao.CarDAO;
import model.Car;
import model.User;
import utils.FilterOptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import utils.CSRFUtil;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;

public class ViewCarsServlet extends HttpServlet {
    private CarDAO carDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = SessionUtil.getLoggedUser(request);

        if ("ADMIN".equals(user.getRole())) {
            CSRFUtil.ensureToken(request.getSession());
            List<Car> cars = carDAO.getAllCars();
            request.setAttribute("cars", cars);
            
            // Add predefined filter options
            request.setAttribute("categories", FilterOptions.getCategories());
            request.setAttribute("fuelTypes", FilterOptions.getFuelTypes());
            request.setAttribute("locations", FilterOptions.getLocations());
            
            request.getRequestDispatcher("/viewCars.jsp").forward(request, response);
        } else {
            // Forward to CustomerDashboardServlet to handle car browsing
            request.getRequestDispatcher("/customer/dashboard").forward(request, response);
        }
    }
}
