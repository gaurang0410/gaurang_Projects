package gui;

import dao.VehicleDAO;
import model.Vehicle;
import service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Customer vehicle registration panel.
 * Uses the already-logged-in customerId — no customer selection needed.
 * Shows registered vehicles in a table and lets the customer add new ones.
 */
public class CustomerVehicleRegistrationPanel extends JPanel {

    private final int customerId;
    private final VehicleService vehicleService = new VehicleService();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    // Form fields
    private JComboBox<String> brandCombo;
    private JComboBox<String> modelCombo;
    private JTextField registrationField;
    private JComboBox<String> categoryCombo;

    // Validation labels
    private JLabel brandError, modelError, regError, catError;

    // Table showing already-registered vehicles
    private JTable vehicleTable;
    private javax.swing.table.DefaultTableModel tableModel;

    // Optional callback to notify when a vehicle is registered (e.g. refresh BookServiceFrame)
    private Runnable onVehicleRegisteredCallback;

    private boolean isLoading = false;

    public CustomerVehicleRegistrationPanel(int customerId) {
        this.customerId = customerId;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(20, 24, 20, 24));
        initComponents();
        SwingUtilities.invokeLater(() -> {
            loadBrands();
            loadMyVehicles();
        });
    }

    /** Optional: set a callback to fire after successful vehicle registration */
    public void setOnVehicleRegisteredCallback(Runnable callback) {
        this.onVehicleRegisteredCallback = callback;
    }

    // ─── Build UI ─────────────────────────────────────────────────────────────

    private void initComponents() {
        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("My Vehicles");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel subtitle = UITheme.bodyLabel(
            "Register your vehicle to book service appointments.");
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ── Split: Form (left) + Table (right) ──────────────────────────────
        JPanel split = new JPanel(new GridLayout(1, 2, 20, 0));
        split.setOpaque(false);

        split.add(buildFormCard());
        split.add(buildTableCard());

        add(split, BorderLayout.CENTER);
    }

    // ── Registration Form Card ─────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = UITheme.accentCard(UITheme.ACCENT_CYAN);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.anchor  = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        int row = 0;

        // Section header
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(sectionLabel("Register a New Vehicle"), gbc);
        gbc.insets = new Insets(6, 0, 2, 0);

        // Brand
        brandCombo = UITheme.styledComboBox();
        brandCombo.addActionListener(e -> {
            if (isLoading) return;
            loadModels((String) brandCombo.getSelectedItem());
            brandError.setText(" ");
        });
        brandError = errorLabel();
        row = addRow(card, gbc, row, "Vehicle Brand", brandCombo, brandError);

        // Model
        modelCombo = UITheme.styledComboBox();
        modelError = errorLabel();
        row = addRow(card, gbc, row, "Vehicle Model", modelCombo, modelError);

        // Registration Number
        registrationField = UITheme.styledTextField("e.g. MH12AB1234");
        // Auto-uppercase on every keystroke
        registrationField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { enforceUpperCase(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { /* no-op */ }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { /* no-op */ }
            private void enforceUpperCase() {
                SwingUtilities.invokeLater(() -> {
                    String text = registrationField.getText();
                    String upper = text.toUpperCase();
                    if (!text.equals(upper)) {
                        int caret = registrationField.getCaretPosition();
                        registrationField.setText(upper);
                        registrationField.setCaretPosition(Math.min(caret, upper.length()));
                    }
                    regError.setText(" ");
                });
            }
        });
        regError = errorLabel();
        row = addRow(card, gbc, row, "Registration No.", registrationField, regError);

        // Category
        categoryCombo = UITheme.styledComboBox(new String[]{
            "STANDARD", "PREMIUM", "SUV", "ELECTRIC", "LUXURY", "COMMERCIAL"
        });
        catError = errorLabel();
        row = addRow(card, gbc, row, "Vehicle Category", categoryCombo, catError);

        // Save Button
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        JButton saveBtn = UITheme.accentButton(
            "Register Vehicle", UITheme.ACCENT_GREEN,
            UITheme.getIcon("vehicle", Color.WHITE, 16));
        saveBtn.setPreferredSize(new Dimension(0, 48));
        saveBtn.addActionListener(e -> handleRegister());
        card.add(saveBtn, gbc);

        return card;
    }

    // ── My Vehicles Table Card ─────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = UITheme.accentCard(UITheme.ACCENT_AMBER);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel(
            "My Registered Vehicles",
            UITheme.getIcon("vehicle", UITheme.ACCENT_AMBER, 18),
            SwingConstants.LEFT);
        tableTitle.setFont(UITheme.FONT_HEADING);
        tableTitle.setForeground(UITheme.TEXT_PRIMARY);
        tableTitle.setIconTextGap(8);
        card.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Brand", "Model", "Registration No.", "Category"};
        tableModel = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vehicleTable = new JTable(tableModel);
        UITheme.styleTable(vehicleTable);
        vehicleTable.setRowHeight(42);

        JScrollPane scroll = UITheme.styledScrollPane(vehicleTable);
        card.add(scroll, BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = UITheme.ghostButton("Refresh List", UITheme.ACCENT_CYAN);
        refreshBtn.setPreferredSize(new Dimension(160, 38));
        refreshBtn.addActionListener(e -> loadMyVehicles());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.add(refreshBtn);
        card.add(btnRow, BorderLayout.SOUTH);

        return card;
    }

    // ── Form Row Helper ────────────────────────────────────────────────────
    private int addRow(JPanel p, GridBagConstraints gbc, int row,
                       String label, JComponent field, JLabel error) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.38;
        gbc.insets = new Insets(6, 0, 2, 10);
        p.add(UITheme.styledLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), gbc);
        gbc.gridx = 1; gbc.weightx = 0.62;
        gbc.insets = new Insets(6, 0, 2, 0);
        p.add(field, gbc);
        row++;
        if (error != null) {
            gbc.gridy = row; gbc.gridx = 1; gbc.gridwidth = 1;
            gbc.insets = new Insets(0, 0, 4, 0);
            p.add(error, gbc);
            row++;
        }
        return row;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(UITheme.ACCENT_CYAN);
        l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_DEFAULT));
        return l;
    }

    private JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(UITheme.FONT_TINY);
        l.setForeground(UITheme.ACCENT_RED);
        return l;
    }

    // ─── Data Loaders ─────────────────────────────────────────────────────────

    private void loadBrands() {
        isLoading = true;
        brandCombo.removeAllItems();
        modelCombo.removeAllItems();
        List<String> brands = vehicleService.getAllBrands();
        for (String b : brands) brandCombo.addItem(b);
        isLoading = false;
        if (brandCombo.getItemCount() > 0) {
            brandCombo.setSelectedIndex(0);
            loadModels((String) brandCombo.getSelectedItem());
        }
    }

    private void loadModels(String brand) {
        modelCombo.removeAllItems();
        if (brand == null || brand.isEmpty()) return;
        List<String> models = vehicleService.getModelsByBrand(brand);
        for (String m : models) modelCombo.addItem(m);
        if (modelCombo.getItemCount() > 0) modelCombo.setSelectedIndex(0);
    }

    private void loadMyVehicles() {
        tableModel.setRowCount(0);
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByCustomerId(customerId);
        for (Vehicle v : vehicles) {
            tableModel.addRow(new Object[]{
                v.getBrand(),
                v.getModel(),
                v.getRegistrationNumber(),
                v.getVehicleCategory() != null ? v.getVehicleCategory() : "STANDARD"
            });
        }
        if (vehicles.isEmpty()) {
            // Show a friendly "none registered yet" row
            tableModel.addRow(new Object[]{
                "—", "No vehicles registered yet", "—", "—"
            });
        }
    }

    // ─── Registration Handler ──────────────────────────────────────────────────

    private void handleRegister() {
        if (!validateForm()) return;

        String brand       = (String) brandCombo.getSelectedItem();
        String model       = (String) modelCombo.getSelectedItem();
        String regNum      = registrationField.getText().trim().toUpperCase();
        String category    = (String) categoryCombo.getSelectedItem();

        // Duplicate check
        Vehicle existing = vehicleDAO.searchByRegistrationNumber(regNum);
        if (existing != null) {
            regError.setText("Registration number already exists.");
            UITheme.showAlert(
                SwingUtilities.getWindowAncestor(this),
                "Duplicate Registration",
                "A vehicle with registration number [" + regNum + "] already exists.",
                UITheme.AlertType.WARNING);
            return;
        }

        // Build vehicle object with category
        Vehicle v = new Vehicle(customerId, brand, model, regNum);
        v.setVehicleCategory(category);

        boolean success = vehicleService.addVehicle(v);

        if (success) {
            UITheme.showAlert(
                SwingUtilities.getWindowAncestor(this),
                "Vehicle Registered",
                brand + " " + model + " [" + regNum + "] has been registered successfully.",
                UITheme.AlertType.SUCCESS);
            clearForm();
            loadMyVehicles(); // refresh table
            // Fire callback so BookServiceFrame (if open) can reload its combo
            if (onVehicleRegisteredCallback != null) {
                onVehicleRegisteredCallback.run();
            }
        } else {
            UITheme.showAlert(
                SwingUtilities.getWindowAncestor(this),
                "Registration Failed",
                "Could not register the vehicle. Please try again.",
                UITheme.AlertType.ERROR);
        }
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    private boolean validateForm() {
        boolean valid = true;

        // Brand
        if (brandCombo.getSelectedItem() == null ||
                ((String) brandCombo.getSelectedItem()).isEmpty()) {
            brandError.setText("Please select a vehicle brand.");
            valid = false;
        } else {
            brandError.setText(" ");
        }

        // Model
        if (modelCombo.getSelectedItem() == null ||
                ((String) modelCombo.getSelectedItem()).isEmpty()) {
            modelError.setText("Please select a vehicle model.");
            valid = false;
        } else {
            modelError.setText(" ");
        }

        // Registration number
        String reg = registrationField.getText().trim().toUpperCase();
        if (reg.isEmpty()) {
            regError.setText("Registration number is required.");
            valid = false;
        } else if (!reg.matches("[A-Z0-9 \\-]{3,20}")) {
            regError.setText("Invalid format. Use letters, digits, spaces or hyphens (3–20 chars).");
            valid = false;
        } else {
            regError.setText(" ");
        }

        // Category
        if (categoryCombo.getSelectedItem() == null) {
            catError.setText("Please select a vehicle category.");
            valid = false;
        } else {
            catError.setText(" ");
        }

        return valid;
    }

    private void clearForm() {
        registrationField.setText("");
        if (categoryCombo.getItemCount() > 0) categoryCombo.setSelectedIndex(0);
        brandError.setText(" ");
        modelError.setText(" ");
        regError.setText(" ");
        catError.setText(" ");
    }
}
