package dao;

import model.ServiceCatalogItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCatalogDAO {
    
    public List<ServiceCatalogItem> getAllServices() {
        List<ServiceCatalogItem> list = new ArrayList<>();
        String sql = "SELECT * FROM service_catalog ORDER BY category, service_name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ServiceCatalogItem(
                    rs.getInt("catalog_id"),
                    rs.getString("service_name"),
                    rs.getString("description"),
                    rs.getDouble("base_cost"),
                    rs.getString("estimated_time"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addService(String name, String desc, double cost, String time, String cat) {
        String sql = "INSERT INTO service_catalog (service_name, description, base_cost, estimated_time, category) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setDouble(3, cost);
            stmt.setString(4, time);
            stmt.setString(5, cat);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addService(ServiceCatalogItem item) {
        return addService(item.getServiceName(), item.getDescription(), item.getBaseCost(), item.getEstimatedTime(), item.getCategory());
    }

    public boolean updateService(int id, String name, String desc, double cost, String time, String cat) {
        String sql = "UPDATE service_catalog SET service_name = ?, description = ?, base_cost = ?, estimated_time = ?, category = ? WHERE catalog_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, desc);
            stmt.setDouble(3, cost);
            stmt.setString(4, time);
            stmt.setString(5, cat);
            stmt.setInt(6, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteService(int id) {
        String sql = "DELETE FROM service_catalog WHERE catalog_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
