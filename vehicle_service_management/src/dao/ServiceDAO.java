package dao;

import model.Service;
import api.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Service DAO - Uses REST API instead of direct database connections
 * Fixed signatures and added support for estimated time from schema
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
            if (service.getMechanicId() != null) {
                data.put("mechanic_id", service.getMechanicId());
            }
            data.put("estimated_time", "2 hours"); // Default from schema logic
            
            JSONObject response = ApiClient.post("/services", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error adding service: " + e.getMessage());
            return false;
        }
    }

    // Overloaded for backward compatibility if needed
    public boolean addService(int vehicleId, String type, String date, String status, double cost, String est) {
        Service s = new Service(vehicleId, type, date, status, cost);
        return addService(s);
    }

    // Update Service via API
    public boolean updateService(Service service) {
        try {
            JSONObject data = new JSONObject();
            data.put("service_id", service.getServiceId());
            data.put("vehicle_id", service.getVehicleId());
            data.put("service_type", service.getServiceType());
            data.put("service_date", service.getServiceDate());
            data.put("status", service.getStatus());
            data.put("cost", service.getCost());
            if (service.getMechanicId() != null) {
                data.put("mechanic_id", service.getMechanicId());
            } else {
                data.put("mechanic_id", JSONObject.NULL);
            }
            
            JSONObject response = ApiClient.put("/services", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error updating service: " + e.getMessage());
            return false;
        }
    }

    // Update Status Only
    public boolean updateServiceStatus(int id, String status) {
        try {
            JSONObject data = new JSONObject();
            data.put("service_id", id);
            data.put("status", status);
            JSONObject response = ApiClient.put("/services", data);
            return response.optBoolean("success", false);
        } catch (Exception e) { return false; }
    }

    // Delete Service via API
    public boolean deleteService(int serviceId) {
        try {
            JSONObject response = ApiClient.delete("/services?id=" + serviceId);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            System.err.println("Error deleting service: " + e.getMessage());
            return false;
        }
    }

    // Get Service by ID
    public Service getServiceById(int serviceId) {
        try {
            JSONObject response = ApiClient.get("/services");
            if (response != null && response.has("data")) {
                JSONArray services = response.getJSONArray("data");
                for (int i = 0; i < services.length(); i++) {
                    JSONObject s = services.getJSONObject(i);
                    if (s.optInt("service_id", -1) == serviceId) {
                        Service service = new Service(
                            s.optInt("service_id", -1),
                            s.optInt("vehicle_id", -1),
                            s.isNull("mechanic_id") ? null : s.optInt("mechanic_id", -1),
                            s.optString("service_type"),
                            s.optString("service_date"),
                            s.optString("status"),
                            s.optDouble("cost", 0.0)
                        );
                        service.setMechanicName(s.optString("mechanic_name", ""));
                        return service;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting service: " + e.getMessage());
        }
        return null;
    }

    // Get All Services
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/services");
            if (response != null && response.has("data")) {
                JSONArray array = response.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject s = array.getJSONObject(i);
                    Service service = new Service(
                        s.optInt("service_id", -1), s.optInt("vehicle_id", -1),
                        s.isNull("mechanic_id") ? null : s.optInt("mechanic_id", -1),
                        s.optString("service_type"), s.optString("service_date"),
                        s.optString("status"), s.optDouble("cost", 0.0)
                    );
                    service.setMechanicName(s.optString("mechanic_name", ""));
                    services.add(service);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading services: " + e.getMessage());
        }
        return services;
    }

    // Get Completed Services
    public List<Service> getCompletedServices() {
        List<Service> list = new ArrayList<>();
        for (Service s : getAllServices()) {
            if ("Completed".equalsIgnoreCase(s.getStatus())) list.add(s);
        }
        return list;
    }

    // Get Services by Vehicle ID
    public List<Service> getServicesByVehicleId(int vehicleId) {
        List<Service> list = new ArrayList<>();
        for (Service s : getAllServices()) {
            if (s.getVehicleId() == vehicleId) list.add(s);
        }
        return list;
    }
}
