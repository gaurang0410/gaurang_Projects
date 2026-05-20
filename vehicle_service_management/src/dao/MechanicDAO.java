package dao;

import api.ApiClient;
import model.Mechanic;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MechanicDAO {
    public List<Mechanic> getAllMechanics() {
        List<Mechanic> list = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/mechanics");
            JSONArray data = response.optJSONArray("data");
            if (data == null) return list;
            for (int i = 0; i < data.length(); i++) {
                JSONObject m = data.getJSONObject(i);
                Mechanic mechanic = new Mechanic(
                        m.optInt("mechanic_id", -1),
                        m.optString("name", ""),
                        m.optString("specialization", ""),
                        m.optString("phone", ""),
                        m.optString("availability", "Available"),
                        m.optDouble("rating", 0)
                );
                mechanic.setActiveJobs(m.optInt("active_jobs", 0));
                mechanic.setCompletedJobs(m.optInt("completed_jobs", 0));
                list.add(mechanic);
            }
        } catch (Exception e) {
            System.err.println("Error loading mechanics: " + e.getMessage());
        }
        return list;
    }

    public boolean addMechanic(Mechanic mechanic) {
        try {
            JSONObject data = new JSONObject();
            data.put("name", mechanic.getName());
            data.put("specialization", mechanic.getSpecialization());
            data.put("phone", mechanic.getPhone());
            data.put("availability", mechanic.getAvailability());
            data.put("rating", mechanic.getRating());
            JSONObject response = ApiClient.post("/mechanics", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateMechanic(Mechanic mechanic) {
        try {
            JSONObject data = new JSONObject();
            data.put("mechanic_id", mechanic.getMechanicId());
            data.put("name", mechanic.getName());
            data.put("specialization", mechanic.getSpecialization());
            data.put("phone", mechanic.getPhone());
            data.put("availability", mechanic.getAvailability());
            data.put("rating", mechanic.getRating());
            JSONObject response = ApiClient.put("/mechanics", data);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteMechanic(int mechanicId) {
        try {
            JSONObject response = ApiClient.delete("/mechanics?id=" + mechanicId);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }
}
