package dao;

import api.ApiClient;
import model.NotificationItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public static class NotificationResponse {
        public List<NotificationItem> notifications = new ArrayList<>();
        public int unreadCount;
    }

    public NotificationResponse getNotifications(int userId) {
        return getNotifications(userId, null);
    }

    public NotificationResponse getNotifications(int userId, String role) {
        NotificationResponse result = new NotificationResponse();
        syncOperationalAlerts();
        try {
            StringBuilder url = new StringBuilder("/notifications?user_id=" + userId);
            if (role != null && !role.isEmpty()) {
                url.append("&role=").append(java.net.URLEncoder.encode(role, java.nio.charset.StandardCharsets.UTF_8));
            }
            JSONObject response = ApiClient.get(url.toString());
            JSONArray data = response.optJSONArray("data");
            result.unreadCount = response.optInt("unread_count", 0);
            if (data == null) return result;
            for (int i = 0; i < data.length(); i++) {
                JSONObject n = data.getJSONObject(i);
                NotificationItem item = new NotificationItem();
                item.setNotificationId(n.optInt("notification_id", -1));
                item.setUserId(n.isNull("user_id") ? null : n.optInt("user_id", -1));
                item.setTitle(n.optString("title", ""));
                item.setMessage(n.optString("message", ""));
                item.setType(n.optString("type", "INFO"));
                item.setRead(n.optBoolean("is_read", false));
                item.setCreatedAt(n.optString("created_at", ""));
                result.notifications.add(item);
            }
        } catch (Exception e) {
            System.err.println("Error loading notifications: " + e.getMessage());
        }
        return result;
    }

    public boolean markRead(int notificationId) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("notification_id", notificationId);
            JSONObject response = ApiClient.put("/notifications", payload);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean markAllRead(int userId) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("user_id", userId);
            JSONObject response = ApiClient.put("/notifications", payload);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a notification for a specific user
     */
    public boolean addNotification(Integer userId, String title, String message, String type) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO notifications (user_id, title, message, type, is_read) VALUES (?, ?, ?, ?, FALSE)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (userId != null && userId > 0) {
                    stmt.setInt(1, userId);
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                stmt.setString(2, title);
                stmt.setString(3, message);
                stmt.setString(4, type);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.err.println("Error adding notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Create service status change notifications for both admin and customer
     */
    public void createServiceNotification(int serviceId, String vehicleLabel, String newStatus) {
        String title;
        String type;
        switch (newStatus) {
            case "Pending Request": title = "New Booking Request"; type = "BOOKING_REQUEST"; break;
            case "Vehicle Pickup Assigned": title = "Pickup Assigned"; type = "PICKUP_ASSIGNED"; break;
            case "Vehicle Picked Up": title = "Vehicle Picked Up"; type = "PICKUP_COMPLETE"; break;
            case "In Inspection": title = "Vehicle Under Inspection"; type = "INSPECTION"; break;
            case "Waiting for Parts": title = "Waiting for Parts"; type = "WAITING_PARTS"; break;
            case "Service In Progress": title = "Service Started"; type = "SERVICE_STARTED"; break;
            case "Quality Check": title = "Quality Check In Progress"; type = "QUALITY_CHECK"; break;
            case "Ready for Delivery": title = "Service Completed - Ready"; type = "READY_DELIVERY"; break;
            case "Out for Delivery": title = "Vehicle Out for Delivery"; type = "OUT_DELIVERY"; break;
            case "Completed": title = "Service Completed"; type = "COMPLETE"; break;
            default: title = "Status Update"; type = "INFO"; break;
        }
        String msg = "Service #" + serviceId + " (" + vehicleLabel + ") → " + newStatus;
        // Admin notification (user_id = null = visible to all admins)
        addNotification(null, title, msg, type);
        // Customer notification - find customer's user_id via service→vehicle→customer→user
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.user_id FROM services s " +
                    "JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                    "JOIN customers c ON v.customer_id = c.customer_id " +
                    "JOIN users u ON u.customer_id = c.customer_id " +
                    "WHERE s.service_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, serviceId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int custUserId = rs.getInt("user_id");
                        addNotification(custUserId, title, msg, type);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating customer notification: " + e.getMessage());
        }
    }

    private void syncOperationalAlerts() {
        try (Connection conn = DBConnection.getConnection()) {
            createLowStockAlerts(conn);
            createUpcomingAlerts(conn);
            createOverdueAlerts(conn);
        } catch (Exception e) {
            System.err.println("Notification sync error: " + e.getMessage());
        }
    }

    private void createLowStockAlerts(Connection conn) throws Exception {
        String sql = "SELECT part_name, stock FROM inventory WHERE stock <= low_stock_threshold";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String msg = rs.getString("part_name") + " low stock (" + rs.getInt("stock") + ")";
                insertAlertIfMissing(conn, "Low Stock Alert", msg, "LOW_STOCK");
            }
        }
    }

    private void createUpcomingAlerts(Connection conn) throws Exception {
        String sql = "SELECT service_id, service_date FROM services WHERE is_active = TRUE AND status IN ('Pending','Scheduled') " +
                "AND service_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 DAY)";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String msg = "Service #" + rs.getInt("service_id") + " is scheduled on " + rs.getString("service_date");
                insertAlertIfMissing(conn, "Upcoming Appointment", msg, "UPCOMING");
            }
        }
    }

    private void createOverdueAlerts(Connection conn) throws Exception {
        String sql = "SELECT service_id, service_date FROM services WHERE is_active = TRUE AND status != 'Completed' " +
                "AND service_date < DATE_SUB(CURDATE(), INTERVAL 180 DAY)";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String msg = "Service #" + rs.getInt("service_id") + " is overdue since " + rs.getString("service_date");
                insertAlertIfMissing(conn, "Overdue Service", msg, "OVERDUE");
            }
        }
    }

    private void insertAlertIfMissing(Connection conn, String title, String message, String type) throws Exception {
        String check = "SELECT notification_id FROM notifications WHERE title=? AND message=? AND DATE(created_at)=CURDATE() LIMIT 1";
        try (PreparedStatement checkStmt = conn.prepareStatement(check)) {
            checkStmt.setString(1, title);
            checkStmt.setString(2, message);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) return;
            }
        }
        String insert = "INSERT INTO notifications (user_id, title, message, type, is_read) VALUES (NULL, ?, ?, ?, FALSE)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, title);
            stmt.setString(2, message);
            stmt.setString(3, type);
            stmt.executeUpdate();
        }
    }
}
