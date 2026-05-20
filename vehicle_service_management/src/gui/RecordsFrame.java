package gui;

import dao.AuditLogDAO;
import dao.FeedbackDAO;
import dao.ServicePriceHistoryDAO;
import service.CustomerService;
import service.VehicleService;
import service.ServiceManager;
import model.Customer;
import model.FeedbackReview;
import model.Vehicle;
import model.Service;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.regex.Pattern;
import java.util.List;

public class RecordsFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private JTable customerTable, vehicleTable, serviceTable;
    private DefaultTableModel customerModel, vehicleModel, serviceModel;
    private TableRowSorter<DefaultTableModel> customerSorter, vehicleSorter, serviceSorter;
    private JTextField customerSearch, vehicleSearch, serviceSearch;
    private JComboBox<String> serviceSortCombo, serviceFilterCombo;
    private JComboBox<String> auditSortCombo;

    private CustomerService customerService = new CustomerService();
    private VehicleService vehicleService = new VehicleService();
    private ServiceManager serviceManager = new ServiceManager();
    
    private int customerId = -1;
    private boolean isCustomerView = false;

    public RecordsFrame() {
        this(-1, -1, false, true);
    }

    public RecordsFrame(int customerId, int unused, boolean isCustomerView) {
        this(customerId, unused, isCustomerView, true);
    }

    public RecordsFrame(int customerId, int unused, boolean isCustomerView, boolean showWindow) {
        this.customerId = customerId;
        this.isCustomerView = isCustomerView;

        setTitle(isCustomerView ? "VehicleFlow - My Service History" : "VehicleFlow - Audit & System Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
        if (showWindow) {
            setVisible(true);
        }
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.gradientPanel(UITheme.BG_DARK, UITheme.BG_CARD);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        JLabel titleLabel = UITheme.styledLabel(isCustomerView ? "My Service History" : "Audit & System Records");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.ACCENT_CYAN);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        if (isCustomerView) {
            mainPanel.add(createServiceTab(), BorderLayout.CENTER);
        } else {
            tabbedPane = new JTabbedPane();
            UITheme.styleTabbedPane(tabbedPane);
            tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
            
            tabbedPane.addTab("  Services  ", createServiceTab());
            tabbedPane.addTab("  Vehicles  ", createVehicleTab());
            tabbedPane.addTab("  Customers  ", createCustomerTab());
            tabbedPane.addTab("  Audit Logs  ", createAuditTab());
            tabbedPane.addTab("  Feedback  ", createFeedbackTab());
            tabbedPane.addTab("  Price History  ", createPriceHistoryTab());
            
            mainPanel.add(tabbedPane, BorderLayout.CENTER);
        }
    }

    private JPanel createCustomerTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        customerSearch = UITheme.styledTextField("Search Customers...");
        customerSearch.setPreferredSize(new Dimension(300, 35));
        customerSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterCustomer(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterCustomer(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterCustomer(); }
        });
        searchPanel.add(UITheme.styledLabel("Filter: "));
        searchPanel.add(customerSearch);
        panel.add(searchPanel, BorderLayout.NORTH);
        String[] cols = {"ID", "Name", "Phone", "Email", "Address"};
        customerModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        customerTable = new JTable(customerModel);
        UITheme.styleTable(customerTable);
        customerSorter = new TableRowSorter<>(customerModel);
        customerTable.setRowSorter(customerSorter);
        loadCustomersData();
        panel.add(UITheme.styledScrollPane(customerTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createVehicleTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        vehicleSearch = UITheme.styledTextField("Search Vehicles...");
        vehicleSearch.setPreferredSize(new Dimension(300, 35));
        vehicleSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterVehicle(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterVehicle(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterVehicle(); }
        });
        searchPanel.add(UITheme.styledLabel("Filter: "));
        searchPanel.add(vehicleSearch);
        panel.add(searchPanel, BorderLayout.NORTH);
        String[] cols = {"ID", "Customer ID", "Brand", "Model", "Registration"};
        vehicleModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        vehicleTable = new JTable(vehicleModel);
        UITheme.styleTable(vehicleTable);
        vehicleSorter = new TableRowSorter<>(vehicleModel);
        vehicleTable.setRowSorter(vehicleSorter);
        loadVehiclesData();
        panel.add(UITheme.styledScrollPane(vehicleTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createServiceTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);

        serviceSearch = UITheme.styledTextField("Search...");
        serviceSearch.setPreferredSize(new Dimension(200, 38));
        serviceSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyServiceFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyServiceFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyServiceFilters(); }
        });

        serviceSortCombo = UITheme.styledComboBox(new String[]{"Latest First", "Earliest First", "Most Expensive", "Least Expensive", "Status Wise"});
        serviceSortCombo.setPreferredSize(new Dimension(160, 38));
        serviceSortCombo.addActionListener(e -> sortServiceTable());

        serviceFilterCombo = UITheme.styledComboBox(new String[]{"All Services", "Completed Only", "Ongoing Only", "Pending Only"});
        serviceFilterCombo.setPreferredSize(new Dimension(160, 38));
        serviceFilterCombo.addActionListener(e -> applyServiceFilters());

        filterPanel.add(UITheme.bodyLabel("🔍"));
        filterPanel.add(serviceSearch);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(UITheme.bodyLabel("Sort:"));
        filterPanel.add(serviceSortCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(UITheme.bodyLabel("Filter:"));
        filterPanel.add(serviceFilterCombo);

        if (!isCustomerView) {
            JButton invoiceBtn = UITheme.accentButton("Invoice", UITheme.ACCENT_GREEN);
            invoiceBtn.setPreferredSize(new Dimension(100, 38));
            invoiceBtn.addActionListener(e -> generateInvoice());
            filterPanel.add(Box.createHorizontalStrut(20));
            filterPanel.add(invoiceBtn);
        }

        panel.add(filterPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "Vehicle ID", "Service Type", "Date", "Status", "Mechanic", "Cost"};
        serviceModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 0 || c == 1) return Integer.class;
                if (c == 6) return Double.class;
                return String.class;
            }
        };
        serviceTable = new JTable(serviceModel);
        UITheme.styleTable(serviceTable);
        serviceSorter = new TableRowSorter<>(serviceModel);
        serviceTable.setRowSorter(serviceSorter);
        
        loadServicesData();
        sortServiceTable(); // Default sort

        panel.add(UITheme.styledScrollPane(serviceTable), BorderLayout.CENTER);
        return panel;
    }

    private void applyServiceFilters() {
        String text = serviceSearch.getText().trim();
        String filter = (String) serviceFilterCombo.getSelectedItem();
        
        java.util.List<RowFilter<DefaultTableModel, Object>> filters = new java.util.ArrayList<>();

        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        }

        if ("Completed Only".equals(filter)) {
            filters.add(RowFilter.regexFilter("(?i)Completed", 4));
        } else if ("Ongoing Only".equals(filter)) {
            filters.add(RowFilter.regexFilter("(?i)In Progress|Inspection|Pickup|Quality", 4));
        } else if ("Pending Only".equals(filter)) {
            filters.add(RowFilter.regexFilter("(?i)Pending", 4));
        }

        if (filters.isEmpty()) serviceSorter.setRowFilter(null);
        else serviceSorter.setRowFilter(RowFilter.andFilter(filters));
    }

    private void sortServiceTable() {
        String sort = (String) serviceSortCombo.getSelectedItem();
        java.util.List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
        
        if ("Latest First".equals(sort)) {
            sortKeys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
        } else if ("Earliest First".equals(sort)) {
            sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        } else if ("Most Expensive".equals(sort)) {
            sortKeys.add(new RowSorter.SortKey(6, SortOrder.DESCENDING));
        } else if ("Least Expensive".equals(sort)) {
            sortKeys.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
        } else if ("Status Wise".equals(sort)) {
            sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        }
        
        serviceSorter.setSortKeys(sortKeys);
    }

    private void filterCustomer() {
        String text = customerSearch.getText();
        if (text.trim().isEmpty()) customerSorter.setRowFilter(null);
        else customerSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }

    private void filterVehicle() {
        String text = vehicleSearch.getText();
        if (text.trim().isEmpty()) vehicleSorter.setRowFilter(null);
        else vehicleSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }

    private void filterService() {
        String text = serviceSearch.getText();
        if (text.trim().isEmpty()) serviceSorter.setRowFilter(null);
        else serviceSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
    }

    private void loadCustomersData() {
        customerModel.setRowCount(0);
        for (Customer c : customerService.getAllCustomers()) {
            customerModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress()});
        }
    }

    private void loadVehiclesData() {
        vehicleModel.setRowCount(0);
        for (Vehicle v : vehicleService.getAllVehicles()) {
            vehicleModel.addRow(new Object[]{v.getVehicleId(), v.getCustomerId(), v.getBrand(), v.getModel(), v.getRegistrationNumber()});
        }
    }

    private void loadServicesData() {
        serviceModel.setRowCount(0);
        List<Service> services = isCustomerView ? serviceManager.getServicesByCustomerId(customerId) : serviceManager.getAllServices();
        for (Service s : services) {
            String mech = s.getMechanicName() == null || s.getMechanicName().isEmpty() ? "Unassigned" : s.getMechanicName();
            serviceModel.addRow(new Object[]{s.getServiceId(), s.getVehicleId(), s.getServiceType(), s.getServiceDate(), s.getStatus(), mech, s.getCost()});
        }
    }

    private JPanel createAuditTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setOpaque(false);
        
        auditSortCombo = UITheme.styledComboBox(new String[]{"Newest Activity", "Oldest Activity", "Action Wise", "User Wise"});
        top.add(UITheme.bodyLabel("Sort Logs:"));
        top.add(auditSortCombo);
        panel.add(top, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Action", "User ID", "Timestamp", "Details"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 || c == 2 ? Integer.class : String.class; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        auditSortCombo.addActionListener(e -> {
            String s = (String) auditSortCombo.getSelectedItem();
            java.util.List<RowSorter.SortKey> keys = new java.util.ArrayList<>();
            if ("Newest Activity".equals(s)) keys.add(new RowSorter.SortKey(3, SortOrder.DESCENDING));
            else if ("Oldest Activity".equals(s)) keys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
            else if ("Action Wise".equals(s)) keys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
            else if ("User Wise".equals(s)) keys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
            sorter.setSortKeys(keys);
        });

        for (AuditLogDAO.AuditLog log : new AuditLogDAO().getRecentLogs()) {
            model.addRow(new Object[]{log.id, log.action, log.userId, log.timestamp, log.details});
        }
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFeedbackTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(new String[]{"Feedback ID", "Service", "Customer", "Mechanic", "Rating", "Comment", "Time"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        for (FeedbackReview feedback : new FeedbackDAO().getAllFeedback()) {
            model.addRow(new Object[]{
                    feedback.getFeedbackId(), feedback.getServiceId(), feedback.getCustomerName(), feedback.getMechanicName(),
                    feedback.getRating(), feedback.getComment(), feedback.getCreatedAt()
            });
        }
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPriceHistoryTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        DefaultTableModel model = new DefaultTableModel(new String[]{"History ID", "Service ID", "Old Price", "New Price", "Changed At"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        for (ServicePriceHistoryDAO.PriceHistoryItem item : new ServicePriceHistoryDAO().getAllHistory()) {
            model.addRow(new Object[]{item.historyId, item.serviceId, String.format("%.2f", item.oldPrice), String.format("%.2f", item.newPrice), item.changedAt});
        }
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void generateInvoice() {
        int serviceId = getSelectedServiceId();
        if (serviceId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a service from the table to generate an invoice.");
            return;
        }
        new BillingFrame(serviceId);
    }

    public int getSelectedServiceId() {
        if (serviceTable == null) return -1;
        int row = serviceTable.getSelectedRow();
        if (row < 0) return -1;
        int modelRow = serviceTable.convertRowIndexToModel(row);
        Object value = serviceModel.getValueAt(modelRow, 0);
        if (value instanceof Integer) return (Integer) value;
        try { return Integer.parseInt(String.valueOf(value)); } catch (Exception ex) { return -1; }
    }
}
