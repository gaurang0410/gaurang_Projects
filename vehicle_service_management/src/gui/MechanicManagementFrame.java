package gui;

import dao.MechanicDAO;
import model.Mechanic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class MechanicManagementFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final MechanicDAO mechanicDAO = new MechanicDAO();
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField nameField;
    private JTextField specializationField;
    private JTextField phoneField;
    private JComboBox<String> availabilityCombo;
    private JTextField ratingField;
    private JTextField filterField;
    private int selectedMechanicId = -1;

    public MechanicManagementFrame() {
        setTitle("VehicleFlow - Mechanic & Staff Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1120, 680);
        setLocationRelativeTo(null);
        initComponents();
        loadMechanics();
        setVisible(true);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(UITheme.BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UITheme.BG_CARD);
        left.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        left.setPreferredSize(new Dimension(360, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        int row = 0;

        nameField = UITheme.styledTextField("Name");
        specializationField = UITheme.styledTextField("Specialization");
        phoneField = UITheme.styledTextField("Phone");
        availabilityCombo = UITheme.styledComboBox(new String[]{"Available", "Busy", "On Leave"});
        ratingField = UITheme.styledTextField("Rating 0-5");

        gbc.gridy = row++; left.add(label("Name"), gbc); gbc.gridy = row++; left.add(nameField, gbc);
        gbc.gridy = row++; left.add(label("Specialization"), gbc); gbc.gridy = row++; left.add(specializationField, gbc);
        gbc.gridy = row++; left.add(label("Phone"), gbc); gbc.gridy = row++; left.add(phoneField, gbc);
        gbc.gridy = row++; left.add(label("Availability"), gbc); gbc.gridy = row++; left.add(availabilityCombo, gbc);
        gbc.gridy = row++; left.add(label("Rating"), gbc); gbc.gridy = row++; left.add(ratingField, gbc);

        JPanel actions = new JPanel(new GridLayout(2, 2, 8, 8));
        actions.setOpaque(false);
        JButton addBtn = UITheme.accentButton("Add", UITheme.ACCENT_GREEN);
        JButton updateBtn = UITheme.accentButton("Update", UITheme.ACCENT_BLUE);
        JButton deleteBtn = UITheme.accentButton("Delete", UITheme.ACCENT_RED);
        JButton clearBtn = UITheme.accentButton("Clear", UITheme.BORDER_DEFAULT);
        addBtn.addActionListener(e -> saveMechanic());
        updateBtn.addActionListener(e -> updateMechanic());
        deleteBtn.addActionListener(e -> deleteMechanic());
        clearBtn.addActionListener(e -> clearForm());
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(clearBtn);
        gbc.gridy = row++;
        left.add(Box.createVerticalStrut(8), gbc);
        gbc.gridy = row++;
        left.add(actions, gbc);

        root.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        filterField = UITheme.styledTextField("Filter by name/specialization/availability");
        filterField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        top.add(filterField, BorderLayout.CENTER);
        right.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Specialization", "Phone", "Availability", "Rating", "Active Jobs", "Completed Jobs"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.getSelectionModel().addListSelectionListener(e -> bindSelection());
        right.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);

        // Add right-click popup for mechanic table
        JPopupMenu popup = new JPopupMenu();
        JMenuItem view = new JMenuItem("View Details");
        view.addActionListener(e -> {
            int selectedRow = table.getSelectedRow(); if (selectedRow < 0) return;
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = Integer.parseInt(String.valueOf(model.getValueAt(modelRow, 0)));
            Mechanic found = null;
            for (Mechanic mm : mechanicDAO.getAllMechanics()) {
                if (mm.getMechanicId() == id) { found = mm; break; }
            }
            if (found != null) JOptionPane.showMessageDialog(this, found.toString(), "Mechanic Details", JOptionPane.INFORMATION_MESSAGE);
        });
        JMenuItem edit = new JMenuItem("Edit");
        edit.addActionListener(e -> bindSelection());
        JMenuItem del = new JMenuItem("Delete");
        del.addActionListener(e -> deleteMechanic());
        popup.add(view); popup.add(edit); popup.addSeparator(); popup.add(del);
        UITheme.styleMenu(popup);
        table.setComponentPopupMenu(popup);

        root.add(right, BorderLayout.CENTER);
    }

    private JLabel label(String text) {
        JLabel label = UITheme.styledLabel(text);
        label.setForeground(UITheme.TEXT_SECONDARY);
        return label;
    }

    private void loadMechanics() {
        model.setRowCount(0);
        List<Mechanic> mechanics = mechanicDAO.getAllMechanics();
        for (Mechanic mechanic : mechanics) {
            model.addRow(new Object[]{
                    mechanic.getMechanicId(),
                    mechanic.getName(),
                    mechanic.getSpecialization(),
                    mechanic.getPhone(),
                    mechanic.getAvailability(),
                    String.format("%.1f", mechanic.getRating()),
                    mechanic.getActiveJobs(),
                    mechanic.getCompletedJobs()
            });
        }
    }

    private void saveMechanic() {
        Mechanic mechanic = buildFromForm();
        if (mechanic == null) return;
        if (mechanicDAO.addMechanic(mechanic)) {
            loadMechanics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add mechanic.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMechanic() {
        if (selectedMechanicId <= 0) {
            JOptionPane.showMessageDialog(this, "Select a mechanic row to update.");
            return;
        }
        Mechanic mechanic = buildFromForm();
        if (mechanic == null) return;
        mechanic.setMechanicId(selectedMechanicId);
        if (mechanicDAO.updateMechanic(mechanic)) {
            loadMechanics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update mechanic.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMechanic() {
        if (selectedMechanicId <= 0) {
            JOptionPane.showMessageDialog(this, "Select a mechanic row to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected mechanic?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (mechanicDAO.deleteMechanic(selectedMechanicId)) {
            loadMechanics();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete mechanic.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Mechanic buildFromForm() {
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String phone = phoneField.getText().trim();
        String availability = String.valueOf(availabilityCombo.getSelectedItem());
        String ratingText = ratingField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return null;
        }
        double rating = 0;
        try {
            rating = ratingText.isEmpty() ? 0 : Double.parseDouble(ratingText);
            if (rating < 0 || rating > 5) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Rating must be between 0 and 5.");
            return null;
        }
        Mechanic mechanic = new Mechanic();
        mechanic.setName(name);
        mechanic.setSpecialization(specialization);
        mechanic.setPhone(phone);
        mechanic.setAvailability(availability);
        mechanic.setRating(rating);
        return mechanic;
    }

    private void bindSelection() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int modelRow = table.convertRowIndexToModel(row);
        selectedMechanicId = Integer.parseInt(String.valueOf(model.getValueAt(modelRow, 0)));
        nameField.setText(String.valueOf(model.getValueAt(modelRow, 1)));
        specializationField.setText(String.valueOf(model.getValueAt(modelRow, 2)));
        phoneField.setText(String.valueOf(model.getValueAt(modelRow, 3)));
        availabilityCombo.setSelectedItem(String.valueOf(model.getValueAt(modelRow, 4)));
        ratingField.setText(String.valueOf(model.getValueAt(modelRow, 5)));
    }

    private void clearForm() {
        selectedMechanicId = -1;
        nameField.setText("");
        specializationField.setText("");
        phoneField.setText("");
        availabilityCombo.setSelectedIndex(0);
        ratingField.setText("");
        table.clearSelection();
    }

    private void applyFilter() {
        String query = filterField.getText().trim();
        if (query.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
    }
}
