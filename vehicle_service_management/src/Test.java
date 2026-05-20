import api.ApiClient;
import org.json.JSONObject;

public class Test {
    public static void main(String[] args) {
        try {
            JSONObject data = new JSONObject();
            data.put("vehicle_id", 1);
            data.put("customer_id", 1);
            data.put("brand", "Toyota");
            data.put("model", "Camry");
            data.put("registration_number", "12345");
            
            JSONObject response = ApiClient.put("/vehicles", data);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
