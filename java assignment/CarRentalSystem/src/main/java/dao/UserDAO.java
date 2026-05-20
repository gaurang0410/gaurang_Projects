package dao;

import model.User;
import utils.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserDAO extends BaseDAO {
    
    public boolean saveResetToken(String email, String token, java.sql.Timestamp expiry) {
        String sql = "INSERT INTO password_reset_tokens (email, token, expiry) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE token = ?, expiry = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, token);
            stmt.setTimestamp(3, expiry);
            stmt.setString(4, token);
            stmt.setTimestamp(5, expiry);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error saving reset token for: " + email, e);
        }
        return false;
    }

    public String getEmailByResetToken(String token) {
        String sql = "SELECT email FROM password_reset_tokens WHERE token = ? AND expiry > NOW()";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            handleException("Error getting email by reset token", e);
        }
        return null;
    }

    public boolean updatePasswordByEmail(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, PasswordUtil.hashPassword(newPassword));
            stmt.setString(2, email);
            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                deleteResetTokenByEmail(email);
            }
            return updated;
        } catch (SQLException e) {
            handleException("Error updating password for email: " + email, e);
        }
        return false;
    }

    public void deleteResetTokenByEmail(String email) {
        String sql = "DELETE FROM password_reset_tokens WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error deleting reset token for: " + email, e);
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            handleException("Error checking if email exists: " + email, e);
        }
        return false;
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, full_name, email, password, phone_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashedPassword);
            stmt.setString(5, user.getPhoneNumber());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error registering user: " + user.getEmail(), e);
        }
        return false;
    }

    public boolean isUsernameTaken(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            handleException("Error checking if username is taken: " + username, e);
        }
        return false;
    }

    public User findByUsernameOrEmail(String loginId) {
        String sql = "SELECT * FROM users WHERE username = ? OR email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);
            stmt.setString(2, loginId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password")); // Hashed password
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            handleException("Error finding user by loginId: " + loginId, e);
        }
        return null;
    }
    
    public User loginUser(String loginId, String password) {
        User user = findByUsernameOrEmail(loginId);
        if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public int getTotalCustomers() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'CUSTOMER'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("Error getting total customers count", e);
        }
        return 0;
    }

    public Map<String, Integer> getMonthlyCustomerRegistrations() {
        Map<String, Integer> monthlyCustomers = new LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month_key, COUNT(*) AS count " +
                "FROM users WHERE role = 'CUSTOMER' GROUP BY month_key ORDER BY month_key";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                monthlyCustomers.put(rs.getString("month_key"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting monthly customer registrations", e);
        }
        return monthlyCustomers;
    }

    public List<User> getAllCustomers() {
        List<User> customers = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setRole(rs.getString("role"));
                customers.add(user);
            }
        } catch (SQLException e) {
            handleException("Error getting all customers", e);
        }
        return customers;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, password = ?, phone_number = ?, role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String passwordToPersist = user.getPassword();
            if (!PasswordUtil.isBCryptHash(passwordToPersist)) {
                passwordToPersist = PasswordUtil.hashPassword(passwordToPersist);
            }
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, passwordToPersist);
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getRole());
            stmt.setInt(6, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error updating user: " + user.getId(), e);
        }
        return false;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            handleException("Error getting user by ID: " + id, e);
        }
        return null;
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error deleting user: " + id, e);
        }
        return false;
    }

    private void updatePasswordHash(Connection conn, int userId, String passwordHash) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error updating password hash for user: " + userId, e);
        }
    }
}
