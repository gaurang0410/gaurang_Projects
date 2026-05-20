package gui;

import dao.ServiceCatalogDAO;
import dao.ServiceDAO;
import dao.VehicleDAO;
import model.Service;
import model.ServiceCatalogItem;
import model.Vehicle;
import service.ServiceManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class BookServiceFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> brandCombo;
    private JComboBox<String> modelCombo;
    private JPanel serviceListPanel;
    private JScrollPane serviceScroll;
    private JTextField costField;
    private JTextField estimatedTimeField;
    private JComboBox<String> statusCombo;
    private JTextField dateField;
    private JComboBox<String> pickupCombo;
    private JComboBox<String> dropCombo;
    private JTextArea notesArea;
    private RouteMapPanel mapPanel;
    private JPanel selectedServicesPanel;

    // Inline validation labels
    private JLabel brandError, modelError, dateError;
    private JLabel serviceCountLabel;

    private final int customerId;
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final ServiceCatalogDAO catalogDAO = new ServiceCatalogDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ServiceManager serviceManager = new ServiceManager();

    private final Map<String, Integer> vehicleMap = new HashMap<>();
    private final List<Vehicle> customerVehicles = new ArrayList<>();
    private final List<ServiceCatalogItem> allServices = new ArrayList<>();
    private final Map<ServiceCatalogItem, JCheckBox> serviceCheckboxes = new HashMap<>();

    public BookServiceFrame(int customerId) {
        this.customerId = customerId;

        setTitle("VehicleFlow - Book a Service");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1160, 820);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 700));

        initComponents();
        setVisible(true);

        // Load data AFTER frame is fully visible to avoid threading/init issues
        SwingUtilities.invokeLater(() -> {
            loadVehicles();
            loadCatalog();
        });
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(20, 14));
        root.setBackground(UITheme.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        // Header
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        JLabel titleLabel = new JLabel("Book a Service", UITheme.getIcon("service", UITheme.ACCENT_CYAN, 24), SwingConstants.LEFT);
        titleLabel.setFont(UITheme.FONT_SUBTITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setIconTextGap(10);
        header.add(titleLabel, BorderLayout.WEST);

        String smartRecommendation = serviceManager.getSmartRecommendationForCustomer(customerId);
        String reminder = serviceManager.getServiceReminderForCustomer(customerId);
        int severity = serviceManager.getReminderSeverityForCustomer(customerId);
        String dueLabel = severity == 2 ? "OVERDUE" : (severity == 1 ? "DUE SOON" : "ON SCHEDULE");
        JLabel info = new JLabel("\uD83D\uDCA1 " + smartRecommendation + "   |   \u26A0 [" + dueLabel + "] " + reminder);
        info.setFont(UITheme.FONT_SMALL);
        info.setForeground(severity == 2 ? UITheme.ACCENT_RED : (severity == 1 ? UITheme.ACCENT_AMBER : UITheme.TEXT_SECONDARY));
        header.add(info, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 20, 0));
        center.setOpaque(false);

        // Form Section (Left)
        JPanel leftPanel = createFormCard();
        JScrollPane leftScroll = new JScrollPane(leftPanel);
        UITheme.applyScrollPaneStyle(leftScroll);
        leftScroll.setBorder(null);
        center.add(leftScroll);

        // Right Section: Map + Selected Services Summary
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setOpaque(false);
        rightPanel.add(createMapCard(), BorderLayout.CENTER);
        
        JPanel summaryCard = UITheme.accentCard(UITheme.ACCENT_PURPLE);
        summaryCard.setLayout(new BorderLayout(0, 10));
        summaryCard.setBorder(new EmptyBorder(15, 18, 15, 18));
        summaryCard.setPreferredSize(new Dimension(0, 220));
        
        JLabel sumTitle = new JLabel("Booking Summary", UITheme.getIcon("billing", UITheme.ACCENT_PURPLE, 18), SwingConstants.LEFT);
        sumTitle.setFont(UITheme.FONT_HEADING);
        sumTitle.setForeground(UITheme.TEXT_PRIMARY);
        sumTitle.setIconTextGap(8);
        summaryCard.add(sumTitle, BorderLayout.NORTH);
        
        selectedServicesPanel = new JPanel();
        selectedServicesPanel.setLayout(new BoxLayout(selectedServicesPanel, BoxLayout.Y_AXIS));
        selectedServicesPanel.setOpaque(false);
        JScrollPane selScroll = new JScrollPane(selectedServicesPanel);
        selScroll.setOpaque(false); selScroll.getViewport().setOpaque(false);
        selScroll.setBorder(null);
        summaryCard.add(selScroll, BorderLayout.CENTER);
        
        JPanel totals = new JPanel(new GridLayout(1, 2, 10, 0));
        totals.setOpaque(false);
        totals.add(miniSummaryCard("Total Cost", "₹0.00", UITheme.ACCENT_GREEN));
        totals.add(miniSummaryCard("Estimated Time", "N/A", UITheme.ACCENT_CYAN));
        summaryCard.add(totals, BorderLayout.SOUTH);
        
        rightPanel.add(summaryCard, BorderLayout.SOUTH);
        center.add(rightPanel);
        root.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        footer.setOpaque(false);

        JButton bookBtn = UITheme.accentButton("Confirm Booking", UITheme.ACCENT_GREEN, UITheme.getIcon("service", Color.WHITE, 16));
        bookBtn.addActionListener(e -> handleBook());
        footer.add(bookBtn);

        JButton cancelBtn = UITheme.ghostButton("Cancel", UITheme.TEXT_SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        footer.add(cancelBtn);
        root.add(footer, BorderLayout.SOUTH);

        estimatedTimeField = new JTextField();
    }

    private JPanel createFormCard() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
            new EmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        int row = 0;

        // Vehicle Info
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2;
        form.add(sectionHeader("Vehicle Information"), gbc);

        brandCombo = UITheme.styledComboBox();
        configureVehicleCombo(brandCombo);
        brandCombo.addActionListener(e -> loadModelsForSelectedBrand());
        brandError = createErrorLabel();
        row = addFormRow(form, gbc, row, "Car Brand", brandCombo, brandError);

        modelCombo = UITheme.styledComboBox();
        configureVehicleCombo(modelCombo);
        modelError = createErrorLabel();
        row = addFormRow(form, gbc, row, "Car Model", modelCombo, modelError);

        // Service Selection (Multi-select)
        gbc.gridy = row++; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 8, 10);
        JPanel svcHeader = new JPanel(new BorderLayout());
        svcHeader.setOpaque(false);
        svcHeader.add(sectionHeader("Select Services"), BorderLayout.WEST);
        serviceCountLabel = new JLabel("0 selected");
        serviceCountLabel.setFont(UITheme.FONT_TINY);
        serviceCountLabel.setForeground(UITheme.ACCENT_CYAN);
        svcHeader.add(serviceCountLabel, BorderLayout.EAST);
        form.add(svcHeader, gbc);
        gbc.insets = new Insets(8, 10, 8, 10);

        serviceListPanel = new JPanel();
        serviceListPanel.setLayout(new BoxLayout(serviceListPanel, BoxLayout.Y_AXIS));
        serviceListPanel.setBackground(UITheme.BG_INPUT);
        serviceScroll = new JScrollPane(serviceListPanel);
        serviceScroll.setPreferredSize(new Dimension(0, 200));
        UITheme.applyScrollPaneStyle(serviceScroll);
        gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(serviceScroll, gbc);

        // Date & Logistics
        gbc.gridy = row++; gbc.insets = new Insets(20, 10, 8, 10);
        form.add(sectionHeader("Appointment & Logistics"), gbc);
        gbc.insets = new Insets(8, 10, 8, 10);

        JPanel datePanel = new JPanel(new BorderLayout(8, 0));
        datePanel.setOpaque(false);
        dateField = UITheme.styledTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        dateField.setEditable(false);
        JButton datePickerBtn = UITheme.accentButton("...", UITheme.ACCENT_CYAN);
        datePickerBtn.setPreferredSize(new Dimension(42, 38));
        datePickerBtn.addActionListener(e -> {
            String picked = new DatePicker(this).setPickedDate();
            if (!picked.isEmpty()) dateField.setText(picked);
        });
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(datePickerBtn, BorderLayout.EAST);
        dateError = createErrorLabel();
        row = addFormRow(form, gbc, row, "Service Date", datePanel, dateError);

        pickupCombo = UITheme.styledComboBox();
        row = addFormRow(form, gbc, row, "Pickup Location", pickupCombo, null);

        dropCombo = UITheme.styledComboBox();
        row = addFormRow(form, gbc, row, "Drop Location", dropCombo, null);

        // Notes
        gbc.gridy = row++; gbc.insets = new Insets(20, 10, 8, 10);
        form.add(sectionHeader("Additional Notes"), gbc);
        gbc.insets = new Insets(8, 10, 8, 10);
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true); notesArea.setWrapStyleWord(true);
        notesArea.setFont(UITheme.FONT_BODY);
        notesArea.setBackground(UITheme.BG_INPUT); notesArea.setForeground(UITheme.TEXT_INPUT);
        notesArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT), new EmptyBorder(8, 10, 8, 10)));
        gbc.gridy = row++; gbc.gridwidth = 2;
        form.add(notesArea, gbc);

        costField = new JTextField(); // Internal storage
        statusCombo = new JComboBox<>(new String[]{"Pending Request"});
        return form;
    }

    private int addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent comp, JLabel error) {
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.4;
        p.add(UITheme.styledLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_SECONDARY), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        p.add(comp, gbc);
        row++;
        if (error != null) {
            gbc.gridy = row; gbc.gridx = 1;
            p.add(error, gbc);
            row++;
        }
        return row;
    }

    private JLabel sectionHeader(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(UITheme.ACCENT_CYAN);
        l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_DEFAULT));
        return l;
    }

    private JLabel createErrorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(UITheme.FONT_TINY); l.setForeground(UITheme.ACCENT_RED);
        return l;
    }

    private JPanel createMapCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1), new EmptyBorder(20, 20, 20, 20)));
        JLabel title = new JLabel("Service Route Map", UITheme.getIcon("vehicle", UITheme.ACCENT_GREEN, 20), SwingConstants.LEFT);
        title.setFont(UITheme.FONT_HEADING); title.setForeground(UITheme.TEXT_PRIMARY); title.setIconTextGap(10);
        card.add(title, BorderLayout.NORTH);
        mapPanel = new RouteMapPanel();
        card.add(mapPanel, BorderLayout.CENTER);
        for (String loc : mapPanel.getLocationNames()) { pickupCombo.addItem(loc); dropCombo.addItem(loc); }
        pickupCombo.setSelectedItem("City Center"); dropCombo.setSelectedItem("Service Center");
        pickupCombo.addActionListener(e -> mapPanel.setRoute((String) pickupCombo.getSelectedItem(), (String) dropCombo.getSelectedItem()));
        dropCombo.addActionListener(e -> mapPanel.setRoute((String) pickupCombo.getSelectedItem(), (String) dropCombo.getSelectedItem()));
        mapPanel.setRoute((String) pickupCombo.getSelectedItem(), (String) dropCombo.getSelectedItem());
        return card;
    }

    private JPanel miniSummaryCard(String title, String value, Color accent) {
        JPanel c = new JPanel(new BorderLayout(2, 2));
        c.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 15));
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40)), new EmptyBorder(8, 10, 8, 10)));
        JLabel t = new JLabel(title.toUpperCase()); t.setFont(UITheme.FONT_TINY); t.setForeground(accent);
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.BOLD, 15)); v.setForeground(UITheme.TEXT_PRIMARY);
        c.add(t, BorderLayout.NORTH); c.add(v, BorderLayout.CENTER);
        return c;
    }

    private void loadVehicles() {
        System.out.println("[BookServiceFrame] Loading vehicles for customerId=" + customerId);
        brandCombo.removeAllItems();
        modelCombo.removeAllItems();
        customerVehicles.clear();
        vehicleMap.clear();

        List<Vehicle> list = vehicleDAO.getVehiclesByCustomerId(customerId);
        System.out.println("[BookServiceFrame] Vehicles found: " + list.size());
        customerVehicles.addAll(list);

        if (list.isEmpty()) {
            brandCombo.addItem("No Vehicles Found");
            brandCombo.setEnabled(false);
            modelCombo.addItem("No Vehicles Found");
            modelCombo.setEnabled(false);
            brandError.setText("Register a vehicle first from the Vehicle Management section.");
            refreshVehicleCombos();
            return;
        }

        brandError.setText(" ");
        brandCombo.setEnabled(true);
        modelCombo.setEnabled(true);

        LinkedHashSet<String> brands = new LinkedHashSet<>();
        for (Vehicle v : list) {
            if (v.getBrand() != null && !v.getBrand().isEmpty()) {
                brands.add(v.getBrand());
            }
        }

        for (String b : brands) {
            brandCombo.addItem(b);
        }

        if (brandCombo.getItemCount() > 0) {
            brandCombo.setSelectedIndex(0);
            loadModelsForSelectedBrand();
        } else {
            modelCombo.setEnabled(false);
            refreshVehicleCombos();
        }
    }

    private void loadModelsForSelectedBrand() {
        modelCombo.removeAllItems(); vehicleMap.clear();
        String brand = (String) brandCombo.getSelectedItem();
        if (brand == null || brand.contains("No")) {
            modelCombo.setEnabled(false);
            refreshVehicleCombos();
            return;
        }
        for (Vehicle v : customerVehicles) {
            if (brand.equalsIgnoreCase(v.getBrand())) {
                String d = v.getModel() + " [" + v.getRegistrationNumber() + "]";
                modelCombo.addItem(d); vehicleMap.put(d, v.getVehicleId());
            }
        }
        if (modelCombo.getItemCount() > 0) {
            modelCombo.setEnabled(true);
            modelCombo.setSelectedIndex(0);
        } else {
            modelCombo.setEnabled(false);
        }
        refreshVehicleCombos();
    }

    private void configureVehicleCombo(JComboBox<String> combo) {
        UITheme.styleInput(combo);
        combo.setLightWeightPopupEnabled(false);
        combo.setMaximumRowCount(8);
        combo.setFocusable(true);
    }

    private void refreshVehicleCombos() {
        brandCombo.revalidate(); brandCombo.repaint();
        modelCombo.revalidate(); modelCombo.repaint();
    }

    private void loadCatalog() {
        serviceListPanel.removeAll();
        serviceCheckboxes.clear();
        allServices.clear();
        try {
            List<ServiceCatalogItem> items = catalogDAO.getAllServices();
            allServices.addAll(items);
            for (ServiceCatalogItem item : items) {
                JCheckBox cb = new JCheckBox(item.getServiceName() + " — ₹" + item.getBaseCost());
                cb.setFont(UITheme.FONT_BODY); cb.setForeground(UITheme.TEXT_PRIMARY);
                cb.setBackground(UITheme.BG_INPUT); cb.setOpaque(true);
                cb.setBorder(new EmptyBorder(8, 12, 8, 12));
                cb.addActionListener(e -> updateSummary());
                serviceListPanel.add(cb);
                serviceCheckboxes.put(item, cb);
            }
            serviceListPanel.revalidate(); serviceListPanel.repaint();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateSummary() {
        selectedServicesPanel.removeAll();
        double totalCost = 0;
        int totalMinutes = 0;
        int count = 0;

        for (ServiceCatalogItem item : allServices) {
            if (serviceCheckboxes.get(item).isSelected()) {
                totalCost += item.getBaseCost();
                totalMinutes += parseMinutes(item.getEstimatedTime());
                count++;
                
                JLabel lbl = new JLabel("✔ " + item.getServiceName() + " (₹" + item.getBaseCost() + ")");
                lbl.setFont(UITheme.FONT_SMALL); lbl.setForeground(UITheme.TEXT_SECONDARY);
                lbl.setBorder(new EmptyBorder(2, 0, 2, 0));
                selectedServicesPanel.add(lbl);
            }
        }

        serviceCountLabel.setText(count + " selected");
        costField.setText(String.valueOf(totalCost));
        
        // Update totals in UI
        Component[] comps = ((JPanel)selectedServicesPanel.getParent().getParent().getComponent(2)).getComponents();
        ((JLabel)((JPanel)comps[0]).getComponent(1)).setText("₹" + totalCost);
        ((JLabel)((JPanel)comps[1]).getComponent(1)).setText(totalMinutes / 60 + "h " + totalMinutes % 60 + "m");

        selectedServicesPanel.revalidate(); selectedServicesPanel.repaint();
    }

    private int parseMinutes(String time) {
        if (time == null) return 60;
        String t = time.toLowerCase();
        if (t.contains("hour")) {
            try { return (int)(Double.parseDouble(t.split(" ")[0]) * 60); } catch(Exception e) { return 120; }
        }
        return 60;
    }

    private void handleBook() {
        if (!validateForm()) return;
        
        List<String> selectedNames = new ArrayList<>();
        double totalCost = 0;
        int totalMinutes = 0;
        for (ServiceCatalogItem item : allServices) {
            if (serviceCheckboxes.get(item).isSelected()) {
                selectedNames.add(item.getServiceName());
                totalCost += item.getBaseCost();
                totalMinutes += parseMinutes(item.getEstimatedTime());
            }
        }

        if (selectedNames.isEmpty()) {
            UITheme.showAlert(this, "No Service Selected", "Please select at least one service to book.", UITheme.AlertType.WARNING);
            return;
        }

        int vId = vehicleMap.get((String) modelCombo.getSelectedItem());
        String servicesStr = String.join(", ", selectedNames);
        String date = dateField.getText();
        String est = (totalMinutes / 60) + " Hours " + (totalMinutes % 60) + " Minutes";

        Service s = new Service(vId, servicesStr, date, "Pending Request", totalCost);
        s.setEstimatedTime(est);

        if (serviceDAO.addService(s)) {
            UITheme.showAlert(this, "Booking Successful", "Your service request for [" + servicesStr + "] has been placed.", UITheme.AlertType.SUCCESS);
            dispose();
        } else {
            UITheme.showAlert(this, "Booking Failed", "System encountered an error during booking.", UITheme.AlertType.ERROR);
        }
    }

    private boolean validateForm() {
        boolean v = true;
        if (brandCombo.getSelectedIndex() < 0 || brandCombo.getSelectedItem().toString().contains("No")) {
            brandError.setText("Vehicle registration required."); v = false;
        } else brandError.setText(" ");
        if (modelCombo.getSelectedIndex() < 0) { modelError.setText("Model required."); v = false; } else modelError.setText(" ");
        if (dateField.getText().isEmpty()) { dateError.setText("Date required."); v = false; } else dateError.setText(" ");
        return v;
    }
}
