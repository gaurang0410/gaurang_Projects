package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import dao.UserDAO;
import model.User;

public class ModernLoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JButton signUpBtn;
    private JLabel messageLabel;
    private JCheckBox rememberMeCheck;
    private static final File USER_PREF_FILE = new File("user.pref");

    public ModernLoginFrame() {
        setTitle("VehicleFlow – Secure Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setResizable(false);
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_DARK);
        setContentPane(mainPanel);

        // Left – Brand panel
        JPanel brand = createBrandPanel();
        brand.setPreferredSize(new Dimension(460, 680));
        mainPanel.add(brand, BorderLayout.WEST);

        // Right – Form panel
        JPanel form = createFormPanel();
        mainPanel.add(form, BorderLayout.CENTER);
    }

    // ─── Brand Panel ──────────────────────────────────────────────────────────
    private JPanel createBrandPanel() {
        JPanel panel = UITheme.brandPanel(
                new Color(0, 90, 120),
                new Color(5, 20, 60)
        );
        panel.setLayout(new BorderLayout());

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(70, 55, 55, 55));

        // Logo emoji
        JLabel logo = new JLabel(UITheme.getIcon("vehicle", Color.WHITE, 80));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(logo);
        inner.add(Box.createVerticalStrut(28));

        // App name
        JLabel appName = new JLabel("VehicleFlow");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 48));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(appName);
        inner.add(Box.createVerticalStrut(10));

        // Tagline
        JLabel tagline = new JLabel("Premium Vehicle Service Management");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        tagline.setForeground(new Color(180, 220, 240));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(tagline);

        inner.add(Box.createVerticalGlue());

        // Bottom badge
        JLabel badge = new JLabel("© 2026 VehicleFlow Systems");
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        badge.setForeground(new Color(120, 170, 200));
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(badge);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    // ─── Form Panel ───────────────────────────────────────────────────────────
    private JPanel createFormPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(UITheme.BG_DARK);

        // Card container
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
            new EmptyBorder(38, 44, 38, 44)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        int y = 0;

        // Title row
        JPanel titleRow = new JPanel(new BorderLayout(12, 0));
        titleRow.setOpaque(false);
        JLabel icon = new JLabel(UITheme.getIcon("lock", UITheme.ACCENT_CYAN, 30));
        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setOpaque(false);
        JLabel title = new JLabel("Welcome Back");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);
        titleText.add(title);
        titleText.add(Box.createVerticalStrut(2));
        titleText.add(sub);
        titleRow.add(icon, BorderLayout.WEST);
        titleRow.add(titleText, BorderLayout.CENTER);
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 28, 0);
        card.add(titleRow, gbc);

        // Username label
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 5, 0);
        card.add(fieldLabel("Username or Email", UITheme.getIcon("user", UITheme.TEXT_SECONDARY, 15)), gbc);

        userField = UITheme.styledTextField("Enter your username");
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 16, 0); gbc.ipady = 4;
        card.add(userField, gbc);

        // Password label
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 5, 0); gbc.ipady = 0;
        card.add(fieldLabel("Password", UITheme.getIcon("lock", UITheme.TEXT_SECONDARY, 15)), gbc);

        passField = UITheme.styledPasswordField("Enter your password");
        JPanel passRow = new JPanel(new BorderLayout(8, 0));
        passRow.setOpaque(false);
        passRow.add(passField, BorderLayout.CENTER);
        passRow.add(buildPasswordToggle(passField), BorderLayout.EAST);
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 12, 0); gbc.ipady = 4;
        card.add(passRow, gbc);

        // Remember me
        rememberMeCheck = new JCheckBox("Remember me");
        rememberMeCheck.setOpaque(false);
        rememberMeCheck.setForeground(UITheme.TEXT_SECONDARY);
        rememberMeCheck.setFont(UITheme.FONT_SMALL);
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 6, 0); gbc.ipady = 0;
        card.add(rememberMeCheck, gbc);
        applyRememberedUser();

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(UITheme.FONT_SMALL);
        messageLabel.setForeground(UITheme.ACCENT_RED);
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 6, 0);
        card.add(messageLabel, gbc);

        // Login button (full width)
        loginBtn = UITheme.accentButton("Sign In Securely", UITheme.ACCENT_CYAN,
                UITheme.getIcon("login", Color.WHITE, 18));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        loginBtn.setPreferredSize(new Dimension(100, 48));
        loginBtn.addActionListener(e -> handleLogin());
        gbc.gridy = y++; gbc.insets = new Insets(10, 0, 14, 0); gbc.ipady = 6;
        card.add(loginBtn, gbc);

        // Divider
        JLabel divider = new JLabel("─────────── or ───────────");
        divider.setFont(UITheme.FONT_SMALL);
        divider.setForeground(UITheme.TEXT_MUTED);
        divider.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 14, 0); gbc.ipady = 0;
        card.add(divider, gbc);

        // Sign up button
        signUpBtn = UITheme.ghostButton("Create New Account", UITheme.ACCENT_PURPLE);
        signUpBtn.setPreferredSize(new Dimension(100, 46));
        signUpBtn.addActionListener(e -> { new ModernSignUpFrame(); dispose(); });
        gbc.gridy = y++; gbc.insets = new Insets(0, 0, 0, 0); gbc.ipady = 4;
        card.add(signUpBtn, gbc);

        // Footer note
        JLabel footer = new JLabel("VehicleFlow · Smart Vehicle Service System", SwingConstants.CENTER);
        footer.setFont(UITheme.FONT_TINY);
        footer.setForeground(UITheme.TEXT_MUTED);
        gbc.gridy = y++; gbc.insets = new Insets(22, 0, 0, 0); gbc.ipady = 0;
        card.add(footer, gbc);

        // Wrap card in outer with padding
        GridBagConstraints o = new GridBagConstraints();
        o.insets = new Insets(30, 40, 30, 40);
        o.fill = GridBagConstraints.BOTH;
        o.weightx = 1.0;
        o.weighty = 1.0;
        outer.add(card, o);

        return outer;
    }

    private JLabel fieldLabel(String text, Icon icon) {
        JLabel label = new JLabel(text, icon, SwingConstants.LEFT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(UITheme.TEXT_PRIMARY);
        label.setIconTextGap(8);
        return label;
    }

    private JButton buildPasswordToggle(JPasswordField field) {
        final char hiddenEcho = field.getEchoChar() == 0 ? '\u2022' : field.getEchoChar();
        JButton btn = new JButton("👁");
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        btn.setFocusable(false);
        btn.setOpaque(true);
        btn.setBackground(darkMode ? UITheme.BG_INPUT : UITheme.LIGHT_BG_INPUT);
        btn.setForeground(darkMode ? UITheme.TEXT_SECONDARY : UITheme.LIGHT_TEXT_SECONDARY);
        btn.setBorder(BorderFactory.createLineBorder(darkMode ? UITheme.BORDER_DEFAULT : UITheme.LIGHT_BORDER_DEFAULT));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(46, 42));
        btn.addActionListener(e -> {
            boolean showing = field.getEchoChar() == '\u0000';
            field.setEchoChar(showing ? hiddenEcho : '\u0000');
            btn.setText(showing ? "👁" : "🙈");
        });
        return btn;
    }

    private static boolean darkMode = UITheme.isDarkMode();

    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            showMessage("Please enter your username and password.", false);
            return;
        }
        User userObj = UserDAO.authenticateUserWithDetails(user, pass);
        if (userObj != null) {
            persistRememberedUser(user);
            showMessage("Success! Redirecting…", true);
            String role = userObj.getRole();
            this.dispose();
            if ("ADMIN".equalsIgnoreCase(role)) {
                new ModernDashboardFrame(userObj.getFullName(), userObj.getUserId());
            } else {
                if (userObj.getCustomerId() <= 0) {
                    UITheme.showAlert(null, "Profile Incomplete", "Customer profile is incomplete. Please contact admin support.", UITheme.AlertType.ERROR);
                    new ModernLoginFrame();
                    return;
                }
                new CustomerDashboardFrame(userObj.getFullName(), userObj.getUserId(), userObj.getCustomerId());
            }
        } else {
            showMessage("Invalid username or password. Try again.", false);
            passField.setText("");
        }
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.setForeground(success ? UITheme.ACCENT_GREEN : UITheme.ACCENT_RED);
    }

    private void applyRememberedUser() {
        if (!USER_PREF_FILE.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(USER_PREF_FILE))) {
            String u = r.readLine();
            if (u != null && !u.trim().isEmpty()) {
                userField.setText(u.trim());
                rememberMeCheck.setSelected(true);
            }
        } catch (Exception ignored) {}
    }

    private void persistRememberedUser(String user) {
        if (rememberMeCheck != null && rememberMeCheck.isSelected()) {
            try (BufferedWriter w = new BufferedWriter(new FileWriter(USER_PREF_FILE))) {
                w.write(user == null ? "" : user.trim());
            } catch (Exception ignored) {}
            return;
        }
        if (USER_PREF_FILE.exists()) USER_PREF_FILE.delete();
    }
}
