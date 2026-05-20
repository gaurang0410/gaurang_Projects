package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
    public static class AuditLog {
        public int id;
        public String action;
        public Integer userId;
        public String timestamp;
        public String details;
    }

    public List<AuditLog> getRecentLogs() {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT log_id, action, user_id, timestamp, details FROM audit_logs ORDER BY timestamp DESC LIMIT 300";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.id = rs.getInt("log_id");
                Object uidObj = rs.getObject("user_id");
                log.userId = uidObj == null ? null : rs.getInt("user_id");
                log.action = rs.getString("action");
                log.timestamp = rs.getString("timestamp");
                log.details = rs.getString("details");
                list.add(log);
            }
        } catch (Exception e) {
            System.err.println("Error loading audit logs: " + e.getMessage());
        }
        return list;
    }

    public void log(String action, Integer userId, String details) {
        String sql = "INSERT INTO audit_logs (action, user_id, details) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            if (userId != null && userId > 0) {
                stmt.setInt(2, userId);
            } else {
                stmt.setNull(2, java.sql.Types.INTEGER);
            }
            stmt.setString(3, details);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error writing audit log: " + e.getMessage());
        }
    }
}
