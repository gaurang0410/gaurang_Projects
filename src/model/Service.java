package model;

public class Service {
    private int serviceId;
    private int vehicleId;
    private String serviceType;
    private String serviceDate;
    private String status;  // Pending, In Progress, Completed
    private double cost;

    public Service() {
    }

    public Service(int serviceId, int vehicleId, String serviceType, String serviceDate, String status, double cost) {
        this.serviceId = serviceId;
        this.vehicleId = vehicleId;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.status = status;
        this.cost = cost;
    }

    public Service(int vehicleId, String serviceType, String serviceDate, String status, double cost) {
        this.vehicleId = vehicleId;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.status = status;
        this.cost = cost;
    }

    // Getters and Setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", vehicleId=" + vehicleId +
                ", serviceType='" + serviceType + '\'' +
                ", serviceDate='" + serviceDate + '\'' +
                ", status='" + status + '\'' +
                ", cost=" + cost +
                '}';
    }
}
