package gui;

import service.ServiceManager;
import service.VehicleService;
import model.Service;
import model.Vehicle;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ServiceForm extends JFrame {
    private JComboBox<String> vehicleCombo;
    private JTextField serviceTypeField;
    private JTextField serviceDateField;
    private JComboBox<String> statusCombo;
    private JTextField costField;
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton viewAllBtn;
    private JButton clearBtn;
    private JTable serviceTable;
    private JScrollPane scrollPane;
    private ServiceManager serviceManager;
    private VehicleService vehicleService;
    private int selectedServiceId = -1;
    private java.util.Map<String, Integer> vehicleMap;

    public ServiceForm() {
        setTitle("Service Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        serviceManager = new ServiceManager();
        vehicleService = new VehicleService();
        vehicleMap = new java.util.HashMap<>();
        initComponents();
        loadAllServices();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 245, 250));

        // Title
        JLabel titleLabel = new JLabel("Service Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(350, 10, 250, 30);
        mainPanel.add(titleLabel);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(10, 50, 400, 280);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Vehicle
        JLabel vehicleLabel = new JLabel("Vehicle:");
        vehicleLabel.setBounds(20, 20, 80, 25);
        formPanel.add(vehicleLabel);
        vehicleCombo = new JComboBox<>();
        vehicleCombo.setBounds(120, 20, 250, 25);
        loadVehiclesToCombo();
        formPanel.add(vehicleCombo);

        // Service Type
        JLabel serviceTypeLabel = new JLabel("Service Type:");
        serviceTypeLabel.setBounds(20, 60, 80, 25);
        formPanel.add(serviceTypeLabel);
        serviceTypeField = new JTextField();
        serviceTypeField.setBounds(120, 60, 250, 25);
        formPanel.add(serviceTypeField);

        // Service Date
        JLabel serviceDateLabel = new JLabel("Service Date:");
        serviceDateLabel.setBounds(20, 100, 80, 25);
        formPanel.add(serviceDateLabel);
        serviceDateField = new JTextField();
        serviceDateField.setBounds(120, 100, 250, 25);
        formPanel.add(serviceDateField);

        // Status
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(20, 140, 80, 25);
        formPanel.add(statusLabel);
        statusCombo = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        statusCombo.setBounds(120, 140, 250, 25);
        formPanel.add(statusCombo);

        // Cost
        JLabel costLabel = new JLabel("Cost:");
        costLabel.setBounds(20, 180, 80, 25);
        formPanel.add(costLabel);
        costField = new JTextField();
        costField.setBounds(120, 180, 250, 25);
        formPanel.add(costField);

        // Buttons
        saveBtn = new JButton("Save");
        saveBtn.setBounds(20, 220, 80, 30);
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
        formPanel.add(saveBtn);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(110, 220, 80, 30);
        updateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdate();
            }
        });
        formPanel.add(updateBtn);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(200, 220, 80, 30);
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });
        formPanel.add(deleteBtn);

        clearBtn = new JButton("Clear");
        clearBtn.setBounds(290, 220, 80, 30);
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        formPanel.add(clearBtn);

        mainPanel.add(formPanel);

        // View All Button
        viewAllBtn = new JButton("View All Services");
        viewAllBtn.setBounds(10, 360, 400, 30);
        viewAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllServices();
            }
        });
        mainPanel.add(viewAllBtn);

        // Table
        serviceTable = new JTable();
        scrollPane = new JScrollPane(serviceTable);
        scrollPane.setBounds(420, 50, 460, 500);
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    private void loadVehiclesToCombo() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        for (Vehicle v : vehicles) {
            String displayText = v.getBrand() + " " + v.getModel() + " (" + v.getRegistrationNumber() + ")";
            vehicleCombo.addItem(displayText);
            vehicleMap.put(displayText, v.getVehicleId());
        }
    }

    private void handleSave() {
        if (vehicleCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String vehicleText = (String) vehicleCombo.getSelectedItem();
        int vehicleId = vehicleMap.get(vehicleText);
        String serviceType = serviceTypeField.getText();
        String serviceDate = serviceDateField.getText();
        String status = (String) statusCombo.getSelectedItem();
        String costStr = costField.getText();

        if (serviceType.isEmpty() || serviceDate.isEmpty() || costStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double cost = Double.parseDouble(costStr);
            if (serviceManager.addService(vehicleId, serviceType, serviceDate, status, cost)) {
                JOptionPane.showMessageDialog(this, "Service added successfully!");
                clearForm();
                loadAllServices();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding service!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cost must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedServiceId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String vehicleText = (String) vehicleCombo.getSelectedItem();
        int vehicleId = vehicleMap.get(vehicleText);
        String serviceType = serviceTypeField.getText();
        String serviceDate = serviceDateField.getText();
        String status = (String) statusCombo.getSelectedItem();
        String costStr = costField.getText();

        if (serviceType.isEmpty() || serviceDate.isEmpty() || costStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double cost = Double.parseDouble(costStr);
            if (serviceManager.updateService(selectedServiceId, vehicleId, serviceType, serviceDate, status, cost)) {
                JOptionPane.showMessageDialog(this, "Service updated successfully!");
                clearForm();
                loadAllServices();
                selectedServiceId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error updating service!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cost must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedServiceId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this service?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (serviceManager.deleteService(selectedServiceId)) {
                JOptionPane.showMessageDialog(this, "Service deleted successfully!");
                clearForm();
                loadAllServices();
                selectedServiceId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting service!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAllServices() {
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

        serviceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = serviceTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    selectedServiceId = (int) serviceTable.getValueAt(row, 0);
                    serviceTypeField.setText((String) serviceTable.getValueAt(row, 2));
                    serviceDateField.setText((String) serviceTable.getValueAt(row, 3));
                    statusCombo.setSelectedItem((String) serviceTable.getValueAt(row, 4));
                    costField.setText((String) serviceTable.getValueAt(row, 5));
                }
            }
        });

        scrollPane.setViewportView(serviceTable);
    }

    private void clearForm() {
        serviceTypeField.setText("");
        serviceDateField.setText("");
        costField.setText("");
        statusCombo.setSelectedIndex(0);
        vehicleCombo.setSelectedIndex(0);
        selectedServiceId = -1;
    }
}
