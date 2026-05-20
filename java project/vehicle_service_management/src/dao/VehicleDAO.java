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

    public boolean addVehicle(Vehicle vehicle) {
        try {
            JSONObject data = new JSONObject();
            data.put("customer_id", vehicle.getCustomerId());
            data.put("brand", vehicle.getBrand());
            data.put("model", vehicle.getModel());
            data.put("registration_number", vehicle.getRegistrationNumber());
            data.put("category", "STANDARD"); 
            
            JSONObject response = ApiClient.post("/vehicles", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error adding vehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVehicle(Vehicle vehicle) {
        try {
            JSONObject data = new JSONObject();
            data.put("vehicle_id", vehicle.getVehicleId());
            data.put("customer_id", vehicle.getCustomerId());
            data.put("brand", vehicle.getBrand());
            data.put("model", vehicle.getModel());
            data.put("registration_number", vehicle.getRegistrationNumber());
            data.put("category", "STANDARD");
            
            JSONObject response = ApiClient.put("/vehicles", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error updating vehicle: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVehicle(int vehicleId) {
        try {
            JSONObject response = ApiClient.delete("/vehicles?id=" + vehicleId);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error deleting vehicle: " + e.getMessage());
            return false;
        }
    }

    public Vehicle getVehicleById(int vehicleId) {
        try {
            JSONObject response = ApiClient.get("/vehicles");
            if (response != null && response.has("data")) {
                JSONArray vehicles = response.getJSONArray("data");
                for (int i = 0; i < vehicles.length(); i++) {
                    JSONObject v = vehicles.getJSONObject(i);
                    if (v.optInt("vehicle_id", -1) == vehicleId) {
                        return new Vehicle(
                            v.optInt("vehicle_id", -1),
                            v.optInt("customer_id", -1),
                            v.optString("brand"),
                            v.optString("model"),
                            v.optString("registration_number")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting vehicle: " + e.getMessage());
        }
        return null;
    }

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/vehicles");
            if (response != null && response.has("data")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject v = array.getJSONObject(i);
                    list.add(new Vehicle(
                        v.optInt("vehicle_id", -1), v.optInt("customer_id", -1),
                        v.optString("brand"), v.optString("model"),
                        v.optString("registration_number")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading vehicles: " + e.getMessage());
        }
        return list;
    }

    public List<Vehicle> getVehiclesByCustomerId(int customerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/vehicles");
            if (response != null && response.has("data")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject v = array.getJSONObject(i);
                    if (v.optInt("customer_id", -1) == customerId) {
                        vehicles.add(new Vehicle(
                            v.optInt("vehicle_id", -1), v.optInt("customer_id", -1),
                            v.optString("brand"), v.optString("model"),
                            v.optString("registration_number")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading customer vehicles: " + e.getMessage());
        }
        return vehicles;
    }

    public Vehicle searchByRegistrationNumber(String reg) {
        for (Vehicle v : getAllVehicles()) {
            if (v.getRegistrationNumber().equalsIgnoreCase(reg)) return v;
        }
        return null;
    }

    public List<String> getAllBrands() {
        List<String> list = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/vehicles/brands");
            if (response.optBoolean("success")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getModelsByBrand(String brand) {
        List<String> list = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/vehicles/models?brand=" + java.net.URLEncoder.encode(brand, "UTF-8"));
            if (response.optBoolean("success")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
