package dao;

import api.ApiClient;
import model.User;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * User DAO - Provides authentication and registration services.
 */
public class UserDAO {

    /**
     * Register a new user via API
     * @return true if registration successful, false otherwise
     */
    public static boolean registerUser(String username, String email, String phone, String password, String fullName) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("email", email);
            data.put("phone", phone);
            data.put("password", password);
            data.put("full_name", fullName);
            
            JSONObject response = ApiClient.post("/users/register", data);
            return response != null && response.optBoolean("success", false);
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user and get user details via API
     * @return User object if successful, null otherwise
     */
    public static User authenticateUserWithDetails(String username, String password) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            
            JSONObject response = ApiClient.post("/users/login", data);
            
            if (response != null && response.optBoolean("success", false)) {
                User user = new User();
                user.setUserId(response.optInt("user_id", -1));
                user.setUsername(response.optString("username", ""));
                user.setEmail(response.optString("email", ""));
                user.setFullName(response.optString("full_name", ""));
                user.setRole(response.optString("role", "CUSTOMER"));
                user.setPhone(response.optString("phone", ""));
                
                int cid = response.optInt("customer_id", -1);
                if (cid <= 0 && "CUSTOMER".equalsIgnoreCase(user.getRole())) {
                    cid = findCustomerIdByEmail(user.getEmail());
                }
                user.setCustomerId(cid);
                
                return user;
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return null;
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
            JSONObject response = ApiClient.get("/users/check?username=" + encode(username));
            return response.optInt("user_id", -1);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Check if username already exists
     */
    public static boolean userExists(String username) {
        try {
            JSONObject response = ApiClient.get("/users/check?username=" + encode(username));
            return response.optBoolean("exists", false);
        } catch (Exception e) {
            System.err.println("Error checking user exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if email already exists
     */
    public static boolean emailExists(String email) {
        try {
            JSONObject response = ApiClient.get("/users/check?email=" + encode(email));
            return response.optBoolean("exists", false);
        } catch (Exception e) {
            System.err.println("Error checking email exists: " + e.getMessage());
            return false;
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static int findCustomerIdByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return -1;
        }
        try {
            JSONObject response = ApiClient.get("/customers");
            JSONArray data = response.optJSONArray("data");
            if (data == null) {
                return -1;
            }
            for (int i = 0; i < data.length(); i++) {
                JSONObject c = data.optJSONObject(i);
                if (c != null && email.equalsIgnoreCase(c.optString("email", ""))) {
                    return c.optInt("customer_id", -1);
                }
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    public static User getUserById(int userId) {
        try {
            JSONObject response = ApiClient.get("/users?id=" + userId);
            if (!response.optBoolean("success", false)) {
                return null;
            }
            User user = new User();
            user.setUserId(response.optInt("user_id", -1));
            user.setUsername(response.optString("username", ""));
            user.setEmail(response.optString("email", ""));
            user.setFullName(response.optString("full_name", ""));
            user.setRole(response.optString("role", "CUSTOMER"));
            user.setPhone(response.optString("phone", ""));
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean updateProfile(int userId, String fullName, String email, String phone) {
        try {
            JSONObject data = new JSONObject();
            data.put("user_id", userId);
            data.put("full_name", fullName);
            data.put("email", email);
            data.put("phone", phone);
            JSONObject response = ApiClient.put("/users/profile", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean changePassword(int userId, String currentPassword, String newPassword) throws Exception {
        JSONObject data = new JSONObject();
        data.put("user_id", userId);
        data.put("current_password", currentPassword);
        data.put("new_password", newPassword);
        JSONObject response = ApiClient.put("/users/password", data);
        return response.optBoolean("success", false);
    }
}
