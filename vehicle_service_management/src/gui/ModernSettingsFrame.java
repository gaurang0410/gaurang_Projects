package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModernSettingsFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(6, 182, 212);
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    private static final Color CARD_COLOR = new Color(30, 41, 59);
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    private String currentUsername;

    public ModernSettingsFrame(String username) {
        this.currentUsername = username;
        setTitle("VehicleFlow - Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    0, getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header
        JLabel titleLabel = new JLabel("[GEAR] Settings & Preferences");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Content
        JScrollPane scrollPane = new JScrollPane(createSettingsPanel());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Account Settings Section
        panel.add(createSection("Account Settings", createAccountSettings()));
        panel.add(Box.createVerticalStrut(20));

        // Appearance Settings Section
        panel.add(createSection("Appearance", createAppearanceSettings()));
        panel.add(Box.createVerticalStrut(20));

        // Notification Settings Section
        panel.add(createSection("Notifications", createNotificationSettings()));
        panel.add(Box.createVerticalStrut(20));

        // Privacy Settings Section
        panel.add(createSection("Privacy & Security", createPrivacySettings()));
        panel.add(Box.createVerticalStrut(20));

        // About Section
        panel.add(createSection("About", createAboutSection()));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createSection(String title, JPanel content) {
        JPanel sectionPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2d.setColor(PRIMARY_COLOR);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        sectionPanel.setOpaque(false);
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, content.getPreferredSize().height + 50));

        JLabel sectionTitle = new JLabel(title);
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(TEXT_PRIMARY);
        sectionTitle.setBounds(20, 15, 300, 25);
        sectionPanel.add(sectionTitle);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        contentPanel.add(content, BorderLayout.CENTER);
        contentPanel.setBounds(0, 45, 500, content.getPreferredSize().height);
        sectionPanel.add(contentPanel);

        sectionPanel.setPreferredSize(new Dimension(500, 45 + content.getPreferredSize().height + 20));

        return sectionPanel;
    }

    private JPanel createAccountSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Username
        panel.add(createSetting("Username", currentUsername, false));
        panel.add(Box.createVerticalStrut(15));

        // Email
        panel.add(createSetting("Email", "user@example.com", false));
        panel.add(Box.createVerticalStrut(15));

        // Change Password Button
        JButton changePassBtn = createSettingButton("Change Password", new Color(33, 150, 243));
        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        panel.add(changePassBtn);

        return panel;
    }

    private JPanel createAppearanceSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Theme
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        themePanel.setOpaque(false);
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        themeLabel.setForeground(TEXT_PRIMARY);
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Dark", "Light", "Auto"});
        styleComboBox(themeCombo);
        themeCombo.setSelectedItem("Dark");
        themePanel.add(themeLabel);
        themePanel.add(themeCombo);
        panel.add(themePanel);

        panel.add(Box.createVerticalStrut(15));

        // Font Size
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        fontPanel.setOpaque(false);
        JLabel fontLabel = new JLabel("Font Size:");
        fontLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fontLabel.setForeground(TEXT_PRIMARY);
        JSlider fontSlider = new JSlider(10, 18, 14);
        fontSlider.setOpaque(false);
        fontSlider.setPaintTicks(true);
        fontSlider.setMajorTickSpacing(2);
        fontSlider.setMinorTickSpacing(1);
        fontSlider.setPreferredSize(new Dimension(150, 40));
        fontPanel.add(fontLabel);
        fontPanel.add(fontSlider);
        panel.add(fontPanel);

        return panel;
    }

    private JPanel createNotificationSettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createToggleSetting("Email Notifications", true));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createToggleSetting("Service Reminders", true));
        panel.add(Box.createVerticalStrut(12));
        panel.add(createToggleSetting("Booking Confirmations", false));

        return panel;
    }

    private JPanel createPrivacySettings() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(createToggleSetting("Two-Factor Authentication", false));
        panel.add(Box.createVerticalStrut(15));

        JButton deleteBtn = createSettingButton("Delete Account", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> showDeleteAccountDialog());
        panel.add(deleteBtn);

        return panel;
    }

    private JPanel createAboutSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel appName = new JLabel("VehicleFlow");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        appName.setForeground(TEXT_PRIMARY);
        panel.add(appName);

        JLabel version = new JLabel("Version 2.0.0");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        version.setForeground(TEXT_SECONDARY);
        panel.add(version);

        panel.add(Box.createVerticalStrut(8));

        JLabel description = new JLabel("Modern Vehicle Service Management System");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        description.setForeground(TEXT_SECONDARY);
        panel.add(description);

        return panel;
    }

    private JPanel createSetting(String label, String value, boolean editable) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComp.setForeground(TEXT_PRIMARY);
        labelComp.setPreferredSize(new Dimension(100, 25));
        panel.add(labelComp);

        JTextField valueField = new JTextField(value);
        valueField.setEditable(editable);
        styleTextField(valueField);
        valueField.setPreferredSize(new Dimension(250, 30));
        panel.add(valueField);

        return panel;
    }

    private JPanel createToggleSetting(String label, boolean defaultValue) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setOpaque(false);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelComp.setForeground(TEXT_PRIMARY);
        panel.add(labelComp);

        JCheckBox toggle = new JCheckBox();
        toggle.setSelected(defaultValue);
        toggle.setOpaque(false);
        toggle.setFocusPainted(false);
        panel.add(toggle);

        return panel;
    }

    private JButton createSettingButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(Math.max(color.getRed() - 20, 0), Math.max(color.getGreen() - 20, 0), Math.max(color.getBlue() - 20, 0)));
                } else if (getModel().isArmed()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(CARD_COLOR);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(null);
        panel.setBackground(BACKGROUND_COLOR);

        // Current Password
        JLabel currentLabel = new JLabel("Current Password:");
        currentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        currentLabel.setForeground(TEXT_PRIMARY);
        currentLabel.setBounds(20, 20, 360, 20);
        panel.add(currentLabel);

        JPasswordField currentField = new JPasswordField();
        stylePasswordField(currentField);
        currentField.setBounds(20, 45, 360, 35);
        panel.add(currentField);

        // New Password
        JLabel newLabel = new JLabel("New Password:");
        newLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newLabel.setForeground(TEXT_PRIMARY);
        newLabel.setBounds(20, 90, 360, 20);
        panel.add(newLabel);

        JPasswordField newField = new JPasswordField();
        stylePasswordField(newField);
        newField.setBounds(20, 115, 360, 35);
        panel.add(newField);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmLabel.setForeground(TEXT_PRIMARY);
        confirmLabel.setBounds(20, 160, 360, 20);
        panel.add(confirmLabel);

        JPasswordField confirmField = new JPasswordField();
        stylePasswordField(confirmField);
        confirmField.setBounds(20, 185, 360, 35);
        panel.add(confirmField);

        // Save Button
        JButton saveBtn = createSettingButton("Update Password", PRIMARY_COLOR);
        saveBtn.setBounds(20, 235, 360, 40);
        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        panel.add(saveBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void showDeleteAccountDialog() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete your account?\nThis action cannot be undone.",
            "Delete Account",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Account deletion initiated. You will be logged out.", "Info", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
    }
}
