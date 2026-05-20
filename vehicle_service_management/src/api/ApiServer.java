package api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;
import service.PasswordUtils;

/**
 * REST API Server for Vehicle Service Management System
 * Provides endpoints for Users, Customers, Vehicles, and Services
 * Database: vehicle_service_db
 */
public class ApiServer {
    private static final int PORT = 8080;
    private static HttpServer server;
    // Updated database name to match user's schema
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_service_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = resolvePassword();

    public static void main(String[] args) {
        // Explicitly load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }

        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Register API endpoints
            server.createContext("/api/users", new UsersHandler());
            server.createContext("/api/customers", new CustomersHandler());
            server.createContext("/api/vehicles", new VehiclesHandler());
            server.createContext("/api/services", new ServicesHandler());
            server.createContext("/api/mechanics", new MechanicsHandler());
            server.createContext("/api/notifications", new NotificationsHandler());
            server.createContext("/api/feedback", new FeedbackHandler());
            server.createContext("/health", new HealthHandler());
            
            server.setExecutor(null);
            server.start();
            
            System.out.println("API Server started on http://localhost:" + PORT);
            
            // Auto-seed Admin if missing
            ensureAdminExists();
            
        } catch (IOException e) {
            System.err.println("Failed to start API server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void ensureAdminExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkSql = "SELECT user_id, password FROM users WHERE role = 'ADMIN' OR username = 'admin'";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
                if (!rs.next()) {
                    System.out.println("Admin user missing. Seeding default admin...");
                    String insertSql = "INSERT INTO users (username, email, password, full_name, role) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        pstmt.setString(1, "admin");
                        pstmt.setString(2, "admin@gmail.com");
                        pstmt.setString(3, PasswordUtils.hashPassword("admin123"));
                        pstmt.setString(4, "System Administrator");
                        pstmt.setString(5, "ADMIN");
                        pstmt.executeUpdate();
                        System.out.println("Default admin seeded: admin / admin123");
                    }
                } else {
                    String storedPass = rs.getString("password");
                    // If existing admin password is not hashed, or is legacy 'admin', update to hashed 'admin123'
                    if (!PasswordUtils.isLikelyHashed(storedPass) || storedPass.equals("admin")) {
                        System.out.println("Migrating legacy admin password to hashed 'admin123'...");
                        String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                            pstmt.setString(1, PasswordUtils.hashPassword("admin123"));
                            pstmt.setInt(2, rs.getInt("user_id"));
                            pstmt.executeUpdate();
                            System.out.println("Admin password migrated successfully.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error ensuring admin exists: " + e.getMessage());
        }
    }

    // Health Check Handler
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            JSONObject response = new JSONObject();
            response.put("status", "running");
            response.put("timestamp", System.currentTimeMillis());
            sendResponse(exchange, 200, response.toString());
        }
    }

    // Users API Handler
    static class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            try {
                if (method.equals("GET") && path.equals("/api/users/check")) {
                    boolean exists = false;
                    int userId = -1;
                    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                        String username = getQueryParam(exchange, "username");
                        String email = getQueryParam(exchange, "email");
                        if (username != null) {
                            String sql = "SELECT user_id FROM users WHERE username = ?";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1, username);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    exists = rs.next();
                                    if (exists) userId = rs.getInt("user_id");
                                }
                            }
                        } else if (email != null) {
                            String sql = "SELECT user_id FROM users WHERE email = ?";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1, email);
                                try (ResultSet rs = stmt.executeQuery()) {
                                    exists = rs.next();
                                    if (exists) userId = rs.getInt("user_id");
                                }
                            }
                        }
                    }
                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("exists", exists);
                    response.put("user_id", userId);
                    sendResponse(exchange, 200, response.toString());
                } else if (method.equals("GET")) {
                    handleGetUser(exchange);
                } else if (method.equals("POST")) {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    
                    if (path.contains("register")) {
                        handleUserRegistration(exchange, json);
                    } else if (path.contains("login")) {
                        handleUserLogin(exchange, json);
                    } else {
                        sendError(exchange, 404, "User endpoint not found");
                    }
                } else if (method.equals("PUT")) {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    if (path.contains("password")) {
                        handleChangePassword(exchange, json);
                    } else if (path.contains("profile")) {
                        handleUpdateProfile(exchange, json);
                    } else {
                        sendError(exchange, 404, "User update endpoint not found");
                    }
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleUserRegistration(HttpExchange exchange, JSONObject json) throws Exception {
            String username = json.getString("username");
            String email = json.getString("email");
            String phone = json.optString("phone", "0000000000");
            String password = json.getString("password");
            String fullName = json.getString("full_name");
            String role = json.optString("role", "CUSTOMER");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Check if username exists
                String checkQuery = "SELECT user_id FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                    stmt.setString(1, username);
                    if (stmt.executeQuery().next()) {
                        sendError(exchange, 400, "Username already exists");
                        return;
                    }
                }

                String hashedPassword = PasswordUtils.hashPassword(password);
                
                int customerId = -1;
                // If it's a customer, first create a record in the customers table
                if ("CUSTOMER".equalsIgnoreCase(role)) {
                    String custQuery = "INSERT INTO customers (name, email, phone) VALUES (?, ?, ?)";
                    try (PreparedStatement cstmt = conn.prepareStatement(custQuery, Statement.RETURN_GENERATED_KEYS)) {
                        cstmt.setString(1, fullName);
                        cstmt.setString(2, email);
                        cstmt.setString(3, phone);
                        cstmt.executeUpdate();
                        ResultSet crs = cstmt.getGeneratedKeys();
                        if (crs.next()) customerId = crs.getInt(1);
                    }
                }

                String insertQuery = "INSERT INTO users (username, email, password, full_name, role, customer_id) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, username);
                    stmt.setString(2, email);
                    stmt.setString(3, hashedPassword);
                    stmt.setString(4, fullName);
                    stmt.setString(5, role);
                    if (customerId > 0) stmt.setInt(6, customerId);
                    else stmt.setNull(6, java.sql.Types.INTEGER);
                    
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    int userId = -1;
                    if (rs.next()) userId = rs.getInt(1);

                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("message", "User registered successfully");
                    response.put("user_id", userId);
                    sendResponse(exchange, 200, response.toString());
                }
            }
        }

        private void handleUserLogin(HttpExchange exchange, JSONObject json) throws Exception {
            String username = json.getString("username");
            String password = json.getString("password");
            
            System.out.println("Login attempt for: " + username);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT u.user_id, u.full_name, u.role, u.username, u.email, u.customer_id, u.password, c.phone " +
                               "FROM users u LEFT JOIN customers c ON u.customer_id = c.customer_id " +
                               "WHERE (u.username = ? OR u.email = ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    stmt.setString(2, username);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String storedPass = rs.getString("password");
                        boolean isHashed = PasswordUtils.isLikelyHashed(storedPass);
                        boolean verified = PasswordUtils.verifyPassword(password, storedPass);
                        
                        System.out.println("User found: " + username + " [Role: " + rs.getString("role") + ", Stored: " + (isHashed ? "HASHED" : "PLAIN") + "]");

                        if (verified) {
                            // Migrate to hashed if it was plain text
                            if (!isHashed) {
                                System.out.println("Migrating plain text password for " + username + " to hash...");
                                try (PreparedStatement update = conn.prepareStatement("UPDATE users SET password = ? WHERE user_id = ?")) {
                                    update.setString(1, PasswordUtils.hashPassword(password));
                                    update.setInt(2, rs.getInt("user_id"));
                                    update.executeUpdate();
                                    System.out.println("Migration complete.");
                                }
                            }

                            JSONObject response = new JSONObject();
                            response.put("success", true);
                            response.put("user_id", rs.getInt("user_id"));
                            response.put("full_name", rs.getString("full_name"));
                            response.put("role", rs.getString("role"));
                            response.put("username", rs.getString("username"));
                            response.put("email", rs.getString("email"));
                            
                            int customerId = rs.getInt("customer_id");
                            if (rs.wasNull()) customerId = -1;
                            response.put("customer_id", customerId);
                            response.put("phone", rs.getString("phone"));
                            
                            System.out.println("Login successful for: " + username);
                            sendResponse(exchange, 200, response.toString());
                        } else {
                            System.out.println("Password mismatch for: " + username);
                            sendError(exchange, 401, "Invalid username or password");
                        }
                    } else {
                        System.out.println("User not found: " + username);
                        sendError(exchange, 401, "Invalid username or password");
                    }
                }
            }
        }

        private void handleGetUser(HttpExchange exchange) throws Exception {
            String id = getQueryParam(exchange, "id");
            if (id == null) {
                sendError(exchange, 400, "Missing user id");
                return;
            }
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT user_id, username, email, full_name, role FROM users WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, Integer.parseInt(id));
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            sendError(exchange, 404, "User not found");
                            return;
                        }
                        JSONObject response = new JSONObject();
                        response.put("success", true);
                        response.put("user_id", rs.getInt("user_id"));
                        response.put("username", rs.getString("username"));
                        response.put("email", rs.getString("email"));
                        response.put("full_name", rs.getString("full_name"));
                        response.put("role", rs.getString("role"));
                        response.put("phone", findPhoneForUser(conn, rs.getInt("user_id"), rs.getString("email")));
                        sendResponse(exchange, 200, response.toString());
                    }
                }
            }
        }

        private String findPhoneForUser(Connection conn, int userId, String email) throws Exception {
            String sql = "SELECT c.phone FROM users u LEFT JOIN customers c ON (u.customer_id = c.customer_id OR u.email = c.email) WHERE u.user_id = ? LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("phone");
                    }
                }
            }
            return "";
        }

        private void handleUpdateProfile(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                int userId = json.getInt("user_id");
                String fullName = json.optString("full_name", "");
                String email = json.optString("email", "");
                String phone = json.optString("phone", "");

                String currentEmail = "";
                try (PreparedStatement currentStmt = conn.prepareStatement("SELECT email FROM users WHERE user_id = ?")) {
                    currentStmt.setInt(1, userId);
                    try (ResultSet rs = currentStmt.executeQuery()) {
                        if (rs.next()) {
                            currentEmail = rs.getString("email");
                        }
                    }
                }

                String sql = "UPDATE users SET full_name = ?, email = ? WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, fullName);
                    stmt.setString(2, email);
                    stmt.setInt(3, userId);
                    int updated = stmt.executeUpdate();

                    if (updated > 0) {
                        String updateCustomerSql = "UPDATE customers c JOIN users u ON (u.customer_id = c.customer_id OR c.email = ?) SET c.name = ?, c.email = ?, c.phone = ? WHERE u.user_id = ?";
                        try (PreparedStatement cstmt = conn.prepareStatement(updateCustomerSql)) {
                            cstmt.setString(1, currentEmail);
                            cstmt.setString(2, fullName);
                            cstmt.setString(3, email);
                            cstmt.setString(4, phone);
                            cstmt.setInt(5, userId);
                            cstmt.executeUpdate();
                        }
                        writeAuditLog(conn, "PROFILE_UPDATE", userId, "Profile updated for user_id=" + userId);
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                }
            }
        }

        private void handleChangePassword(HttpExchange exchange, JSONObject json) throws Exception {
            int userId = json.getInt("user_id");
            String currentPassword = json.getString("current_password");
            String newPassword = json.getString("new_password");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String verifySql = "SELECT password FROM users WHERE user_id = ?";
                try (PreparedStatement verify = conn.prepareStatement(verifySql)) {
                    verify.setInt(1, userId);
                    try (ResultSet rs = verify.executeQuery()) {
                        if (!rs.next() || !PasswordUtils.verifyPassword(currentPassword, rs.getString("password"))) {
                            sendError(exchange, 401, "Current password is incorrect");
                            return;
                        }
                    }
                }

                String updateSql = "UPDATE users SET password = ? WHERE user_id = ?";
                try (PreparedStatement update = conn.prepareStatement(updateSql)) {
                    update.setString(1, PasswordUtils.hashPassword(newPassword));
                    update.setInt(2, userId);
                    int updated = update.executeUpdate();
                    if (updated > 0) {
                        writeAuditLog(conn, "PASSWORD_CHANGE", userId, "Password changed for user_id=" + userId);
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                }
            }
        }
    }

    // Customers API Handler
    static class CustomersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equals("GET")) {
                    handleGetCustomers(exchange);
                } else if (method.equals("POST")) {
                    handleAddCustomer(exchange, new JSONObject(readRequestBody(exchange)));
                } else if (method.equals("PUT")) {
                    handleUpdateCustomer(exchange, new JSONObject(readRequestBody(exchange)));
                } else if (method.equals("DELETE")) {
                    String id = getQueryParam(exchange, "id");
                    if (id != null) {
                        handleDeleteCustomer(exchange, Integer.parseInt(id));
                    } else {
                        sendError(exchange, 400, "Missing ID");
                    }
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) { sendError(exchange, 500, e.getMessage()); }
        }

        private void handleGetCustomers(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT * FROM customers WHERE is_active = TRUE ORDER BY name";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        JSONObject c = new JSONObject();
                        c.put("customer_id", rs.getInt("customer_id"));
                        c.put("name", rs.getString("name"));
                        c.put("phone", rs.getString("phone"));
                        c.put("email", rs.getString("email"));
                        c.put("address", rs.getString("address"));
                        data.put(c);
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    res.put("data", data);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleAddCustomer(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, json.getString("name"));
                    stmt.setString(2, json.getString("phone"));
                    stmt.setString(3, json.optString("email", ""));
                    stmt.setString(4, json.optString("address", ""));
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    if (rs.next()) res.put("customer_id", rs.getInt(1));
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleUpdateCustomer(HttpExchange exchange, JSONObject data) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, data.getString("name"));
                    stmt.setString(2, data.getString("phone"));
                    stmt.setString(3, data.optString("email", ""));
                    stmt.setString(4, data.optString("address", ""));
                    stmt.setInt(5, data.getInt("customer_id"));
                    int updated = stmt.executeUpdate();
                    JSONObject res = new JSONObject();
                    res.put("success", updated > 0);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleDeleteCustomer(HttpExchange exchange, int id) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE customers SET is_active = FALSE WHERE customer_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int deleted = stmt.executeUpdate();
                    if (deleted > 0) {
                        writeAuditLog(conn, "CUSTOMER_SOFT_DELETE", -1, "customer_id=" + id);
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", deleted > 0);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }
    }

    // Vehicles API Handler
    static class VehiclesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            try {
                if (method.equals("GET")) {
                    if (path.endsWith("/brands")) {
                        handleGetBrands(exchange);
                    } else if (path.endsWith("/models")) {
                        String brand = getQueryParam(exchange, "brand");
                        handleGetModels(exchange, brand);
                    } else {
                        handleGetVehicles(exchange);
                    }
                } else if (method.equals("POST")) handleAddVehicle(exchange, new JSONObject(readRequestBody(exchange)));
                else if (method.equals("PUT")) handleUpdateVehicle(exchange, new JSONObject(readRequestBody(exchange)));
                else if (method.equals("DELETE")) {
                    String id = getQueryParam(exchange, "id");
                    if (id != null) {
                        handleDeleteVehicle(exchange, Integer.parseInt(id));
                    } else {
                        sendError(exchange, 400, "Missing ID");
                    }
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) { sendError(exchange, 500, e.getMessage()); }
        }

        private void handleGetBrands(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // First check if brands exist, if not, seed some
                String checkSql = "SELECT COUNT(*) FROM vehicle_brands";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSql)) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        String seedSql = "INSERT INTO vehicle_brands (brand_name) VALUES (?)";
                        String[] brands = {
                            "Toyota", "Honda", "Ford", "BMW", "Audi", "Mercedes-Benz", "Hyundai", "Nissan",
                            "Volkswagen", "Tesla", "Kia", "Skoda", "Mahindra", "Tata", "Suzuki", "Lexus",
                            "Porsche", "Jeep", "Chevrolet", "Volvo", "Renault", "Peugeot", "MG", "Land Rover",
                            "Ferrari", "Lamborghini", "Rolls Royce", "Bentley", "Aston Martin", "Jaguar", "Maserati"
                        };
                        try (PreparedStatement pstmt = conn.prepareStatement(seedSql)) {
                            for (String b : brands) {
                                pstmt.setString(1, b);
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }

                String sql = "SELECT brand_name FROM vehicle_brands ORDER BY brand_name";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        data.put(rs.getString("brand_name"));
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    res.put("data", data);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleGetModels(HttpExchange exchange, String brand) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Check if models exist for this brand, if not, seed some
                String checkSql = "SELECT COUNT(*) FROM vehicle_models m JOIN vehicle_brands b ON m.brand_id = b.brand_id WHERE b.brand_name = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                    pstmt.setString(1, brand);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        seedModels(conn, brand);
                    }
                }

                String sql = "SELECT m.model_name FROM vehicle_models m JOIN vehicle_brands b ON m.brand_id = b.brand_id WHERE b.brand_name = ? ORDER BY m.model_name";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, brand);
                    ResultSet rs = stmt.executeQuery();
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        data.put(rs.getString("model_name"));
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    res.put("data", data);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void seedModels(Connection conn, String brand) throws Exception {
            Map<String, String[]> modelMap = new HashMap<>();
            modelMap.put("Toyota", new String[]{"Camry", "Corolla", "RAV4", "Highlander", "Prius", "Fortuner", "Innova", "Land Cruiser", "Yaris", "Glanza"});
            modelMap.put("Honda", new String[]{"Civic", "Accord", "CR-V", "Pilot", "Odyssey", "City", "Amaze", "Elevate", "Jazz"});
            modelMap.put("Ford", new String[]{"F-150", "Mustang", "Explorer", "Escape", "Focus", "Endeavour", "EcoSport", "Ranger"});
            modelMap.put("BMW", new String[]{"2 Series", "3 Series", "5 Series", "7 Series", "X1", "X3", "X5", "X7", "M4", "i7"});
            modelMap.put("Audi", new String[]{"A3", "A4", "A6", "A8", "Q3", "Q5", "Q7", "Q8", "e-tron", "RS5"});
            modelMap.put("Mercedes-Benz", new String[]{"A-Class", "C-Class", "E-Class", "S-Class", "GLA", "GLC", "GLE", "GLS", "EQS", "G-Wagon"});
            modelMap.put("Hyundai", new String[]{"i10", "i20", "Verna", "Creta", "Tucson", "Kona", "Elantra", "Sonata", "Venue", "Exter"});
            modelMap.put("Nissan", new String[]{"Altima", "Sentra", "Rogue", "Pathfinder", "Magnite", "X-Trail", "GT-R", "Leaf"});
            modelMap.put("Volkswagen", new String[]{"Polo", "Virtus", "Taigun", "Jetta", "Passat", "Tiguan", "Arteon", "Golf"});
            modelMap.put("Tesla", new String[]{"Model 3", "Model S", "Model X", "Model Y", "Cybertruck"});
            modelMap.put("Kia", new String[]{"Seltos", "Sonet", "Carnival", "Sportage", "EV6", "Carens", "Telluride"});
            modelMap.put("Skoda", new String[]{"Slavia", "Kushaq", "Octavia", "Superb", "Kodiaq", "Fabia"});
            modelMap.put("Mahindra", new String[]{"Scorpio", "Thar", "XUV700", "Bolero", "XUV300", "XUV400", "Marazzo"});
            modelMap.put("Tata", new String[]{"Nexon", "Harrier", "Safari", "Altroz", "Tiago", "Punch", "Tigor", "Curvv"});
            modelMap.put("Suzuki", new String[]{"Swift", "Baleno", "Brezza", "Ertiga", "Ciaz", "Grand Vitara", "Jimny", "Ignis"});
            modelMap.put("Lexus", new String[]{"ES", "RX", "NX", "LX", "UX", "LS"});
            modelMap.put("Porsche", new String[]{"911", "Cayenne", "Macan", "Panamera", "Taycan", "718 Boxster"});
            modelMap.put("Jeep", new String[]{"Compass", "Wrangler", "Grand Cherokee", "Renegade", "Meridian"});
            modelMap.put("Chevrolet", new String[]{"Spark", "Cruze", "Malibu", "Trailblazer", "Tahoe", "Camaro", "Corvette"});
            modelMap.put("Volvo", new String[]{"XC40", "XC60", "XC90", "S60", "S90", "V90"});
            modelMap.put("Renault", new String[]{"Kwid", "Triber", "Kiger", "Duster", "Captur", "Megane"});
            modelMap.put("Peugeot", new String[]{"208", "308", "3008", "5008", "2008", "508"});
            modelMap.put("MG", new String[]{"Hector", "Astor", "ZS EV", "Gloster", "Comet EV", "Cyberster"});
            modelMap.put("Land Rover", new String[]{"Defender", "Discovery", "Range Rover", "Evoque", "Velar", "Sport"});
            modelMap.put("Ferrari", new String[]{"488 GTB", "Portofino", "Roma", "SF90 Stradale", "F8 Tributo", "296 GTB"});
            modelMap.put("Lamborghini", new String[]{"Huracan", "Aventador", "Urus", "Revuelto"});
            modelMap.put("Rolls Royce", new String[]{"Phantom", "Ghost", "Cullinan", "Spectre", "Wraith"});
            modelMap.put("Bentley", new String[]{"Continental GT", "Flying Spur", "Bentayga"});
            modelMap.put("Aston Martin", new String[]{"DB11", "Vantage", "DBS", "DBX"});
            modelMap.put("Jaguar", new String[]{"XE", "XF", "F-PACE", "E-PACE", "I-PACE", "F-TYPE"});
            modelMap.put("Maserati", new String[]{"Ghibli", "Quattroporte", "Levante", "Grecale", "MC20"});

            String[] models = modelMap.getOrDefault(brand, new String[]{brand + " Model 1", brand + " Model 2"});
            
            String brandIdSql = "SELECT brand_id FROM vehicle_brands WHERE brand_name = ?";
            int brandId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(brandIdSql)) {
                pstmt.setString(1, brand);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) brandId = rs.getInt("brand_id");
            }

            if (brandId != -1) {
                String insertSql = "INSERT INTO vehicle_models (brand_id, model_name) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String m : models) {
                        pstmt.setInt(1, brandId);
                        pstmt.setString(2, m);
                        pstmt.executeUpdate();
                    }
                }
            }
        }

        private void handleGetVehicles(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT v.*, c.name as customer_name FROM vehicles v LEFT JOIN customers c ON v.customer_id = c.customer_id WHERE v.is_active = TRUE";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        JSONObject v = new JSONObject();
                        v.put("vehicle_id", rs.getInt("vehicle_id"));
                        v.put("customer_id", rs.getInt("customer_id"));
                        v.put("customer_name", rs.getString("customer_name"));
                        v.put("brand", rs.getString("brand"));
                        v.put("model", rs.getString("model"));
                        v.put("registration_number", rs.getString("registration_number"));
                        v.put("category", rs.getString("vehicle_category"));
                        data.put(v);
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    res.put("data", data);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleAddVehicle(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                int customerId = json.optInt("customer_id", -1);
                if (customerId <= 0) {
                    sendError(exchange, 400, "Valid customer_id is required");
                    return;
                }
                String sql = "INSERT INTO vehicles (customer_id, brand, model, registration_number, vehicle_category) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, customerId);
                    stmt.setString(2, json.getString("brand"));
                    stmt.setString(3, json.getString("model"));
                    stmt.setString(4, json.getString("registration_number"));
                    stmt.setString(5, json.optString("category", "STANDARD"));
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    if (rs.next()) res.put("vehicle_id", rs.getInt(1));
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleUpdateVehicle(HttpExchange exchange, JSONObject data) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE vehicles SET customer_id = ?, brand = ?, model = ?, registration_number = ?, vehicle_category = ? WHERE vehicle_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, data.getInt("customer_id"));
                    stmt.setString(2, data.getString("brand"));
                    stmt.setString(3, data.getString("model"));
                    stmt.setString(4, data.getString("registration_number"));
                    stmt.setString(5, data.optString("category", "STANDARD"));
                    stmt.setInt(6, data.getInt("vehicle_id"));
                    int updated = stmt.executeUpdate();
                    JSONObject res = new JSONObject();
                    res.put("success", updated > 0);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleDeleteVehicle(HttpExchange exchange, int id) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE vehicles SET is_active = FALSE WHERE vehicle_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int deleted = stmt.executeUpdate();
                    if (deleted > 0) {
                        writeAuditLog(conn, "VEHICLE_SOFT_DELETE", -1, "vehicle_id=" + id);
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", deleted > 0);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }
    }

    // Services API Handler
    static class ServicesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equals("GET")) handleGetServices(exchange);
                else if (method.equals("POST")) handleAddService(exchange, new JSONObject(readRequestBody(exchange)));
                else if (method.equals("PUT")) handleUpdateService(exchange, new JSONObject(readRequestBody(exchange)));
                else if (method.equals("DELETE")) {
                    String id = getQueryParam(exchange, "id");
                    if (id != null) {
                        handleDeleteService(exchange, Integer.parseInt(id));
                    } else {
                        sendError(exchange, 400, "Missing ID");
                    }
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) { sendError(exchange, 500, e.getMessage()); }
        }

        private void handleGetServices(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT s.*, v.registration_number, m.name AS mechanic_name " +
                        "FROM services s " +
                        "JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                        "LEFT JOIN mechanics m ON s.mechanic_id = m.mechanic_id " +
                        "WHERE s.is_active = TRUE " +
                        "ORDER BY s.service_date DESC";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        JSONObject s = new JSONObject();
                        s.put("service_id", rs.getInt("service_id"));
                        s.put("vehicle_id", rs.getInt("vehicle_id"));
                        s.put("registration_number", rs.getString("registration_number"));
                        s.put("service_type", rs.getString("service_type"));
                        s.put("service_date", rs.getString("service_date"));
                        s.put("status", rs.getString("status"));
                        s.put("cost", rs.getDouble("cost"));
                        s.put("estimated_time", rs.getString("estimated_time"));
                        s.put("mechanic_id", rs.getObject("mechanic_id") == null ? JSONObject.NULL : rs.getInt("mechanic_id"));
                        s.put("mechanic_name", rs.getString("mechanic_name"));
                        data.put(s);
                    }
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    res.put("data", data);
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleAddService(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO services (vehicle_id, mechanic_id, service_type, service_date, status, cost, estimated_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, json.getInt("vehicle_id"));
                    if (json.has("mechanic_id") && !json.isNull("mechanic_id")) {
                        stmt.setInt(2, json.getInt("mechanic_id"));
                    } else {
                        stmt.setNull(2, Types.INTEGER);
                    }
                    stmt.setString(3, json.getString("service_type"));
                    stmt.setString(4, json.getString("service_date"));
                    stmt.setString(5, json.optString("status", "Pending"));
                    stmt.setDouble(6, json.getDouble("cost"));
                    stmt.setString(7, json.optString("estimated_time", "N/A"));
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    JSONObject res = new JSONObject();
                    res.put("success", true);
                    if (rs.next()) {
                        int serviceId = rs.getInt(1);
                        res.put("service_id", serviceId);
                        writeAuditLog(conn, "SERVICE_CREATE", -1, "service_id=" + serviceId);
                        createServiceNotification(conn, "Service booked", "Service #" + serviceId + " created", "SERVICE_CREATED");
                    }
                    sendResponse(exchange, 200, res.toString());
                }
            }
        }

        private void handleUpdateService(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                int id = json.getInt("service_id");
                String sql;
                if (json.has("service_type")) {
                    double oldCost = getServiceCost(conn, id);
                    Integer oldMechanicId = getServiceMechanicId(conn, id);
                    sql = "UPDATE services SET vehicle_id = ?, mechanic_id = ?, service_type = ?, service_date = ?, status = ?, cost = ?, estimated_time = ? WHERE service_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, json.getInt("vehicle_id"));
                        if (json.has("mechanic_id") && !json.isNull("mechanic_id")) {
                            stmt.setInt(2, json.getInt("mechanic_id"));
                        } else {
                            stmt.setNull(2, Types.INTEGER);
                        }
                        stmt.setString(3, json.getString("service_type"));
                        stmt.setString(4, json.getString("service_date"));
                        stmt.setString(5, json.getString("status"));
                        stmt.setDouble(6, json.getDouble("cost"));
                        stmt.setString(7, json.optString("estimated_time", "N/A"));
                        stmt.setInt(8, id);
                        int updated = stmt.executeUpdate();
                        if (updated > 0) {
                            double newCost = json.getDouble("cost");
                            if (Math.abs(newCost - oldCost) > 0.0001d) {
                                insertPriceHistory(conn, id, oldCost, newCost);
                            }
                            Integer newMechanicId = json.has("mechanic_id") && !json.isNull("mechanic_id") ? json.getInt("mechanic_id") : null;
                            if (!Objects.equals(oldMechanicId, newMechanicId) && newMechanicId != null) {
                                createServiceNotification(conn, "Mechanic assigned", "Service #" + id + " assigned to mechanic #" + newMechanicId, "ASSIGNMENT");
                            }
                            writeAuditLog(conn, "SERVICE_UPDATE", -1, "service_id=" + id + ", status=" + json.optString("status"));
                        }
                        sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                    }
                } else {
                    sql = "UPDATE services SET status = ? WHERE service_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, json.getString("status"));
                        stmt.setInt(2, id);
                        int updated = stmt.executeUpdate();
                        if (updated > 0) {
                            String status = json.getString("status");
                            writeAuditLog(conn, "SERVICE_STATUS", -1, "service_id=" + id + ", status=" + status);
                            if ("Completed".equalsIgnoreCase(status)) {
                                createServiceNotification(conn, "Service completed", "Service #" + id + " marked as completed", "SERVICE_COMPLETED");
                            }
                        }
                        sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                    }
                }
            }
        }

        private void handleDeleteService(HttpExchange exchange, int id) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE services SET is_active = FALSE WHERE service_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int deleted = stmt.executeUpdate();
                    if (deleted > 0) {
                        writeAuditLog(conn, "SERVICE_SOFT_DELETE", -1, "service_id=" + id);
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", deleted > 0).toString());
                }
            }
        }
    }

    static class MechanicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equals("GET")) {
                    handleGetMechanics(exchange);
                } else if (method.equals("POST")) {
                    handleAddMechanic(exchange, new JSONObject(readRequestBody(exchange)));
                } else if (method.equals("PUT")) {
                    handleUpdateMechanic(exchange, new JSONObject(readRequestBody(exchange)));
                } else if (method.equals("DELETE")) {
                    String id = getQueryParam(exchange, "id");
                    if (id == null) {
                        sendError(exchange, 400, "Missing ID");
                        return;
                    }
                    handleDeleteMechanic(exchange, Integer.parseInt(id));
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGetMechanics(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT m.*, " +
                        "SUM(CASE WHEN s.status IN ('Pending','In Progress','Scheduled') AND s.is_active = TRUE THEN 1 ELSE 0 END) AS active_jobs, " +
                        "SUM(CASE WHEN s.status = 'Completed' AND s.is_active = TRUE THEN 1 ELSE 0 END) AS completed_jobs " +
                        "FROM mechanics m LEFT JOIN services s ON m.mechanic_id = s.mechanic_id " +
                        "WHERE m.is_active = TRUE GROUP BY m.mechanic_id ORDER BY m.name";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        JSONObject m = new JSONObject();
                        m.put("mechanic_id", rs.getInt("mechanic_id"));
                        m.put("name", rs.getString("name"));
                        m.put("specialization", rs.getString("specialization"));
                        m.put("phone", rs.getString("phone"));
                        m.put("availability", rs.getString("availability"));
                        m.put("rating", rs.getDouble("rating"));
                        m.put("active_jobs", rs.getInt("active_jobs"));
                        m.put("completed_jobs", rs.getInt("completed_jobs"));
                        data.put(m);
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", true).put("data", data).toString());
                }
            }
        }

        private void handleAddMechanic(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO mechanics (name, specialization, phone, availability, rating, is_active) VALUES (?, ?, ?, ?, ?, TRUE)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, json.getString("name"));
                    stmt.setString(2, json.optString("specialization", ""));
                    stmt.setString(3, json.optString("phone", ""));
                    stmt.setString(4, json.optString("availability", "Available"));
                    stmt.setDouble(5, json.optDouble("rating", 0));
                    stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    int id = -1;
                    if (rs.next()) id = rs.getInt(1);
                    writeAuditLog(conn, "MECHANIC_CREATE", -1, "mechanic_id=" + id);
                    sendResponse(exchange, 200, new JSONObject().put("success", true).put("mechanic_id", id).toString());
                }
            }
        }

        private void handleUpdateMechanic(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE mechanics SET name=?, specialization=?, phone=?, availability=?, rating=? WHERE mechanic_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, json.getString("name"));
                    stmt.setString(2, json.optString("specialization", ""));
                    stmt.setString(3, json.optString("phone", ""));
                    stmt.setString(4, json.optString("availability", "Available"));
                    stmt.setDouble(5, json.optDouble("rating", 0));
                    stmt.setInt(6, json.getInt("mechanic_id"));
                    int updated = stmt.executeUpdate();
                    if (updated > 0) {
                        writeAuditLog(conn, "MECHANIC_UPDATE", -1, "mechanic_id=" + json.getInt("mechanic_id"));
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                }
            }
        }

        private void handleDeleteMechanic(HttpExchange exchange, int id) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "UPDATE mechanics SET is_active = FALSE WHERE mechanic_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    int updated = stmt.executeUpdate();
                    if (updated > 0) {
                        writeAuditLog(conn, "MECHANIC_SOFT_DELETE", -1, "mechanic_id=" + id);
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                }
            }
        }
    }

    static class NotificationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equals("GET")) {
                    handleGetNotifications(exchange);
                } else if (method.equals("PUT")) {
                    handleMarkRead(exchange, new JSONObject(readRequestBody(exchange)));
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGetNotifications(HttpExchange exchange) throws Exception {
            String userId = getQueryParam(exchange, "user_id");
            String unreadOnly = getQueryParam(exchange, "unread");
            String role = getQueryParam(exchange, "role");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                StringBuilder sql = new StringBuilder("SELECT * FROM notifications WHERE ");
                
                // Role-based separation: ADMIN sees only global (user_id IS NULL),
                // CUSTOMER sees only their own (user_id = ?), default = both
                if ("ADMIN".equalsIgnoreCase(role)) {
                    sql.append("user_id IS NULL");
                } else if ("CUSTOMER".equalsIgnoreCase(role) && userId != null) {
                    sql.append("user_id = ?");
                } else {
                    // Legacy fallback: show global + user-specific
                    sql.append("(user_id IS NULL");
                    if (userId != null) {
                        sql.append(" OR user_id = ?)");
                    } else {
                        sql.append(")");
                    }
                }
                
                if ("true".equalsIgnoreCase(unreadOnly)) {
                    sql.append(" AND is_read = FALSE");
                }
                sql.append(" ORDER BY created_at DESC LIMIT 200");

                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    // Bind user_id param only when SQL contains a placeholder
                    boolean needsParam = ("CUSTOMER".equalsIgnoreCase(role) && userId != null)
                            || (!"ADMIN".equalsIgnoreCase(role) && !"CUSTOMER".equalsIgnoreCase(role) && userId != null);
                    if (needsParam) {
                        stmt.setInt(1, Integer.parseInt(userId));
                    }
                    try (ResultSet rs = stmt.executeQuery()) {
                        JSONArray data = new JSONArray();
                        int unreadCount = 0;
                        while (rs.next()) {
                            JSONObject n = new JSONObject();
                            n.put("notification_id", rs.getInt("notification_id"));
                            n.put("user_id", rs.getObject("user_id") == null ? JSONObject.NULL : rs.getInt("user_id"));
                            n.put("title", rs.getString("title"));
                            n.put("message", rs.getString("message"));
                            n.put("type", rs.getString("type"));
                            boolean read = rs.getBoolean("is_read");
                            n.put("is_read", read);
                            n.put("created_at", rs.getString("created_at"));
                            data.put(n);
                            if (!read) unreadCount++;
                        }
                        JSONObject res = new JSONObject();
                        res.put("success", true);
                        res.put("data", data);
                        res.put("unread_count", unreadCount);
                        sendResponse(exchange, 200, res.toString());
                    }
                }
            }
        }

        private void handleMarkRead(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                if (json.has("notification_id")) {
                    try (PreparedStatement stmt = conn.prepareStatement("UPDATE notifications SET is_read = TRUE WHERE notification_id = ?")) {
                        stmt.setInt(1, json.getInt("notification_id"));
                        int updated = stmt.executeUpdate();
                        sendResponse(exchange, 200, new JSONObject().put("success", updated > 0).toString());
                    }
                } else if (json.has("user_id")) {
                    try (PreparedStatement stmt = conn.prepareStatement("UPDATE notifications SET is_read = TRUE WHERE user_id = ? OR user_id IS NULL")) {
                        stmt.setInt(1, json.getInt("user_id"));
                        stmt.executeUpdate();
                        sendResponse(exchange, 200, new JSONObject().put("success", true).toString());
                    }
                } else {
                    sendError(exchange, 400, "Missing notification_id or user_id");
                }
            }
        }
    }

    static class FeedbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                if (method.equals("GET")) {
                    handleGetFeedback(exchange);
                } else if (method.equals("POST")) {
                    handleAddFeedback(exchange, new JSONObject(readRequestBody(exchange)));
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGetFeedback(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "SELECT f.*, c.name AS customer_name, m.name AS mechanic_name " +
                        "FROM feedback f " +
                        "LEFT JOIN customers c ON f.customer_id = c.customer_id " +
                        "LEFT JOIN mechanics m ON f.mechanic_id = m.mechanic_id " +
                        "ORDER BY f.created_at DESC";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        JSONObject f = new JSONObject();
                        f.put("feedback_id", rs.getInt("feedback_id"));
                        f.put("service_id", rs.getInt("service_id"));
                        f.put("customer_id", rs.getInt("customer_id"));
                        f.put("mechanic_id", rs.getObject("mechanic_id") == null ? JSONObject.NULL : rs.getInt("mechanic_id"));
                        f.put("rating", rs.getInt("rating"));
                        f.put("comment", rs.getString("comment"));
                        f.put("created_at", rs.getString("created_at"));
                        f.put("customer_name", rs.getString("customer_name"));
                        f.put("mechanic_name", rs.getString("mechanic_name"));
                        data.put(f);
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", true).put("data", data).toString());
                }
            }
        }

        private void handleAddFeedback(HttpExchange exchange, JSONObject json) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "INSERT INTO feedback (service_id, customer_id, mechanic_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, json.getInt("service_id"));
                    stmt.setInt(2, json.getInt("customer_id"));
                    if (json.has("mechanic_id") && !json.isNull("mechanic_id")) {
                        stmt.setInt(3, json.getInt("mechanic_id"));
                    } else {
                        stmt.setNull(3, Types.INTEGER);
                    }
                    stmt.setInt(4, json.getInt("rating"));
                    stmt.setString(5, json.optString("comment", ""));
                    int inserted = stmt.executeUpdate();
                    if (inserted > 0 && json.has("mechanic_id") && !json.isNull("mechanic_id")) {
                        updateMechanicRating(conn, json.getInt("mechanic_id"));
                    }
                    if (inserted > 0) {
                        writeAuditLog(conn, "FEEDBACK_ADD", json.optInt("customer_id", -1), "service_id=" + json.getInt("service_id"));
                    }
                    sendResponse(exchange, 200, new JSONObject().put("success", inserted > 0).toString());
                }
            }
        }
    }

    private static double getServiceCost(Connection conn, int serviceId) throws Exception {
        String sql = "SELECT cost FROM services WHERE service_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("cost");
            }
        }
        return 0;
    }

    private static Integer getServiceMechanicId(Connection conn, int serviceId) throws Exception {
        String sql = "SELECT mechanic_id FROM services WHERE service_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Object value = rs.getObject("mechanic_id");
                    return value == null ? null : rs.getInt("mechanic_id");
                }
            }
        }
        return null;
    }

    private static void insertPriceHistory(Connection conn, int serviceId, double oldPrice, double newPrice) throws Exception {
        String sql = "INSERT INTO service_price_history (service_id, old_price, new_price) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            stmt.setDouble(2, oldPrice);
            stmt.setDouble(3, newPrice);
            stmt.executeUpdate();
        }
    }

    private static void updateMechanicRating(Connection conn, int mechanicId) throws Exception {
        String sql = "UPDATE mechanics m SET rating = (SELECT COALESCE(AVG(rating), 0) FROM feedback WHERE mechanic_id = ?) WHERE m.mechanic_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mechanicId);
            stmt.setInt(2, mechanicId);
            stmt.executeUpdate();
        }
    }

    private static void createServiceNotification(Connection conn, String title, String message, String type) throws Exception {
        String sql = "INSERT INTO notifications (user_id, title, message, type, is_read) VALUES (NULL, ?, ?, ?, FALSE)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, message);
            stmt.setString(3, type);
            stmt.executeUpdate();
        }
    }

    private static void writeAuditLog(Connection conn, String action, int userId, String details) throws Exception {
        String sql = "INSERT INTO audit_logs (action, user_id, details) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            if (userId > 0) {
                stmt.setInt(2, userId);
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, details);
            stmt.executeUpdate();
        }
    }

    // Utility Methods
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendResponse(exchange, statusCode, new JSONObject().put("success", false).put("error", message).toString());
    }

    private static String getQueryParam(HttpExchange exchange, String key) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query == null || query.isEmpty()) {
            return null;
        }

        for (String param : query.split("&")) {
            String[] pair = param.split("=", 2);
            if (pair.length == 2 && key.equals(decode(pair[0]))) {
                return decode(pair[1]);
            }
        }
        return null;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String resolvePassword() {
        String value = System.getProperty("db.password");
        if (value != null) return value;

        value = System.getenv("DB_PASSWORD");
        if (value != null) return value;

        value = System.getenv("MYSQL_PASSWORD");
        if (value != null) return value;

        return "#Gaurang04#";
    }
}
