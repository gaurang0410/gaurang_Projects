package service;

import model.Vehicle;
import dao.VehicleDAO;
import java.util.List;

public class VehicleService {
    private VehicleDAO vehicleDAO;

    public VehicleService() {
        this.vehicleDAO = new VehicleDAO();
    }

    public boolean addVehicle(int customerId, String brand, String model, String registrationNumber) {
        if (brand == null || brand.trim().isEmpty()) {
            System.out.println("Brand cannot be empty!");
            return false;
        }
        if (model == null || model.trim().isEmpty()) {
            System.out.println("Model cannot be empty!");
            return false;
        }
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            System.out.println("Registration Number cannot be empty!");
            return false;
        }

        Vehicle vehicle = new Vehicle(customerId, brand, model, registrationNumber);
        return vehicleDAO.addVehicle(vehicle);
    }

    public boolean updateVehicle(int vehicleId, int customerId, String brand, String model, String registrationNumber) {
        if (brand == null || brand.trim().isEmpty()) {
            System.out.println("Brand cannot be empty!");
            return false;
        }

        Vehicle vehicle = new Vehicle(vehicleId, customerId, brand, model, registrationNumber);
        return vehicleDAO.updateVehicle(vehicle);
    }

    public boolean deleteVehicle(int vehicleId) {
        return vehicleDAO.deleteVehicle(vehicleId);
    }

    public Vehicle getVehicleById(int vehicleId) {
        return vehicleDAO.getVehicleById(vehicleId);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleDAO.getAllVehicles();
    }

    public List<Vehicle> getVehiclesByCustomerId(int customerId) {
        return vehicleDAO.getVehiclesByCustomerId(customerId);
    }

    public Vehicle searchByRegistrationNumber(String registrationNumber) {
        return vehicleDAO.searchByRegistrationNumber(registrationNumber);
    }
}
