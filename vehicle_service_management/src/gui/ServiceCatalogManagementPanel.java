package gui;

import dao.ServiceCatalogDAO;
import model.ServiceCatalogItem;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ServiceCatalogManagementPanel extends JPanel {
    private final ServiceCatalogDAO dao = new ServiceCatalogDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, categoryField, costField, timeField;

    public ServiceCatalogManagementPanel() {
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

        nameField = UITheme.styledTextField("Service Name");
        categoryField = UITheme.styledTextField("Category (General, Engine, etc.)");
        costField = UITheme.styledTextField("Base Cost");
        timeField = UITheme.styledTextField("Estimated Time (e.g. 2 hours)");

        int row = 0;
        gbc.gridy = row++; left.add(label("Service Name"), gbc); gbc.gridy = row++; left.add(nameField, gbc);
        gbc.gridy = row++; left.add(label("Category"), gbc); gbc.gridy = row++; left.add(categoryField, gbc);
        gbc.gridy = row++; left.add(label("Base Cost (₹)"), gbc); gbc.gridy = row++; left.add(costField, gbc);
        gbc.gridy = row++; left.add(label("Estimated Time"), gbc); gbc.gridy = row++; left.add(timeField, gbc);

        JButton addBtn = UITheme.accentButton("Save Service", UITheme.ACCENT_PURPLE);
        addBtn.addActionListener(e -> handleAdd());
        gbc.gridy = row++; left.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = row++; left.add(addBtn, gbc);

        add(left, BorderLayout.WEST);

        model = new DefaultTableModel(new String[]{"ID", "Service Name", "Category", "Base Cost", "Time"}, 0);
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
        for (ServiceCatalogItem item : dao.getAllServices()) {
            model.addRow(new Object[]{item.getServiceId(), item.getServiceName(), item.getCategory(), UITheme.formatCurrency(item.getBaseCost()), item.getEstimatedTime()});
        }
    }

    private void handleAdd() {
        String name = nameField.getText().trim();
        String cat = categoryField.getText().trim();
        String costStr = costField.getText().trim();
        String time = timeField.getText().trim();
        if (name.isEmpty() || costStr.isEmpty()) return;
        try {
            double cost = Double.parseDouble(costStr);
            ServiceCatalogItem item = new ServiceCatalogItem();
            item.setServiceName(name);
            item.setCategory(cat);
            item.setBaseCost(cost);
            item.setEstimatedTime(time);
            if (dao.addService(item)) {
                refreshTable();
                nameField.setText(""); categoryField.setText(""); costField.setText(""); timeField.setText("");
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid cost."); }
    }
}
