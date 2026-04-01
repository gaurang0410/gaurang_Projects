package gui;

import service.CustomerService;
import service.VehicleService;
import service.ServiceManager;
import model.Customer;
import model.Vehicle;
import model.Service;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RecordsFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable customerTable;
    private JTable vehicleTable;
    private JTable serviceTable;
    private JScrollPane customerScroll;
    private JScrollPane vehicleScroll;
    private JScrollPane serviceScroll;
    private CustomerService customerService;
    private VehicleService vehicleService;
    private ServiceManager serviceManager;

    public RecordsFrame() {
        setTitle("View Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        customerService = new CustomerService();
        vehicleService = new VehicleService();
        serviceManager = new ServiceManager();
        initComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();

        // Customer Tab
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new BorderLayout());
        customerTable = new JTable();
        customerScroll = new JScrollPane(customerTable);
        customerPanel.add(customerScroll, BorderLayout.CENTER);
        loadCustomersData();
        tabbedPane.addTab("Customers", customerPanel);

        // Vehicle Tab
        JPanel vehiclePanel = new JPanel();
        vehiclePanel.setLayout(new BorderLayout());
        vehicleTable = new JTable();
        vehicleScroll = new JScrollPane(vehicleTable);
        vehiclePanel.add(vehicleScroll, BorderLayout.CENTER);
        loadVehiclesData();
        tabbedPane.addTab("Vehicles", vehiclePanel);

        // Service Tab
        JPanel servicePanel = new JPanel();
        servicePanel.setLayout(new BorderLayout());
        serviceTable = new JTable();
        serviceScroll = new JScrollPane(serviceTable);
        servicePanel.add(serviceScroll, BorderLayout.CENTER);
        loadServicesData();
        tabbedPane.addTab("Services", servicePanel);

        add(tabbedPane);
    }

    private void loadCustomersData() {
        List<Customer> customers = customerService.getAllCustomers();
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};
        Object[][] data = new Object[customers.size()][5];

        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            data[i][0] = c.getCustomerId();
            data[i][1] = c.getName();
            data[i][2] = c.getPhone();
            data[i][3] = c.getEmail();
            data[i][4] = c.getAddress();
        }

        customerTable = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerScroll.setViewportView(customerTable);
    }

    private void loadVehiclesData() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        String[] columns = {"ID", "Customer ID", "Brand", "Model", "Registration"};
        Object[][] data = new Object[vehicles.size()][5];

        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle v = vehicles.get(i);
            data[i][0] = v.getVehicleId();
            data[i][1] = v.getCustomerId();
            data[i][2] = v.getBrand();
            data[i][3] = v.getModel();
            data[i][4] = v.getRegistrationNumber();
        }

        vehicleTable = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleScroll.setViewportView(vehicleTable);
    }

    private void loadServicesData() {
        List<Service> services = serviceManager.getAllServices();
        String[] columns = {"ID", "Vehicle ID", "Service Type", "Date", "Status", "Cost"};
        Object[][] data = new Object[services.size()][6];

        for (int i = 0; i < services.size(); i++) {
            Service s = services.get(i);
            data[i][0] = s.getServiceId();
            data[i][1] = s.getVehicleId();
            data[i][2] = s.getServiceType();
            data[i][3] = s.getServiceDate();
            data[i][4] = s.getStatus();
            data[i][5] = String.format("%.2f", s.getCost());
        }

        serviceTable = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceScroll.setViewportView(serviceTable);
    }
}
