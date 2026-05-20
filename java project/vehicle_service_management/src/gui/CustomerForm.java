package gui;

import service.CustomerService;
import model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton clearBtn;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    
    private CustomerService customerService;
    private int selectedCustomerId = -1;

    public CustomerForm() {
        setTitle("VehicleFlow - Customer Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        customerService = new CustomerService();
        initComponents();
        loadAllCustomers();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = UITheme.gradientPanel(UITheme.BG_DARK, UITheme.BG_CARD);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header
        JLabel titleLabel = UITheme.styledLabel(" Customer Management");
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
        formPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        formPanel.setPreferredSize(new Dimension(380, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        int gridy = 0;

        // Name
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = UITheme.styledTextField("Full Name");
        formPanel.add(nameField, gbc);
        gridy++;

        // Phone
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = UITheme.styledTextField("Phone Number");
        formPanel.add(phoneField, gbc);
        gridy++;

        // Email
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = UITheme.styledTextField("Email Address");
        formPanel.add(emailField, gbc);
        gridy++;

        // Address
        gbc.gridx = 0; gbc.gridy = gridy;
        formPanel.add(UITheme.styledLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressField = UITheme.styledTextField("Home Address");
        formPanel.add(addressField, gbc);
        gridy++;

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 12, 12));
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
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(buttonPanel, gbc);

        centerPanel.add(formPanel, BorderLayout.WEST);

        // Table
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        customerTable = new JTable(tableModel);
        UITheme.styleTable(customerTable);
        centerPanel.add(UITheme.styledScrollPane(customerTable), BorderLayout.CENTER);

        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = customerTable.getSelectedRow();
                if (row >= 0) {
                    selectedCustomerId = (int) tableModel.getValueAt(row, 0);
                    setRowActionState(true);
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                    phoneField.setText((String) tableModel.getValueAt(row, 2));
                    emailField.setText((String) tableModel.getValueAt(row, 3));
                    addressField.setText((String) tableModel.getValueAt(row, 4));
                }
            }
        });

        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private void handleSave() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            UITheme.showAlert(this, "Required Fields", "Name and Phone Number are mandatory!", UITheme.AlertType.ERROR);
            return;
        }

        if (customerService.addCustomer(name, phone, email, address)) {
            UITheme.showAlert(this, "Success", "Customer profile created successfully.", UITheme.AlertType.SUCCESS);
            clearForm();
            loadAllCustomers();
        } else {
            UITheme.showAlert(this, "Error", "Failed to add customer profile to the system.", UITheme.AlertType.ERROR);
        }
    }

    private void handleUpdate() {
        if (selectedCustomerId == -1) {
            UITheme.showAlert(this, "Selection Error", "Please select a customer from the table to update.", UITheme.AlertType.WARNING);
            return;
        }

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            UITheme.showAlert(this, "Required Fields", "Name and Phone Number cannot be empty.", UITheme.AlertType.ERROR);
            return;
        }

        if (customerService.updateCustomer(selectedCustomerId, name, phone, email, address)) {
            UITheme.showAlert(this, "Updated", "Customer profile has been updated successfully.", UITheme.AlertType.SUCCESS);
            clearForm();
            loadAllCustomers();
            selectedCustomerId = -1;
        } else {
            UITheme.showAlert(this, "Error", "System encountered an error during update.", UITheme.AlertType.ERROR);
        }
    }

    private void handleDelete() {
        if (selectedCustomerId == -1) {
            UITheme.showAlert(this, "Selection Error", "Please select a customer to delete.", UITheme.AlertType.WARNING);
            return;
        }

        if (UITheme.showConfirm(this, "Confirm Delete", "Are you sure you want to remove this customer profile? This will hide their vehicle history.")) {
            if (customerService.deleteCustomer(selectedCustomerId)) {
                UITheme.showAlert(this, "Deleted", "Customer profile removed successfully.", UITheme.AlertType.SUCCESS);
                clearForm();
                loadAllCustomers();
                selectedCustomerId = -1;
            } else {
                UITheme.showAlert(this, "Error", "Could not remove the customer profile.", UITheme.AlertType.ERROR);
            }
        }
    }

    private void loadAllCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerService.getAllCustomers();
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getPhone(), c.getEmail(), c.getAddress()});
        }
        setRowActionState(false);
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        selectedCustomerId = -1;
        setRowActionState(false);
    }

    private void setRowActionState(boolean selected) {
        updateBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
    }
}
