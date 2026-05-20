package service;

import model.Service;
import model.Vehicle;
import model.Customer;
import dao.ServiceDAO;
import dao.VehicleDAO;
import dao.CustomerDAO;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ServiceManager {
    private ServiceDAO serviceDAO;
    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;

    public ServiceManager() {
        this.serviceDAO = new ServiceDAO();
        this.vehicleDAO = new VehicleDAO();
        this.customerDAO = new CustomerDAO();
    }

    public boolean addService(int vehicleId, String serviceType, String serviceDate, String status, double cost) {
        if (serviceType == null || serviceType.trim().isEmpty()) {
            System.out.println("Service Type cannot be empty!");
            return false;
        }
        if (serviceDate == null || serviceDate.trim().isEmpty()) {
            System.out.println("Service Date cannot be empty!");
            return false;
        }
        if (cost < 0) {
            System.out.println("Cost cannot be negative!");
            return false;
        }

        Service service = new Service(vehicleId, serviceType, serviceDate, status, cost);
        return serviceDAO.addService(service);
    }

    public boolean updateService(int serviceId, int vehicleId, String serviceType, String serviceDate, String status, double cost) {
        Service service = new Service(serviceId, vehicleId, serviceType, serviceDate, status, cost);
        return serviceDAO.updateService(service);
    }

    public boolean deleteService(int serviceId) {
        return serviceDAO.deleteService(serviceId);
    }

    public Service getServiceById(int serviceId) {
        return serviceDAO.getServiceById(serviceId);
    }

    public List<Service> getAllServices() {
        return serviceDAO.getAllServices();
    }

    public List<Service> getServicesByVehicleId(int vehicleId) {
        return serviceDAO.getServicesByVehicleId(vehicleId);
    }

    public List<Service> getCompletedServices() {
        return serviceDAO.getCompletedServices();
    }

    public boolean updateServiceStatus(int serviceId, String status) {
        return serviceDAO.updateServiceStatus(serviceId, status);
    }

    public double calculateTotalCostForVehicle(int vehicleId) {
        List<Service> services = serviceDAO.getServicesByVehicleId(vehicleId);
        double totalCost = 0;
        for (Service service : services) {
            totalCost += service.getCost();
        }
        return totalCost;
    }

    public String generateBillInfo(int serviceId) {
        Service service = serviceDAO.getServiceById(serviceId);
        if (service == null) {
            return "Service not found!";
        }

        Vehicle vehicle = vehicleDAO.getVehicleById(service.getVehicleId());
        if (vehicle == null) {
            return "Vehicle not found!";
        }

        Customer customer = customerDAO.getCustomerById(vehicle.getCustomerId());
        if (customer == null) {
            return "Customer not found!";
        }

        StringBuilder bill = new StringBuilder();
        bill.append("========== SERVICE BILL ==========\n\n");
        bill.append("Customer Information:\n");
        bill.append("Name: ").append(customer.getName()).append("\n");
        bill.append("Phone: ").append(customer.getPhone()).append("\n");
        bill.append("Email: ").append(customer.getEmail()).append("\n");
        bill.append("Address: ").append(customer.getAddress()).append("\n\n");
        bill.append("Vehicle Information:\n");
        bill.append("Brand: ").append(vehicle.getBrand()).append("\n");
        bill.append("Model: ").append(vehicle.getModel()).append("\n");
        bill.append("Registration: ").append(vehicle.getRegistrationNumber()).append("\n\n");
        bill.append("Service Information:\n");
        bill.append("Service ID: ").append(service.getServiceId()).append("\n");
        bill.append("Service Type: ").append(service.getServiceType()).append("\n");
        bill.append("Service Date: ").append(service.getServiceDate()).append("\n");
        bill.append("Status: ").append(service.getStatus()).append("\n");
        bill.append("Cost: $").append(String.format("%.2f", service.getCost())).append("\n\n");
        bill.append("================================\n");

        return bill.toString();
    }

    public List<Service> getServicesByCustomerId(int customerId) {
        List<Service> allServices = serviceDAO.getAllServices();
        List<Service> customerServices = new java.util.ArrayList<>();
        VehicleDAO vDao = new VehicleDAO();
        List<Vehicle> customerVehicles = vDao.getVehiclesByCustomerId(customerId);
        
        java.util.Set<Integer> vehicleIds = new java.util.HashSet<>();
        for (Vehicle v : customerVehicles) {
            vehicleIds.add(v.getVehicleId());
        }
        
        for (Service s : allServices) {
            if (vehicleIds.contains(s.getVehicleId())) {
                customerServices.add(s);
            }
        }
        return customerServices;
    }

    public String getSmartRecommendationForCustomer(int customerId) {
        List<Service> services = getServicesByCustomerId(customerId);
        if (services.isEmpty()) {
            return "No service history found. General check-up recommended.";
        }

        Service latest = null;
        LocalDate latestDate = null;
        for (Service service : services) {
            LocalDate parsed = tryParseDate(service.getServiceDate());
            if (parsed != null && (latestDate == null || parsed.isAfter(latestDate))) {
                latestDate = parsed;
                latest = service;
            }
        }

        if (latestDate == null || latest == null) {
            return "Regular maintenance recommended for optimal performance.";
        }

        long months = ChronoUnit.MONTHS.between(latestDate, LocalDate.now());
        if (months >= 6) {
            return "Oil Change recommended (last done " + months + " month(s) ago).";
        }
        if (months >= 3) {
            return "General inspection recommended (last service " + months + " month(s) ago).";
        }
        return "Next routine service due in about " + (6 - months) + " month(s).";
    }

    public String getServiceReminderForCustomer(int customerId) {
        List<Service> services = getServicesByCustomerId(customerId);
        if (services.isEmpty()) {
            return "No previous service record. Book your first service.";
        }

        LocalDate latestDate = null;
        for (Service service : services) {
            if (!"Completed".equalsIgnoreCase(service.getStatus())) {
                continue;
            }
            LocalDate parsed = tryParseDate(service.getServiceDate());
            if (parsed != null && (latestDate == null || parsed.isAfter(latestDate))) {
                latestDate = parsed;
            }
        }

        if (latestDate == null) {
            return "No completed service found yet. Complete one service to enable reminders.";
        }

        long daysSince = ChronoUnit.DAYS.between(latestDate, LocalDate.now());
        if (daysSince > 180) {
            return "Overdue: your last completed service was " + daysSince + " day(s) ago.";
        }
        if (daysSince > 90) {
            return "Reminder: service is due soon (" + daysSince + " day(s) since last completion).";
        }
        long nextDueIn = 90 - daysSince;
        return "On track: next routine service due in " + Math.max(nextDueIn, 0) + " day(s).";
    }

    public int getReminderSeverityForCustomer(int customerId) {
        List<Service> services = getServicesByCustomerId(customerId);
        LocalDate latestDate = null;
        for (Service service : services) {
            if (!"Completed".equalsIgnoreCase(service.getStatus())) {
                continue;
            }
            LocalDate parsed = tryParseDate(service.getServiceDate());
            if (parsed != null && (latestDate == null || parsed.isAfter(latestDate))) {
                latestDate = parsed;
            }
        }
        if (latestDate == null) return 0;
        long daysSince = ChronoUnit.DAYS.between(latestDate, LocalDate.now());
        if (daysSince > 180) return 2;
        if (daysSince > 90) return 1;
        return 0;
    }

    private LocalDate tryParseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}
