package api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * REST API Server for Vehicle Service Management System
 * Provides endpoints for Users, Customers, Vehicles, and Services
 */
public class ApiServer {
    private static final int PORT = 8080;
    private static HttpServer server;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vehicle_service";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Register API endpoints
            server.createContext("/api/users", new UsersHandler());
            server.createContext("/api/customers", new CustomersHandler());
            server.createContext("/api/vehicles", new VehiclesHandler());
            server.createContext("/api/services", new ServicesHandler());
            server.createContext("/health", new HealthHandler());
            
            server.setExecutor(null);
            server.start();
            
            System.out.println("✓ API Server started on http://localhost:" + PORT);
            System.out.println("✓ Available endpoints:");
            System.out.println("  - POST /api/users/register - Register new user");
            System.out.println("  - POST /api/users/login - User login");
            System.out.println("  - GET /api/customers - Get all customers");
            System.out.println("  - POST /api/customers - Add new customer");
            System.out.println("  - GET /api/vehicles - Get all vehicles");
            System.out.println("  - POST /api/vehicles - Add new vehicle");
            System.out.println("  - GET /api/services - Get all services");
            System.out.println("  - POST /api/services - Add new service");
            
        } catch (IOException e) {
            System.err.println("✗ Failed to start API server: " + e.getMessage());
            e.printStackTrace();
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
                if (method.equals("POST")) {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    
                    if (path.contains("register")) {
                        handleUserRegistration(exchange, json);
                    } else if (path.contains("login")) {
                        handleUserLogin(exchange, json);
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
            String password = json.getString("password");
            String fullName = json.getString("full_name");

            // Validate input
            if (username.length() < 4) {
                sendError(exchange, 400, "Username must be at least 4 characters");
                return;
            }
            if (password.length() < 6) {
                sendError(exchange, 400, "Password must be at least 6 characters");
                return;
            }

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

                // Hash password
                String hashedPassword = hashPassword(password);

                // Insert user
                String insertQuery = "INSERT INTO users (username, email, password, full_name) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, username);
                    stmt.setString(2, email);
                    stmt.setString(3, hashedPassword);
                    stmt.setString(4, fullName);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        JSONObject response = new JSONObject();
                        response.put("success", true);
                        response.put("message", "User registered successfully");
                        response.put("user_id", rs.getInt(1));
                        sendResponse(exchange, 200, response.toString());
                    }
                }
            }
        }

        private void handleUserLogin(HttpExchange exchange, JSONObject json) throws Exception {
            String username = json.getString("username");
            String password = json.getString("password");
            String hashedPassword = hashPassword(password);

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT user_id, full_name FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    stmt.setString(2, hashedPassword);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        JSONObject response = new JSONObject();
                        response.put("success", true);
                        response.put("user_id", rs.getInt("user_id"));
                        response.put("full_name", rs.getString("full_name"));
                        response.put("username", username);
                        sendResponse(exchange, 200, response.toString());
                    } else {
                        sendError(exchange, 401, "Invalid username or password");
                    }
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
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    handleAddCustomer(exchange, json);
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGetCustomers(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT * FROM customers ORDER BY name";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    
                    JSONArray customers = new JSONArray();
                    while (rs.next()) {
                        JSONObject customer = new JSONObject();
                        customer.put("customer_id", rs.getInt("customer_id"));
                        customer.put("name", rs.getString("name"));
                        customer.put("phone", rs.getString("phone"));
                        customer.put("email", rs.getString("email"));
                        customer.put("address", rs.getString("address"));
                        customers.put(customer);
                    }

                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("data", customers);
                    sendResponse(exchange, 200, response.toString());
                }
            }
        }

        private void handleAddCustomer(HttpExchange exchange, JSONObject json) throws Exception {
            String name = json.getString("name");
            String phone = json.getString("phone");
            String email = json.optString("email", "");
            String address = json.optString("address", "");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, name);
                    stmt.setString(2, phone);
                    stmt.setString(3, email);
                    stmt.setString(4, address);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        JSONObject response = new JSONObject();
                        response.put("success", true);
                        response.put("message", "Customer added successfully");
                        response.put("customer_id", rs.getInt(1));
                        sendResponse(exchange, 200, response.toString());
                    }
                }
            }
        }
    }

    // Vehicles API Handler
    static class VehiclesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            try {
                if (method.equals("GET")) {
                    handleGetVehicles(exchange);
                } else if (method.equals("POST")) {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    handleAddVehicle(exchange, json);
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGetVehicles(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT v.*, c.name as customer_name FROM vehicles v " +
                              "LEFT JOIN customers c ON v.customer_id = c.customer_id ORDER BY v.registration_number";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    
                    JSONArray vehicles = new JSONArray();
                    while (rs.next()) {
                        JSONObject vehicle = new JSONObject();
                        vehicle.put("vehicle_id", rs.getInt("vehicle_id"));
                        vehicle.put("customer_id", rs.getInt("customer_id"));
                        vehicle.put("customer_name", rs.getString("customer_name"));
                        vehicle.put("brand", rs.getString("brand"));
                        vehicle.put("model", rs.getString("model"));
                        vehicle.put("registration_number", rs.getString("registration_number"));
                        vehicles.put(vehicle);
                    }

                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("data", vehicles);
                    sendResponse(exchange, 200, response.toString());
                }
            }
        }

        private void handleAddVehicle(HttpExchange exchange, JSONObject json) throws Exception {
            int customerId = json.getInt("customer_id");
            String brand = json.getString("brand");
            String model = json.getString("model");
            String registrationNumber = json.getString("registration_number");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO vehicles (customer_id, brand, model, registration_number) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, customerId);
                    stmt.setString(2, brand);
                    stmt.setString(3, model);
                    stmt.setString(4, registrationNumber);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        JSONObject response = new JSONObject();
                        response.put("success", true);
                        response.put("message", "Vehicle added successfully");
                        response.put("vehicle_id", rs.getInt(1));
                        sendResponse(exchange, 200, response.toString());
                    }
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
                if (method.equals("GET")) {
                    handleGetServices(exchange);
                } else if (method.equals("POST")) {
                    String body = readRequestBody(exchange);
                    JSONObject json = new JSONObject(body);
                    handleAddService(exchange, json);
                } else {
                    sendError(exchange, 400, "Invalid request method");
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }

        private void handleGetServices(HttpExchange exchange) throws Exception {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT s.*, v.registration_number, c.name as customer_name FROM services s " +
                              "JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                              "JOIN customers c ON v.customer_id = c.customer_id ORDER BY s.service_date DESC";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(query)) {
                    
                    JSONArray services = new JSONArray();
                    while (rs.next()) {
                        JSONObject service = new JSONObject();
                        service.put("service_id", rs.getInt("service_id"));
                        service.put("vehicle_id", rs.getInt("vehicle_id"));
                        service.put("registration_number", rs.getString("registration_number"));
                        service.put("customer_name", rs.getString("customer_name"));
                        service.put("service_type", rs.getString("service_type"));
                        service.put("service_date", rs.getString("service_date"));
                        service.put("status", rs.getString("status"));
                        service.put("cost", rs.getDouble("cost"));
                        services.put(service);
                    }

                    JSONObject response = new JSONObject();
                    response.put("success", true);
                    response.put("data", services);
                    sendResponse(exchange, 200, response.toString());
                }
            }
        }

        private void handleAddService(HttpExchange exchange, JSONObject json) throws Exception {
            int vehicleId = json.getInt("vehicle_id");
            String serviceType = json.getString("service_type");
            String serviceDate = json.getString("service_date");
            double cost = json.getDouble("cost");

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "INSERT INTO services (vehicle_id, service_type, service_date, status, cost) VALUES (?, ?, ?, 'Pending', ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, vehicleId);
                    stmt.setString(2, serviceType);
                    stmt.setString(3, serviceDate);
                    stmt.setDouble(4, cost);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        JSONObject response = new JSONObject();
                        response.put("success", true);
                        response.put("message", "Service added successfully");
                        response.put("service_id", rs.getInt(1));
                        sendResponse(exchange, 200, response.toString());
                    }
                }
            }
        }
    }

    // Utility Methods
    private static String readRequestBody(HttpExchange exchange) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("success", false);
        error.put("error", message);
        sendResponse(exchange, statusCode, error.toString());
    }

    private static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : messageDigest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return password;
        }
    }
}
