package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServicePriceHistoryDAO {
    public static class PriceHistoryItem {
        public int historyId;
        public int serviceId;
        public double oldPrice;
        public double newPrice;
        public String changedAt;
    }

    public List<PriceHistoryItem> getAllHistory() {
        List<PriceHistoryItem> list = new ArrayList<>();
        String sql = "SELECT history_id, service_id, old_price, new_price, changed_at FROM service_price_history ORDER BY changed_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PriceHistoryItem item = new PriceHistoryItem();
                item.historyId = rs.getInt("history_id");
                item.serviceId = rs.getInt("service_id");
                item.oldPrice = rs.getDouble("old_price");
                item.newPrice = rs.getDouble("new_price");
                item.changedAt = rs.getString("changed_at");
                list.add(item);
            }
        } catch (Exception e) {
            System.err.println("Error loading service cost history: " + e.getMessage());
        }
        return list;
    }
}
