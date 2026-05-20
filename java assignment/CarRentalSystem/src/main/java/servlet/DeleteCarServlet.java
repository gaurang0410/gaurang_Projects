package servlet;

import dao.CarDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class DeleteCarServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeleteCarServlet.class);
    private CarDAO carDAO;

    @Override
    public void init() {
        carDAO = new CarDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            logger.error("Invalid car id for deletion: {}", request.getParameter("id"), e);
            response.sendRedirect(request.getContextPath() + "/admin/cars?error=Invalid car id");
            return;
        }
        
        if (carDAO.deleteCar(id)) {
            response.sendRedirect(request.getContextPath() + "/admin/cars?success=Car deleted successfully");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/cars?error=Failed to delete car");
        }
    }
}
