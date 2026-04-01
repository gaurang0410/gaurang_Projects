package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import dao.UserDAO;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JTextField passVisibleField;
    private JButton loginBtn;
    private JButton signUpBtn;
    private JButton exitBtn;
    private JButton eyeBtn;
    private JLabel messageLabel;
    private boolean passwordVisible = false;

    public LoginFrame() {
        setTitle("Vehicle Service Management - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 380);
        setResizable(false);

        initComponents();
        
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        setAlwaysOnTop(false);
        requestFocus();
        
        System.out.println("LoginFrame initialized and made visible");
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 245, 250));

        // Title
        JLabel titleLabel = new JLabel("Vehicle Service Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(30, 100, 180));
        titleLabel.setBounds(90, 20, 300, 30);
        panel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Enter your credentials to continue");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setBounds(100, 50, 300, 15);
        panel.add(subtitleLabel);

        // Username/Email Label
        JLabel userLabel = new JLabel("Username or Email:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userLabel.setBounds(50, 80, 130, 25);
        panel.add(userLabel);

        // Username Field
        userField = new JTextField();
        userField.setBounds(50, 105, 400, 28);
        userField.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(userField);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        passLabel.setBounds(50, 145, 100, 25);
        panel.add(passLabel);

        // Password Field (hidden)
        passField = new JPasswordField();
        passField.setBounds(50, 170, 350, 28);
        passField.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(passField);

        // Password Visible Field (shown when toggle is on)
        passVisibleField = new JTextField();
        passVisibleField.setBounds(50, 170, 350, 28);
        passVisibleField.setFont(new Font("Arial", Font.PLAIN, 12));
        passVisibleField.setVisible(false);
        panel.add(passVisibleField);

        // Eye Button to toggle password visibility
        eyeBtn = new JButton("Show");
        eyeBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        eyeBtn.setBounds(410, 170, 50, 28);
        eyeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });
        panel.add(eyeBtn);

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        messageLabel.setForeground(Color.RED);
        messageLabel.setBounds(50, 210, 400, 20);
        panel.add(messageLabel);

        // Login Button
        loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 12));
        loginBtn.setBackground(new Color(25, 118, 210));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBounds(50, 245, 100, 35);
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        panel.add(loginBtn);

        // Sign Up Button
        signUpBtn = new JButton("Create Account");
        signUpBtn.setFont(new Font("Arial", Font.BOLD, 12));
        signUpBtn.setBackground(new Color(76, 175, 80));
        signUpBtn.setForeground(Color.WHITE);
        signUpBtn.setBounds(170, 245, 130, 35);
        signUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignUp();
            }
        });
        panel.add(signUpBtn);

        // Exit Button
        exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        exitBtn.setBounds(320, 245, 130, 35);
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(exitBtn);

        // Info Labels
        JLabel infoLabel1 = new JLabel("Test Account: Username = admin, Password = admin");
        infoLabel1.setFont(new Font("Arial", Font.ITALIC, 9));
        infoLabel1.setForeground(new Color(120, 120, 120));
        infoLabel1.setBounds(50, 290, 400, 15);
        panel.add(infoLabel1);

        JLabel infoLabel2 = new JLabel("New user? Click 'Create Account' to sign up with your real email");
        infoLabel2.setFont(new Font("Arial", Font.ITALIC, 9));
        infoLabel2.setForeground(new Color(120, 120, 120));
        infoLabel2.setBounds(50, 310, 400, 15);
        panel.add(infoLabel2);

        add(panel);
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            // Hide password
            passVisibleField.setVisible(false);
            passField.setVisible(true);
            eyeBtn.setText("Show");
            passwordVisible = false;
        } else {
            // Show password
            passVisibleField.setText(new String(passField.getPassword()));
            passField.setVisible(false);
            passVisibleField.setVisible(true);
            eyeBtn.setText("Hide");
            passwordVisible = true;
        }
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = passwordVisible ? passVisibleField.getText() : new String(passField.getPassword());

        // Validation
        if (username.isEmpty()) {
            messageLabel.setText("Please enter your username or email!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        if (password.isEmpty()) {
            messageLabel.setText("Please enter your password!");
            messageLabel.setForeground(Color.RED);
            return;
        }

        // Test admin account
        if ((username.equals("admin") && password.equals("admin"))) {
            messageLabel.setText("Login Successful! Opening dashboard...");
            messageLabel.setForeground(new Color(76, 175, 80));
            new DashboardFrame("admin", 0);
            this.dispose();
            return;
        }

        // Check database using API
        try {
            messageLabel.setText("Verifying credentials...");
            messageLabel.setForeground(new Color(100, 100, 100));
            
            org.json.JSONObject result = UserDAO.authenticateUserWithDetails(username, password);
            if (result != null && result.optBoolean("success", false)) {
                messageLabel.setText("Login Successful! Opening dashboard...");
                messageLabel.setForeground(new Color(76, 175, 80));
                int userId = result.optInt("user_id", -1);
                String fullName = result.optString("full_name", username);
                
                // Give time for the user to see the success message
                Timer timer = new Timer(1000, e -> {
                    new DashboardFrame(fullName, userId);
                    LoginFrame.this.dispose();
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                messageLabel.setText("Invalid username/email or password!");
                messageLabel.setForeground(Color.RED);
                userField.setText("");
                passField.setText("");
                passVisibleField.setText("");
                userField.requestFocus();
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            messageLabel.setText("Connection error. Check if API server is running.");
            messageLabel.setForeground(Color.RED);
        }
    }

    private void handleSignUp() {
        new SignUpFrame();
    }
}
