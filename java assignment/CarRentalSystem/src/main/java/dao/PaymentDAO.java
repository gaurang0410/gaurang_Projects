package dao;

import model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAO extends BaseDAO {

    public boolean addPaymentWithConnection(Connection conn, Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, status, transaction_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getStatus());
            stmt.setString(5, payment.getTransactionId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, status, transaction_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getStatus());
            stmt.setString(5, payment.getTransactionId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error adding payment for booking: " + payment.getBookingId(), e);
        }
        return false;
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY transaction_date DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(extractPaymentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            handleException("Error getting payments for booking: " + bookingId, e);
        }
        return payments;
    }

    public Payment getLatestPaymentByBookingId(int bookingId) {
        String sql = "SELECT * FROM payments WHERE booking_id = ? ORDER BY transaction_date DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            handleException("Error getting latest payment for booking: " + bookingId, e);
        }
        return null;
    }

    public Map<String, Integer> getPaymentMethodDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        String sql = "SELECT payment_method, COUNT(*) AS count FROM payments GROUP BY payment_method ORDER BY count DESC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getString("payment_method"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting payment method distribution", e);
        }
        return distribution;
    }

    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id"));
        p.setBookingId(rs.getInt("booking_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setStatus(rs.getString("status"));
        p.setTransactionId(rs.getString("transaction_id"));
        p.setTransactionDate(rs.getTimestamp("transaction_date"));
        return p;
    }
}
