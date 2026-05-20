package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    
    public static class InventoryItem {
        public int id, stock, threshold;
        public String name;
        public double price;

        public InventoryItem(int id, String name, double price, int stock, int threshold) {
            this.id = id; this.name = name; this.price = price; this.stock = stock; this.threshold = threshold;
        }
    }

    public List<InventoryItem> getAllItems() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY part_name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new InventoryItem(
                    rs.getInt("part_id"), rs.getString("part_name"),
                    rs.getDouble("price"), rs.getInt("stock"), rs.getInt("low_stock_threshold")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addItem(String name, double price, int stock, int threshold) {
        String sql = "INSERT INTO inventory (part_name, price, stock, low_stock_threshold) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name); stmt.setDouble(2, price); stmt.setInt(3, stock); stmt.setInt(4, threshold);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStock(int id, int newStock) {
        String sql = "UPDATE inventory SET stock = ? WHERE part_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock); stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
