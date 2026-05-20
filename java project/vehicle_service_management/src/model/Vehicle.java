package model;

public class Vehicle {
    private int vehicleId;
    private int customerId;
    private String brand;
    private String model;
    private String registrationNumber;
    private String customerName;
    private String vehicleCategory;

    public Vehicle() {
    }

    public Vehicle(int vehicleId, int customerId, String brand, String model, String registrationNumber) {
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
    }

    public Vehicle(int customerId, String brand, String model, String registrationNumber) {
        this.customerId = customerId;
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
    }

    // Getters and Setters
    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehicleCategory() {
        return vehicleCategory;
    }

    public void setVehicleCategory(String vehicleCategory) {
        this.vehicleCategory = vehicleCategory;
    }

    /**
     * Returns user-friendly label for display in JComboBox dropdowns.
     */
    @Override
    public String toString() {
        return (brand != null ? brand : "") + " " + (model != null ? model : "") + " [" + (registrationNumber != null ? registrationNumber : "") + "]";
    }
}
