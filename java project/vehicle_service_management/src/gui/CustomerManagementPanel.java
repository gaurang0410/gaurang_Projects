package gui;

import service.CustomerService;
import model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, emailField, phoneField, addressField;
    private JButton updateBtn, deleteBtn;
    private int selectedCustomerId = -1;

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UITheme.BG_CARD);
        left.setBorder(new EmptyBorder(20, 20, 20, 20));
        left.setPreferredSize(new Dimension(380, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        nameField = UITheme.styledTextField("Full Name");
        emailField = UITheme.styledTextField("Email Address");
        phoneField = UITheme.styledTextField("Phone Number");
        addressField = UITheme.styledTextField("Full Address");

        int row = 0;
        gbc.gridy = row++; left.add(label("Customer Name"), gbc); gbc.gridy = row++; left.add(nameField, gbc);
        gbc.gridy = row++; left.add(label("Email Address"), gbc); gbc.gridy = row++; left.add(emailField, gbc);
        gbc.gridy = row++; left.add(label("Phone Number"), gbc); gbc.gridy = row++; left.add(phoneField, gbc);
        gbc.gridy = row++; left.add(label("Residential Address"), gbc); gbc.gridy = row++; left.add(addressField, gbc);

        JPanel actions = new JPanel(new GridLayout(2, 2, 10, 10));
        actions.setOpaque(false);
        JButton addBtn = UITheme.accentButton("Register", UITheme.ACCENT_GREEN);
        updateBtn = UITheme.accentButton("Update", UITheme.ACCENT_BLUE);
        deleteBtn = UITheme.accentButton("Remove", UITheme.ACCENT_RED);
        JButton clearBtn = UITheme.accentButton("Reset", UITheme.BORDER_DEFAULT);

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());

        setRowActionState(false);
        actions.add(addBtn); actions.add(updateBtn); actions.add(deleteBtn); actions.add(clearBtn);
        gbc.gridy = row++; left.add(Box.createVerticalStrut(15), gbc);
        gbc.gridy = row++; left.add(actions, gbc);

        add(left, BorderLayout.WEST);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone", "Address"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> bindSelection());
        
        // Add right-click popup
        JPopupMenu popup = new JPopupMenu();
        JMenuItem view = new JMenuItem("View Profile");
        view.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r < 0) return;
            int mr = table.convertRowIndexToModel(r);
            int id = (int) model.getValueAt(mr, 0);
            String n = (String) model.getValueAt(mr, 1);
            String em = (String) model.getValueAt(mr, 2);
            String ph = (String) model.getValueAt(mr, 3);
            String ad = (String) model.getValueAt(mr, 4);
            UITheme.showInfoDialog(this, "Customer Profile", "Profile of " + n + "\nEmail: " + em + "\nPhone: " + ph + "\nAddress: " + (ad != null ? ad : "N/A"));
        });
        JMenuItem svcHistory = new JMenuItem("Service History");
        svcHistory.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r < 0) return;
            int mr = table.convertRowIndexToModel(r);
            int id = (int) model.getValueAt(mr, 0);
            new RecordsFrame(id, id, true, false);
        });
        JMenuItem regSvc = new JMenuItem("Register Service");
        regSvc.addActionListener(e -> {
            int r = table.getSelectedRow(); if (r < 0) return;
            UITheme.showInfoDialog(this, "Info", "Use the Register Service panel from the sidebar to book a service for this customer.");
        });
        popup.add(view);
        popup.add(svcHistory);
        popup.add(regSvc);
        popup.addSeparator();
        JMenuItem edit = new JMenuItem("Edit Customer");
        edit.addActionListener(e -> bindSelection());
        popup.add(edit);
        JMenuItem del = new JMenuItem("Delete Customer");
        del.addActionListener(e -> handleDelete());
        popup.add(del);
        
        UITheme.styleMenu(popup);
        table.setComponentPopupMenu(popup);

        add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
    }

    private JLabel label(String text) {
        JLabel l = UITheme.styledLabel(text);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setFont(UITheme.FONT_TINY);
        return l;
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Customer c : customerService.getAllCustomers()) {
            model.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()});
        }
    }

    private void handleAdd() {
        Customer c = buildFromForm();
        if (c == null) return;
        if (customerService.addCustomer(c)) {
            refreshTable();
            clearForm();
        } else UITheme.showErrorDialog(this, "Error", "Failed to register customer.");
    }

    private void handleUpdate() {
        if (selectedCustomerId == -1) return;
        Customer c = buildFromForm();
        if (c == null) return;
        c.setCustomerId(selectedCustomerId);
        if (customerService.updateCustomer(c)) {
            refreshTable();
            clearForm();
        } else UITheme.showErrorDialog(this, "Error", "Failed to update customer.");
    }

    private void handleDelete() {
        if (selectedCustomerId == -1) return;
        if (UITheme.showConfirmDialog(this, "Confirm", "Remove this customer?")) {
            if (customerService.deleteCustomer(selectedCustomerId)) {
                refreshTable();
                clearForm();
            } else UITheme.showErrorDialog(this, "Error", "Failed to remove customer.");
        }
    }

    private Customer buildFromForm() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String addr = addressField.getText().trim();
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            UITheme.showErrorDialog(this, "Validation Error", "Name, Email and Phone are required.");
            return null;
        }
        Customer c = new Customer();
        c.setName(name);
        c.setEmail(email);
        c.setPhone(phone);
        c.setAddress(addr);
        return c;
    }

    private void bindSelection() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int mr = table.convertRowIndexToModel(r);
        selectedCustomerId = (int) model.getValueAt(mr, 0);
        nameField.setText((String) model.getValueAt(mr, 1));
        emailField.setText((String) model.getValueAt(mr, 2));
        phoneField.setText((String) model.getValueAt(mr, 3));
        addressField.setText((String) model.getValueAt(mr, 4));
        setRowActionState(true);
    }

    private void clearForm() {
        nameField.setText(""); emailField.setText(""); phoneField.setText(""); addressField.setText("");
        selectedCustomerId = -1;
        setRowActionState(false);
    }

    private void setRowActionState(boolean selected) {
        updateBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
    }
}
