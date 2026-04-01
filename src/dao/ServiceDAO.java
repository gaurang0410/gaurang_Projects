package dao;

import model.Service;
import api.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Service DAO - Uses REST API instead of direct database connections
 */
public class ServiceDAO {

    // Add Service via API
    public boolean addService(Service service) {
        try {
            JSONObject data = new JSONObject();
            data.put("vehicle_id", service.getVehicleId());
            data.put("service_type", service.getServiceType());
            data.put("service_date", service.getServiceDate());
            data.put("status", service.getStatus());
            data.put("cost", service.getCost());
            
            JSONObject response = ApiClient.post("/services", data);
            System.out.println("✓ Service added: " + service.getServiceType());
            return response.optBoolean("success", true);
        } catch (Exception e) {
            System.err.println("✗ Error adding service: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update Service (placeholder)
    public boolean updateService(Service service) {
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error updating service: " + e.getMessage());
            return false;
        }
    }

    // Delete Service (placeholder)
    public boolean deleteService(int serviceId) {
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error deleting service: " + e.getMessage());
            return false;
        }
    }

    // Get Service by ID
    public Service getServiceById(int serviceId) {
        try {
            JSONObject response = ApiClient.get("/services");
            JSONArray services = response.getJSONArray("data");
            
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                if (service.getInt("service_id") == serviceId) {
                    return new Service(
                        service.getInt("service_id"),
                        service.getInt("vehicle_id"),
                        service.getString("service_type"),
                        service.getString("service_date"),
                        service.getString("status"),
                        service.getDouble("cost")
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting service: " + e.getMessage());
        }
        return null;
    }

    // Get All Services via API
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/services");
            JSONArray serviceArray = response.getJSONArray("data");
            
            for (int i = 0; i < serviceArray.length(); i++) {
                JSONObject svc = serviceArray.getJSONObject(i);
                services.add(new Service(
                    svc.getInt("service_id"),
                    svc.getInt("vehicle_id"),
                    svc.getString("service_type"),
                    svc.getString("service_date"),
                    svc.getString("status"),
                    svc.getDouble("cost")
                ));
            }
        } catch (Exception e) {
            System.err.println("Error getting services: " + e.getMessage());
        }
        return services;
    }

    // Get Services by Vehicle ID
    public List<Service> getServicesByVehicleId(int vehicleId) {
        List<Service> services = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/services");
            JSONArray serviceArray = response.getJSONArray("data");
            
            for (int i = 0; i < serviceArray.length(); i++) {
                JSONObject svc = serviceArray.getJSONObject(i);
                if (svc.getInt("vehicle_id") == vehicleId) {
                    services.add(new Service(
                        svc.getInt("service_id"),
                        svc.getInt("vehicle_id"),
                        svc.getString("service_type"),
                        svc.getString("service_date"),
                        svc.getString("status"),
                        svc.getDouble("cost")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting vehicle services: " + e.getMessage());
        }
        return services;
    }

    // Get Completed Services
    public List<Service> getCompletedServices() {
        List<Service> services = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/services");
            JSONArray serviceArray = response.getJSONArray("data");
            
            for (int i = 0; i < serviceArray.length(); i++) {
                JSONObject svc = serviceArray.getJSONObject(i);
                if ("Completed".equalsIgnoreCase(svc.getString("status"))) {
                    services.add(new Service(
                        svc.getInt("service_id"),
                        svc.getInt("vehicle_id"),
                        svc.getString("service_type"),
                        svc.getString("service_date"),
                        svc.getString("status"),
                        svc.getDouble("cost")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting completed services: " + e.getMessage());
        }
        return services;
    }

    // Update Service Status (placeholder)
    public boolean updateServiceStatus(int serviceId, String status) {
        try {
            return true; // Placeholder
        } catch (Exception e) {
            System.err.println("Error updating service status: " + e.getMessage());
            return false;
        }
    }
}
