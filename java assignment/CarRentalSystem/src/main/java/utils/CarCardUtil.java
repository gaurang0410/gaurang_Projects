package utils;

/**
 * Utility class for car card rendering and display
 * Provides methods for formatting car information for UI display
 */
public class CarCardUtil {
    
    /**
     * Get fuel type icon class
     */
    public static String getFuelTypeIcon(String fuelType) {
        if (fuelType == null || fuelType.isEmpty()) {
            return "fas fa-gas-pump";
        }
        
        switch (fuelType.toLowerCase()) {
            case "petrol":
                return "fas fa-gas-pump";
            case "diesel":
                return "fas fa-oil-can";
            case "electric":
                return "fas fa-bolt";
            case "hybrid":
                return "fas fa-leaf";
            case "cng":
                return "fas fa-wind";
            default:
                return "fas fa-car";
        }
    }
    
    /**
     * Get availability badge class
     */
    public static String getAvailabilityBadgeClass(String status) {
        if (status == null || status.isEmpty()) {
            return "bg-secondary";
        }
        
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                return "bg-success";
            case "BOOKED":
                return "bg-danger";
            case "MAINTENANCE":
                return "bg-warning";
            default:
                return "bg-secondary";
        }
    }
    
    /**
     * Get category badge color class
     */
    public static String getCategoryBadgeClass(String category) {
        if (category == null || category.isEmpty()) {
            return "bg-info";
        }
        
        switch (category.toLowerCase()) {
            case "sedan":
                return "bg-primary";
            case "suv":
                return "bg-danger";
            case "hatchback":
                return "bg-info";
            case "luxury":
                return "bg-warning";
            case "convertible":
                return "bg-success";
            case "ev":
                return "bg-dark";
            case "muv":
                return "bg-secondary";
            default:
                return "bg-light";
        }
    }
    
    /**
     * Format price for display
     */
    public static String formatPrice(double price) {
        return String.format("₹%.0f", price);
    }
    
    /**
     * Get booking action button label
     */
    public static String getBookingActionLabel(String status) {
        if (status == null || status.isEmpty()) {
            return "Book Now";
        }
        
        switch (status.toUpperCase()) {
            case "AVAILABLE":
                return "Book Now";
            case "BOOKED":
                return "Unavailable";
            case "MAINTENANCE":
                return "Under Maintenance";
            default:
                return "Book Now";
        }
    }
    
    /**
     * Check if car is available for booking
     */
    public static boolean isAvailable(String status) {
        return status != null && status.equalsIgnoreCase("AVAILABLE");
    }
}
