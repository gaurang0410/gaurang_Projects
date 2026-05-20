package dao;

import model.Customer;
import api.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer DAO - Uses REST API instead of direct database connections
 */
public class CustomerDAO {

    // Add Customer via API
    public boolean addCustomer(Customer customer) {
        try {
            JSONObject data = new JSONObject();
            data.put("name", customer.getName());
            data.put("phone", customer.getPhone());
            data.put("email", customer.getEmail());
            data.put("address", customer.getAddress());
            
            JSONObject response = ApiClient.post("/customers", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    // Update Customer via API
    public boolean updateCustomer(Customer customer) {
        try {
            JSONObject data = new JSONObject();
            data.put("customer_id", customer.getCustomerId());
            data.put("name", customer.getName());
            data.put("phone", customer.getPhone());
            data.put("email", customer.getEmail());
            data.put("address", customer.getAddress());
            
            JSONObject response = ApiClient.put("/customers", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    // Delete Customer via API
    public boolean deleteCustomer(int customerId) {
        try {
            JSONObject response = ApiClient.delete("/customers?id=" + customerId);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    // Get Customer by ID
    public Customer getCustomerById(int customerId) {
        try {
            JSONObject response = ApiClient.get("/customers");
            if (response != null && response.has("data")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject c = array.getJSONObject(i);
                    if (c.optInt("customer_id", -1) == customerId) {
                        return new Customer(
                            c.optInt("customer_id", -1), c.optString("name"),
                            c.optString("phone"), c.optString("email"), c.optString("address")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting customer: " + e.getMessage());
        }
        return null;
    }

    // Get All Customers
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/customers");
            if (response != null && response.has("data")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject c = array.getJSONObject(i);
                    list.add(new Customer(
                        c.optInt("customer_id", -1), c.optString("name"),
                        c.optString("phone"), c.optString("email"), c.optString("address")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
        return list;
    }

    // Search Customer by Name
    public List<Customer> searchCustomerByName(String name) {
        List<Customer> result = new ArrayList<>();
        String n = name.toLowerCase();
        for (Customer c : getAllCustomers()) {
            if (c.getName().toLowerCase().contains(n)) result.add(c);
        }
        return result;
    }
}
