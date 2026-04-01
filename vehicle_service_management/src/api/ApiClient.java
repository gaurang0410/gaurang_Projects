package api;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * API Client for making HTTP requests to the REST API server
 */
public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final int TIMEOUT = 5000;

    /**
     * Make a GET request to the API
     */
    public static JSONObject get(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);

        int responseCode = conn.getResponseCode();
        String response = readResponse(conn, responseCode);

        if (responseCode == 200) {
            return new JSONObject(response);
        } else {
            try {
                JSONObject error = new JSONObject(response);
                throw new Exception(error.optString("error", "API Error: " + responseCode));
            } catch (Exception e) {
                throw new Exception("API Error (" + responseCode + "): " + response);
            }
        }
    }

    /**
     * Make a POST request to the API
     */
    public static JSONObject post(String endpoint, JSONObject data) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = data.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        String response = readResponse(conn, responseCode);

        if (responseCode == 200) {
            return new JSONObject(response);
        } else {
            try {
                JSONObject error = new JSONObject(response);
                throw new Exception(error.optString("error", "API Error: " + responseCode));
            } catch (Exception e) {
                throw new Exception("API Error (" + responseCode + "): " + response);
            }
        }
    }

    /**
     * Read the response from HTTP connection
     */
    private static String readResponse(HttpURLConnection conn, int responseCode) throws IOException {
        InputStream stream = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
        if (stream == null) {
            return "";
        }
        BufferedReader br = new BufferedReader(
            new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        return response.toString();
    }

    /**
     * Check if API server is running
     */
    public static boolean isServerRunning() {
        try {
            URL url = new URL(BASE_URL + "/health");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
