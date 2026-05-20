package dao;

import model.Car;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarDAO extends BaseDAO {
    private String lastErrorMessage;

    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars (brand, model, category, price_per_day, status, image_url, fuel_type, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        lastErrorMessage = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, car.getBrand());
            stmt.setString(2, car.getModel());
            stmt.setString(3, car.getCategory());
            stmt.setDouble(4, car.getPricePerDay());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getImageUrl());
            stmt.setString(7, car.getFuelType());
            stmt.setString(8, car.getLocation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            handleException("Error adding car: " + car.getBrand() + " " + car.getModel(), e);
        }
        return false;
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting all cars", e);
        }
        return cars;
    }

    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE status = 'AVAILABLE' ORDER BY brand, model";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error getting available cars", e);
        }
        return cars;
    }

    public List<Car> getAvailableCars(String brand, String category, String fuelType, String location, Double minPrice, Double maxPrice) {
        List<Car> cars = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cars WHERE status = 'AVAILABLE'");
        List<Object> params = new ArrayList<>();

        if (brand != null && !brand.isBlank()) {
            sql.append(" AND brand = ?");
            params.add(brand.trim());
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            params.add(category.trim());
        }
        if (fuelType != null && !fuelType.isBlank()) {
            sql.append(" AND fuel_type = ?");
            params.add(fuelType.trim());
        }
        if (location != null && !location.isBlank()) {
            sql.append(" AND location = ?");
            params.add(location.trim());
        }
        if (minPrice != null) {
            sql.append(" AND price_per_day >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price_per_day <= ?");
            params.add(maxPrice);
        }
        sql.append(" ORDER BY brand, model");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cars.add(extractCarFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            handleException("Error searching available cars with filters", e);
        }
        return cars;
    }

    public Car getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCarFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            handleException("Error getting car by ID: " + id, e);
        }
        return null;
    }

    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET brand=?, model=?, category=?, price_per_day=?, status=?, image_url=?, fuel_type=?, location=? WHERE id=?";
        lastErrorMessage = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, car.getBrand());
            stmt.setString(2, car.getModel());
            stmt.setString(3, car.getCategory());
            stmt.setDouble(4, car.getPricePerDay());
            stmt.setString(5, car.getStatus());
            stmt.setString(6, car.getImageUrl());
            stmt.setString(7, car.getFuelType());
            stmt.setString(8, car.getLocation());
            stmt.setInt(9, car.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            handleException("Error updating car: " + car.getId(), e);
        }
        return false;
    }

    public boolean deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("Error deleting car: " + id, e);
        }
        return false;
    }

    public int getTotalCars() {
        String sql = "SELECT COUNT(*) FROM cars";
        return getCount(sql);
    }

    public int getAvailableCarsCount() {
        String sql = "SELECT COUNT(*) FROM cars WHERE status = 'AVAILABLE'";
        return getCount(sql);
    }

    public int getBookedCarsCount() {
        String sql = "SELECT COUNT(*) FROM cars WHERE status = 'BOOKED'";
        return getCount(sql);
    }

    public Map<String, Integer> getCategoryDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        String sql = "SELECT category, COUNT(*) as count FROM cars GROUP BY category";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                distribution.put(rs.getString("category"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            handleException("Error getting category distribution", e);
        }
        return distribution;
    }

    public List<String> getDistinctBrands() {
        return getDistinctValues("brand");
    }

    public List<String> getDistinctCategories() {
        return getDistinctValues("category");
    }

    public List<String> getDistinctFuelTypes() {
        return getDistinctValues("fuel_type");
    }

    public List<String> getDistinctLocations() {
        return getDistinctValues("location");
    }

    private List<String> getDistinctValues(String columnName) {
        List<String> values = new ArrayList<>();
        String sql = "SELECT DISTINCT " + columnName + " FROM cars WHERE " + columnName + " IS NOT NULL AND " + columnName + " <> '' ORDER BY " + columnName;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                values.add(rs.getString(1));
            }
        } catch (SQLException e) {
            handleException("Error getting distinct values for column: " + columnName, e);
        }
        return values;
    }

    public List<Map<String, Object>> getMostBookedCars(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT c.brand, c.model, COUNT(b.id) AS booking_count " +
                "FROM bookings b JOIN cars c ON b.car_id = c.id " +
                "WHERE b.status IN ('PENDING','CONFIRMED','COMPLETED') " +
                "GROUP BY c.id, c.brand, c.model ORDER BY booking_count DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("car", rs.getString("brand") + " " + rs.getString("model"));
                    row.put("count", rs.getInt("booking_count"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            handleException("Error getting most booked cars", e);
        }
        return result;
    }

    public boolean carExists(String brand, String model, String fuelType, String location) {
        String sql = "SELECT COUNT(*) FROM cars WHERE LOWER(brand)=LOWER(?) AND LOWER(model)=LOWER(?) " +
                "AND LOWER(fuel_type)=LOWER(?) AND LOWER(location)=LOWER(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, brand);
            stmt.setString(2, model);
            stmt.setString(3, fuelType);
            stmt.setString(4, location);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            handleException("Error checking car existence", e);
            return false;
        }
    }

    public boolean carExistsForUpdate(int id, String brand, String model, String fuelType, String location) {
        String sql = "SELECT COUNT(*) FROM cars WHERE LOWER(brand)=LOWER(?) AND LOWER(model)=LOWER(?) " +
                "AND LOWER(fuel_type)=LOWER(?) AND LOWER(location)=LOWER(?) AND id <> ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, brand);
            stmt.setString(2, model);
            stmt.setString(3, fuelType);
            stmt.setString(4, location);
            stmt.setInt(5, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            handleException("Error checking car existence for update", e);
            return false;
        }
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private int getCount(String sql) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            handleException("Error executing count query: " + sql, e);
        }
        return 0;
    }

    private Car extractCarFromResultSet(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setCategory(rs.getString("category"));
        car.setPricePerDay(rs.getDouble("price_per_day"));
        car.setStatus(rs.getString("status"));
        car.setImageUrl(rs.getString("image_url"));
        car.setFuelType(rs.getString("fuel_type"));
        car.setLocation(rs.getString("location"));
        return car;
    }
}
