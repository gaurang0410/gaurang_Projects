package model;

public class Car {
    private int id;
    private String brand;
    private String model;
    private String category;
    private double pricePerDay;
    private String status;
    private String imageUrl;
    private String fuelType;
    private String location;

    public Car() {}

    public Car(int id, String brand, String model, String category, double pricePerDay, String status, String imageUrl) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public Car(int id, String brand, String model, String category, double pricePerDay, String status, String imageUrl, String fuelType, String location) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.imageUrl = imageUrl;
        this.fuelType = fuelType;
        this.location = location;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}