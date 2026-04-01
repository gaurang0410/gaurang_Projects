package gui;

import service.VehicleService;
import service.CustomerService;
import model.Vehicle;
import model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VehicleForm extends JFrame {
    private JComboBox<String> customerCombo;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField registrationField;
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton searchBtn;
    private JButton viewAllBtn;
    private JButton clearBtn;
    private JTable vehicleTable;
    private JScrollPane scrollPane;
    private VehicleService vehicleService;
    private CustomerService customerService;
    private int selectedVehicleId = -1;
    private java.util.Map<String, Integer> customerMap;

    public VehicleForm() {
        setTitle("Vehicle Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        vehicleService = new VehicleService();
        customerService = new CustomerService();
        customerMap = new java.util.HashMap<>();
        initComponents();
        loadAllVehicles();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 245, 250));

        // Title
        JLabel titleLabel = new JLabel("Vehicle Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(350, 10, 250, 30);
        mainPanel.add(titleLabel);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(10, 50, 400, 300);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Customer
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setBounds(20, 20, 80, 25);
        formPanel.add(customerLabel);
        customerCombo = new JComboBox<>();
        customerCombo.setBounds(120, 20, 250, 25);
        loadCustomersToCombo();
        formPanel.add(customerCombo);

        // Brand
        JLabel brandLabel = new JLabel("Brand:");
        brandLabel.setBounds(20, 60, 80, 25);
        formPanel.add(brandLabel);
        brandField = new JTextField();
        brandField.setBounds(120, 60, 250, 25);
        formPanel.add(brandField);

        // Model
        JLabel modelLabel = new JLabel("Model:");
        modelLabel.setBounds(20, 100, 80, 25);
        formPanel.add(modelLabel);
        modelField = new JTextField();
        modelField.setBounds(120, 100, 250, 25);
        formPanel.add(modelField);

        // Registration
        JLabel registrationLabel = new JLabel("Registration:");
        registrationLabel.setBounds(20, 140, 80, 25);
        formPanel.add(registrationLabel);
        registrationField = new JTextField();
        registrationField.setBounds(120, 140, 250, 25);
        formPanel.add(registrationField);

        // Buttons
        saveBtn = new JButton("Save");
        saveBtn.setBounds(20, 180, 80, 30);
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
        formPanel.add(saveBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(110, 180, 80, 30);
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdate();
            }
        });
        formPanel.add(updateBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(200, 180, 80, 30);
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });
        formPanel.add(deleteBtn);

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(290, 180, 80, 30);
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        formPanel.add(clearBtn);

        // Search
        searchBtn = new JButton("Search by Reg");
        searchBtn.setBounds(20, 240, 120, 30);
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });
        formPanel.add(searchBtn);

        viewAllBtn = new JButton("View All");
        viewAllBtn.setBounds(160, 240, 100, 30);
        viewAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllVehicles();
            }
        });
        formPanel.add(viewAllBtn);

        mainPanel.add(formPanel);

        // Table
        vehicleTable = new JTable();
        scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBounds(420, 50, 460, 500);
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    private void loadCustomersToCombo() {
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer c : customers) {
            customerCombo.addItem(c.getName());
            customerMap.put(c.getName(), c.getCustomerId());
        }
    }

    private void handleSave() {
        if (customerCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String customerName = (String) customerCombo.getSelectedItem();
        int customerId = customerMap.get(customerName);
        String brand = brandField.getText();
        String model = modelField.getText();
        String registration = registrationField.getText();

        if (brand.isEmpty() || model.isEmpty() || registration.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (vehicleService.addVehicle(customerId, brand, model, registration)) {
            JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
            clearForm();
            loadAllVehicles();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String customerName = (String) customerCombo.getSelectedItem();
        int customerId = customerMap.get(customerName);
        String brand = brandField.getText();
        String model = modelField.getText();
        String registration = registrationField.getText();

        if (brand.isEmpty() || model.isEmpty() || registration.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (vehicleService.updateVehicle(selectedVehicleId, customerId, brand, model, registration)) {
            JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
            clearForm();
            loadAllVehicles();
            selectedVehicleId = -1;
        } else {
            JOptionPane.showMessageDialog(this, "Error updating vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedVehicleId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this vehicle?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (vehicleService.deleteVehicle(selectedVehicleId)) {
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
                clearForm();
                loadAllVehicles();
                selectedVehicleId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch() {
        String registration = JOptionPane.showInputDialog(this, "Enter registration number:");
        if (registration != null && !registration.isEmpty()) {
            Vehicle vehicle = vehicleService.searchByRegistrationNumber(registration);
            if (vehicle == null) {
                JOptionPane.showMessageDialog(this, "Vehicle not found!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                java.util.List<Vehicle> vehicles = new java.util.ArrayList<>();
                vehicles.add(vehicle);
                loadVehiclesToTable(vehicles);
            }
        }
    }

    private void loadAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        loadVehiclesToTable(vehicles);
    }

    private void loadVehiclesToTable(List<Vehicle> vehicles) {
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

        vehicleTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = vehicleTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    selectedVehicleId = (int) vehicleTable.getValueAt(row, 0);
                    customerCombo.setSelectedIndex((int) vehicleTable.getValueAt(row, 1) - 1);
                    brandField.setText((String) vehicleTable.getValueAt(row, 2));
                    modelField.setText((String) vehicleTable.getValueAt(row, 3));
                    registrationField.setText((String) vehicleTable.getValueAt(row, 4));
                }
            }
        });

        scrollPane.setViewportView(vehicleTable);
    }

    private void clearForm() {
        brandField.setText("");
        modelField.setText("");
        registrationField.setText("");
        customerCombo.setSelectedIndex(0);
        selectedVehicleId = -1;
    }
}
