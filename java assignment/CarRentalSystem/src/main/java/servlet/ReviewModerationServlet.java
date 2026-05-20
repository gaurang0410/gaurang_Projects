package servlet;

import dao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CSRFUtil;
import utils.SessionUtil;

import java.io.IOException;

public class ReviewModerationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReviewModerationServlet.class);
    private ReviewDAO reviewDAO;

    @Override
    public void init() {
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!CSRFUtil.isValid(request)) {
            response.sendRedirect(request.getContextPath() + "/admin/reviews?error=Invalid security token");
            return;
        }

        int reviewId;
        try {
            reviewId = Integer.parseInt(request.getParameter("reviewId"));
        } catch (NumberFormatException e) {
            logger.error("Invalid review id: {}", request.getParameter("reviewId"), e);
            response.sendRedirect(request.getContextPath() + "/admin/reviews?error=Invalid review id");
            return;
        }

        String action = request.getParameter("action");
        boolean success = false;
        
        if ("approve".equalsIgnoreCase(action)) {
            success = reviewDAO.moderateReview(reviewId, "APPROVED");
        } else if ("reject".equalsIgnoreCase(action)) {
            success = reviewDAO.moderateReview(reviewId, "REJECTED");
        } else if ("delete".equalsIgnoreCase(action)) {
            success = reviewDAO.deleteReview(reviewId);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/reviews?error=Invalid action");
            return;
        }

        String msg = "Action " + action + " completed";
        response.sendRedirect(request.getContextPath() + (success ? "/admin/reviews?success=" + msg : "/admin/reviews?error=Unable to " + action + " review"));
    }
}
