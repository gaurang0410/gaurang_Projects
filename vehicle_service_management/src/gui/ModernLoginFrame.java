package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.RoundRectangle2D;
import dao.UserDAO;

public class ModernLoginFrame extends JFrame {
    private ModernTextField userField;
    private JPasswordField passField;
    private JTextField passVisibleField;
    private JButton loginBtn;
    private JButton signUpBtn;
    private JButton exitBtn;
    private JButton eyeBtn;
    private JLabel messageLabel;
    private boolean passwordVisible = false;
    
    // Modern color palette
    private static final Color PRIMARY_COLOR = new Color(6, 182, 212);      // Cyan
    private static final Color SECONDARY_COLOR = new Color(14, 165, 233);   // Sky Blue
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);    // Dark slate
    private static final Color CARD_COLOR = new Color(30, 41, 59);          // Slate
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    public ModernLoginFrame() {
        setTitle("Vehicle Service Management - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setUndecorated(false);
        setResizable(false);
        
        initComponents();
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        setAlwaysOnTop(false);
        requestFocus();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative circles
                g2d.setColor(new Color(6, 182, 212, 30));
                g2d.fillOval(-100, -100, 300, 300);
                g2d.setColor(new Color(14, 165, 233, 20));
                g2d.fillOval(getWidth() - 150, getHeight() - 150, 350, 350);
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Left Panel - Branding
        JPanel brandPanel = createBrandPanel();
        brandPanel.setBounds(0, 0, 450, 600);
        mainPanel.add(brandPanel);

        // Right Panel - Login Form
        JPanel formPanel = createFormPanel();
        formPanel.setBounds(450, 0, 450, 600);
        mainPanel.add(formPanel);
    }
    
    private JPanel createBrandPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(6, 182, 212),
                    0, getHeight(), new Color(14, 165, 233)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(60, 30, 60, 30));

        // Logo/Icon
        JLabel logoLabel = new JLabel("[VEHICLE]");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 50));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(40));
        panel.add(logoLabel);

        panel.add(Box.createVerticalStrut(30));

        // Brand Title
        JLabel brandTitle = new JLabel("VehicleFlow");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 42));
        brandTitle.setForeground(Color.WHITE);
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(brandTitle);

        panel.add(Box.createVerticalStrut(15));

        // Tagline
        JLabel tagline = new JLabel("Modern Service Management");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagline.setForeground(new Color(220, 240, 255));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(tagline);

        panel.add(Box.createVerticalStrut(50));

        // Features
        JLabel feature1 = createFeatureLabel("Easy to manage");
        JLabel feature2 = createFeatureLabel("Real-time tracking");
        JLabel feature3 = createFeatureLabel("Secure & reliable");
        
        panel.add(feature1);
        panel.add(Box.createVerticalStrut(15));
        panel.add(feature2);
        panel.add(Box.createVerticalStrut(15));
        panel.add(feature3);

        panel.add(Box.createVerticalGlue());

        return panel;
    }
    
    private JLabel createFeatureLabel(String text) {
        JLabel label = new JLabel("[OK] " + text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(220, 240, 255));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBounds(40, 50, 350, 40);
        panel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to your account to continue");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBounds(40, 95, 350, 20);
        panel.add(subtitleLabel);

        // Username Label
        JLabel userLabel = new JLabel("Username or Email");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(TEXT_PRIMARY);
        userLabel.setBounds(40, 140, 350, 20);
        panel.add(userLabel);

        // Username Field
        userField = createModernTextField();
        userField.setHint("Enter username or email");
        userField.setBounds(40, 165, 370, 45);
        panel.add(userField);

        // Password Label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(TEXT_PRIMARY);
        passLabel.setBounds(40, 225, 350, 20);
        panel.add(passLabel);

        // Password Field
        passField = new JPasswordField();
        stylePasswordField(passField);
        passField.setBounds(40, 250, 325, 45);
        panel.add(passField);

        // Visible Password Field
        passVisibleField = createModernTextField();
        passVisibleField.setBounds(40, 250, 325, 45);
        passVisibleField.setVisible(false);
        panel.add(passVisibleField);

        // Eye Button
        eyeBtn = createIconButton("Show", PRIMARY_COLOR);
        eyeBtn.setBounds(375, 250, 35, 45);
        eyeBtn.addActionListener(e -> togglePasswordVisibility());
        panel.add(eyeBtn);

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(new Color(239, 68, 68));
        messageLabel.setBounds(40, 305, 370, 20);
        panel.add(messageLabel);

        // Login Button
        loginBtn = createModernButton("Sign In", PRIMARY_COLOR);
        loginBtn.setBounds(40, 340, 370, 50);
        loginBtn.addActionListener(e -> handleLogin());
        panel.add(loginBtn);

        // Divider
        JLabel divider = new JLabel("Or");
        divider.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        divider.setForeground(TEXT_SECONDARY);
        divider.setHorizontalAlignment(SwingConstants.CENTER);
        divider.setBounds(40, 405, 370, 20);
        panel.add(divider);

        // Sign Up Button
        signUpBtn = createModernButton("Create Account", SECONDARY_COLOR);
        signUpBtn.setBounds(40, 430, 370, 50);
        signUpBtn.addActionListener(e -> {
            ModernSignUpFrame signUpFrame = new ModernSignUpFrame();
            this.dispose();
        });
        panel.add(signUpBtn);

        // Footer
        JLabel footerLabel = new JLabel("Vehicle Service Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        footerLabel.setForeground(TEXT_SECONDARY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        footerLabel.setBounds(40, 540, 370, 20);
        panel.add(footerLabel);

        return panel;
    }
    
    private ModernTextField createModernTextField() {
        return new ModernTextField();
    }
    
    public static class ModernTextField extends JTextField {
        private String hint;

        public ModernTextField() {
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBackground(CARD_COLOR);
            setForeground(TEXT_PRIMARY);
            setCaretColor(PRIMARY_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && hint != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(148, 163, 184));
                g2d.setFont(getFont());
                g2d.drawString(hint, getInsets().left + 5, getHeight() / 2 + 5);
            }
        }
    }
    
    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(PRIMARY_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }
    
    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(
                        Math.max(color.getRed() - 20, 0),
                        Math.max(color.getGreen() - 20, 0),
                        Math.max(color.getBlue() - 20, 0)
                    ));
                } else if (getModel().isArmed()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JButton createIconButton(String text, Color color) {
        JButton button = createModernButton(text, color);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        return button;
    }
    
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passVisibleField.setText(new String(passField.getPassword()));
            passField.setVisible(false);
            passVisibleField.setVisible(true);
            eyeBtn.setText("Hide");
        } else {
            passField.setText(passVisibleField.getText());
            passVisibleField.setVisible(false);
            passField.setVisible(true);
            eyeBtn.setText("Show");
        }
    }
    
    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = passwordVisible ? passVisibleField.getText() : new String(passField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Please enter username/email and password");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        UserDAO userDAO = new UserDAO();
        org.json.JSONObject result = userDAO.authenticateUserWithDetails(user, pass);
        
        if (result != null && result.has("user_id") && result.getInt("user_id") > 0) {
            int userId = result.getInt("user_id");
            String fullName = result.optString("full_name", user);
            messageLabel.setText("Login successful!");
            messageLabel.setForeground(new Color(34, 197, 94));
            
            this.dispose();
            new ModernDashboardFrame(fullName, userId);
        } else {
            messageLabel.setText("Invalid credentials. Please try again.");
            messageLabel.setForeground(new Color(239, 68, 68));
            passField.setText("");
            passVisibleField.setText("");
        }
    }
}
