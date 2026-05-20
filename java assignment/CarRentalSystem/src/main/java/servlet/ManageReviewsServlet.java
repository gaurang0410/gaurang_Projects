package servlet;

import dao.ReviewDAO;
import model.Review;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.CSRFUtil;
import utils.SessionUtil;
import java.io.IOException;
import java.util.List;

public class ManageReviewsServlet extends HttpServlet {
    private ReviewDAO reviewDAO;

    @Override
    public void init() {
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtil.hasRole(request, "ADMIN")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setAttribute("pageTitle", "Review Management");
        request.setAttribute("pageDescription", "View and manage customer reviews. Reviews are automatically published.");
        CSRFUtil.ensureToken(request.getSession());
        
        // Get all reviews (no approval status filtering - all reviews are public)
        List<Review> allReviews = reviewDAO.getAllReviews();
        request.setAttribute("allReviews", allReviews);
        
        request.getRequestDispatcher("/manageReviews.jsp").forward(request, response);
    }
}
