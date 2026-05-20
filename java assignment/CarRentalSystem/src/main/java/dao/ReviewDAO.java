package dao;

import model.Review;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReviewDAO extends BaseDAO {

    public boolean addReview(Review review) {
        String sql = "INSERT INTO reviews (booking_id, customer_id, car_id, rating, comment, status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, review.getBookingId());
            stmt.setInt(2, review.getCustomerId());
            stmt.setInt(3, review.getCarId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error adding review for booking: " + review.getBookingId(), e);
        }
        return false;
    }

    public List<Review> getApprovedReviewsByCarId(int carId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, c.brand, c.model " +
                "FROM reviews r JOIN users u ON r.customer_id = u.id " +
                "JOIN cars c ON r.car_id = c.id WHERE r.car_id = ? AND r.status = 'APPROVED' ORDER BY r.created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(extractReview(rs));
                }
            }
        } catch (SQLException e) {
            handleException("Error getting approved reviews for car: " + carId, e);
        }
        return reviews;
    }

    public boolean hasReviewForBooking(int bookingId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            handleException("Error checking review for booking: " + bookingId, e);
        }
        return false;
    }

    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name, c.brand, c.model " +
                "FROM reviews r JOIN users u ON r.customer_id = u.id " +
                "JOIN cars c ON r.car_id = c.id ORDER BY r.created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reviews.add(extractReview(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting all reviews", e);
        }
        return reviews;
    }

    public boolean moderateReview(int reviewId, String status) {
        String sql = "UPDATE reviews SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error moderating review: " + reviewId, e);
        }
        return false;
    }

    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error deleting review: " + reviewId, e);
        }
        return false;
    }

    public int getPendingReviewsCount() {
        String sql = "SELECT COUNT(*) FROM reviews WHERE status = 'PENDING'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("Error getting pending reviews count", e);
        }
        return 0;
    }

    public int getTotalReviewsCount() {
        String sql = "SELECT COUNT(*) FROM reviews";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("Error getting total reviews count", e);
        }
        return 0;
    }

    public Map<Integer, Integer> getRatingDistribution() {
        Map<Integer, Integer> distribution = new LinkedHashMap<>();
        String sql = "SELECT rating, COUNT(*) AS count FROM reviews GROUP BY rating ORDER BY rating";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getInt("rating"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting review rating distribution", e);
        }
        return distribution;
    }

    public Map<String, Integer> getMonthlyReviewCounts() {
        Map<String, Integer> monthlyReviews = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month_key, COUNT(*) AS count " +
                "FROM reviews GROUP BY month_key ORDER BY month_key";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                monthlyReviews.put(rs.getString("month_key"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting monthly review counts", e);
        }
        return monthlyReviews;
    }

    private Review extractReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setBookingId(rs.getInt("booking_id"));
        review.setCustomerId(rs.getInt("customer_id"));
        review.setCarId(rs.getInt("car_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setStatus(rs.getString("status"));
        review.setCreatedAt(rs.getTimestamp("created_at"));
        review.setCustomerName(rs.getString("full_name"));
        review.setCarDetails(rs.getString("brand") + " " + rs.getString("model"));
        return review;
    }
}
