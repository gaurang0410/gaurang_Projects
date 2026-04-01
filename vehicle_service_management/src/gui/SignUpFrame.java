package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Pattern;
import dao.UserDAO;

public class SignUpFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField passwordVisibleField;
    private JPasswordField confirmPasswordField;
    private JTextField confirmPasswordVisibleField;
    private JTextField fullNameField;
    private JButton signUpBtn;
    private JButton backBtn;
    private JButton eyeBtn1;
    private JButton eyeBtn2;
    private JLabel messageLabel;
    private JLabel passwordStrengthLabel;
    private JLabel emailValidationLabel;
    private boolean password1Visible = false;
    private boolean password2Visible = false;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public SignUpFrame() {
        setTitle("Vehicle Service Management - Sign Up");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 630);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 245, 250));

        // Title
        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 100, 180));
        titleLabel.setBounds(130, 15, 300, 30);
        panel.add(titleLabel);

        // Info label
        JLabel infoLabel = new JLabel("Use a valid email and strong password");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setBounds(100, 45, 380, 15);
        panel.add(infoLabel);

        // Full Name Label
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fullNameLabel.setBounds(40, 70, 100, 25);
        panel.add(fullNameLabel);

        // Full Name Field
        fullNameField = new JTextField();
        fullNameField.setBounds(150, 70, 380, 25);
        panel.add(fullNameField);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        usernameLabel.setBounds(40, 110, 100, 25);
        panel.add(usernameLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(150, 110, 380, 25);
        panel.add(usernameField);

        // Email Label
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setBounds(40, 150, 100, 25);
        panel.add(emailLabel);

        // Email Field
        emailField = new JTextField();
        emailField.setBounds(150, 150, 380, 25);
        emailField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                validateEmail();
            }
        });
        panel.add(emailField);

        // Email Validation Label
        emailValidationLabel = new JLabel("");
        emailValidationLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        emailValidationLabel.setBounds(150, 175, 380, 15);
        panel.add(emailValidationLabel);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordLabel.setBounds(40, 200, 100, 25);
        panel.add(passwordLabel);

        // Password Field (hidden)
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 200, 330, 25);
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                updatePasswordStrength();
            }
        });
        panel.add(passwordField);

        // Password Visible Field
        passwordVisibleField = new JTextField();
        passwordVisibleField.setBounds(150, 200, 330, 25);
        passwordVisibleField.setVisible(false);
        passwordVisibleField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                updatePasswordStrength();
            }
        });
        panel.add(passwordVisibleField);

        // Eye Button 1
        eyeBtn1 = new JButton("Show");
        eyeBtn1.setFont(new Font("Arial", Font.PLAIN, 10));
        eyeBtn1.setBounds(490, 200, 50, 25);
        eyeBtn1.addActionListener(e -> togglePasswordVisibility(1));
        panel.add(eyeBtn1);

        // Password Strength Label
        passwordStrengthLabel = new JLabel("Strength: [____] Weak");
        passwordStrengthLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        passwordStrengthLabel.setForeground(Color.RED);
        passwordStrengthLabel.setBounds(150, 225, 380, 15);
        panel.add(passwordStrengthLabel);

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Confirm:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        confirmPasswordLabel.setBounds(40, 250, 100, 25);
        panel.add(confirmPasswordLabel);

        // Confirm Password Field (hidden)
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(150, 250, 330, 25);
        panel.add(confirmPasswordField);

        // Confirm Password Visible Field
        confirmPasswordVisibleField = new JTextField();
        confirmPasswordVisibleField.setBounds(150, 250, 330, 25);
        confirmPasswordVisibleField.setVisible(false);
        panel.add(confirmPasswordVisibleField);

        // Eye Button 2
        eyeBtn2 = new JButton("Show");
        eyeBtn2.setFont(new Font("Arial", Font.PLAIN, 10));
        eyeBtn2.setBounds(490, 250, 50, 25);
        eyeBtn2.addActionListener(e -> togglePasswordVisibility(2));
        panel.add(eyeBtn2);

        // Message Label (for errors/info)
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(40, 290, 490, 25);
        panel.add(messageLabel);

        // Sign Up Button
        signUpBtn = new JButton("Create Account");
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 12));
        signUpBtn.setBackground(new Color(76, 175, 80));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setBounds(150, 330, 140, 35);
        signUpBtn.addActionListener(e -> handleSignUp());
        panel.add(signUpBtn);

        // Back Button
        backBtn = new JButton("Back to Login");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        backBtn.setBounds(310, 330, 140, 35);
        backBtn.addActionListener(e -> SignUpFrame.this.dispose());
        panel.add(backBtn);

        // Info text
        JLabel infoLabel2 = new JLabel("Password must contain: uppercase, lowercase, number, special char");
        infoLabel2.setFont(new Font("Arial", Font.ITALIC, 9));
        infoLabel2.setForeground(new Color(100, 100, 100));
        infoLabel2.setBounds(40, 380, 500, 20);
        panel.add(infoLabel2);

        JLabel infoLabel3 = new JLabel("Example: MyPassword123!");
        infoLabel3.setFont(new Font("Arial", Font.ITALIC, 9));
        infoLabel3.setForeground(new Color(100, 100, 100));
        infoLabel3.setBounds(40, 400, 500, 20);
        panel.add(infoLabel3);

        add(panel);
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailValidationLabel.setText("");
            return;
        }
        
        if (Pattern.matches(EMAIL_REGEX, email)) {
            emailValidationLabel.setText("Valid email address");
            emailValidationLabel.setForeground(new Color(76, 175, 80));
        } else {
            emailValidationLabel.setText("Invalid email format (example: user@example.com)");
            emailValidationLabel.setForeground(Color.RED);
        }
    }

    private void updatePasswordStrength() {
        String password = password1Visible ? passwordVisibleField.getText() : new String(passwordField.getPassword());
        
        int strength = 0;
        String strengthText = "Weak";
        Color strengthColor = Color.RED;
        String strengthBar = "[_____]";
        
        if (password.length() == 0) {
            strengthText = "No password";
            strengthBar = "[_____]";
        } else if (password.length() < 6) {
            strength = 1;
            strengthText = "Very Weak";
            strengthBar = "[#____]";
        } else if (password.length() >= 6 && hasOnlyLettersAndNumbers(password)) {
            strength = 2;
            strengthText = "Weak";
            strengthBar = "[##___]";
        } else if (password.length() >= 8 && hasLettersNumbersAndSpecial(password)) {
            strength = 3;
            strengthText = "Fair";
            strengthBar = "[###__]";
            strengthColor = new Color(255, 152, 0);
        } else if (password.length() >= 10 && hasUpperLowerNumberSpecial(password)) {
            strength = 4;
            strengthText = "Strong";
            strengthBar = "[####_]";
            strengthColor = new Color(76, 175, 80);
        } else if (password.length() >= 12 && hasUpperLowerNumberSpecial(password)) {
            strength = 5;
            strengthText = "Very Strong";
            strengthBar = "[#####]";
            strengthColor = new Color(76, 175, 80);
        }
        
        passwordStrengthLabel.setText("Strength: " + strengthBar + " " + strengthText);
        passwordStrengthLabel.setForeground(strengthColor);
    }

    private boolean hasOnlyLettersAndNumbers(String password) {
        return password.matches("^[a-zA-Z0-9]+$");
    }

    private boolean hasLettersNumbersAndSpecial(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]+$") ||
               password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&#-_().+=]+$");
    }

    private boolean hasUpperLowerNumberSpecial(String password) {
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*?&\\-_().+=].*");
        return hasUpper && hasLower && hasNumber && hasSpecial;
    }

    private void togglePasswordVisibility(int field) {
        if (field == 1) {
            if (password1Visible) {
                passwordVisibleField.setVisible(false);
                passwordField.setVisible(true);
                eyeBtn1.setText("Show");
                password1Visible = false;
            } else {
                passwordVisibleField.setText(new String(passwordField.getPassword()));
                passwordField.setVisible(false);
                passwordVisibleField.setVisible(true);
                eyeBtn1.setText("Hide");
                password1Visible = true;
            }
        } else if (field == 2) {
            if (password2Visible) {
                confirmPasswordVisibleField.setVisible(false);
                confirmPasswordField.setVisible(true);
                eyeBtn2.setText("Show");
                password2Visible = false;
            } else {
                confirmPasswordVisibleField.setText(new String(confirmPasswordField.getPassword()));
                confirmPasswordField.setVisible(false);
                confirmPasswordVisibleField.setVisible(true);
                eyeBtn2.setText("Hide");
                password2Visible = true;
            }
        }
    }

    private void handleSignUp() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = password1Visible ? passwordVisibleField.getText() : new String(passwordField.getPassword());
        String confirmPassword = password2Visible ? confirmPasswordVisibleField.getText() : new String(confirmPasswordField.getPassword());

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() 
            || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (fullName.length() < 3) {
            messageLabel.setText("Full name must be at least 3 characters!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (username.length() < 4) {
            messageLabel.setText("Username must be at least 4 characters!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            messageLabel.setText("Please enter a valid email address (e.g., user@example.com)!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (password.length() < 8) {
            messageLabel.setText("Password must be at least 8 characters!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (!isStrongPassword(password)) {
            messageLabel.setText("Password must have: uppercase, lowercase, number & special char");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        // Check if username already exists
        if (UserDAO.userExists(username)) {
            messageLabel.setText("Username already taken! Choose another.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        // Check if email already exists
        if (UserDAO.emailExists(email)) {
            messageLabel.setText("Email already registered! Use a different email.");
            messageLabel.setForeground(Color.RED);
            return;
        }

        // Try to register user
        try {
            System.out.println("Attempting to register user: " + username);
            if (UserDAO.registerUser(username, email, password, fullName)) {
                messageLabel.setText("✓ Sign up successful! Redirecting to login...");
                messageLabel.setForeground(new Color(76, 175, 80));
                signUpBtn.setEnabled(false);
                System.out.println("User registered successfully!");
                
                // Wait a moment, then close and show login
                Timer timer = new Timer(1500, e -> SignUpFrame.this.dispose());
                timer.setRepeats(false);
                timer.start();
            } else {
                messageLabel.setText("Sign up failed! Check your connection.");
                messageLabel.setForeground(Color.RED);
                System.out.println("User registration returned false");
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setForeground(Color.RED);
            System.out.println("Sign up exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validate password strength: must have uppercase, lowercase, number, and special character
     */
    private boolean isStrongPassword(String password) {
        return hasUpperLowerNumberSpecial(password);
    }
}
