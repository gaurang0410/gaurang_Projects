package gui;

import service.VehicleService;
import service.CustomerService;
import model.Vehicle;
import model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

public class VehicleForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> customerCombo;
    private JComboBox<String> brandCombo;
    private JComboBox<String> modelCombo;
    private JTextField registrationField;
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton clearBtn;
    private JTable vehicleTable;
    private JScrollPane scrollPane;
    private VehicleService vehicleService;
    private CustomerService customerService;
    private int selectedVehicleId = -1;
    private java.util.Map<String, Integer> customerMap;
    
    private int fixedCustomerId = -1; // -1 means admin view (can choose customer)
    private boolean isLoading = false;

    public VehicleForm() {
        this(-1); // Admin mode
    }

    public VehicleForm(int customerId) {
        this.fixedCustomerId = customerId;
        setTitle(fixedCustomerId == -1 ? "Vehicle Management (Admin)" : "My Vehicles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        
        vehicleService = new VehicleService();
        customerService = new CustomerService();
        customerMap = new java.util.HashMap<>();
        
        isLoading = true;
        initComponents();
        loadAllVehicles();
        isLoading = false;
        
        if (brandCombo.getItemCount() > 0) {
            loadModelsToCombo((String) brandCombo.getSelectedItem());
        }
        
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.gradientPanel(UITheme.BG_DARK, UITheme.BG_CARD);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Title
        JLabel titleLabel = UITheme.styledLabel(fixedCustomerId == -1 ? " Vehicle Management" : " Register My Vehicle");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.ACCENT_CYAN);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center Content
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

        // Customer (Hide if Customer mode)
        if (fixedCustomerId == -1) {
            gbc.gridx = 0; gbc.gridy = gridy;
            formPanel.add(UITheme.styledLabel("Customer:"), gbc);
            gbc.gridx = 1;
            customerCombo = UITheme.styledComboBox();
            loadCustomersToCombo();
            formPanel.add(customerCombo, gbc);
            gridy++;
        }

        // Brand
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Brand:"), gbc);
        gbc.gridx = 1;
        brandCombo = UITheme.styledComboBox();
        loadBrandsToCombo();
        brandCombo.addActionListener(e -> {
            if (isLoading) return;
            String sel = (String) brandCombo.getSelectedItem();
            loadModelsToCombo(sel);
        });
        formPanel.add(brandCombo, gbc);
        gridy++;

        // Model
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Model:"), gbc);
        gbc.gridx = 1;
        modelCombo = UITheme.styledComboBox();
        formPanel.add(modelCombo, gbc);
        gridy++;

        // Registration
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Registration:"), gbc);
        gbc.gridx = 1;
        registrationField = UITheme.styledTextField("Registration No.");
        formPanel.add(registrationField, gbc);
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
        vehicleTable = new JTable();
        UITheme.styleTable(vehicleTable);
        scrollPane = UITheme.styledScrollPane(vehicleTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void loadCustomersToCombo() {
        customerMap.clear();
        customerCombo.removeAllItems();
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer c : customers) {
            String display = c.getCustomerId() + " - " + c.getName();
            customerCombo.addItem(display);
            customerMap.put(display, c.getCustomerId());
        }
    }

    private void loadBrandsToCombo() {
        brandCombo.removeAllItems();
        List<String> brands = vehicleService.getAllBrands();
        for (String brand : brands) {
            brandCombo.addItem(brand);
        }
    }

    private void loadModelsToCombo(String brand) {
        modelCombo.removeAllItems();
        if (brand == null) return;
        List<String> models = vehicleService.getModelsByBrand(brand);
        for (String model : models) {
            modelCombo.addItem(model);
        }
    }

    private void handleSave() {
        int customerId = fixedCustomerId;
        if (fixedCustomerId == -1) {
            if (customerCombo.getSelectedItem() == null) {
                UITheme.showAlert(this, "Customer Required", "Please select a customer!", UITheme.AlertType.ERROR);
                return;
            }
            Integer selected = customerMap.get((String) customerCombo.getSelectedItem());
            customerId = selected == null ? -1 : selected;
        }

        if (customerId <= 0) {
            UITheme.showAlert(this, "Invalid Customer", "Please choose a valid customer profile.", UITheme.AlertType.WARNING);
            return;
        }

        String brand = (String) brandCombo.getSelectedItem();
        String model = (String) modelCombo.getSelectedItem();
        String registration = registrationField.getText().trim();

        if (brand == null || model == null || registration.isEmpty()) {
            UITheme.showAlert(this, "Validation Error", "All vehicle fields are required!", UITheme.AlertType.WARNING);
            return;
        }

        if (vehicleService.addVehicle(customerId, brand, model, registration)) {
            UITheme.showAlert(this, "Vehicle Registered", "Vehicle " + registration + " has been added successfully.", UITheme.AlertType.SUCCESS);
            clearForm();
            loadAllVehicles();
        } else {
            UITheme.showAlert(this, "Registration Failed", "Could not register vehicle. Check if registration number already exists.", UITheme.AlertType.ERROR);
        }
    }

    private void handleUpdate() {
        if (selectedVehicleId == -1) {
            UITheme.showAlert(this, "No Selection", "Please select a vehicle from the table to update.", UITheme.AlertType.ERROR);
            return;
        }

        int customerId = fixedCustomerId;
        if (fixedCustomerId == -1) {
            if (customerCombo.getSelectedItem() == null) {
                UITheme.showAlert(this, "Selection Error", "Please select a customer first.", UITheme.AlertType.ERROR);
                return;
            }
            Integer selected = customerMap.get((String) customerCombo.getSelectedItem());
            customerId = selected == null ? -1 : selected;
        }

        if (customerId <= 0) {
            UITheme.showAlert(this, "Invalid Selection", "Please choose a valid customer profile.", UITheme.AlertType.WARNING);
            return;
        }

        String brand = (String) brandCombo.getSelectedItem();
        String model = (String) modelCombo.getSelectedItem();
        String registration = registrationField.getText().trim();

        if (brand == null || model == null || registration.isEmpty()) {
            UITheme.showAlert(this, "Missing Data", "All fields are required for update.", UITheme.AlertType.ERROR);
            return;
        }

        if (vehicleService.updateVehicle(selectedVehicleId, customerId, brand, model, registration)) {
            UITheme.showAlert(this, "Update Successful", "Vehicle details have been updated.", UITheme.AlertType.SUCCESS);
            clearForm();
            loadAllVehicles();
            selectedVehicleId = -1;
            setRowActionState(false);
        } else {
            UITheme.showAlert(this, "Update Failed", "System encountered an error during update.", UITheme.AlertType.ERROR);
        }
    }

    private void handleDelete() {
        if (selectedVehicleId == -1) {
            UITheme.showAlert(this, "No Selection", "Please select a vehicle to delete.", UITheme.AlertType.ERROR);
            return;
        }

        if (UITheme.showConfirm(this, "Confirm Delete", "Are you sure you want to delete this vehicle? This action cannot be undone.")) {
            if (vehicleService.deleteVehicle(selectedVehicleId)) {
                UITheme.showAlert(this, "Deleted", "Vehicle record has been removed.", UITheme.AlertType.SUCCESS);
                clearForm();
                loadAllVehicles();
                selectedVehicleId = -1;
                setRowActionState(false);
            } else {
                UITheme.showAlert(this, "Delete Failed", "System could not remove the vehicle record.", UITheme.AlertType.ERROR);
            }
        }
    }

    private void loadAllVehicles() {
        List<Vehicle> vehicles;
        if (fixedCustomerId == -1) {
            vehicles = vehicleService.getAllVehicles();
        } else {
            vehicles = vehicleService.getVehiclesByCustomerId(fixedCustomerId);
        }
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

        vehicleTable.setModel(new javax.swing.table.DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        UITheme.styleTable(vehicleTable);
        setRowActionState(false);

        // Ensure we don't add multiple mouse listeners
        for (java.awt.event.MouseListener ml : vehicleTable.getMouseListeners()) {
            vehicleTable.removeMouseListener(ml);
        }
        
        vehicleTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = vehicleTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    selectedVehicleId = (int) vehicleTable.getValueAt(row, 0);
                    setRowActionState(true);
                    if (fixedCustomerId == -1) {
                        int cid = (int) vehicleTable.getValueAt(row, 1);
                        for(int i=0; i<customerCombo.getItemCount(); i++) {
                            Integer mappedId = customerMap.get(customerCombo.getItemAt(i));
                            if (mappedId != null && mappedId == cid) {
                                customerCombo.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    brandCombo.setSelectedItem((String) vehicleTable.getValueAt(row, 2));
                    modelCombo.setSelectedItem((String) vehicleTable.getValueAt(row, 3));
                    registrationField.setText((String) vehicleTable.getValueAt(row, 4));
                }
            }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { maybeSelectRow(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeSelectRow(e); }
            private void maybeSelectRow(java.awt.event.MouseEvent e) {
                int row = vehicleTable.rowAtPoint(e.getPoint());
                if (row >= 0) vehicleTable.setRowSelectionInterval(row, row);
            }
        });

        // Right-click popup menu
        JPopupMenu vpopup = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("View Details");
        viewItem.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow();
            if (row < 0) return;
            int modelRow = vehicleTable.convertRowIndexToModel(row);
            int vid = (int) vehicleTable.getValueAt(modelRow, 0);
            Vehicle v = vehicleService.getVehicleById(vid);
            if (v != null) {
                JOptionPane.showMessageDialog(this, "Vehicle:\n" + v.toString(), "Vehicle Details", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        JMenuItem editItem = new JMenuItem("Edit");
        editItem.addActionListener(e -> {
            int row = vehicleTable.getSelectedRow(); if (row < 0) return;
            int modelRow = vehicleTable.convertRowIndexToModel(row);
            selectedVehicleId = (int) vehicleTable.getValueAt(modelRow, 0);
            setRowActionState(true);
        });
        JMenuItem delItem = new JMenuItem("Delete");
        delItem.addActionListener(e -> handleDelete());
        vpopup.add(viewItem); vpopup.add(editItem); vpopup.addSeparator(); vpopup.add(delItem);
        UITheme.styleMenu(vpopup);
        vehicleTable.setComponentPopupMenu(vpopup);
    }

    private void clearForm() {
        if (customerCombo != null && customerCombo.getItemCount() > 0) {
            customerCombo.setSelectedIndex(0);
        }
        if (brandCombo.getItemCount() > 0) brandCombo.setSelectedIndex(0);
        registrationField.setText("");
        selectedVehicleId = -1;
        setRowActionState(false);
    }

    private void setRowActionState(boolean selected) {
        updateBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
    }
}
