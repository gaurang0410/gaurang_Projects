package gui;

import dao.ServiceCatalogDAO;
import model.ServiceCatalogItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServiceCatalogForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private ServiceCatalogDAO catalogDAO = new ServiceCatalogDAO();
    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, descField, costField, timeField, catField;
    private int selectedId = -1;

    public ServiceCatalogForm() {
        setTitle("Register Service");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initComponents();
        refreshTable();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UITheme.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(300, 0));

        formPanel.add(UITheme.styledLabel("Service Name:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        nameField = UITheme.styledTextField("Oil Change");
        formPanel.add(nameField);

        formPanel.add(UITheme.styledLabel("Description:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        descField = UITheme.styledTextField("Engine oil replacement");
        formPanel.add(descField);

        formPanel.add(UITheme.styledLabel("Base Cost:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        costField = UITheme.styledTextField("1000.00");
        formPanel.add(costField);

        formPanel.add(UITheme.styledLabel("Est. Time:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        timeField = UITheme.styledTextField("1 Hour");
        formPanel.add(timeField);

        formPanel.add(UITheme.styledLabel("Category:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        catField = UITheme.styledTextField("Maintenance");
        formPanel.add(catField);

        JButton addBtn = UITheme.accentButton("Add Service", UITheme.ACCENT_GREEN);
        addBtn.addActionListener(e -> handleAdd());
        formPanel.add(addBtn);

        JButton updateBtn = UITheme.accentButton("Update", UITheme.ACCENT_BLUE);
        updateBtn.addActionListener(e -> handleUpdate());
        formPanel.add(updateBtn);

        JButton deleteBtn = UITheme.accentButton("Delete", UITheme.ACCENT_RED);
        deleteBtn.addActionListener(e -> handleDelete());
        formPanel.add(deleteBtn);

        JButton clearBtn = UITheme.accentButton("Clear", UITheme.BORDER_DEFAULT);
        clearBtn.addActionListener(e -> clearForm());
        formPanel.add(clearBtn);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table
        String[] cols = {"ID", "Name", "Cost", "Time", "Category"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        catalogTable = new JTable(tableModel);
        UITheme.styleTable(catalogTable);
        mainPanel.add(UITheme.styledScrollPane(catalogTable), BorderLayout.CENTER);
        
        catalogTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = catalogTable.getSelectedRow();
                if (r >= 0) {
                    selectedId = (int) tableModel.getValueAt(r, 0);
                    nameField.setText((String) tableModel.getValueAt(r, 1));
                    costField.setText(tableModel.getValueAt(r, 2).toString().replace("₹", "").replace(",", ""));
                    timeField.setText((String) tableModel.getValueAt(r, 3));
                    catField.setText((String) tableModel.getValueAt(r, 4));
                }
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<ServiceCatalogItem> items = catalogDAO.getAllServices();
        for (ServiceCatalogItem i : items) {
            tableModel.addRow(new Object[]{i.getCatalogId(), i.getServiceName(), UITheme.formatCurrency(i.getBaseCost()), i.getEstimatedTime(), i.getCategory()});
        }
    }

    private void handleAdd() {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        double cost;
        try {
            cost = Double.parseDouble(costField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid cost value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String time = timeField.getText().trim();
        String cat = catField.getText().trim();

        if (name.isEmpty() || time.isEmpty() || cat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, estimated time and category are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (catalogDAO.addService(name, desc, cost, time, cat)) {
            JOptionPane.showMessageDialog(this, "Service added!");
            refreshTable();
            clearForm();
        }
    }

    private void handleUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a service from the table to update.");
            return;
        }
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String time = timeField.getText().trim();
        String cat = catField.getText().trim();
        double cost;
        try {
            cost = Double.parseDouble(costField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid cost value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (catalogDAO.updateService(selectedId, name, desc, cost, time, cat)) {
            JOptionPane.showMessageDialog(this, "Service updated!");
            refreshTable();
            clearForm();
        }
    }

    private void handleDelete() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a service from the table to delete.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Delete selected service?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        if (catalogDAO.deleteService(selectedId)) {
            JOptionPane.showMessageDialog(this, "Service deleted!");
            refreshTable();
            clearForm();
        }
    }

    private void clearForm() {
        nameField.setText(""); descField.setText(""); costField.setText(""); timeField.setText(""); catField.setText("");
        selectedId = -1;
    }
}
