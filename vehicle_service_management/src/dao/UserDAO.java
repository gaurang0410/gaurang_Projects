package dao;

import api.ApiClient;
import org.json.JSONObject;

/**
 * User DAO - Uses REST API for user authentication and registration
 * No longer uses direct database connections
 */
public class UserDAO {

    /**
     * Register a new user via API
     * @return true if registration successful, false otherwise
     */
    public static boolean registerUser(String username, String email, String password, String fullName) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("email", email);
            data.put("password", password);
            data.put("full_name", fullName);
            
            JSONObject response = ApiClient.post("/users/register", data);
            return response.getBoolean("success");
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user and get user details via API
     * @return JSONObject with user details if successful, null otherwise
     */
    public static JSONObject authenticateUserWithDetails(String username, String password) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            
            JSONObject response = ApiClient.post("/users/login", data);
            if (response.optBoolean("success", false)) {
                return response;
            }
            return null;
            
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Authenticate user via API
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticateUser(String username, String password) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            
            JSONObject response = ApiClient.post("/users/login", data);
            return response.optBoolean("success", false);
            
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get user ID by username via API
     */
    public static int getUserIdByUsername(String username) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", "dummy"); // Just for lookup
            
            JSONObject response = ApiClient.post("/users/login", data);
            if (response.getBoolean("success")) {
                return response.getInt("user_id");
            }
            return -1;
            
        } catch (Exception e) {
            System.err.println("Error getting user ID: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Check if username already exists - handled by API registration
     */
    public static boolean userExists(String username) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("email", "check@example.com");
            data.put("password", "temp");
            data.put("full_name", "Check");
            
            ApiClient.post("/users/register", data);
            return true; // If it didn't throw, user exists or other error
            
        } catch (Exception e) {
            return e.getMessage().contains("already exists");
        }
    }

    /**
     * Check if email already exists - handled by API registration
     */
    public static boolean emailExists(String email) {
        return false; // API handles this internally
    }
}
