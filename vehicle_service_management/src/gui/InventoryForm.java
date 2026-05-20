package gui;

import dao.InventoryDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private InventoryDAO inventoryDAO = new InventoryDAO();
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, priceField, stockField, thresholdField;
    private int selectedId = -1;

    public InventoryForm() {
        setTitle("Inventory Management 📦");
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
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setPreferredSize(new Dimension(300, 0));

        formPanel.add(UITheme.styledLabel("Part Name:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        nameField = UITheme.styledTextField("Engine Oil");
        formPanel.add(nameField);

        formPanel.add(UITheme.styledLabel("Price:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        priceField = UITheme.styledTextField("1500.00");
        formPanel.add(priceField);

        formPanel.add(UITheme.styledLabel("Stock:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        stockField = UITheme.styledTextField("10");
        formPanel.add(stockField);

        formPanel.add(UITheme.styledLabel("Threshold:", UITheme.FONT_SMALL, UITheme.TEXT_PRIMARY));
        thresholdField = UITheme.styledTextField("5");
        formPanel.add(thresholdField);

        JButton addBtn = UITheme.accentButton("Add Part", UITheme.ACCENT_GREEN);
        addBtn.addActionListener(e -> handleAdd());
        formPanel.add(addBtn);

        JButton updateBtn = UITheme.accentButton("Update Stock", UITheme.ACCENT_BLUE);
        updateBtn.addActionListener(e -> handleUpdate());
        formPanel.add(updateBtn);

        JButton clearBtn = UITheme.accentButton("Clear", UITheme.BORDER_DEFAULT);
        clearBtn.addActionListener(e -> clearForm());
        formPanel.add(clearBtn);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table
        String[] cols = {"ID", "Part Name", "Price", "Stock", "Threshold"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        inventoryTable = new JTable(tableModel);
        UITheme.styleTable(inventoryTable);
        mainPanel.add(UITheme.styledScrollPane(inventoryTable), BorderLayout.CENTER);
        
        inventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = inventoryTable.getSelectedRow();
                if (r >= 0) {
                    selectedId = (int) tableModel.getValueAt(r, 0);
                    nameField.setText((String) tableModel.getValueAt(r, 1));
                    priceField.setText(tableModel.getValueAt(r, 2).toString().replace("₹", "").replace(",", ""));
                    stockField.setText(tableModel.getValueAt(r, 3).toString());
                    thresholdField.setText(tableModel.getValueAt(r, 4).toString());
                }
            }
            @Override public void mousePressed(java.awt.event.MouseEvent e) { maybeSelect(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeSelect(e); }
            private void maybeSelect(java.awt.event.MouseEvent e) {
                int r = inventoryTable.rowAtPoint(e.getPoint()); if (r >= 0) inventoryTable.setRowSelectionInterval(r, r);
            }
        });

        // Popup menu
        JPopupMenu ipopup = new JPopupMenu();
        JMenuItem view = new JMenuItem("View Details");
        view.addActionListener(e -> {
            int r = inventoryTable.getSelectedRow(); if (r < 0) return;
            int id = (int) tableModel.getValueAt(inventoryTable.convertRowIndexToModel(r), 0);
            InventoryDAO.InventoryItem found = null;
            for (InventoryDAO.InventoryItem it : inventoryDAO.getAllItems()) { if (it.id == id) { found = it; break; } }
            if (found != null) JOptionPane.showMessageDialog(this, found.name + "\nPrice: " + found.price + "\nStock: " + found.stock, "Part Details", JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem edit = new JMenuItem("Edit"); edit.addActionListener(e -> handleUpdate());
        JMenuItem del = new JMenuItem("Delete"); del.addActionListener(e -> handleUpdate());
        ipopup.add(view); ipopup.add(edit); ipopup.addSeparator(); ipopup.add(del);
        UITheme.styleMenu(ipopup);
        inventoryTable.setComponentPopupMenu(ipopup);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<InventoryDAO.InventoryItem> items = inventoryDAO.getAllItems();
        for (InventoryDAO.InventoryItem i : items) {
            tableModel.addRow(new Object[]{i.id, i.name, UITheme.formatCurrency(i.price), i.stock, i.threshold});
        }
    }

    private void handleAdd() {
        String name = nameField.getText().trim();
        double price;
        int stock;
        int threshold;
        try {
            price = Double.parseDouble(priceField.getText().trim());
            stock = Integer.parseInt(stockField.getText().trim());
            threshold = Integer.parseInt(thresholdField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid numeric values for price/stock/threshold.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Part name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (inventoryDAO.addItem(name, price, stock, threshold)) {
            JOptionPane.showMessageDialog(this, "Part added to inventory!");
            refreshTable();
            clearForm();
        }
    }

    private void handleUpdate() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select an inventory row to update stock.");
            return;
        }
        int stock;
        try {
            stock = Integer.parseInt(stockField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid stock value.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (inventoryDAO.updateStock(selectedId, stock)) {
            JOptionPane.showMessageDialog(this, "Inventory stock updated.");
            refreshTable();
        }
    }

    private void clearForm() {
        nameField.setText(""); priceField.setText(""); stockField.setText(""); thresholdField.setText("");
        selectedId = -1;
    }
}
