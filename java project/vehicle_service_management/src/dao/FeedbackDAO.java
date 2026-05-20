package dao;

import api.ApiClient;
import model.FeedbackReview;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {
    public boolean addFeedback(int serviceId, int customerId, Integer mechanicId, int rating, String comment) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("service_id", serviceId);
            payload.put("customer_id", customerId);
            if (mechanicId != null) payload.put("mechanic_id", mechanicId);
            payload.put("rating", rating);
            payload.put("comment", comment == null ? "" : comment);
            JSONObject response = ApiClient.post("/feedback", payload);
            return response.optBoolean("success", false);
        } catch (Exception e) {
            return false;
        }
    }

    public List<FeedbackReview> getAllFeedback() {
        List<FeedbackReview> list = new ArrayList<>();
        try {
            JSONObject response = ApiClient.get("/feedback");
            JSONArray data = response.optJSONArray("data");
            if (data == null) return list;
            for (int i = 0; i < data.length(); i++) {
                JSONObject f = data.getJSONObject(i);
                FeedbackReview review = new FeedbackReview();
                review.setFeedbackId(f.optInt("feedback_id", -1));
                review.setServiceId(f.optInt("service_id", -1));
                review.setCustomerId(f.optInt("customer_id", -1));
                review.setMechanicId(f.isNull("mechanic_id") ? null : f.optInt("mechanic_id", -1));
                review.setRating(f.optInt("rating", 0));
                review.setComment(f.optString("comment", ""));
                review.setCreatedAt(f.optString("created_at", ""));
                review.setCustomerName(f.optString("customer_name", ""));
                review.setMechanicName(f.optString("mechanic_name", ""));
                list.add(review);
            }
        } catch (Exception e) {
            System.err.println("Error loading feedback: " + e.getMessage());
        }
        return list;
    }
}
