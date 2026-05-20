package dao;

import model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO extends BaseDAO {

    public int addBookingWithConnection(Connection conn, Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (customer_id, car_id, pickup_date, return_date, total_amount, status, pickup_location, drop_location, gst_amount, invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getCarId());
            stmt.setDate(3, booking.getPickupDate());
            stmt.setDate(4, booking.getReturnDate());
            stmt.setDouble(5, booking.getTotalAmount());
            stmt.setString(6, booking.getStatus());
            stmt.setString(7, booking.getPickupLocation());
            stmt.setString(8, booking.getDropLocation());
            stmt.setDouble(9, booking.getGstAmount());
            stmt.setString(10, booking.getInvoiceId());
            
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int bookingId = generatedKeys.getInt(1);
                        updateCarStatus(conn, booking.getCarId(), "BOOKED");
                        return bookingId;
                    }
                }
            }
        }
        return -1;
    }

    public int addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, car_id, pickup_date, return_date, total_amount, status, pickup_location, drop_location, gst_amount, invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getCarId());
            stmt.setDate(3, booking.getPickupDate());
            stmt.setDate(4, booking.getReturnDate());
            stmt.setDouble(5, booking.getTotalAmount());
            stmt.setString(6, booking.getStatus());
            stmt.setString(7, booking.getPickupLocation());
            stmt.setString(8, booking.getDropLocation());
            stmt.setDouble(9, booking.getGstAmount());
            stmt.setString(10, booking.getInvoiceId());
            
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int bookingId = generatedKeys.getInt(1);
                        // Update car status to BOOKED
                        updateCarStatus(conn, booking.getCarId(), "BOOKED");
                        return bookingId;
                    }
                }
            }
        } catch (SQLException e) {
            handleException("Error adding booking for car: " + booking.getCarId(), e);
        }
        return -1;
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            boolean updated = stmt.executeUpdate() > 0;
            if (updated && ("CANCELLED".equals(status) || "COMPLETED".equals(status))) {
                int carId = getCarIdByBookingId(conn, bookingId);
                if (carId > 0 && !hasActiveBookingsForCar(conn, carId)) {
                    updateCarStatus(conn, carId, "AVAILABLE");
                }
            }
            return updated;
        } catch (SQLException e) {
            handleException("Error updating booking status for ID: " + bookingId, e);
        }
        return false;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, u.full_name, c.brand, c.model FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN cars c ON b.car_id = c.id WHERE b.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking b = extractBookingFromResultSet(rs);
                    b.setCustomerName(rs.getString("full_name"));
                    b.setCarDetails(rs.getString("brand") + " " + rs.getString("model"));
                    return b;
                }
            }
        } catch (SQLException e) {
            handleException("Error getting booking by ID: " + bookingId, e);
        }
        return null;
    }

    public boolean isCarAvailableForDates(int carId, java.sql.Date pickupDate, java.sql.Date returnDate) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE car_id = ? " +
                "AND status IN ('PENDING','CONFIRMED') " +
                "AND NOT (return_date <= ? OR pickup_date >= ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carId);
            stmt.setDate(2, pickupDate);
            stmt.setDate(3, returnDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            handleException("Error checking car availability for car: " + carId, e);
        }
        return false;
    }

    private void updateCarStatus(Connection conn, int carId, String status) throws SQLException {
        String sql = "UPDATE cars SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, carId);
            stmt.executeUpdate();
        }
    }

    public List<Booking> searchBookings(String searchTerm, String status) {
        List<Booking> bookings = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT b.*, u.full_name, c.brand, c.model FROM bookings b ")
                .append("JOIN users u ON b.customer_id = u.id ")
                .append("JOIN cars c ON b.car_id = c.id WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (status != null && !status.isEmpty()) {
            sql.append("AND b.status = ? ");
            params.add(status);
        }
        
        if (searchTerm != null && !searchTerm.isEmpty()) {
            sql.append("AND (b.id = ? OR u.full_name LIKE ? OR c.brand LIKE ? OR c.model LIKE ?) ");
            int id = -1;
            try { id = Integer.parseInt(searchTerm); } catch (NumberFormatException e) {}
            params.add(id);
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
            params.add("%" + searchTerm + "%");
        }
        
        sql.append("ORDER BY b.booking_date DESC");
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = extractBookingFromResultSet(rs);
                    b.setCustomerName(rs.getString("full_name"));
                    b.setCarBrand(rs.getString("brand"));
                    b.setCarModel(rs.getString("model"));
                    b.setCarDetails(rs.getString("brand") + " " + rs.getString("model"));
                    bookings.add(b);
                }
            }
        } catch (SQLException e) {
            handleException("Error searching bookings with term: " + searchTerm, e);
        }
        return bookings;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name, c.brand, c.model FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN cars c ON b.car_id = c.id ORDER BY b.booking_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Booking b = extractBookingFromResultSet(rs);
                b.setCustomerName(rs.getString("full_name"));
                b.setCarBrand(rs.getString("brand"));
                b.setCarModel(rs.getString("model"));
                b.setCarDetails(rs.getString("brand") + " " + rs.getString("model"));
                bookings.add(b);
            }
        } catch (SQLException e) {
            handleException("Error getting all bookings", e);
        }
        return bookings;
    }

    public List<Booking> getBookingsByCustomerId(int customerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, c.brand, c.model FROM bookings b " +
                     "JOIN cars c ON b.car_id = c.id WHERE b.customer_id = ? ORDER BY b.booking_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = extractBookingFromResultSet(rs);
                    b.setCarBrand(rs.getString("brand"));
                    b.setCarModel(rs.getString("model"));
                    b.setCarDetails(rs.getString("brand") + " " + rs.getString("model"));
                    bookings.add(b);
                }
            }
        } catch (SQLException e) {
            handleException("Error getting bookings for customer: " + customerId, e);
        }
        return bookings;
    }

    public List<Booking> getRecentBookings(int limit) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, u.full_name, c.brand, c.model FROM bookings b " +
                     "JOIN users u ON b.customer_id = u.id " +
                     "JOIN cars c ON b.car_id = c.id ORDER BY b.booking_date DESC LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Booking b = extractBookingFromResultSet(rs);
                    b.setCustomerName(rs.getString("full_name"));
                    b.setCarDetails(rs.getString("brand") + " " + rs.getString("model"));
                    bookings.add(b);
                }
            }
        } catch (SQLException e) {
            handleException("Error getting recent bookings", e);
        }
        return bookings;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getInt("id"));
        b.setCustomerId(rs.getInt("customer_id"));
        b.setCarId(rs.getInt("car_id"));
        b.setPickupDate(rs.getDate("pickup_date"));
        b.setReturnDate(rs.getDate("return_date"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setStatus(rs.getString("status"));
        b.setBookingDate(rs.getTimestamp("booking_date"));
        b.setPickupLocation(rs.getString("pickup_location"));
        b.setDropLocation(rs.getString("drop_location"));
        b.setGstAmount(rs.getDouble("gst_amount"));
        b.setInvoiceId(rs.getString("invoice_id"));
        return b;
    }

    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM bookings WHERE status = 'CONFIRMED' OR status = 'COMPLETED'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            handleException("Error getting total revenue", e);
        }
        return 0.0;
    }

    public int getActiveRentalsCount() {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status IN ('PENDING','CONFIRMED')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("Error getting active rentals count", e);
        }
        return 0;
    }

    public Map<String, Integer> getBookingStatusDistribution() {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS count FROM bookings GROUP BY status ORDER BY status";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting booking status distribution", e);
        }
        return distribution;
    }

    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(booking_date, '%Y-%m') AS month_key, SUM(total_amount) AS amount " +
                "FROM bookings WHERE status IN ('CONFIRMED', 'COMPLETED') " +
                "GROUP BY month_key ORDER BY month_key";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                monthlyRevenue.put(rs.getString("month_key"), rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            handleException("Error getting monthly revenue", e);
        }
        return monthlyRevenue;
    }

    public Map<String, Integer> getMonthlyBookingCounts() {
        Map<String, Integer> monthlyBookings = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(booking_date, '%Y-%m') AS month_key, COUNT(*) AS count " +
                "FROM bookings GROUP BY month_key ORDER BY month_key";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                monthlyBookings.put(rs.getString("month_key"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting monthly booking counts", e);
        }
        return monthlyBookings;
    }

    public List<Map<String, Object>> getMostActiveCustomers(int limit) {
        List<Map<String, Object>> customers = new ArrayList<>();
        String sql = "SELECT u.full_name, COUNT(b.id) AS booking_count " +
                "FROM bookings b JOIN users u ON b.customer_id = u.id " +
                "GROUP BY u.id, u.full_name ORDER BY booking_count DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("customer", rs.getString("full_name"));
                    row.put("count", rs.getInt("booking_count"));
                    customers.add(row);
                }
            }
        } catch (SQLException e) {
            handleException("Error getting most active customers", e);
        }
        return customers;
    }

    private int getCarIdByBookingId(Connection conn, int bookingId) throws SQLException {
        String sql = "SELECT car_id FROM bookings WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("car_id");
                }
            }
        }
        return -1;
    }

    private boolean hasActiveBookingsForCar(Connection conn, int carId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bookings WHERE car_id = ? AND status IN ('PENDING','CONFIRMED')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, carId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
