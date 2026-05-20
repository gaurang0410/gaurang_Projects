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
        if (customerId <= 0) {
            System.out.println("Valid customer is required!");
            return false;
        }
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

    public boolean addVehicle(Vehicle v) {
        if (v == null || v.getCustomerId() <= 0 || v.getBrand() == null) return false;
        return vehicleDAO.addVehicle(v);
    }

    public boolean updateVehicle(int vehicleId, int customerId, String brand, String model, String registrationNumber) {
        if (customerId <= 0) {
            System.out.println("Valid customer is required!");
            return false;
        }
        if (brand == null || brand.trim().isEmpty()) {
            System.out.println("Brand cannot be empty!");
            return false;
        }

        Vehicle vehicle = new Vehicle(vehicleId, customerId, brand, model, registrationNumber);
        return vehicleDAO.updateVehicle(vehicle);
    }

    public boolean updateVehicle(Vehicle v) {
        if (v == null || v.getVehicleId() <= 0 || v.getCustomerId() <= 0) return false;
        return vehicleDAO.updateVehicle(v);
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

    public List<String> getAllBrands() {
        List<String> brands = vehicleDAO.getAllBrands();
        if (brands == null || brands.isEmpty()) {
            brands = new java.util.ArrayList<>();
            String[] defaults = {"BMW","Audi","Mercedes","Toyota","Honda","Hyundai","Porsche","Ferrari","Lamborghini","Rolls Royce","Kia","Ford","Chevrolet"};
            for (String b : defaults) brands.add(b);
        }
        return brands;
    }

    public List<String> getModelsByBrand(String brand) {
        List<String> models = vehicleDAO.getModelsByBrand(brand);
        if (models == null || models.isEmpty()) {
            models = new java.util.ArrayList<>();
            if (brand == null) return models;
            java.util.Map<String, String[]> sample = new java.util.HashMap<>();
            sample.put("BMW", new String[]{"3 Series","5 Series","X3","X5","i8"});
            sample.put("Audi", new String[]{"A3","A4","A6","Q5","Q7"});
            sample.put("Mercedes", new String[]{"C-Class","E-Class","S-Class","GLA","GLE"});
            sample.put("Toyota", new String[]{"Corolla","Camry","RAV4","Fortuner"});
            sample.put("Honda", new String[]{"Civic","Accord","CR-V","City"});
            sample.put("Hyundai", new String[]{"i10","i20","Creta","Elantra"});
            sample.put("Porsche", new String[]{"911","Cayenne","Panamera"});
            sample.put("Ferrari", new String[]{"488 GTB","Portofino"});
            sample.put("Lamborghini", new String[]{"Huracan","Aventador"});
            sample.put("Rolls Royce", new String[]{"Phantom","Ghost"});
            if (sample.containsKey(brand)) {
                for (String m : sample.get(brand)) models.add(m);
            } else {
                models.add(brand + " Model A");
                models.add(brand + " Model B");
            }
        }
        return models;
    }
}
