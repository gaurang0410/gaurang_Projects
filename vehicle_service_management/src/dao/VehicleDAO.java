package dao;

import model.Vehicle;
import api.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Vehicle DAO - Uses REST API instead of direct database connections
 */
public class VehicleDAO {

    // Add Vehicle via API
    public boolean addVehicle(Vehicle vehicle) {
        try {
            JSONObject data = new JSONObject();
            data.put("customer_id", vehicle.getCustomerId());
            data.put("brand", vehicle.getBrand());
            data.put("model", vehicle.getModel());
            data.put("registration_number", vehicle.getRegistrationNumber());
            
            JSONObject response = ApiClient.post("/vehicles", data);
            System.out.println("✓ Vehicle added: " + vehicle.getBrand() + " " + vehicle.getModel());
            return response.optBoolean("success", true);
        } catch (Exception e) {
            System.err.println("✗ Error adding vehicle: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update Vehicle (placeholder)
    public boolean updateVehicle(Vehicle vehicle) {
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }

    // Delete Vehicle (placeholder)
    public boolean deleteVehicle(int vehicleId) {
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            return false;
        }
    }

    // Get Vehicle by ID
    public Vehicle getVehicleById(int vehicleId) {
        try {
            JSONObject response = ApiClient.get("/vehicles");
            JSONArray vehicles = response.getJSONArray("data");
            
            for (int i = 0; i < vehicles.length(); i++) {
                JSONObject vehicle = vehicles.getJSONObject(i);
                if (vehicle.getInt("vehicle_id") == vehicleId) {
                    return new Vehicle(
                        vehicle.getInt("vehicle_id"),
                        vehicle.getInt("customer_id"),
                        vehicle.getString("brand"),
                        vehicle.getString("model"),
                        vehicle.getString("registration_number")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting vehicle: " + e.getMessage());
        }
        return null;
    }

    // Get All Vehicles via API
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/vehicles");
            JSONArray vehicleArray = response.getJSONArray("data");
            
            for (int i = 0; i < vehicleArray.length(); i++) {
                JSONObject veh = vehicleArray.getJSONObject(i);
                vehicles.add(new Vehicle(
                    veh.getInt("vehicle_id"),
                    veh.getInt("customer_id"),
                    veh.getString("brand"),
                    veh.getString("model"),
                    veh.getString("registration_number")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    // Get Vehicles by Customer ID
    public List<Vehicle> getVehiclesByCustomerId(int customerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/vehicles");
            JSONArray vehicleArray = response.getJSONArray("data");
            
            for (int i = 0; i < vehicleArray.length(); i++) {
                JSONObject veh = vehicleArray.getJSONObject(i);
                if (veh.getInt("customer_id") == customerId) {
                    vehicles.add(new Vehicle(
                        veh.getInt("vehicle_id"),
                        veh.getInt("customer_id"),
                        veh.getString("brand"),
                        veh.getString("model"),
                        veh.getString("registration_number")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting customer vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    // Search Vehicle by Registration Number
    public Vehicle searchByRegistrationNumber(String registrationNumber) {
        try {
            JSONObject response = ApiClient.get("/vehicles");
            JSONArray vehicleArray = response.getJSONArray("data");
            
            for (int i = 0; i < vehicleArray.length(); i++) {
                JSONObject veh = vehicleArray.getJSONObject(i);
                if (veh.getString("registration_number").equalsIgnoreCase(registrationNumber)) {
                    return new Vehicle(
                        veh.getInt("vehicle_id"),
                        veh.getInt("customer_id"),
                        veh.getString("brand"),
                        veh.getString("model"),
                        veh.getString("registration_number")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error searching vehicle: " + e.getMessage());
        }
        return null;
    }
}
