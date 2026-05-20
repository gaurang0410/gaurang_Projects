package gui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ModernSettingsFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final int userId;
    private final String role;
    private JComboBox<String> themeCombo;
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;

    public ModernSettingsFrame(int userId, String role) {
        this.userId = userId;
        this.role = role;
        setTitle(role.equals("ADMIN") ? "VehicleFlow - Admin Settings" : "VehicleFlow - My Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        loadProfile();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UITheme.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Settings & Preferences", UITheme.getIcon("settings", UITheme.ACCENT_CYAN, 32), SwingConstants.LEFT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setIconTextGap(15);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        contentPanel.add(createSection("Account Settings", createAccountSettings()));
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(createSection("Appearance", createAppearanceSettings()));
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(createSection("Notifications", createNotificationSettings()));
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(createAboutSection());

        JScrollPane scrollPane = UITheme.styledScrollPane(contentPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton saveProfileBtn = UITheme.accentButton("Save Profile", UITheme.ACCENT_GREEN);
        saveProfileBtn.addActionListener(e -> saveProfile());
        JButton closeBtn = UITheme.accentButton("Close", UITheme.ACCENT_CYAN);
        closeBtn.addActionListener(e -> this.dispose());
        footer.add(saveProfileBtn);
        footer.add(closeBtn);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    private JPanel createSection(String title, JPanel content) {
        JPanel sectionPanel = new JPanel(new BorderLayout(0, 15));
        sectionPanel.setBackground(UITheme.BG_CARD);
        sectionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        sectionPanel.setMaximumSize(new Dimension(760, 340));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(UITheme.FONT_HEADING);
        sectionTitle.setForeground(UITheme.ACCENT_CYAN);
        sectionPanel.add(sectionTitle, BorderLayout.NORTH);
        sectionPanel.add(content, BorderLayout.CENTER);
        return sectionPanel;
    }

    private JPanel createAccountSettings() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(UITheme.styledLabel("User ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panel.add(UITheme.styledLabel(String.valueOf(userId)), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; panel.add(UITheme.styledLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; panel.add(UITheme.styledLabel(role), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; panel.add(UITheme.styledLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        nameField = UITheme.styledTextField("Enter full name");
        panel.add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; panel.add(UITheme.styledLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        phoneField = UITheme.styledTextField("Enter phone");
        panel.add(phoneField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; panel.add(UITheme.styledLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        emailField = UITheme.styledTextField("Enter email");
        panel.add(emailField, gbc);
        row++;

        JButton changePassBtn = UITheme.accentButton("Change Password", UITheme.ACCENT_PURPLE, UITheme.getIcon("lock", Color.WHITE, 16));
        changePassBtn.setPreferredSize(new Dimension(220, 35));
        changePassBtn.addActionListener(e -> openChangePasswordDialog());
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(changePassBtn, gbc);
        return panel;
    }

    private void loadProfile() {
        User user = UserDAO.getUserById(userId);
        if (user == null) return;
        nameField.setText(user.getFullName());
        phoneField.setText(user.getPhone() == null ? "" : user.getPhone());
        emailField.setText(user.getEmail());
    }

    private void saveProfile() {
        String fullName = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        if (fullName.isEmpty() || email.isEmpty()) {
            UITheme.showInfoDialog(this, "Validation", "Name and email are required.");
            return;
        }
        boolean ok = UserDAO.updateProfile(userId, fullName, email, phone);
        if (ok) {
            UITheme.showSuccessDialog(this, "Profile Saved", "Profile updated successfully.");
        } else {
            UITheme.showErrorDialog(this, "Update Failed", "Failed to update profile.");
        }
    }

    private void openChangePasswordDialog() {
        JPasswordField current = UITheme.styledPasswordField("Current password");
        JPasswordField next = UITheme.styledPasswordField("New password");
        JPasswordField confirm = UITheme.styledPasswordField("Confirm new password");

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Current Password"));
        panel.add(current);
        panel.add(new JLabel("New Password"));
        panel.add(next);
        panel.add(new JLabel("Confirm New Password"));
        panel.add(confirm);

        int choice = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String currentPwd = new String(current.getPassword());
        String newPwd = new String(next.getPassword());
        String confirmPwd = new String(confirm.getPassword());
        if (currentPwd.trim().isEmpty() || newPwd.trim().isEmpty()) {
            UITheme.showInfoDialog(this, "Validation", "Password fields cannot be empty.");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            UITheme.showInfoDialog(this, "Mismatch", "New password and confirmation do not match.");
            return;
        }
        if (newPwd.length() < 6) {
            UITheme.showInfoDialog(this, "Too Short", "New password must be at least 6 characters.");
            return;
        }
        try {
            boolean ok = UserDAO.changePassword(userId, currentPwd, newPwd);
            if (ok) {
                UITheme.showSuccessDialog(this, "Success", "Password changed successfully.");
            } else {
                UITheme.showErrorDialog(this, "Failed", "Password change failed.");
            }
        } catch (Exception ex) {
            UITheme.showErrorDialog(this, "Error", ex.getMessage());
        }
    }

    private JPanel createAppearanceSettings() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel themeLabel = new JLabel("Theme Mode:");
        themeLabel.setFont(UITheme.FONT_BODY);
        themeLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(themeLabel, gbc);

        themeCombo = UITheme.styledComboBox(new String[]{"Dark Theme", "Light Theme"});
        themeCombo.setPreferredSize(new Dimension(240, 35));
        themeCombo.setSelectedItem(UITheme.isDarkMode() ? "Dark Theme" : "Light Theme");
        themeCombo.addActionListener(e -> UITheme.applyGlobalTheme("Dark Theme".equals(themeCombo.getSelectedItem())));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(themeCombo, gbc);
        return panel;
    }

    private JPanel createNotificationSettings() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setOpaque(false);
        panel.add(createToggleSetting("Enable Email Notifications", true));
        panel.add(createToggleSetting("Service Reminders", true));
        return panel;
    }

    private JPanel createToggleSetting(String label, boolean defaultValue) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(UITheme.FONT_BODY);
        labelComp.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(labelComp, BorderLayout.WEST);
        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(defaultValue);
        toggle.setOpaque(false);
        toggle.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(toggle, BorderLayout.EAST);
        return panel;
    }

    private JPanel createAboutSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setMaximumSize(new Dimension(600, 150));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel appName = new JLabel("VehicleFlow", UITheme.getIcon("vehicle", UITheme.ACCENT_CYAN, 24), SwingConstants.LEFT);
        appName.setFont(UITheme.FONT_HEADING);
        appName.setForeground(UITheme.ACCENT_CYAN);
        appName.setIconTextGap(10);
        topRow.add(appName, BorderLayout.WEST);
        JLabel version = new JLabel("Version 2.6.0 Premium");
        version.setFont(UITheme.FONT_SMALL);
        version.setForeground(UITheme.TEXT_SECONDARY);
        topRow.add(version, BorderLayout.EAST);
        panel.add(topRow, BorderLayout.NORTH);

        JLabel description = new JLabel("<html>Professional Grade Vehicle Service Management System. Designed for performance and reliability.</html>");
        description.setFont(UITheme.FONT_SMALL);
        description.setForeground(UITheme.TEXT_MUTED);
        panel.add(description, BorderLayout.CENTER);
        return panel;
    }
}
