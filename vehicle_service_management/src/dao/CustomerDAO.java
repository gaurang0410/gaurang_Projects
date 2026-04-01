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
            System.out.println("✓ Customer added: " + customer.getName());
            return response.optBoolean("success", true);
        } catch (Exception e) {
            System.err.println("✗ Error adding customer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update Customer (handled locally, can be extended)
    public boolean updateCustomer(Customer customer) {
        // API update not implemented yet, using direct DB for now
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    // Delete Customer (handled locally, can be extended)
    public boolean deleteCustomer(int customerId) {
        // API delete not implemented yet
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    // Get Customer by ID
    public Customer getCustomerById(int customerId) {
        try {
            JSONObject response = ApiClient.get("/customers");
            JSONArray customers = response.getJSONArray("data");
            
            for (int i = 0; i < customers.length(); i++) {
                JSONObject cust = customers.getJSONObject(i);
                if (cust.getInt("customer_id") == customerId) {
                    return new Customer(
                        cust.getInt("customer_id"),
                        cust.getString("name"),
                        cust.getString("phone"),
                        cust.getString("email"),
                        cust.getString("address")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting customer: " + e.getMessage());
        }
        return null;
    }

    // Get All Customers via API
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/customers");
            JSONArray customerArray = response.getJSONArray("data");
            
            for (int i = 0; i < customerArray.length(); i++) {
                JSONObject cust = customerArray.getJSONObject(i);
                customers.add(new Customer(
                    cust.getInt("customer_id"),
                    cust.getString("name"),
                    cust.getString("phone"),
                    cust.getString("email"),
                    cust.getString("address")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting customers: " + e.getMessage());
        }
        return customers;
    }

    // Search Customer by Name
    public List<Customer> searchCustomerByName(String name) {
        List<Customer> customers = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/customers");
            JSONArray customerArray = response.getJSONArray("data");
            
            String searchName = name.toLowerCase();
            for (int i = 0; i < customerArray.length(); i++) {
                JSONObject cust = customerArray.getJSONObject(i);
                if (cust.getString("name").toLowerCase().contains(searchName)) {
                    customers.add(new Customer(
                        cust.getInt("customer_id"),
                        cust.getString("name"),
                        cust.getString("phone"),
                        cust.getString("email"),
                        cust.getString("address")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
        return customers;
    }
}
