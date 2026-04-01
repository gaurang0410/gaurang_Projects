package gui;

import service.CustomerService;
import model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerForm extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JButton saveBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton searchBtn;
    private JButton viewAllBtn;
    private JButton clearBtn;
    private JTable customerTable;
    private JScrollPane scrollPane;
    private CustomerService customerService;
    private int selectedCustomerId = -1;

    public CustomerForm() {
        setTitle("Customer Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        customerService = new CustomerService();
        initComponents();
        loadAllCustomers();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 245, 250));

        // Title
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBounds(350, 10, 250, 30);
        mainPanel.add(titleLabel);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBounds(10, 50, 400, 300);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 80, 25);
        formPanel.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(120, 20, 250, 25);
        formPanel.add(nameField);

        // Phone
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(20, 60, 80, 25);
        formPanel.add(phoneLabel);
        phoneField = new JTextField();
        phoneField.setBounds(120, 60, 250, 25);
        formPanel.add(phoneField);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 100, 80, 25);
        formPanel.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(120, 100, 250, 25);
        formPanel.add(emailField);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(20, 140, 80, 25);
        formPanel.add(addressLabel);
        addressField = new JTextField();
        addressField.setBounds(120, 140, 250, 25);
        formPanel.add(addressField);

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
        searchBtn = new JButton("Search");
        searchBtn.setBounds(20, 240, 100, 30);
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });
        formPanel.add(searchBtn);

        viewAllBtn = new JButton("View All");
        viewAllBtn.setBounds(140, 240, 100, 30);
        viewAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAllCustomers();
            }
        });
        formPanel.add(viewAllBtn);

        mainPanel.add(formPanel);

        // Table
        customerTable = new JTable();
        scrollPane = new JScrollPane(customerTable);
        scrollPane.setBounds(420, 50, 460, 500);
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    private void handleSave() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressField.getText();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Phone are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (customerService.addCustomer(name, phone, email, address)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearForm();
            loadAllCustomers();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding customer!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String address = addressField.getText();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Phone are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (customerService.updateCustomer(selectedCustomerId, name, phone, email, address)) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            clearForm();
            loadAllCustomers();
            selectedCustomerId = -1;
        } else {
            JOptionPane.showMessageDialog(this, "Error updating customer!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedCustomerId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (customerService.deleteCustomer(selectedCustomerId)) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                clearForm();
                loadAllCustomers();
                selectedCustomerId = -1;
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting customer!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleSearch() {
        String name = JOptionPane.showInputDialog(this, "Enter customer name to search:");
        if (name != null && !name.isEmpty()) {
            List<Customer> customers = customerService.searchCustomerByName(name);
            if (customers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No customers found!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                loadCustomersToTable(customers);
            }
        }
    }

    private void loadAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        loadCustomersToTable(customers);
    }

    private void loadCustomersToTable(List<Customer> customers) {
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

        customerTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = customerTable.rowAtPoint(evt.getPoint());
                if (row >= 0) {
                    selectedCustomerId = (int) customerTable.getValueAt(row, 0);
                    nameField.setText((String) customerTable.getValueAt(row, 1));
                    phoneField.setText((String) customerTable.getValueAt(row, 2));
                    emailField.setText((String) customerTable.getValueAt(row, 3));
                    addressField.setText((String) customerTable.getValueAt(row, 4));
                }
            }
        });

        scrollPane.setViewportView(customerTable);
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        selectedCustomerId = -1;
    }
}
