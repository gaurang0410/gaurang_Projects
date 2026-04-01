package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModernDashboardFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(6, 182, 212);
    private static final Color BACKGROUND_COLOR = new Color(15, 23, 42);
    private static final Color CARD_COLOR = new Color(30, 41, 59);
    private static final Color CARD_HOVER = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    public ModernDashboardFrame(String username, int userId) {
        setTitle("VehicleFlow - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents(username, userId);
        setVisible(true);
    }

    private void initComponents(String username, int userId) {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    getWidth(), getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header
        JPanel headerPanel = createHeaderPanel(username);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel(String username) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR,
                    getWidth(), 0, new Color(14, 165, 233)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 100));

        // Left section - Title
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("[VEHICLE] VehicleFlow Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);

        JLabel welcomeLabel = new JLabel("Welcome back, " + username + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(new Color(220, 240, 255));
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(welcomeLabel);

        panel.add(leftPanel, BorderLayout.WEST);

        // Right section - Buttons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JButton settingsBtn = createHeaderButton("[GEAR] Settings", new Color(14, 165, 233));
        settingsBtn.addActionListener(e -> new ModernSettingsFrame(username));
        rightPanel.add(settingsBtn);

        JButton logoutBtn = createHeaderButton("[EXIT] Logout", new Color(239, 68, 68));
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new ModernLoginFrame();
        });
        rightPanel.add(logoutBtn);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JButton createHeaderButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(getModel().isArmed() ? color.brighter() : color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 25, 25));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Customer Card
        panel.add(createModernCard(
            "[PEOPLE] Customers",
            "Manage customer profiles\nand contact information",
            new Color(33, 150, 243),
            e -> new CustomerForm()
        ));

        // Vehicle Card
        panel.add(createModernCard(
            "[AUTO] Vehicles",
            "Track vehicles and\nregistration details",
            new Color(76, 175, 80),
            e -> new VehicleForm()
        ));

        // Service Card
        panel.add(createModernCard(
            "[TOOLS] Services",
            "Schedule and manage\nvehicle services",
            new Color(255, 152, 0),
            e -> new ServiceForm()
        ));

        // Billing Card
        panel.add(createModernCard(
            "[MONEY] Billing",
            "Generate and manage\nsystem billing",
            new Color(156, 39, 176),
            e -> new BillingFrame()
        ));

        // Records Card
        panel.add(createModernCard(
            "[FILE] Records",
            "View and manage\nservice records",
            new Color(244, 67, 54),
            e -> new RecordsFrame()
        ));

        // Analytics Card
        panel.add(createModernCard(
            "[CHART] Analytics",
            "View performance metrics\nand statistics",
            new Color(63, 81, 181),
            e -> JOptionPane.showMessageDialog(this, "Analytics feature coming soon!", "Analytics", JOptionPane.INFORMATION_MESSAGE)
        ));

        return panel;
    }

    private JPanel createModernCard(String title, String description, Color color, ActionListener action) {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                // Border with color
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBounds(15, 15, 250, 35);
        card.add(titleLabel);

        // Description
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setBounds(15, 55, 250, 60);
        card.add(descLabel);

        // Action Button
        JButton actionBtn = new JButton("Open") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isArmed() ? color.brighter() : color);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setBorderPainted(false);
        actionBtn.setContentAreaFilled(false);
        actionBtn.setFocusPainted(false);
        actionBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionBtn.setBounds(15, 130, 250, 35);
        actionBtn.addActionListener(action);
        card.add(actionBtn);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(CARD_HOVER);
                card.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(CARD_COLOR);
                card.repaint();
            }
        });

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout());

        JLabel footerLabel = new JLabel("VehicleFlow © 2024 | Modern Vehicle Service Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footerLabel.setForeground(TEXT_SECONDARY);
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(footerLabel, BorderLayout.CENTER);

        return panel;
    }
}
