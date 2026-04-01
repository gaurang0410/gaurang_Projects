package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(20, 150, 230);      // Modern blue
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 245);  // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33);           // Dark gray

    public DashboardFrame(String username, int userId) {
        this.setTitle("Vehicle Service Management System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1200, 700);
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        // Main Panel with modern styling
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel(username);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Button Panel (Grid Layout)
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
        this.setVisible(true);
    }

    private JPanel createHeaderPanel(String username) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout(15, 0));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title
        JLabel titleLabel = new JLabel("[VEHICLE] Service Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + username + " !");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(220, 240, 255));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PRIMARY_COLOR);
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(welcomeLabel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        // Logout Button
        JButton logoutBtn = createButton("Logout", new Color(220, 50, 50));
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame();
        });
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 3, 20, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        // Card 1: Customers
        JPanel customerCard = createCard(
            "[PEOPLE] Customers",
            "Manage customer details and profiles",
            new Color(33, 150, 243),
            e -> new CustomerForm()
        );
        buttonPanel.add(customerCard);

        // Card 2: Vehicles
        JPanel vehicleCard = createCard(
            "[AUTO] Vehicles",
            "Track vehicle information and registration",
            new Color(76, 175, 80),
            e -> new VehicleForm()
        );
        buttonPanel.add(vehicleCard);

        // Card 3: Services
        JPanel serviceCard = createCard(
            "[TOOLS] Services",
            "Schedule and manage vehicle services",
            new Color(255, 152, 0),
            e -> new ServiceForm()
        );
        buttonPanel.add(serviceCard);

        // Card 4: Billing
        JPanel billingCard = createCard(
            "[MONEY] Billing",
            "Generate and manage service bills",
            new Color(156, 39, 176),
            e -> new BillingFrame()
        );
        buttonPanel.add(billingCard);

        // Card 5: Records
        JPanel recordsCard = createCard(
            "[LIST] Records",
            "View all service records and history",
            new Color(244, 67, 54),
            e -> new RecordsFrame()
        );
        buttonPanel.add(recordsCard);

        // Card 6: Settings
        JPanel settingsCard = createCard(
            "[GEAR] Settings",
            "Application settings and preferences",
            new Color(63, 81, 181),
            e -> JOptionPane.showMessageDialog(null, "Settings coming soon!")
        );
        buttonPanel.add(settingsCard);

        return buttonPanel;
    }

    private JPanel createCard(String title, String description, Color accentColor, java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 3),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Top accent bar
        JPanel topBar = new JPanel();
        topBar.setBackground(accentColor);
        topBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        card.add(topBar);
        card.add(Box.createVerticalStrut(15));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        card.add(titleLabel);

        // Description
        JLabel descLabel = new JLabel("<html><p>" + description + "</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.add(descLabel);

        card.add(Box.createVerticalStrut(15));

        // Button
        JButton actionBtn = createButton("Open", accentColor);
        actionBtn.addActionListener(action);
        card.add(Box.createVerticalGlue());
        card.add(actionBtn);

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(220, 220, 220));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(180, 180, 180)),
            new EmptyBorder(10, 0, 10, 0)
        ));

        JLabel footerLabel = new JLabel("🔒 Secure Vehicle Service Management System | © 2026");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(brightenColor(bgColor, 30));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private Color brightenColor(Color color, int amount) {
        return new Color(
            Math.min(color.getRed() + amount, 255),
            Math.min(color.getGreen() + amount, 255),
            Math.min(color.getBlue() + amount, 255)
        );
    }
}
