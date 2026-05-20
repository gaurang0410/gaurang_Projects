package gui;

import dao.ServiceCatalogDAO;
import dao.FeedbackDAO;
import dao.NotificationDAO;
import model.Service;
import model.ServiceCatalogItem;
import service.ServiceManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomerDashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final int customerId;
    private final int userId;
    private final ServiceManager serviceManager = new ServiceManager();
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private JPanel contentCards;
    private CardLayout cardLayout;
    private JTable trackingTable;
    private DefaultTableModel trackingModel;
    private final ServiceTrackingPanel trackingSteps = new ServiceTrackingPanel();
    private final RouteMapPanel trackingMap = new RouteMapPanel();
    private SessionTimeoutManager sessionTimeoutManager;
    private JButton notificationButton;
    private CustomerVehicleRegistrationPanel vehicleRegPanel;

    public CustomerDashboardFrame(String username, int userId, int customerId) {
        this.userId = userId;
        this.customerId = customerId;
        setTitle("VehicleFlow – Customer Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setLocationRelativeTo(null);
        initComponents(username);
        sessionTimeoutManager = new SessionTimeoutManager(this, this::performAutoLogout);
        sessionTimeoutManager.start();
        updateNotificationBadge();
        setVisible(true);
    }

    // ─── Root Layout ─────────────────────────────────────────────────────────
    private void initComponents(String username) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        root.add(createSidebar(username), BorderLayout.WEST);

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setBackground(UITheme.BG_DARK);
        body.setBorder(new EmptyBorder(14, 16, 16, 16));
        body.add(createHeader(username), BorderLayout.NORTH);

        cardLayout   = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setOpaque(false);
        contentCards.add(createDashboardPage(),      "dashboard");
        contentCards.add(createServiceRequestPage(), "request");
        contentCards.add(createServiceHistoryPage(), "history");
        contentCards.add(createTrackingPage(),       "tracking");
        contentCards.add(createVehiclePage(),        "vehicles");
        body.add(contentCards, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);
        activateNav("dashboard");
    }

    // ─── Sidebar ─────────────────────────────────────────────────────────────
    private JPanel createSidebar(String username) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(10, 16, 30));
        sidebar.setPreferredSize(new Dimension(224, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Brand header
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBackground(new Color(8, 13, 26));
        brand.setBorder(new EmptyBorder(22, 18, 18, 18));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel appName = new JLabel("VehicleFlow");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 19));
        appName.setForeground(Color.WHITE);
        JLabel appSub = new JLabel("Customer Service Hub");
        appSub.setFont(UITheme.FONT_TINY);
        appSub.setForeground(new Color(80, 110, 150));
        brand.add(appName);
        brand.add(Box.createVerticalStrut(2));
        brand.add(appSub);
        sidebar.add(brand);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(28, 42, 68));
        sep.setBackground(new Color(28, 42, 68));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        // Nav label
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(new Color(60, 85, 120));
        navLabel.setBorder(new EmptyBorder(0, 18, 6, 0));
        sidebar.add(navLabel);

        // Nav items
        String[][] items = {
            {"dashboard", "Dashboard"},
            {"request",   "Book a Service"},
            {"vehicles",  "My Vehicles"},
            {"history",   "Service History"},
            {"tracking",  "Live Tracking"}
        };
        Color[] navColors = {
            UITheme.ACCENT_CYAN, UITheme.ACCENT_GREEN,
            UITheme.ACCENT_PURPLE, UITheme.ACCENT_AMBER, UITheme.ACCENT_BLUE
        };
        for (int i = 0; i < items.length; i++) {
            String[] item = items[i];
            JButton btn = UITheme.sidebarNavButton(item[1], navColors[i]);
            btn.setBorder(new EmptyBorder(0, 12, 0, 12));
            btn.addActionListener(e -> activateNav(item[0]));
            navButtons.put(item[0], btn);
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());

        // Bottom separator + user row
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(28, 42, 68));
        sep2.setBackground(new Color(28, 42, 68));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep2);

        JPanel userRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        userRow.setBackground(new Color(8, 13, 26));
        userRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JPanel userText = new JPanel();
        userText.setLayout(new BoxLayout(userText, BoxLayout.Y_AXIS));
        userText.setOpaque(false);
        JLabel uname = new JLabel(username);
        uname.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uname.setForeground(UITheme.TEXT_PRIMARY);
        JLabel urole = new JLabel("Customer");
        urole.setFont(UITheme.FONT_TINY);
        urole.setForeground(UITheme.ACCENT_GREEN);
        userText.add(uname);
        userText.add(urole);
        userRow.add(avatar);
        userRow.add(userText);
        sidebar.add(userRow);

        return sidebar;
    }

    // ─── Header ──────────────────────────────────────────────────────────────
    private JPanel createHeader(String username) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.ACCENT_GREEN);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
            new EmptyBorder(13, 20, 13, 16)
        ));

        JLabel title = new JLabel("Customer Workspace");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        notificationButton = iconBtn("notification", "Notifications");
        notificationButton.addActionListener(e -> {
            new NotificationsPanel(userId, "CUSTOMER");
            updateNotificationBadge();
        });
        JButton settings = iconBtn("settings",  "Settings");
        settings.addActionListener(e -> new ModernSettingsFrame(userId, "CUSTOMER"));
        JButton logout = iconBtn("logout", "Logout");
        logout.addActionListener(e -> confirmAndLogout());

        actions.add(notificationButton);
        actions.add(settings);
        actions.add(logout);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JButton iconBtn(String iconName, String tip) {
        JButton b = new JButton();
        Icon ic = UITheme.getIcon(iconName, UITheme.ACCENT_GREEN, 18);
        b.setIcon(ic);
        b.setToolTipText(tip);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(36, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ─── Dashboard Page ───────────────────────────────────────────────────────
    private JPanel createDashboardPage() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);
        panel.add(createSummaryCards(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 3, 14, 0));
        center.setOpaque(false);
        center.add(actionCard("Book a Service",
            "Create a new service request with pickup and drop-off details.",
            UITheme.ACCENT_GREEN, () -> activateNav("request")));
        center.add(actionCard("Track My Service",
            "View live service movement status from pickup to delivery.",
            UITheme.ACCENT_CYAN, () -> activateNav("tracking")));
        center.add(actionCard("Service History",
            "View all past service records and invoices.",
            UITheme.ACCENT_AMBER, () -> activateNav("history")));

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 14));
        centerWrapper.setOpaque(false);
        centerWrapper.add(center, BorderLayout.NORTH);
        centerWrapper.add(createReminderAndCalendar(), BorderLayout.CENTER);
        panel.add(centerWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReminderAndCalendar() {
        JPanel wrapper = new JPanel(new GridLayout(1, 2, 12, 0));
        wrapper.setOpaque(false);
        String reminder = serviceManager.getServiceReminderForCustomer(customerId);
        int severity = serviceManager.getReminderSeverityForCustomer(customerId);
        Color color = severity == 2 ? UITheme.ACCENT_RED : (severity == 1 ? UITheme.ACCENT_AMBER : UITheme.ACCENT_GREEN);
        JPanel reminderCard = UITheme.accentCard(color);
        reminderCard.setLayout(new BorderLayout());
        reminderCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel(severity == 2 ? "Service Overdue" : (severity == 1 ? "Service Reminder" : "Service Status"));
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);
        JLabel body = new JLabel("<html>" + reminder + "</html>");
        body.setFont(UITheme.FONT_BODY);
        body.setForeground(UITheme.TEXT_SECONDARY);
        reminderCard.add(title, BorderLayout.NORTH);
        reminderCard.add(body, BorderLayout.CENTER);
        wrapper.add(reminderCard);

        AppointmentCalendarPanel calendar = new AppointmentCalendarPanel();
        calendar.setAppointments(serviceManager.getServicesByCustomerId(customerId));
        wrapper.add(calendar);
        return wrapper;
    }

    private JPanel createSummaryCards() {
        List<Service> services = serviceManager.getServicesByCustomerId(customerId);
        long pending    = services.stream().filter(s -> "Pending".equalsIgnoreCase(s.getStatus()) || "Pending Request".equalsIgnoreCase(s.getStatus())).count();
        long inProgress = services.stream().filter(s -> {
            String st = s.getStatus() == null ? "" : s.getStatus().toLowerCase();
            return st.contains("progress") || st.contains("inspection") || st.contains("pickup") || st.contains("quality");
        }).count();
        long waiting    = services.stream().filter(s -> {
            String st = s.getStatus() == null ? "" : s.getStatus().toLowerCase();
            return st.contains("waiting") || st.contains("parts");
        }).count();
        long completed  = services.stream().filter(s -> "Completed".equalsIgnoreCase(s.getStatus())).count();

        JPanel grid = new JPanel(new GridLayout(1, 5, 10, 0));
        grid.setOpaque(false);
        grid.add(createClickableMetric("Total", String.valueOf(services.size()), UITheme.ACCENT_CYAN, "history"));
        grid.add(createClickableMetric("Pending",        String.valueOf(pending),    UITheme.ACCENT_AMBER, "tracking"));
        grid.add(createClickableMetric("In Service",     String.valueOf(inProgress), UITheme.ACCENT_BLUE, "tracking"));
        grid.add(createClickableMetric("Waiting Parts",  String.valueOf(waiting),    new Color(249, 115, 22), "tracking"));
        grid.add(createClickableMetric("Completed",      String.valueOf(completed),  UITheme.ACCENT_GREEN, "history"));

        // If any service is waiting for parts, show a notification banner
        if (waiting > 0) {
            JPanel wrapper = new JPanel(new BorderLayout(0, 8));
            wrapper.setOpaque(false);
            JPanel banner = new JPanel(new BorderLayout(8, 0));
            banner.setBackground(new Color(60, 35, 10));
            banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(249, 115, 22, 80)),
                new EmptyBorder(8, 14, 8, 14)
            ));
            JLabel warnIcon = new JLabel(UITheme.getIcon("notification", new Color(249, 115, 22), 16));
            JLabel warnText = new JLabel("Your vehicle service is delayed due to parts availability. We'll update you when parts arrive.");
            warnText.setFont(UITheme.FONT_SMALL);
            warnText.setForeground(new Color(255, 180, 80));
            banner.add(warnIcon, BorderLayout.WEST);
            banner.add(warnText, BorderLayout.CENTER);
            wrapper.add(grid, BorderLayout.NORTH);
            wrapper.add(banner, BorderLayout.SOUTH);
            return wrapper;
        }
        return grid;
    }

    private JPanel createClickableMetric(String title, String value, Color accent, String navKey) {
        JPanel card = UITheme.metricCard(title, value, accent);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { activateNav(navKey); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(UITheme.BG_CARD_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { card.setBackground(UITheme.BG_CARD); }
        });
        return card;
    }

    private JPanel actionCard(String title, String message, Color accent, Runnable action) {
        JPanel card = UITheme.accentCard(accent);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(20, 20, 18, 20));

        JLabel heading = new JLabel(title);
        heading.setFont(UITheme.FONT_HEADING);
        heading.setForeground(UITheme.TEXT_PRIMARY);

        JLabel body = new JLabel("<html>" + message + "</html>");
        body.setFont(UITheme.FONT_SMALL);
        body.setForeground(UITheme.TEXT_SECONDARY);

        JButton btn = UITheme.accentButton("Open", accent);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.addActionListener(e -> action.run());

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(body,    BorderLayout.CENTER);

        card.add(top, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    // ─── Service Request Page ─────────────────────────────────────────────────
    private JPanel createServiceRequestPage() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        // Top booking card
        JPanel card = UITheme.accentCard(UITheme.ACCENT_GREEN);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Book a New Service");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hint = new JLabel("Create a service request, specify pickup address and choose your service type.");
        hint.setFont(UITheme.FONT_BODY);
        hint.setForeground(UITheme.TEXT_SECONDARY);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel vehicleHint = new JLabel("⚠  No vehicles registered? Use \"My Vehicles\" to register one first.");
        vehicleHint.setFont(UITheme.FONT_SMALL);
        vehicleHint.setForeground(UITheme.ACCENT_AMBER);
        vehicleHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton openBooking = UITheme.accentButton("Open Booking Screen", UITheme.ACCENT_GREEN);
        openBooking.setAlignmentX(Component.LEFT_ALIGNMENT);
        openBooking.addActionListener(e -> new BookServiceFrame(customerId));

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(hint);
        card.add(Box.createVerticalStrut(6));
        card.add(vehicleHint);
        card.add(Box.createVerticalStrut(16));
        card.add(openBooking);
        panel.add(card, BorderLayout.NORTH);

        // Catalog table
        panel.add(createCatalogPreview(), BorderLayout.CENTER);
        return panel;
    }

    // ─── My Vehicles Page ─────────────────────────────────────────────────────
    private JPanel createVehiclePage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        vehicleRegPanel = new CustomerVehicleRegistrationPanel(customerId);
        page.add(vehicleRegPanel, BorderLayout.CENTER);
        return page;
    }

    private JPanel createCatalogPreview() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel title = new JLabel("Available Services");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Service Name", "Category", "Base Cost", "Est. Time"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        ServiceCatalogDAO dao = new ServiceCatalogDAO();
        for (ServiceCatalogItem s : dao.getAllServices()) {
            model.addRow(new Object[]{
                s.getServiceName(), s.getCategory(),
                UITheme.formatCurrency(s.getBaseCost()), s.getEstimatedTime()
            });
        }
        panel.add(UITheme.styledScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ─── Service History Page ─────────────────────────────────────────────────
    private JPanel createServiceHistoryPage() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        RecordsFrame recordsFrame = new RecordsFrame(customerId, customerId, true, false);
        panel.add(recordsFrame.getContentPane(), BorderLayout.CENTER);
        return panel;
    }

    // ─── Tracking Page ────────────────────────────────────────────────────────
    private JPanel createTrackingPage() {
        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Header for tracking
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        
        JLabel title = new JLabel("Service Lifecycle Tracker", UITheme.getIcon("vehicle", UITheme.ACCENT_CYAN, 24), JLabel.LEFT);
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setIconTextGap(12);
        topRow.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        JButton expandMapBtn = UITheme.accentButton("Expand Map", UITheme.ACCENT_CYAN);
        expandMapBtn.setPreferredSize(new Dimension(140, 38));
        expandMapBtn.addActionListener(e -> UITheme.showFullscreenMap(trackingMap));
        
        JButton feedbackBtn = UITheme.accentButton("Submit Feedback", UITheme.ACCENT_PURPLE);
        feedbackBtn.setPreferredSize(new Dimension(160, 38));
        feedbackBtn.addActionListener(e -> submitFeedback());
        
        actions.add(expandMapBtn);
        actions.add(feedbackBtn);
        topRow.add(actions, BorderLayout.EAST);
        mainContent.add(topRow, BorderLayout.NORTH);

        // Center Split
        JPanel splitPanel = new JPanel(new BorderLayout(20, 0));
        splitPanel.setOpaque(false);

        // Left: Data Table
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        
        String[] cols = {"ID", "Service Type", "Date", "Status", "Mechanic", "ETA"};
        trackingModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        trackingTable = new JTable(trackingModel);
        UITheme.styleTable(trackingTable);
        loadTrackingData();
        trackingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) applySelectedTrackingStatus();
        });
        leftPanel.add(UITheme.styledScrollPane(trackingTable), BorderLayout.CENTER);
        splitPanel.add(leftPanel, BorderLayout.CENTER);

        // Right: Visuals (Steps + Map)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(440, 0));

        // Progress Steps
        JPanel stepsCard = UITheme.accentCard(UITheme.ACCENT_AMBER);
        stepsCard.setLayout(new BorderLayout());
        stepsCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        stepsCard.add(trackingSteps, BorderLayout.CENTER);
        rightPanel.add(stepsCard);
        rightPanel.add(Box.createVerticalStrut(20));

        // Map Section
        JPanel mapCard = UITheme.accentCard(UITheme.ACCENT_CYAN);
        mapCard.setLayout(new BorderLayout(0, 10));
        mapCard.setBorder(new EmptyBorder(15, 15, 15, 15));
        mapCard.setMinimumSize(new Dimension(0, 380));
        mapCard.setPreferredSize(new Dimension(0, 420));

        JComboBox<String> pickup = UITheme.styledComboBox(trackingMap.getLocationNames());
        JComboBox<String> drop   = UITheme.styledComboBox(trackingMap.getLocationNames());
        pickup.setSelectedItem("City Center");
        drop.setSelectedItem("Service Center");

        JPanel mapControls = new JPanel(new GridLayout(1, 2, 10, 0));
        mapControls.setOpaque(false);
        mapControls.add(pickup);
        mapControls.add(drop);
        mapCard.add(mapControls, BorderLayout.NORTH);
        
        // Ensure map panel doesn't squash
        trackingMap.setPreferredSize(new Dimension(400, 300));
        mapCard.add(trackingMap, BorderLayout.CENTER);

        pickup.addActionListener(e -> trackingMap.setRoute((String) pickup.getSelectedItem(), (String) drop.getSelectedItem()));
        drop.addActionListener(e -> trackingMap.setRoute((String) pickup.getSelectedItem(), (String) drop.getSelectedItem()));
        trackingMap.setRoute((String) pickup.getSelectedItem(), (String) drop.getSelectedItem());
        
        rightPanel.add(mapCard);
        splitPanel.add(rightPanel, BorderLayout.EAST);
        
        mainContent.add(splitPanel, BorderLayout.CENTER);

        // Final Wrap in ScrollPane
        JScrollPane scrollPane = UITheme.styledScrollPane(mainContent);
        scrollPane.setBorder(null);
        
        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(scrollPane, BorderLayout.CENTER);

        if (trackingTable.getRowCount() > 0) {
            trackingTable.setRowSelectionInterval(0, 0);
            applySelectedTrackingStatus();
        }
        return outer;
    }

    private void loadTrackingData() {
        trackingModel.setRowCount(0);
        for (Service s : serviceManager.getServicesByCustomerId(customerId)) {
            String mechName = (s.getMechanicName() == null || s.getMechanicName().isEmpty()) ? "Unassigned" : s.getMechanicName();
            String estTime = (s.getEstimatedTime() == null || s.getEstimatedTime().isEmpty()) ? "N/A" : s.getEstimatedTime();
            trackingModel.addRow(new Object[]{
                s.getServiceId(), s.getServiceType(), s.getServiceDate(), s.getStatus(), mechName, estTime
            });
        }
    }

    private void applySelectedTrackingStatus() {
        int row = trackingTable.getSelectedRow();
        if (row < 0) { trackingSteps.updateStage(0); return; }
        String status = String.valueOf(
            trackingModel.getValueAt(trackingTable.convertRowIndexToModel(row), 3));
        trackingSteps.updateByStatus(status);
    }

    // ─── Nav Activation ───────────────────────────────────────────────────────
    private void activateNav(String key) {
        cardLayout.show(contentCards, key);
        if ("tracking".equals(key)) {
            loadTrackingData();
            if (trackingTable.getRowCount() > 0) {
                trackingTable.setRowSelectionInterval(0, 0);
                applySelectedTrackingStatus();
            }
        }
        // When switching to the vehicles page, refresh the table
        if ("vehicles".equals(key) && vehicleRegPanel != null) {
            SwingUtilities.invokeLater(() -> vehicleRegPanel.setOnVehicleRegisteredCallback(() -> {
                // nothing extra needed — panel refreshes itself; BookServiceFrame reloads on open
            }));
        }
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(key);
            entry.getValue().putClientProperty("active", active);
            entry.getValue().repaint();
        }
    }

    private void submitFeedback() {
        int row = trackingTable == null ? -1 : trackingTable.getSelectedRow();
        if (row < 0) {
            UITheme.showInfoDialog(this, "No Selection", "Select a completed service row first.");
            return;
        }
        int modelRow = trackingTable.convertRowIndexToModel(row);
        int serviceId = Integer.parseInt(String.valueOf(trackingModel.getValueAt(modelRow, 0)));
        Service service = serviceManager.getServiceById(serviceId);
        if (service == null || !"Completed".equalsIgnoreCase(service.getStatus())) {
            UITheme.showInfoDialog(this, "Action Restricted", "Feedback can be submitted only for completed services.");
            return;
        }

        JComboBox<String> ratingCombo = UITheme.styledComboBox(new String[]{"1", "2", "3", "4", "5"});
        JTextArea comment = new JTextArea(4, 20);
        comment.setLineWrap(true);
        comment.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(ratingCombo, BorderLayout.NORTH);
        panel.add(new JScrollPane(comment), BorderLayout.CENTER);
        int choice = JOptionPane.showConfirmDialog(this, panel, "Rate Service", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;
        int rating = Integer.parseInt(String.valueOf(ratingCombo.getSelectedItem()));
        boolean ok = new FeedbackDAO().addFeedback(serviceId, customerId, service.getMechanicId(), rating, comment.getText().trim());
        if (ok) {
            UITheme.showSuccessDialog(this, "Success", "Feedback submitted. Thank you!");
        } else {
            UITheme.showErrorDialog(this, "Error", "Failed to submit feedback.");
        }
    }

    private void confirmAndLogout() {
        if (UITheme.showConfirmDialog(this, "Confirm Logout", "Are you sure you want to logout?")) {
            if (sessionTimeoutManager != null) sessionTimeoutManager.stop();
            dispose();
            new ModernLoginFrame();
        }
    }

    private void performAutoLogout() {
        UITheme.showInfoDialog(this, "Session Timeout", "Session timed out due to inactivity.");
        dispose();
        new ModernLoginFrame();
    }

    private void updateNotificationBadge() {
        if (notificationButton == null) return;
        NotificationDAO.NotificationResponse response = new NotificationDAO().getNotifications(userId);
        int unread = response.unreadCount;
        notificationButton.setText(unread > 0 ? "🔔(" + unread + ")" : "🔔");
    }

    @Override
    public void dispose() {
        if (sessionTimeoutManager != null) sessionTimeoutManager.stop();
        super.dispose();
    }
}
