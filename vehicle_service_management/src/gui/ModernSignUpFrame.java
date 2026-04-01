package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;
import dao.UserDAO;

public class ModernSignUpFrame extends JFrame {
    private ModernTextField fullNameField;
    private ModernTextField usernameField;
    private ModernTextField emailField;
    private JPasswordField passwordField;
    private ModernTextField passwordVisibleField;
    private JPasswordField confirmPasswordField;
    private ModernTextField confirmPasswordVisibleField;
    private JButton signUpBtn;
    private JButton loginBtn;
    private JLabel messageLabel;
    private JLabel strengthLabel;
    private boolean password1Visible = false;
    private boolean password2Visible = false;
    
    private static final Color PRIMARY_COLOR = new Color(6, 182, 212);
    private static final Color SECONDARY_COLOR = new Color(14, 165, 233);
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    private static final Color CARD_COLOR = new Color(30, 41, 59);
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public ModernSignUpFrame() {
        setTitle("Vehicle Service Management - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 750);
        setResizable(false);
        
        initComponents();
        setLocationRelativeTo(null);
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
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Scroll Pane for form
        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBounds(0, 0, 500, 750);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        mainPanel.add(scrollPane);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(450, 900));

        int y = 30;

        // Title
        JLabel titleLabel = new JLabel("[NEW] Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBounds(25, y, 400, 40);
        panel.add(titleLabel);

        y += 50;

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join VehicleFlow today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBounds(25, y, 400, 20);
        panel.add(subtitleLabel);

        y += 40;

        // Full Name
        addField(panel, "Full Name", "fullName", y);
        fullNameField = new ModernTextField();
        fullNameField.setBounds(25, y + 25, 400, 45);
        panel.add(fullNameField);
        y += 75;

        // Username
        addField(panel, "Username", "username", y);
        usernameField = new ModernTextField();
        usernameField.setBounds(25, y + 25, 400, 45);
        panel.add(usernameField);
        y += 75;

        // Email
        addField(panel, "Email Address", "email", y);
        emailField = new ModernTextField();
        emailField.setBounds(25, y + 25, 400, 45);
        emailField.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) { validateEmail(); }
        });
        panel.add(emailField);
        y += 75;

        // Password
        addField(panel, "Password", "password", y);
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        passwordField.setBounds(25, y + 25, 350, 45);
        passwordField.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) { updatePasswordStrength(); }
        });
        panel.add(passwordField);

        JButton eyeBtn1 = createEyeButton();
        eyeBtn1.setBounds(385, y + 25, 40, 45);
        eyeBtn1.addActionListener(e -> togglePassword1());
        panel.add(eyeBtn1);
        y += 55;

        // Password Visible Field
        passwordVisibleField = new ModernTextField();
        passwordVisibleField.setBounds(25, y, 350, 45);
        passwordVisibleField.setVisible(false);
        panel.add(passwordVisibleField);

        // Strength Indicator
        strengthLabel = new JLabel("Password Strength: [_____]");
        strengthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        strengthLabel.setForeground(new Color(239, 68, 68));
        strengthLabel.setBounds(25, y + 55, 400, 20);
        panel.add(strengthLabel);
        y += 95;

        // Confirm Password
        addField(panel, "Confirm Password", "confirm", y);
        confirmPasswordField = new JPasswordField();
        stylePasswordField(confirmPasswordField);
        confirmPasswordField.setBounds(25, y + 25, 350, 45);
        panel.add(confirmPasswordField);

        JButton eyeBtn2 = createEyeButton();
        eyeBtn2.setBounds(385, y + 25, 40, 45);
        eyeBtn2.addActionListener(e -> togglePassword2());
        panel.add(eyeBtn2);
        y += 75;

        // Confirm Password Visible
        confirmPasswordVisibleField = new ModernTextField();
        confirmPasswordVisibleField.setBounds(25, y - 50, 350, 45);
        confirmPasswordVisibleField.setVisible(false);
        panel.add(confirmPasswordVisibleField);

        y += 40;

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        messageLabel.setForeground(new Color(239, 68, 68));
        messageLabel.setBounds(25, y, 400, 20);
        panel.add(messageLabel);
        y += 35;

        // Sign Up Button
        signUpBtn = createModernButton("Create Account", PRIMARY_COLOR);
        signUpBtn.setBounds(25, y, 400, 50);
        signUpBtn.addActionListener(e -> handleSignUp());
        panel.add(signUpBtn);
        y += 65;

        // Login Link
        JLabel loginText = new JLabel("Already have an account?");
        loginText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loginText.setForeground(TEXT_SECONDARY);
        loginText.setBounds(25, y, 250, 20);
        panel.add(loginText);

        loginBtn = createModernButton("Sign In", SECONDARY_COLOR);
        loginBtn.setBounds(25, y + 25, 400, 45);
        loginBtn.addActionListener(e -> {
            this.dispose();
            new ModernLoginFrame();
        });
        panel.add(loginBtn);

        return panel;
    }
    
    private void addField(JPanel panel, String label, String id, int y) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldLabel.setForeground(TEXT_PRIMARY);
        fieldLabel.setBounds(25, y, 400, 20);
        panel.add(fieldLabel);
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
    
    private JButton createEyeButton() {
        JButton button = new JButton("Show");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        button.setForeground(Color.WHITE);
        button.setBackground(CARD_COLOR);
        button.setBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void togglePassword1() {
        password1Visible = !password1Visible;
        if (password1Visible) {
            passwordVisibleField.setText(new String(passwordField.getPassword()));
            passwordField.setVisible(false);
            passwordVisibleField.setVisible(true);
        } else {
            passwordField.setText(passwordVisibleField.getText());
            passwordVisibleField.setVisible(false);
            passwordField.setVisible(true);
        }
    }
    
    private void togglePassword2() {
        password2Visible = !password2Visible;
        if (password2Visible) {
            confirmPasswordVisibleField.setText(new String(confirmPasswordField.getPassword()));
            confirmPasswordField.setVisible(false);
            confirmPasswordVisibleField.setVisible(true);
        } else {
            confirmPasswordField.setText(confirmPasswordVisibleField.getText());
            confirmPasswordVisibleField.setVisible(false);
            confirmPasswordField.setVisible(true);
        }
    }
    
    private void validateEmail() {
        String email = emailField.getText();
        if (!email.isEmpty() && !Pattern.matches(EMAIL_REGEX, email)) {
            emailField.setBackground(new Color(127, 29, 29));
        } else if (!email.isEmpty()) {
            emailField.setBackground(new Color(15, 118, 110));
        } else {
            emailField.setBackground(CARD_COLOR);
        }
    }
    
    private void updatePasswordStrength() {
        String pass = password1Visible ? passwordVisibleField.getText() : new String(passwordField.getPassword());
        int strength = calculatePasswordStrength(pass);
        
        String[] strengthTexts = {"No password", "Very Weak", "Weak", "Fair", "Strong", "Very Strong"};
        Color[] strengthColors = {
            new Color(239, 68, 68),      // Red
            new Color(239, 68, 68),      // Red
            new Color(239, 68, 68),      // Red
            new Color(245, 158, 11),     // Orange
            new Color(34, 197, 94),      // Green
            new Color(22, 163, 74)       // Dark Green
        };
        
        String bar = "[" + "#".repeat(strength) + "_".repeat(5 - strength) + "]";
        strengthLabel.setText("Password Strength: " + bar + " " + strengthTexts[strength]);
        strengthLabel.setForeground(strengthColors[strength]);
    }
    
    private int calculatePasswordStrength(String password) {
        if (password.isEmpty()) return 0;
        
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[@$!%*?&-_()+=].*")) score++;
        
        return Math.min(score / 2 + 1, 5);
    }
    
    private JButton createModernButton(String text, Color color) {
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
    
    private void handleSignUp() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = password1Visible ? passwordVisibleField.getText() : new String(passwordField.getPassword());
        String confirmPassword = password2Visible ? confirmPasswordVisibleField.getText() : new String(confirmPasswordField.getPassword());

        // Validations
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        if (username.length() < 4) {
            messageLabel.setText("Username must be at least 4 characters");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            messageLabel.setText("Please enter a valid email");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        if (!isStrongPassword(password)) {
            messageLabel.setText("Password must contain uppercase, lowercase, number, and special character");
            messageLabel.setForeground(new Color(239, 68, 68));
            return;
        }

        // Register user
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.registerUser(username, email, password, fullName);

        if (success) {
            messageLabel.setText("Account created successfully!");
            messageLabel.setForeground(new Color(34, 197, 94));
            
            Timer timer = new Timer(1500, e -> {
                this.dispose();
                new ModernLoginFrame();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            messageLabel.setText("Sign up failed. Username or email may already exist.");
            messageLabel.setForeground(new Color(239, 68, 68));
        }
    }
    
    private boolean isStrongPassword(String password) {
        return password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*[0-9].*") &&
               password.matches(".*[@$!%*?&-_()+=].*");
    }
    
    public static class ModernTextField extends JTextField {
        private String hint;

        public ModernTextField() {
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBackground(new Color(30, 41, 59));
            setForeground(Color.WHITE);
            setCaretColor(new Color(6, 182, 212));
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
}
