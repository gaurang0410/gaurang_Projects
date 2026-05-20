package gui;

import service.VehicleService;
import service.CustomerService;
import model.Vehicle;
import model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VehicleManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final VehicleService vehicleService = new VehicleService();
    private final CustomerService customerService = new CustomerService();
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> brandCombo;
    private JComboBox<String> modelCombo;
    private JComboBox<Customer> customerCombo;
    private JTextField regField;
    private JComboBox<String> categoryCombo;
    private JButton updateBtn, deleteBtn;
    private int selectedVehicleId = -1;
    private boolean isLoading = false;

    public VehicleManagementPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_DARK);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UITheme.BG_CARD);
        left.setBorder(new EmptyBorder(20, 20, 20, 20));
        left.setPreferredSize(new Dimension(380, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        brandCombo = UITheme.styledComboBox();
        modelCombo = UITheme.styledComboBox();
        customerCombo = new JComboBox<>();
        UITheme.styleInput(customerCombo);
        regField = UITheme.styledTextField("Registration Number (e.g. MH12AB1234)");
        categoryCombo = UITheme.styledComboBox(new String[]{"STANDARD", "PREMIUM", "LUXURY", "EV", "HEAVY"});

        int row = 0;
        gbc.gridy = row++; left.add(label("Owner Customer"), gbc); gbc.gridy = row++; left.add(customerCombo, gbc);
        gbc.gridy = row++; left.add(label("Vehicle Brand"), gbc); gbc.gridy = row++; left.add(brandCombo, gbc);
        gbc.gridy = row++; left.add(label("Vehicle Model"), gbc); gbc.gridy = row++; left.add(modelCombo, gbc);
        gbc.gridy = row++; left.add(label("Registration Number"), gbc); gbc.gridy = row++; left.add(regField, gbc);
        gbc.gridy = row++; left.add(label("Category"), gbc); gbc.gridy = row++; left.add(categoryCombo, gbc);

        brandCombo.addActionListener(e -> {
            if (isLoading) return;
            String sel = (String) brandCombo.getSelectedItem();
            loadModelsToCombo(sel);
        });

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
        gbc.gridy = row++; left.add(Box.createVerticalStrut(12), gbc);
        gbc.gridy = row++; left.add(actions, gbc);

        add(left, BorderLayout.WEST);

        // Right side: Table
        model = new DefaultTableModel(new String[]{"ID", "Owner", "Brand", "Model", "Reg. No", "Category"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> bindSelection());
        
        // Add right-click popup
        JPopupMenu popup = new JPopupMenu();
        JMenuItem edit = new JMenuItem("Edit Vehicle");
        edit.addActionListener(e -> bindSelection());
        JMenuItem track = new JMenuItem("View Service History");
        track.addActionListener(e -> {
             int r = table.getSelectedRow(); if (r < 0) return;
             int id = (int) model.getValueAt(table.convertRowIndexToModel(r), 0);
             new RecordsFrame(-1, -1, false);
        });
        JMenuItem viewDetails = new JMenuItem("View Details");
        viewDetails.addActionListener(e -> {
             int r = table.getSelectedRow(); if (r < 0) return;
             int mr = table.convertRowIndexToModel(r);
             String info = "Vehicle #" + model.getValueAt(mr, 0) + "\nOwner: " + model.getValueAt(mr, 1) +
                 "\nBrand: " + model.getValueAt(mr, 2) + "\nModel: " + model.getValueAt(mr, 3) +
                 "\nReg: " + model.getValueAt(mr, 4) + "\nCategory: " + model.getValueAt(mr, 5);
             JOptionPane.showMessageDialog(this, info, "Vehicle Details", JOptionPane.INFORMATION_MESSAGE);
        });
        popup.add(viewDetails);
        popup.add(edit);
        popup.add(track);
        popup.addSeparator();
        JMenuItem del = new JMenuItem("Delete Vehicle");
        del.addActionListener(e -> handleDelete());
        popup.add(del);
        UITheme.styleMenu(popup);
        table.setComponentPopupMenu(popup);
        // Select row on right-click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) { selectRowAt(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { selectRowAt(e); }
            private void selectRowAt(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int r = table.rowAtPoint(e.getPoint());
                    if (r >= 0 && !table.isRowSelected(r)) table.setRowSelectionInterval(r, r);
                }
            }
        });

        add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
    }

    private JLabel label(String text) {
        JLabel l = UITheme.styledLabel(text);
        l.setForeground(UITheme.TEXT_SECONDARY);
        l.setFont(UITheme.FONT_TINY);
        return l;
    }

    private void loadData() {
        isLoading = true;
        loadCustomers();
        loadBrandsToCombo();
        refreshTable();
        isLoading = false;
        if (brandCombo.getItemCount() > 0) loadModelsToCombo((String) brandCombo.getSelectedItem());
    }

    private void loadCustomers() {
        customerCombo.removeAllItems();
        for (Customer c : customerService.getAllCustomers()) customerCombo.addItem(c);
    }

    private void loadBrandsToCombo() {
        brandCombo.removeAllItems();
        List<String> brands = vehicleService.getAllBrands();
        for (String brand : brands) brandCombo.addItem(brand);
    }

    private void loadModelsToCombo(String brand) {
        modelCombo.removeAllItems();
        if (brand == null) return;
        List<String> models = vehicleService.getModelsByBrand(brand);
        for (String m : models) modelCombo.addItem(m);
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Vehicle v : vehicleService.getAllVehicles()) {
            model.addRow(new Object[]{
                v.getVehicleId(), v.getCustomerName(), v.getBrand(), v.getModel(), v.getRegistrationNumber(), v.getVehicleCategory()
            });
        }
    }

    private void handleAdd() {
        Vehicle v = buildFromForm();
        if (v == null) return;
        if (vehicleService.addVehicle(v)) {
            refreshTable();
            clearForm();
        } else JOptionPane.showMessageDialog(this, "Failed to register vehicle.");
    }

    private void handleUpdate() {
        if (selectedVehicleId == -1) return;
        Vehicle v = buildFromForm();
        if (v == null) return;
        v.setVehicleId(selectedVehicleId);
        if (vehicleService.updateVehicle(v)) {
            refreshTable();
            clearForm();
        } else JOptionPane.showMessageDialog(this, "Failed to update vehicle.");
    }

    private void handleDelete() {
        if (selectedVehicleId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Remove this vehicle?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (vehicleService.deleteVehicle(selectedVehicleId)) {
                refreshTable();
                clearForm();
            } else JOptionPane.showMessageDialog(this, "Failed to remove vehicle.");
        }
    }

    private Vehicle buildFromForm() {
        Customer c = (Customer) customerCombo.getSelectedItem();
        String brand = (String) brandCombo.getSelectedItem();
        String model = (String) modelCombo.getSelectedItem();
        String reg = regField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        if (c == null || brand == null || model == null || reg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return null;
        }
        Vehicle v = new Vehicle();
        v.setCustomerId(c.getCustomerId());
        v.setBrand(brand);
        v.setModel(model);
        v.setRegistrationNumber(reg);
        v.setVehicleCategory(category);
        return v;
    }

    private void bindSelection() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int mr = table.convertRowIndexToModel(r);
        selectedVehicleId = (int) model.getValueAt(mr, 0);
        String custName = (String) model.getValueAt(mr, 1);
        for (int i=0; i<customerCombo.getItemCount(); i++) {
            if (customerCombo.getItemAt(i).getName().equals(custName)) { customerCombo.setSelectedIndex(i); break; }
        }
        brandCombo.setSelectedItem(model.getValueAt(mr, 2));
        loadModelsToCombo((String) brandCombo.getSelectedItem());
        modelCombo.setSelectedItem(model.getValueAt(mr, 3));
        regField.setText((String) model.getValueAt(mr, 4));
        categoryCombo.setSelectedItem(model.getValueAt(mr, 5));
        setRowActionState(true);
    }

    private void clearForm() {
        regField.setText("");
        selectedVehicleId = -1;
        setRowActionState(false);
    }

    private void setRowActionState(boolean selected) {
        updateBtn.setEnabled(selected);
        deleteBtn.setEnabled(selected);
    }
}
