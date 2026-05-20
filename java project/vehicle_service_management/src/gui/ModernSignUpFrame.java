package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.regex.Pattern;
import dao.UserDAO;

public class ModernSignUpFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signUpBtn;
    private JButton loginBtn;
    private JLabel messageLabel;
    
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";

    public ModernSignUpFrame() {
        setTitle("VehicleFlow - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 850); // Increased height for more fields
        setResizable(false);
        
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.BG_DARK);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int y = 0;

        // Title
        JLabel titleLabel = new JLabel("Create Account", UITheme.getIcon("user", UITheme.ACCENT_CYAN, 32), SwingConstants.LEFT);
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setIconTextGap(15);
        gbc.gridy = y++;
        formPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join VehicleFlow professional network");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 0, 20, 0);
        formPanel.add(subtitleLabel, gbc);

        gbc.insets = new Insets(8, 0, 4, 0);

        // Full Name
        gbc.gridy = y++;
        formPanel.add(UITheme.styledLabel("Full Name"), gbc);
        fullNameField = UITheme.styledTextField("Enter your full name");
        gbc.gridy = y++;
        formPanel.add(fullNameField, gbc);

        // Username
        gbc.gridy = y++;
        formPanel.add(UITheme.styledLabel("Username"), gbc);
        usernameField = UITheme.styledTextField("Choose a unique username");
        gbc.gridy = y++;
        formPanel.add(usernameField, gbc);

        // Email
        gbc.gridy = y++;
        formPanel.add(UITheme.styledLabel("Email Address"), gbc);
        emailField = UITheme.styledTextField("Enter your email address");
        gbc.gridy = y++;
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridy = y++;
        formPanel.add(UITheme.styledLabel("Phone Number"), gbc);
        phoneField = UITheme.styledTextField("Enter 10-digit mobile number");
        gbc.gridy = y++;
        formPanel.add(phoneField, gbc);

        // Password
        gbc.gridy = y++;
        formPanel.add(UITheme.styledLabel("Password"), gbc);
        passwordField = UITheme.styledPasswordField("Create a strong password");
        JPanel passwordPanel = new JPanel(new BorderLayout(8, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(createPasswordToggleButton(passwordField), BorderLayout.EAST);
        gbc.gridy = y++;
        formPanel.add(passwordPanel, gbc);

        // Confirm Password
        gbc.gridy = y++;
        formPanel.add(UITheme.styledLabel("Confirm Password"), gbc);
        confirmPasswordField = UITheme.styledPasswordField("Repeat your password");
        JPanel confirmPanel = new JPanel(new BorderLayout(8, 0));
        confirmPanel.setOpaque(false);
        confirmPanel.add(confirmPasswordField, BorderLayout.CENTER);
        confirmPanel.add(createPasswordToggleButton(confirmPasswordField), BorderLayout.EAST);
        gbc.gridy = y++;
        formPanel.add(confirmPanel, gbc);

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setFont(UITheme.FONT_SMALL);
        messageLabel.setForeground(UITheme.ACCENT_RED);
        gbc.gridy = y++;
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(messageLabel, gbc);

        // Sign Up Button
        signUpBtn = UITheme.accentButton("Register Now", UITheme.ACCENT_CYAN, UITheme.getIcon("login", Color.WHITE, 20));
        signUpBtn.addActionListener(e -> handleSignUp());
        gbc.gridy = y++;
        gbc.ipady = 10;
        formPanel.add(signUpBtn, gbc);

        // Login Section
        JPanel loginSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        loginSection.setOpaque(false);
        JLabel loginText = new JLabel("Already have an account?");
        loginText.setFont(UITheme.FONT_SMALL);
        loginText.setForeground(UITheme.TEXT_SECONDARY);
        loginSection.add(loginText);

        loginBtn = UITheme.accentButton("Sign In", UITheme.ACCENT_BLUE);
        loginBtn.addActionListener(e -> {
            new ModernLoginFrame();
            this.dispose();
        });
        loginSection.add(loginBtn);

        gbc.gridy = y++;
        gbc.ipady = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(loginSection, gbc);

        mainPanel.add(formPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        setContentPane(scrollPane);
    }

    private JButton createPasswordToggleButton(JPasswordField field) {
        final char hiddenEcho = field.getEchoChar() == 0 ? '\u2022' : field.getEchoChar();
        JButton toggleBtn = new JButton("👁");
        toggleBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        toggleBtn.setFocusable(false);
        toggleBtn.setOpaque(true);
        boolean darkMode = UITheme.isDarkMode();
        toggleBtn.setBackground(darkMode ? UITheme.BG_INPUT : UITheme.LIGHT_BG_INPUT);
        toggleBtn.setForeground(darkMode ? UITheme.TEXT_SECONDARY : UITheme.LIGHT_TEXT_SECONDARY);
        toggleBtn.setBorder(BorderFactory.createLineBorder(darkMode ? UITheme.BORDER_DEFAULT : UITheme.LIGHT_BORDER_DEFAULT));
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.setPreferredSize(new Dimension(48, 42));
        toggleBtn.addActionListener(e -> {
            boolean showing = field.getEchoChar() == '\u0000';
            field.setEchoChar(showing ? hiddenEcho : '\u0000');
            toggleBtn.setText(showing ? "👁" : "🙈");
        });
        return toggleBtn;
    }
    
    private void handleSignUp() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            messageLabel.setText("Invalid email format!");
            return;
        }

        if (!Pattern.matches(PHONE_REGEX, phone)) {
            messageLabel.setText("Invalid phone number (10 digits)!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("Password too short (min 6 chars)!");
            return;
        }

        boolean success = UserDAO.registerUser(username, email, phone, password, fullName);

        if (success) {
            messageLabel.setText("Account created! Redirecting...");
            messageLabel.setForeground(UITheme.ACCENT_GREEN);
            
            Timer timer = new Timer(1500, e -> {
                new ModernLoginFrame();
                this.dispose();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            messageLabel.setText("Registration failed! Username/Email may exist.");
            messageLabel.setForeground(UITheme.ACCENT_RED);
        }
    }
}
