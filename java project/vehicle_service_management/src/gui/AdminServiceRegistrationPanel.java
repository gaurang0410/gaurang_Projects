package gui;

import dao.CustomerDAO;
import dao.MechanicDAO;
import dao.ServiceCatalogDAO;
import dao.ServiceDAO;
import dao.VehicleDAO;
import model.Customer;
import model.Mechanic;
import model.Service;
import model.ServiceCatalogItem;
import model.Vehicle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AdminServiceRegistrationPanel extends JPanel {
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ServiceCatalogDAO catalogDAO = new ServiceCatalogDAO();
    private final MechanicDAO mechanicDAO = new MechanicDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    private JComboBox<Customer> customerCombo;
    private JComboBox<Vehicle> vehicleCombo;
    private JComboBox<ServiceCatalogItem> serviceCombo;
    private JComboBox<Mechanic> mechanicCombo;
    private JTextField dateField;
    private JComboBox<String> pickupCombo, dropCombo;
    private JTextArea notesArea;
    private JLabel costLabel, estTimeLabel;
    private boolean isLoading = false;

    // Inline validation labels
    private JLabel customerError, vehicleError, serviceError, dateError;

    public AdminServiceRegistrationPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(20, 30, 20, 30));
        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        // Title section
        JPanel info = new JPanel(new BorderLayout(0, 4));
        info.setOpaque(false);
        JLabel title = new JLabel("Manual Service Registration");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitle = UITheme.bodyLabel("Register a new service request for walk-in customers or phone bookings.");
        info.add(title, BorderLayout.NORTH);
        info.add(subtitle, BorderLayout.CENTER);
        add(info, BorderLayout.NORTH);

        // Main form in a scrollable card
        JPanel mainCard = UITheme.accentCard(UITheme.ACCENT_GREEN);
        mainCard.setLayout(new GridBagLayout());
        mainCard.setBorder(new EmptyBorder(24, 28, 24, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        customerCombo = new JComboBox<>(); UITheme.styleInput(customerCombo);
        vehicleCombo = new JComboBox<>(); UITheme.styleInput(vehicleCombo);
        serviceCombo = new JComboBox<>(); UITheme.styleInput(serviceCombo);
        mechanicCombo = new JComboBox<>(); UITheme.styleInput(mechanicCombo);
        dateField = UITheme.styledTextField(LocalDate.now().toString());
        pickupCombo = UITheme.styledComboBox(new String[]{"No Pickup", "City Center", "North Zone", "West End", "East Gate", "South Bay"});
        dropCombo = UITheme.styledComboBox(new String[]{"No Drop", "City Center", "North Zone", "West End", "East Gate", "South Bay"});
        notesArea = new JTextArea(3, 20);
        notesArea.setBackground(UITheme.BG_INPUT);
        notesArea.setForeground(UITheme.TEXT_INPUT);
        notesArea.setCaretColor(UITheme.ACCENT_CYAN);
        notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        costLabel = new JLabel("Est. Cost: \u20B90.00");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        costLabel.setForeground(UITheme.ACCENT_GREEN);
        estTimeLabel = new JLabel("Est. Time: N/A");
        estTimeLabel.setFont(UITheme.FONT_BODY);
        estTimeLabel.setForeground(UITheme.TEXT_SECONDARY);

        // Inline error labels
        customerError = createErrorLabel();
        vehicleError = createErrorLabel();
        serviceError = createErrorLabel();
        dateError = createErrorLabel();

        customerCombo.addActionListener(e -> {
            if (isLoading) return;
            // Use invokeLater to ensure combo selection state is settled before loading
            SwingUtilities.invokeLater(() -> {
                loadVehiclesForSelectedCustomer();
                customerError.setText(" ");
            });
        });

        serviceCombo.addActionListener(e -> {
            if (isLoading) return;
            updateCost();
            serviceError.setText(" ");
        });

        // Row layout - 2 columns: icon+label | field
        int r = 0;

        // --- Section: Customer & Vehicle ---
        gbc.gridy = r++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1;
        mainCard.add(sectionHeader("Customer & Vehicle"), gbc);

        r = addFormRowWithIcon(mainCard, gbc, r, "user", "Select Customer", customerCombo, customerError);
        r = addFormRowWithIcon(mainCard, gbc, r, "vehicle", "Select Vehicle", vehicleCombo, vehicleError);

        // --- Section: Service Details ---
        gbc.gridy = r++; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1;
        gbc.insets = new Insets(14, 8, 6, 8);
        mainCard.add(sectionHeader("Service Details"), gbc);
        gbc.insets = new Insets(6, 8, 6, 8);

        r = addFormRowWithIcon(mainCard, gbc, r, "service", "Service Type", serviceCombo, serviceError);
        r = addFormRowWithIcon(mainCard, gbc, r, "mechanic", "Assign Mechanic", mechanicCombo, null);

        // Date with icon
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        JButton datePickerBtn = UITheme.accentButton("...", UITheme.ACCENT_CYAN);
        datePickerBtn.setPreferredSize(new Dimension(42, 38));
        datePickerBtn.addActionListener(e -> {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            String pickedDate = new DatePicker(parent).setPickedDate();
            if (!pickedDate.isEmpty()) {
                dateField.setText(pickedDate);
                dateError.setText(" ");
            }
        });
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(datePickerBtn, BorderLayout.EAST);
        r = addFormRowWithIcon(mainCard, gbc, r, "billing", "Service Date", datePanel, dateError);

        // --- Section: Logistics ---
        gbc.gridy = r++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 6, 8);
        mainCard.add(sectionHeader("Logistics"), gbc);
        gbc.insets = new Insets(6, 8, 6, 8);

        r = addFormRowWithIcon(mainCard, gbc, r, "vehicle", "Pickup Location", pickupCombo, null);
        r = addFormRowWithIcon(mainCard, gbc, r, "vehicle", "Drop Location", dropCombo, null);

        // Notes
        gbc.gridy = r++; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JPanel notesLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        notesLabelPanel.setOpaque(false);
        notesLabelPanel.add(new JLabel(UITheme.getIcon("feedback", UITheme.ACCENT_CYAN, 16)));
        notesLabelPanel.add(UITheme.styledLabel("Special Notes"));
        mainCard.add(notesLabelPanel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        mainCard.add(new JScrollPane(notesArea), gbc);

        // --- Cost Summary ---
        gbc.gridy = r++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(14, 8, 6, 8);
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.add(costLabel);
        summaryPanel.add(estTimeLabel);
        mainCard.add(summaryPanel, gbc);
        gbc.insets = new Insets(6, 8, 6, 8);

        // Register Button
        JButton registerBtn = UITheme.accentButton("Register Service", UITheme.ACCENT_GREEN, UITheme.getIcon("service", Color.WHITE, 16));
        registerBtn.setPreferredSize(new Dimension(0, 48));
        registerBtn.addActionListener(e -> handleRegistration());
        gbc.gridy = r++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        mainCard.add(registerBtn, gbc);

        JScrollPane scroll = new JScrollPane(mainCard);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private int addFormRowWithIcon(JPanel p, GridBagConstraints gbc, int row, String iconName, String label, JComponent comp, JLabel errorLabel) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        labelPanel.setOpaque(false);
        labelPanel.add(new JLabel(UITheme.getIcon(iconName, UITheme.ACCENT_CYAN, 16)));
        labelPanel.add(UITheme.styledLabel(label));
        p.add(labelPanel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(comp, gbc);
        row++;
        if (errorLabel != null) {
            gbc.gridy = row; gbc.gridx = 1;
            p.add(errorLabel, gbc);
            row++;
        }
        return row;
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(UITheme.ACCENT_CYAN);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_DEFAULT));
        return lbl;
    }

    private JLabel createErrorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(UITheme.FONT_TINY);
        lbl.setForeground(UITheme.ACCENT_RED);
        return lbl;
    }

    private void loadInitialData() {
        isLoading = true;
        customerCombo.removeAllItems();
        for (Customer c : customerDAO.getAllCustomers()) customerCombo.addItem(c);
        
        serviceCombo.removeAllItems();
        for (ServiceCatalogItem item : catalogDAO.getAllServices()) serviceCombo.addItem(item);
        
        mechanicCombo.removeAllItems();
        mechanicCombo.addItem(null); // Allow unassigned
        for (Mechanic m : mechanicDAO.getAllMechanics()) mechanicCombo.addItem(m);
        
        // Custom renderer for null mechanic display
        mechanicCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                Component c = super.getListCellRendererComponent(list, value, idx, sel, focus);
                if (value == null) {
                    setText("-- Auto Assign --");
                } else {
                    setText(value.toString());
                }
                c.setBackground(sel ? UITheme.ACCENT_CYAN : UITheme.BG_INPUT);
                c.setForeground(sel ? Color.WHITE : UITheme.TEXT_INPUT);
                return c;
            }
        });
        
        loadVehiclesForSelectedCustomer();

        // Apply themed renderer to vehicleCombo after population
        vehicleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                Component c = super.getListCellRendererComponent(list, value, idx, sel, focus);
                c.setBackground(sel ? UITheme.ACCENT_CYAN : UITheme.BG_INPUT);
                c.setForeground(sel ? Color.WHITE : UITheme.TEXT_INPUT);
                if (c instanceof JLabel) ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                c.setFont(UITheme.FONT_BODY);
                return c;
            }
        });

        // Apply themed renderer to customerCombo
        customerCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                Component c = super.getListCellRendererComponent(list, value, idx, sel, focus);
                c.setBackground(sel ? UITheme.ACCENT_CYAN : UITheme.BG_INPUT);
                c.setForeground(sel ? Color.WHITE : UITheme.TEXT_INPUT);
                if (c instanceof JLabel) ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                c.setFont(UITheme.FONT_BODY);
                return c;
            }
        });

        // Apply themed renderer to serviceCombo
        serviceCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                Component c = super.getListCellRendererComponent(list, value, idx, sel, focus);
                c.setBackground(sel ? UITheme.ACCENT_CYAN : UITheme.BG_INPUT);
                c.setForeground(sel ? Color.WHITE : UITheme.TEXT_INPUT);
                if (c instanceof JLabel) ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                c.setFont(UITheme.FONT_BODY);
                return c;
            }
        });

        updateCost();
        isLoading = false;
    }

    private void loadVehiclesForSelectedCustomer() {
        vehicleCombo.removeAllItems();
        Customer c = (Customer) customerCombo.getSelectedItem();
        if (c == null) return;
        System.out.println("[AdminServiceReg] Loading vehicles for customerId=" + c.getCustomerId());
        List<Vehicle> vehicles = vehicleDAO.getVehiclesByCustomerId(c.getCustomerId());
        System.out.println("[AdminServiceReg] Vehicles found: " + vehicles.size());
        if (vehicles.isEmpty()) {
            // Add a placeholder indicator instead of leaving combo empty
            vehicleCombo.setEnabled(false);
        } else {
            vehicleCombo.setEnabled(true);
            for (Vehicle v : vehicles) vehicleCombo.addItem(v);
        }
        vehicleCombo.revalidate();
        vehicleCombo.repaint();
    }

    private void updateCost() {
        ServiceCatalogItem item = (ServiceCatalogItem) serviceCombo.getSelectedItem();
        if (item != null) {
            costLabel.setText("Est. Cost: " + UITheme.formatCurrency(item.getBaseCost()));
            estTimeLabel.setText("Est. Time: " + (item.getEstimatedTime() != null ? item.getEstimatedTime() : "N/A"));
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        if (customerCombo.getSelectedItem() == null) {
            customerError.setText("Please select a customer");
            valid = false;
        } else { customerError.setText(" "); }
        if (vehicleCombo.getSelectedItem() == null) {
            vehicleError.setText("Please select a vehicle");
            valid = false;
        } else { vehicleError.setText(" "); }
        if (serviceCombo.getSelectedItem() == null) {
            serviceError.setText("Please select a service type");
            valid = false;
        } else { serviceError.setText(" "); }
        String date = dateField.getText().trim();
        if (date.isEmpty()) {
            dateError.setText("Date is required (YYYY-MM-DD)");
            valid = false;
        } else {
            try {
                java.time.LocalDate.parse(date);
                dateError.setText(" ");
            } catch (Exception e) {
                dateError.setText("Invalid date format. Use YYYY-MM-DD");
                valid = false;
            }
        }
        return valid;
    }

    private void handleRegistration() {
        if (!validateForm()) return;

        Customer c = (Customer) customerCombo.getSelectedItem();
        Vehicle v = (Vehicle) vehicleCombo.getSelectedItem();
        ServiceCatalogItem item = (ServiceCatalogItem) serviceCombo.getSelectedItem();
        Mechanic m = (Mechanic) mechanicCombo.getSelectedItem();
        String date = dateField.getText().trim();
        
        Service s = new Service();
        s.setVehicleId(v.getVehicleId());
        s.setServiceType(item.getServiceName());
        s.setServiceDate(date);
        s.setStatus("Pending Request");
        s.setCost(item.getBaseCost());
        s.setEstimatedTime(item.getEstimatedTime());
        if (m != null) s.setMechanicId(m.getMechanicId());

        if (serviceDAO.addService(s)) {
            UITheme.showSuccessDialog(this, "Success", "Service Registered Successfully!");
            clearForm();
        } else {
            UITheme.showErrorDialog(this, "Error", "Failed to register service.");
        }
    }

    private void clearForm() {
        notesArea.setText("");
        dateField.setText(LocalDate.now().toString());
        customerError.setText(" ");
        vehicleError.setText(" ");
        serviceError.setText(" ");
        dateError.setText(" ");
    }
}
