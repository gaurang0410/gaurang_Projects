package utils;

import java.util.*;

/**
 * Configuration class for predefined filter options
 * Ensures filters always show all available options regardless of DB contents
 */
public class FilterOptions {
    
    // Car Category Options
    public static final List<String> CAR_CATEGORIES = Collections.unmodifiableList(Arrays.asList(
        "Sedan",
        "SUV",
        "Hatchback",
        "Luxury",
        "Convertible",
        "EV",
        "MUV"
    ));
    
    // Fuel Type Options
    public static final List<String> FUEL_TYPES = Collections.unmodifiableList(Arrays.asList(
        "Petrol",
        "Diesel",
        "Electric",
        "Hybrid",
        "CNG"
    ));
    
    // Location Options (Major Indian Cities)
    public static final List<String> LOCATIONS = Collections.unmodifiableList(Arrays.asList(
        "Mumbai",
        "Delhi",
        "Bangalore",
        "Pune",
        "Hyderabad",
        "Chennai",
        "Goa",
        "Kolkata"
    ));
    
    // Car Status Options
    public static final List<String> CAR_STATUS = Collections.unmodifiableList(Arrays.asList(
        "Available",
        "Booked",
        "Maintenance"
    ));
    
    // Booking Status Options
    public static final List<String> BOOKING_STATUS = Collections.unmodifiableList(Arrays.asList(
        "PENDING",
        "CONFIRMED",
        "COMPLETED",
        "CANCELLED"
    ));
    
    /**
     * Get all car categories
     */
    public static List<String> getCategories() {
        return CAR_CATEGORIES;
    }
    
    /**
     * Get all fuel types
     */
    public static List<String> getFuelTypes() {
        return FUEL_TYPES;
    }
    
    /**
     * Get all locations
     */
    public static List<String> getLocations() {
        return LOCATIONS;
    }
    
    /**
     * Get all car statuses
     */
    public static List<String> getCarStatus() {
        return CAR_STATUS;
    }
    
    /**
     * Get all booking statuses
     */
    public static List<String> getBookingStatus() {
        return BOOKING_STATUS;
    }
    
    /**
     * Check if a value is valid for a category
     */
    public static boolean isValidCategory(String category) {
        return CAR_CATEGORIES.contains(category);
    }
    
    /**
     * Check if a value is valid for fuel type
     */
    public static boolean isValidFuelType(String fuelType) {
        return FUEL_TYPES.contains(fuelType);
    }
    
    /**
     * Check if a value is valid for location
     */
    public static boolean isValidLocation(String location) {
        return LOCATIONS.contains(location);
    }
    
    /**
     * Check if a value is valid for car status
     */
    public static boolean isValidCarStatus(String status) {
        return CAR_STATUS.contains(status);
    }
    
    /**
     * Check if a value is valid for booking status
     */
    public static boolean isValidBookingStatus(String status) {
        return BOOKING_STATUS.contains(status);
    }
}
