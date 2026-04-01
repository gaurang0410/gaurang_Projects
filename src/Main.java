import gui.ModernLoginFrame;
import api.ApiServer;
import api.ApiClient;

public class Main {
    public static void main(String[] args) {
        // Set look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start API Server in a separate thread
        System.out.println("Starting REST API Server...");
        Thread apiThread = new Thread(() -> {
            try {
                ApiServer.main(new String[]{});
            } catch (Exception e) {
                System.err.println("Error starting API server: " + e.getMessage());
                e.printStackTrace();
            }
        });
        apiThread.setDaemon(true);
        apiThread.start();

        // Wait for API server to start
        System.out.println("Waiting for API server to be ready...");
        int retries = 0;
        while (retries < 30) {
            try {
                Thread.sleep(500);
                if (ApiClient.isServerRunning()) {
                    System.out.println("✓ API Server is ready!");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retries++;
        }

        if (retries >= 30) {
            System.err.println("⚠ API Server did not start in time. Continuing anyway...");
        }

        // Start the application with Modern Login Frame
        System.out.println("Launching Modern GUI...");
        new ModernLoginFrame();
    }
}
