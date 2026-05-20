package gui;

import service.ServiceManager;
import service.VehicleService;
import dao.InventoryDAO;
import dao.ServiceCatalogDAO;
import dao.MechanicDAO;
import model.Mechanic;
import model.Service;
import model.Vehicle;
import model.ServiceCatalogItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ServiceForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> vehicleCombo;
    private JComboBox<String> serviceTypeCombo;
    private JComboBox<String> partCombo;
    private JTextField serviceDateField;
    private JComboBox<String> statusCombo;
    private JComboBox<String> mechanicCombo;
    private JTextField costField;
    
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton clearBtn;
    private JTable serviceTable;
    private JScrollPane scrollPane;
    
    private ServiceManager serviceManager;
    private VehicleService vehicleService;
    private ServiceCatalogDAO catalogDAO;
    private InventoryDAO inventoryDAO;
    
    private int selectedServiceId = -1;
    private java.util.Map<String, Integer> vehicleMap;
    private java.util.Map<String, ServiceCatalogItem> catalogMap;
    private java.util.Map<String, InventoryDAO.InventoryItem> inventoryMap;
    private java.util.Map<String, Integer> mechanicMap;

    public ServiceForm() {
        setTitle("VehicleFlow - Service Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        serviceManager = new ServiceManager();
        vehicleService = new VehicleService();
        catalogDAO = new ServiceCatalogDAO();
        inventoryDAO = new InventoryDAO();
        
        vehicleMap = new java.util.HashMap<>();
        catalogMap = new java.util.HashMap<>();
        inventoryMap = new java.util.HashMap<>();
        mechanicMap = new java.util.HashMap<>();
        
        initComponents();
        loadAllServices();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.gradientPanel(UITheme.BG_DARK, UITheme.BG_CARD);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Title
        JLabel titleLabel = UITheme.styledLabel(" Service Management");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.ACCENT_CYAN);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setOpaque(false);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        int gridy = 0;

        // Vehicle
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Vehicle:"), gbc);
        gbc.gridx = 1;
        vehicleCombo = UITheme.styledComboBox();
        loadVehiclesToCombo();
        formPanel.add(vehicleCombo, gbc);
        gridy++;

        // Service Type
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Service:"), gbc);
        gbc.gridx = 1;
        serviceTypeCombo = UITheme.styledComboBox();
        loadCatalogToCombo();
        serviceTypeCombo.addActionListener(e -> updateCostFromSelection());
        formPanel.add(serviceTypeCombo, gbc);
        gridy++;

        // Parts Used
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Part Used:"), gbc);
        gbc.gridx = 1;
        partCombo = UITheme.styledComboBox();
        loadInventoryToCombo();
        partCombo.addActionListener(e -> updateCostFromSelection());
        formPanel.add(partCombo, gbc);
        gridy++;

        // Date
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Date:"), gbc);
        gbc.gridx = 1;
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        serviceDateField = UITheme.styledTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        serviceDateField.setEditable(false);
        JButton dateBtn = UITheme.accentButton("...", UITheme.ACCENT_CYAN);
        dateBtn.setPreferredSize(new Dimension(40, 42));
        dateBtn.addActionListener(e -> {
            String picked = new DatePicker(this).setPickedDate();
            if (!picked.isEmpty()) serviceDateField.setText(picked);
        });
        datePanel.add(serviceDateField, BorderLayout.CENTER);
        datePanel.add(dateBtn, BorderLayout.EAST);
        formPanel.add(datePanel, gbc);
        gridy++;

        // Status
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = UITheme.styledComboBox();
        for (String s : new String[]{"Pending", "In Progress", "Completed"}) statusCombo.addItem(s);
        formPanel.add(statusCombo, gbc);
        gridy++;

        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Assigned Mechanic:"), gbc);
        gbc.gridx = 1;
        mechanicCombo = UITheme.styledComboBox();
        loadMechanicsToCombo();
        formPanel.add(mechanicCombo, gbc);
        gridy++;

        // Cost
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Total Cost:"), gbc);
        gbc.gridx = 1;
        costField = UITheme.styledTextField("0.00");
        formPanel.add(costField, gbc);
        gridy++;

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setOpaque(false);
        
        saveBtn = UITheme.accentButton("Save", UITheme.ACCENT_GREEN);
        saveBtn.addActionListener(e -> handleSave());
        buttonPanel.add(saveBtn);

        updateBtn = UITheme.accentButton("Update", UITheme.ACCENT_BLUE);
        updateBtn.addActionListener(e -> handleUpdate());
        buttonPanel.add(updateBtn);

        deleteBtn = UITheme.accentButton("Delete", UITheme.ACCENT_RED);
        deleteBtn.addActionListener(e -> handleDelete());
        buttonPanel.add(deleteBtn);

        clearBtn = UITheme.accentButton("Clear", UITheme.BORDER_DEFAULT);
        clearBtn.addActionListener(e -> clearForm());
        buttonPanel.add(clearBtn);
        setRowActionState(false);

        gbc.gridx = 0; gbc.gridy = gridy; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.WEST);

        // Table
        serviceTable = new JTable();
        UITheme.styleTable(serviceTable);
        scrollPane = UITheme.styledScrollPane(serviceTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        updateCostFromSelection();
    }

    private void loadVehiclesToCombo() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        vehicleCombo.removeAllItems();
        for (Vehicle v : vehicles) {
            String display = v.getRegistrationNumber() + " (" + v.getBrand() + ")";
            vehicleCombo.addItem(display);
            vehicleMap.put(display, v.getVehicleId());
        }
    }

    private void loadCatalogToCombo() {
        serviceTypeCombo.removeAllItems();
        List<ServiceCatalogItem> items = catalogDAO.getAllServices();
        for (ServiceCatalogItem item : items) {
            serviceTypeCombo.addItem(item.getServiceName());
            catalogMap.put(item.getServiceName(), item);
        }
    }

    private void loadInventoryToCombo() {
        partCombo.removeAllItems();
        partCombo.addItem("None");
        List<InventoryDAO.InventoryItem> items = inventoryDAO.getAllItems();
        for (InventoryDAO.InventoryItem item : items) {
            if (item.stock > 0) {
                partCombo.addItem(item.name);
                inventoryMap.put(item.name, item);
            }
        }
    }

    private void loadMechanicsToCombo() {
        mechanicCombo.removeAllItems();
        mechanicMap.clear();
        mechanicCombo.addItem("Unassigned");
        mechanicMap.put("Unassigned", null);
        List<Mechanic> mechanics = new MechanicDAO().getAllMechanics();
        for (Mechanic mechanic : mechanics) {
            String display = mechanic.getName() + " (" + mechanic.getSpecialization() + ")";
            mechanicCombo.addItem(display);
            mechanicMap.put(display, mechanic.getMechanicId());
        }
    }

    private void updateCostFromSelection() {
        double total = 0;
        String svc = (String) serviceTypeCombo.getSelectedItem();
        if (svc != null && catalogMap.containsKey(svc)) {
            total += catalogMap.get(svc).getBaseCost();
        }
        
        String part = (String) partCombo.getSelectedItem();
        if (part != null && !part.equals("None") && inventoryMap.containsKey(part)) {
            total += inventoryMap.get(part).price;
        }
        
        costField.setText(String.format("%.2f", total));
    }

    private void handleSave() {
        if (vehicleCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Select a vehicle", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Integer selectedVehicleId = vehicleMap.get((String) vehicleCombo.getSelectedItem());
        if (selectedVehicleId == null || selectedVehicleId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a valid vehicle", "Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int vehicleId = selectedVehicleId;
        String serviceType = (String) serviceTypeCombo.getSelectedItem();
        String date = serviceDateField.getText();
        String status = (String) statusCombo.getSelectedItem();
        if (serviceType == null || serviceType.trim().isEmpty() || date == null || date.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select service type and date", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double cost;
        try {
            cost = Double.parseDouble(costField.getText().replace(",", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid cost format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer mechanicId = mechanicMap.get((String) mechanicCombo.getSelectedItem());
        Service s = new Service(vehicleId, mechanicId, serviceType, date, status, cost);
        if (new dao.ServiceDAO().addService(s)) {
            
            // Reduce stock
            String part = (String) partCombo.getSelectedItem();
            if (part != null && !part.equals("None")) {
                InventoryDAO.InventoryItem item = inventoryMap.get(part);
                if (item != null) {
                    inventoryDAO.updateStock(item.id, item.stock - 1);
                    loadInventoryToCombo(); // reload
                }
            }
            
            JOptionPane.showMessageDialog(this, "Service saved successfully!");
            clearForm();
            loadAllServices();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving service", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedServiceId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (vehicleCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer selectedVehicleId = vehicleMap.get((String) vehicleCombo.getSelectedItem());
        int vehicleId = selectedVehicleId == null ? -1 : selectedVehicleId;
        String serviceType = (String) serviceTypeCombo.getSelectedItem();
        String date = serviceDateField.getText();
        String status = (String) statusCombo.getSelectedItem();
        double cost;
        try {
            cost = Double.parseDouble(costField.getText().replace(",", ""));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid cost format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (vehicleId <= 0 || serviceType == null || serviceType.trim().isEmpty() || date == null || date.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All service fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer mechanicId = mechanicMap.get((String) mechanicCombo.getSelectedItem());
        Service updatedService = new Service(selectedServiceId, vehicleId, mechanicId, serviceType, date, status, cost);
        if (new dao.ServiceDAO().updateService(updatedService)) {
            JOptionPane.showMessageDialog(this, "Service updated successfully!");
            clearForm();
            loadAllServices();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating service", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedServiceId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete Service?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (serviceManager.deleteService(selectedServiceId)) {
                JOptionPane.showMessageDialog(this, "Deleted successfully");
                clearForm();
                loadAllServices();
            }
        }
    }

    private void loadAllServices() {
        List<Service> list = serviceManager.getAllServices();
        String[] cols = {"ID", "Vehicle ID", "Type", "Date", "Status", "Mechanic", "Cost"};
        Object[][] data = new Object[list.size()][7];
        
        for (int i=0; i<list.size(); i++) {
            Service s = list.get(i);
            data[i][0] = s.getServiceId();
            data[i][1] = s.getVehicleId();
            data[i][2] = s.getServiceType();
            data[i][3] = s.getServiceDate();
            data[i][4] = s.getStatus();
            data[i][5] = s.getMechanicName() == null || s.getMechanicName().trim().isEmpty() ? "Unassigned" : s.getMechanicName();
            data[i][6] = s.getCost();
        }
        
        serviceTable.setModel(new javax.swing.table.DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(serviceTable);
        setRowActionState(false);
        
        for (java.awt.event.MouseListener ml : serviceTable.getMouseListeners()) {
            serviceTable.removeMouseListener(ml);
        }
        serviceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = serviceTable.rowAtPoint(e.getPoint());
                if (r >= 0) {
                    selectedServiceId = (int) serviceTable.getValueAt(r, 0);
                    setRowActionState(true);
                    // Match vehicle combo
                    int vid = (int) serviceTable.getValueAt(r, 1);
                    for (int i=0; i<vehicleCombo.getItemCount(); i++) {
                        Integer mappedId = vehicleMap.get(vehicleCombo.getItemAt(i));
                        if (mappedId != null && mappedId == vid) {
                            vehicleCombo.setSelectedIndex(i); break;
                        }
                    }
                    serviceTypeCombo.setSelectedItem((String)serviceTable.getValueAt(r, 2));
                    serviceDateField.setText((String)serviceTable.getValueAt(r, 3));
                    statusCombo.setSelectedItem((String)serviceTable.getValueAt(r, 4));
                    String mechanicName = String.valueOf(serviceTable.getValueAt(r, 5));
                    boolean matched = false;
                    for (int i = 0; i < mechanicCombo.getItemCount(); i++) {
                        String option = mechanicCombo.getItemAt(i);
                        if (option.startsWith(mechanicName)) {
                            mechanicCombo.setSelectedIndex(i);
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) mechanicCombo.setSelectedIndex(0);
                    costField.setText(String.valueOf(serviceTable.getValueAt(r, 6)));
                }
            }
        });
    }

    private void clearForm() {
        if (vehicleCombo.getItemCount() > 0) vehicleCombo.setSelectedIndex(0);
        if (serviceTypeCombo.getItemCount() > 0) serviceTypeCombo.setSelectedIndex(0);
        if (partCombo.getItemCount() > 0) partCombo.setSelectedIndex(0);
        if (mechanicCombo.getItemCount() > 0) mechanicCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        selectedServiceId = -1;
        updateCostFromSelection();
        setRowActionState(false);
    }

    private void setRowActionState(boolean selected) {
        updateBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
    }
}
