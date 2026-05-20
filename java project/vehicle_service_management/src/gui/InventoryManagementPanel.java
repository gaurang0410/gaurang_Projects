package gui;

import dao.InventoryDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryManagementPanel extends JPanel {
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField partNameField, quantityField, priceField;

    public InventoryManagementPanel() {
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

        partNameField = UITheme.styledTextField("Part Name");
        quantityField = UITheme.styledTextField("Quantity");
        priceField = UITheme.styledTextField("Unit Price");

        int row = 0;
        gbc.gridy = row++; left.add(label("Part Name"), gbc); gbc.gridy = row++; left.add(partNameField, gbc);
        gbc.gridy = row++; left.add(label("Quantity In Stock"), gbc); gbc.gridy = row++; left.add(quantityField, gbc);
        gbc.gridy = row++; left.add(label("Unit Price (₹)"), gbc); gbc.gridy = row++; left.add(priceField, gbc);

        JButton addBtn = UITheme.accentButton("Add Stock", UITheme.ACCENT_CYAN);
        addBtn.addActionListener(e -> handleAdd());
        gbc.gridy = row++; left.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = row++; left.add(addBtn, gbc);

        add(left, BorderLayout.WEST);

        model = new DefaultTableModel(new String[]{"ID", "Part Name", "Quantity", "Unit Price", "Value"}, 0);
        table = new JTable(model);
        UITheme.styleTable(table);
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
        for (InventoryDAO.InventoryItem item : inventoryDAO.getAllItems()) {
            model.addRow(new Object[]{item.id, item.name, item.stock, UITheme.formatCurrency(item.price), UITheme.formatCurrency(item.stock * item.price)});
        }
    }

    private void handleAdd() {
        String name = partNameField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        String prcStr = priceField.getText().trim();
        if (name.isEmpty() || qtyStr.isEmpty() || prcStr.isEmpty()) return;
        try {
            int qty = Integer.parseInt(qtyStr);
            double prc = Double.parseDouble(prcStr);
            // Search if exists
            List<InventoryDAO.InventoryItem> items = inventoryDAO.getAllItems();
            boolean found = false;
            for(InventoryDAO.InventoryItem item : items) {
                if(item.name.equalsIgnoreCase(name)) {
                    inventoryDAO.updateStock(item.id, item.stock + qty);
                    found = true;
                    break;
                }
            }
            if(!found) {
                inventoryDAO.addItem(name, prc, qty, 10);
            }
            
            refreshTable();
            partNameField.setText(""); quantityField.setText(""); priceField.setText("");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid numbers."); }
    }
}
