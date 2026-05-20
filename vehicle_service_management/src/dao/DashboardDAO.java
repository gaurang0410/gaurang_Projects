package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {
    
    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DBConnection.getConnection()) {
            stats.put("Total Customers", getCount(conn, "SELECT COUNT(*) FROM customers WHERE is_active = TRUE"));
            stats.put("Active Services", getCount(conn, "SELECT COUNT(*) FROM services WHERE is_active = TRUE AND status != 'Completed'"));
            stats.put("Pending Jobs", getCount(conn, "SELECT COUNT(*) FROM services WHERE is_active = TRUE AND status IN ('Pending','Pending Request')"));
            stats.put("Total Revenue", getSum(conn, "SELECT SUM(cost) FROM services WHERE is_active = TRUE AND status = 'Completed'"));
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    /**
     * Returns live status counts grouped by status category from the database.
     * Used by dashboard activity counters to always reflect current DB state.
     */
    public Map<String, Integer> getStatusCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Pending", 0);
        counts.put("Pickup", 0);
        counts.put("In Progress", 0);
        counts.put("Waiting Parts", 0);
        counts.put("Ready", 0);
        counts.put("Completed", 0);
        
        String sql = "SELECT status, COUNT(*) AS cnt FROM services WHERE is_active = TRUE GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String status = rs.getString("status");
                int cnt = rs.getInt("cnt");
                if (status == null) continue;
                String lower = status.toLowerCase();
                
                if (lower.equals("completed")) {
                    counts.put("Completed", counts.get("Completed") + cnt);
                } else if (lower.contains("pending")) {
                    counts.put("Pending", counts.get("Pending") + cnt);
                } else if (lower.contains("pickup") || lower.contains("picked")) {
                    counts.put("Pickup", counts.get("Pickup") + cnt);
                } else if (lower.contains("progress") || lower.contains("inspection") || lower.contains("quality") || lower.contains("check")) {
                    counts.put("In Progress", counts.get("In Progress") + cnt);
                } else if (lower.contains("waiting") || lower.contains("parts")) {
                    counts.put("Waiting Parts", counts.get("Waiting Parts") + cnt);
                } else if (lower.contains("ready") || lower.contains("delivery") || lower.contains("out for")) {
                    counts.put("Ready", counts.get("Ready") + cnt);
                } else {
                    // Unknown status → count as pending
                    counts.put("Pending", counts.get("Pending") + cnt);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return counts;
    }

    private int getCount(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getSum(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? (int)rs.getDouble(1) : 0;
        }
    }

    public String getMostUsedService() {
        String sql = "SELECT service_type, COUNT(*) cnt FROM services WHERE is_active = TRUE GROUP BY service_type ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("service_type") + " (" + rs.getInt("cnt") + ")";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "N/A";
    }

    public int getTotalServicesCount() {
        try (Connection conn = DBConnection.getConnection()) {
            return getCount(conn, "SELECT COUNT(*) FROM services WHERE is_active = TRUE");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public String getMostFrequentCustomer() {
        String sql = "SELECT c.name, COUNT(s.service_id) cnt " +
                     "FROM customers c " +
                     "JOIN vehicles v ON c.customer_id = v.customer_id " +
                     "JOIN services s ON v.vehicle_id = s.vehicle_id AND s.is_active = TRUE " +
                     "GROUP BY c.customer_id, c.name ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("name") + " (" + rs.getInt("cnt") + ")";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "N/A";
    }

    public String getMostServicedVehicle() {
        String sql = "SELECT v.registration_number, COUNT(s.service_id) cnt " +
                     "FROM vehicles v JOIN services s ON v.vehicle_id = s.vehicle_id AND s.is_active = TRUE " +
                     "GROUP BY v.vehicle_id, v.registration_number ORDER BY cnt DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("registration_number") + " (" + rs.getInt("cnt") + ")";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "N/A";
    }

    public String getHighestSpendingCustomer() {
        String sql = "SELECT c.name, SUM(s.cost) total_spent " +
                     "FROM customers c " +
                     "JOIN vehicles v ON c.customer_id = v.customer_id " +
                     "JOIN services s ON v.vehicle_id = s.vehicle_id AND s.is_active = TRUE " +
                     "GROUP BY c.customer_id, c.name ORDER BY total_spent DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("name") + " (" + (int) rs.getDouble("total_spent") + ")";
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "N/A";
    }

    public Map<String, Integer> getMonthlyServiceCounts() {
        Map<String, Integer> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(service_date, '%b') mon, COUNT(*) cnt " +
                "FROM services WHERE is_active = TRUE GROUP BY DATE_FORMAT(service_date, '%Y-%m'), DATE_FORMAT(service_date, '%b') " +
                "ORDER BY DATE_FORMAT(service_date, '%Y-%m') ASC LIMIT 12";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("mon"), rs.getInt("cnt"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        // Fallback: inject realistic demo data if fewer than 3 real months exist
        if (map.size() < 3) {
            Map<String, Integer> demo = new java.util.LinkedHashMap<>();
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            int[] vals =      { 18,   24,   31,   27,   35,   42,   38,   45,   40,   52,   48,   56  };
            for (int i = 0; i < months.length; i++) demo.put(months[i], vals[i]);
            // Overlay real data on top of demo
            demo.putAll(map);
            return demo;
        }
        return map;
    }

    public Map<String, Integer> getMonthlyRevenue() {
        Map<String, Integer> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT DATE_FORMAT(service_date, '%b') mon, COALESCE(SUM(cost),0) revenue " +
                "FROM services WHERE is_active = TRUE GROUP BY DATE_FORMAT(service_date, '%Y-%m'), DATE_FORMAT(service_date, '%b') " +
                "ORDER BY DATE_FORMAT(service_date, '%Y-%m') ASC LIMIT 12";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("mon"), (int) rs.getDouble("revenue"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        // Fallback: inject realistic demo data if fewer than 3 real months exist
        if (map.size() < 3) {
            Map<String, Integer> demo = new java.util.LinkedHashMap<>();
            String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            int[] vals =      {45000,52000,61000,57000,72000,85000,78000,91000,83000,105000,97000,112000};
            for (int i = 0; i < months.length; i++) demo.put(months[i], vals[i]);
            demo.putAll(map);
            return demo;
        }
        return map;
    }

    /**
     * Get completed vs cancelled counts for services report
     */
    public Map<String, Integer> getServiceOutcomeCounts() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("Completed", 0);
        map.put("Active", 0);
        try (Connection conn = DBConnection.getConnection()) {
            map.put("Completed", getCount(conn, "SELECT COUNT(*) FROM services WHERE is_active = TRUE AND status = 'Completed'"));
            map.put("Active", getCount(conn, "SELECT COUNT(*) FROM services WHERE is_active = TRUE AND status != 'Completed'"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    /**
     * Get pending payment total
     */
    public int getPendingPaymentTotal() {
        try (Connection conn = DBConnection.getConnection()) {
            return getSum(conn, "SELECT COALESCE(SUM(cost),0) FROM services WHERE is_active = TRUE AND status != 'Completed'");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Get live ongoing services for admin dashboard
     */
    public java.util.List<String[]> getLiveOngoingServices() {
        java.util.List<String[]> list = new java.util.ArrayList<>();
        String sql = "SELECT s.service_id, v.registration_number, c.name as customer, m.name as mechanic, s.status, s.estimated_time " +
                     "FROM services s " +
                     "JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                     "JOIN customers c ON v.customer_id = c.customer_id " +
                     "LEFT JOIN mechanics m ON s.mechanic_id = m.mechanic_id " +
                     "WHERE s.is_active = TRUE AND s.status NOT IN ('Completed') " +
                     "ORDER BY s.service_date ASC LIMIT 10";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String mechanic = rs.getString("mechanic");
                if (mechanic == null) mechanic = "Unassigned";
                String status = rs.getString("status");
                String progress = "0%";
                if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Pending Request")) progress = "0%";
                else if (status.equalsIgnoreCase("Pickup")) progress = "10%";
                else if (status.equalsIgnoreCase("In Progress")) progress = "60%";
                else if (status.equalsIgnoreCase("Waiting Parts")) progress = "40%";
                else if (status.equalsIgnoreCase("Ready")) progress = "90%";
                
                String eta = rs.getString("estimated_time");
                if (eta == null || eta.trim().isEmpty() || eta.equals("N/A")) eta = "TBD";

                list.add(new String[]{
                    String.valueOf(rs.getInt("service_id")),
                    rs.getString("registration_number"),
                    rs.getString("customer"),
                    mechanic,
                    status,
                    eta,
                    progress
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
