package gui;

import dao.DashboardDAO;
import dao.InventoryDAO;
import dao.MechanicDAO;
import dao.NotificationDAO;
import model.Customer;
import model.Mechanic;
import model.Service;
import model.Vehicle;
import service.CustomerService;
import service.ServiceManager;
import service.VehicleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModernDashboardFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final ServiceManager serviceManager = new ServiceManager();
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final VehicleService vehicleService = new VehicleService();
    private final CustomerService customerService = new CustomerService();
    private final int userId;

    private JTable serviceTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> serviceSorter;
    private JTextField searchField;
    private JPanel contentCards;
    private CardLayout cardLayout;
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private JButton notificationButton;
    private SessionTimeoutManager sessionTimeoutManager;
    private JPanel activityPanelContainer;
    private JPanel metricsPanelContainer;
    private JPanel chartsPanelContainer;

    public ModernDashboardFrame(String username, int userId) {
        this.userId = userId;
        setTitle("VehicleFlow - Admin Command Center");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 860);
        setLocationRelativeTo(null);

        initComponents(username);
        refreshData();
        updateNotificationBadge();
        sessionTimeoutManager = new SessionTimeoutManager(this, this::performAutoLogout);
        sessionTimeoutManager.start();
        setVisible(true);
    }

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

        // Standard Pages
        contentCards.add(createDashboardPage(),      "dashboard");
        contentCards.add(createServiceRequestsPage(), "requests");
        
        // Integrated Modules (Direct Access)
        contentCards.add(new MechanicManagementPanel(),   "mechanics");
        contentCards.add(new VehicleManagementPanel(),    "vehicles");
        contentCards.add(new CustomerManagementPanel(),   "customers");
        contentCards.add(new AdminServiceRegistrationPanel(), "register_service");
        contentCards.add(new InventoryManagementPanel(),  "inventory");
        contentCards.add(new ServiceCatalogManagementPanel(), "catalog");
        contentCards.add(new RecordsFrame(-1, -1, false).getContentPane(), "reports");
        contentCards.add(new BillingFrame().getContentPane(), "billing");

        body.add(contentCards, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        activateNav("dashboard");
    }

    private JPanel createSidebar(String username) {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(10, 16, 30));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Brand header
        JPanel brand = UITheme.brandPanel(new Color(15, 23, 42), new Color(30, 41, 59));
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(24, 20, 24, 20));
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JLabel appName = new JLabel("VehicleFlow");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 22));
        appName.setForeground(Color.WHITE);
        JLabel appSub = new JLabel("Enterprise Management");
        appSub.setFont(UITheme.FONT_TINY);
        appSub.setForeground(UITheme.ACCENT_CYAN);
        brand.add(appName);
        brand.add(Box.createVerticalStrut(4));
        brand.add(appSub);
        sidebar.add(brand);

        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(sidebarLabel("MAIN NAVIGATION"));

        addSidebarButton(sidebar, "dashboard", "Dashboard", UITheme.ACCENT_CYAN);
        addSidebarButton(sidebar, "requests", "Service Requests", UITheme.ACCENT_AMBER);
        addSidebarButton(sidebar, "register_service", "Register Service", UITheme.ACCENT_GREEN);
        
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(sidebarLabel("OPERATIONS"));
        addSidebarButton(sidebar, "mechanics", "Mechanics", UITheme.ACCENT_PURPLE);
        addSidebarButton(sidebar, "vehicles", "Vehicles / Brands", UITheme.ACCENT_BLUE);
        addSidebarButton(sidebar, "customers", "Customers", UITheme.ACCENT_GREEN);
        addSidebarButton(sidebar, "inventory", "Inventory", UITheme.ACCENT_CYAN);
        
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(sidebarLabel("FINANCE & AUDIT"));
        addSidebarButton(sidebar, "billing", "Billing / Invoices", UITheme.ACCENT_GREEN);
        addSidebarButton(sidebar, "catalog", "Service Catalog", UITheme.ACCENT_PURPLE);
        addSidebarButton(sidebar, "reports", "Reports / Audit", UITheme.ACCENT_AMBER);

        sidebar.add(Box.createVerticalGlue());
        
        // Logout Button at bottom
        JButton logoutBtn = UITheme.sidebarNavButton("Logout System", UITheme.ACCENT_RED);
        logoutBtn.addActionListener(e -> confirmAndLogout());
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(10));

        return sidebar;
    }

    private JLabel sidebarLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(new Color(71, 85, 105));
        l.setBorder(new EmptyBorder(5, 20, 5, 0));
        return l;
    }

    private void addSidebarButton(JPanel container, String key, String text, Color accent) {
        JButton button = UITheme.sidebarNavButton(text, accent);
        button.addActionListener(e -> activateNav(key));
        navButtons.put(key, button);
        container.add(button);
    }

    private JPanel createHeader(String username) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
            new EmptyBorder(0, 24, 0, 20)
        ));

        JLabel title = new JLabel("Admin Control Center");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 18));
        actions.setOpaque(false);

        notificationButton = iconBtn("notification", "Notifications");
        notificationButton.addActionListener(e -> new NotificationsPanel(userId, "ADMIN"));
        
        JButton settings = iconBtn("settings", "Settings");
        settings.addActionListener(e -> new ModernSettingsFrame(-1, "ADMIN"));

        actions.add(notificationButton);
        actions.add(settings);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JButton iconBtn(String name, String tip) {
        JButton b = new JButton(UITheme.getIcon(name, UITheme.ACCENT_CYAN, 20));
        b.setToolTipText(tip);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(40, 40));
        // Hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setOpaque(true); b.setBackground(new Color(255,255,255,10)); }
            public void mouseExited(java.awt.event.MouseEvent e) { b.setOpaque(false); }
        });
        return b;
    }

    private JPanel createDashboardPage() {
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setOpaque(false);
        dashboard.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Metrics (rebuildable)
        metricsPanelContainer = new JPanel(new BorderLayout());
        metricsPanelContainer.setOpaque(false);
        metricsPanelContainer.add(createMetricsPanel(), BorderLayout.CENTER);
        metricsPanelContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        dashboard.add(metricsPanelContainer);
        dashboard.add(Box.createVerticalStrut(20));

        // Charts
        chartsPanelContainer = new JPanel(new BorderLayout());
        chartsPanelContainer.setOpaque(false);
        chartsPanelContainer.add(createChartsPanel(), BorderLayout.CENTER);
        chartsPanelContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        dashboard.add(chartsPanelContainer);
        dashboard.add(Box.createVerticalStrut(20));

        // Live Ongoing Service table
        activityPanelContainer = new JPanel(new BorderLayout(0, 10));
        activityPanelContainer.setOpaque(false);
        JLabel title = new JLabel("Live Ongoing Services");
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        activityPanelContainer.add(title, BorderLayout.NORTH);
        activityPanelContainer.add(createLiveOngoingTable(), BorderLayout.CENTER);
        dashboard.add(activityPanelContainer);
        dashboard.add(Box.createVerticalStrut(20));

        // Automatic Booking Load Analysis Calendar
        JPanel calendarPanelWrapper = new JPanel(new BorderLayout(0, 10));
        calendarPanelWrapper.setOpaque(false);
        JLabel calTitle = new JLabel("Booking Load Analysis Calendar");
        calTitle.setFont(UITheme.FONT_SUBTITLE);
        calTitle.setForeground(UITheme.TEXT_PRIMARY);
        calendarPanelWrapper.add(calTitle, BorderLayout.NORTH);
        
        AppointmentCalendarPanel calendarPanel = new AppointmentCalendarPanel();
        calendarPanel.setAppointments(new service.ServiceManager().getAllServices());
        calendarPanelWrapper.add(calendarPanel, BorderLayout.CENTER);
        dashboard.add(calendarPanelWrapper);
        dashboard.add(Box.createVerticalStrut(20));

        // Wrap in scroll pane for proper scrolling - use styled smooth version
        JScrollPane scrollPane = UITheme.styledScrollPane(dashboard);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JScrollPane createLiveOngoingTable() {
        String[] columns = {"ID", "Vehicle", "Customer", "Mechanic", "Status", "ETA", "Progress"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        java.util.List<String[]> rows = dashboardDAO.getLiveOngoingServices();
        for (String[] row : rows) {
            model.addRow(row);
        }
        
        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Add Right-Click Context Menu
        JPopupMenu popupMenu = new JPopupMenu();
        
        JMenu updateStatusMenu = new JMenu("Update Status");
        String[] statuses = {"In Progress", "Waiting Parts", "Ready", "Completed"};
        for (String s : statuses) {
            JMenuItem item = new JMenuItem(s);
            item.addActionListener(e -> updateServiceField(table, "status", s));
            updateStatusMenu.add(item);
        }
        popupMenu.add(updateStatusMenu);
        
        JMenuItem updateEtaItem = new JMenuItem("Update ETA");
        updateEtaItem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String newEta = JOptionPane.showInputDialog(this, "Enter new ETA (e.g., '2 Hrs', 'Tomorrow 4 PM'):");
                if (newEta != null && !newEta.trim().isEmpty()) {
                    updateServiceField(table, "estimated_time", newEta);
                }
            }
        });
        popupMenu.add(updateEtaItem);
        
        JMenuItem assignMechanicItem = new JMenuItem("Assign Mechanic");
        assignMechanicItem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                int serviceId = Integer.parseInt((String) table.getValueAt(r, 0));
                java.util.List<model.Mechanic> mechanics = new dao.MechanicDAO().getAllMechanics();
                String[] mechNames = mechanics.stream().map(m -> m.getName() + " (ID:" + m.getMechanicId() + ")").toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(this, "Select Mechanic:", "Assign Mechanic", JOptionPane.QUESTION_MESSAGE, null, mechNames, mechNames.length > 0 ? mechNames[0] : null);
                if (selected != null) {
                    int mechId = Integer.parseInt(selected.split("ID:")[1].replace(")", ""));
                    updateServiceMechanic(serviceId, mechId);
                }
            }
        });
        popupMenu.add(assignMechanicItem);

        UITheme.styleMenu(popupMenu);
        UITheme.styleMenu(updateStatusMenu);

        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) { maybeSelectRow(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeSelectRow(e); }
            private void maybeSelectRow(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && !table.isRowSelected(row)) {
                        table.setRowSelectionInterval(row, row);
                    }
                }
            }
        });

        JScrollPane sp = UITheme.styledScrollPane(table);
        sp.setPreferredSize(new Dimension(0, 200));
        return sp;
    }

    private void updateServiceField(JTable table, String field, String value) {
        int r = table.getSelectedRow();
        if (r < 0) return;
        int serviceId = Integer.parseInt((String) table.getValueAt(r, 0));
        try (java.sql.Connection conn = dao.DBConnection.getConnection()) {
            java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE services SET " + field + " = ? WHERE service_id = ?");
            stmt.setString(1, value);
            stmt.setInt(2, serviceId);
            stmt.executeUpdate();
            refreshDashboardCounters();
            UITheme.showSuccessDialog(this, "Updated", "Updated successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            UITheme.showErrorDialog(this, "Update Error", "Error updating " + field + ".");
        }
    }

    private void updateServiceMechanic(int serviceId, int mechId) {
        try (java.sql.Connection conn = dao.DBConnection.getConnection()) {
            java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE services SET mechanic_id = ? WHERE service_id = ?");
            stmt.setInt(1, mechId);
            stmt.setInt(2, serviceId);
            stmt.executeUpdate();
            refreshDashboardCounters();
            UITheme.showSuccessDialog(this, "Success", "Mechanic assigned successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
            UITheme.showErrorDialog(this, "Assignment Error", "Error assigning mechanic.");
        }
    }

    /** Rebuild all dashboard counters from live DB data */
    private void refreshDashboardCounters() {
        if (metricsPanelContainer != null) {
            metricsPanelContainer.removeAll();
            metricsPanelContainer.add(createMetricsPanel(), java.awt.BorderLayout.CENTER);
            metricsPanelContainer.revalidate();
            metricsPanelContainer.repaint();
        }
        if (activityPanelContainer != null && activityPanelContainer.getComponentCount() > 0) {
            // Rebuild the Live Ongoing table
            Component[] comps = activityPanelContainer.getComponents();
            for (Component c : comps) {
                if (c instanceof JScrollPane) {
                    activityPanelContainer.remove(c);
                }
            }
            activityPanelContainer.add(createLiveOngoingTable(), java.awt.BorderLayout.CENTER);
            activityPanelContainer.revalidate();
            activityPanelContainer.repaint();
        }
    }

    private JPanel activityCounter(String label, String count, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 2));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60)),
            new EmptyBorder(10, 12, 10, 12)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel countLabel = new JLabel(count, SwingConstants.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        countLabel.setForeground(accent);
        JLabel nameLabel = new JLabel(label, SwingConstants.CENTER);
        nameLabel.setFont(UITheme.FONT_TINY);
        nameLabel.setForeground(UITheme.TEXT_SECONDARY);
        card.add(countLabel, BorderLayout.CENTER);
        card.add(nameLabel, BorderLayout.SOUTH);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { activateNav("requests"); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(UITheme.BG_CARD_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { card.setBackground(UITheme.BG_CARD); }
        });
        return card;
    }

    private JPanel dashboardActionCard(String title, String desc, Color accent, String navKey) {
        JPanel card = UITheme.accentCard(accent);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(22, 22, 20, 22));

        JLabel h = new JLabel(title);
        h.setFont(UITheme.FONT_HEADING);
        h.setForeground(UITheme.TEXT_PRIMARY);

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(UITheme.FONT_TINY);
        d.setForeground(UITheme.TEXT_SECONDARY);

        JButton btn = UITheme.accentButton("Open Module", accent);
        btn.setPreferredSize(new Dimension(0, 38));
        btn.addActionListener(e -> activateNav(navKey));

        JPanel top = new JPanel(new BorderLayout(0, 5));
        top.setOpaque(false);
        top.add(h, BorderLayout.NORTH);
        top.add(d, BorderLayout.CENTER);

        card.add(top, BorderLayout.CENTER);
        card.add(btn, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createServiceRequestsPage() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        JLabel heading = new JLabel("Service Requests");
        heading.setFont(UITheme.FONT_HEADING);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        top.add(heading, BorderLayout.WEST);

        searchField = UITheme.styledTextField("Search by vehicle, customer, service or status");
        searchField.setPreferredSize(new Dimension(360, 40));
        top.add(searchField, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        String[] columns = {"ID", "Vehicle", "Customer", "Service Type", "Date", "Status", "Assigned Mechanic", "Total Cost"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable = new JTable(tableModel);
        UITheme.styleTable(serviceTable);
        serviceSorter = new TableRowSorter<>(tableModel);
        serviceTable.setRowSorter(serviceSorter);
        serviceTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value == null ? "" : value.toString().toLowerCase();
                Color fg;
                String prefix;
                if (status.contains("completed")) { fg = new Color(34, 197, 94); prefix = "\u2714 "; }
                else if (status.contains("in progress") || status.contains("service in progress")) { fg = new Color(59, 130, 246); prefix = "\u25B6 "; }
                else if (status.contains("waiting") || status.contains("parts")) { fg = new Color(249, 115, 22); prefix = "\u23F3 "; }
                else if (status.contains("quality") || status.contains("check")) { fg = new Color(168, 85, 247); prefix = "\u2605 "; }
                else if (status.contains("ready") || status.contains("delivery")) { fg = new Color(34, 211, 238); prefix = "\u2192 "; }
                else if (status.contains("out for")) { fg = new Color(20, 184, 166); prefix = "\u27A4 "; }
                else if (status.contains("pickup") || status.contains("picked")) { fg = new Color(96, 165, 250); prefix = "\u2690 "; }
                else if (status.contains("inspection")) { fg = new Color(139, 92, 246); prefix = "\u2690 "; }
                else if (status.contains("delayed")) { fg = new Color(239, 68, 68); prefix = "\u26A0 "; }
                else { fg = new Color(245, 158, 11); prefix = "\u25CF "; } // Pending default
                setText(prefix + (value == null ? "Pending" : value.toString()));
                setForeground(isSelected ? Color.WHITE : fg);
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
        installStatusPopup();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applySearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applySearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applySearch();
            }
        });

        panel.add(UITheme.styledScrollPane(serviceTable), BorderLayout.CENTER);
        return panel;
    }

    private class ModernPopupMenu extends JPopupMenu {
        public ModernPopupMenu() {
            setOpaque(true);
        }
        @Override protected void paintComponent(Graphics g) {
            boolean dark = UITheme.isDarkMode();
            g.setColor(dark ? new Color(11, 16, 32) : Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        @Override protected void paintBorder(Graphics g) {
            boolean dark = UITheme.isDarkMode();
            g.setColor(dark ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 30));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    private class ModernMenuItem extends JMenuItem {
        private boolean isHovered = false;
        public ModernMenuItem(String text, Icon icon) {
            super(text, icon);
            init();
        }
        public ModernMenuItem(String text) {
            super(text);
            init();
        }
        private void init() {
            setOpaque(true);
            setFont(UITheme.FONT_BODY);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int width = 14 + 24 + 10 + fm.stringWidth(getText() == null ? "" : getText()) + 14;
            return new Dimension(Math.max(220, width), 36);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean dark = UITheme.isDarkMode();
            
            Color bg = dark ? new Color(11, 16, 32) : Color.WHITE;
            Color fg = dark ? Color.WHITE : new Color(17, 17, 17);
            Color hoverBg = dark ? new Color(14, 165, 233) : new Color(224, 242, 254);
            Color hoverFg = dark ? Color.WHITE : Color.BLACK;

            g2.setColor(isHovered ? hoverBg : bg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(isHovered ? hoverFg : fg);
            g2.setFont(getFont());
            
            if (getIcon() != null) {
                int iy = (getHeight() - getIcon().getIconHeight()) / 2;
                getIcon().paintIcon(this, g2, 14, iy);
            }
            
            FontMetrics fm = g2.getFontMetrics();
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText() == null ? "" : getText(), 48, ty);
            g2.dispose();
        }
    }

    private class ModernMenu extends JMenu {
        private boolean isHovered = false;
        public ModernMenu(String text) {
            super(text);
            setOpaque(true);
            setFont(UITheme.FONT_BODY);
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
            });
        }
        @Override public Dimension getPreferredSize() {
            FontMetrics fm = getFontMetrics(getFont());
            int width = 14 + 24 + 10 + fm.stringWidth(getText() == null ? "" : getText()) + 30;
            return new Dimension(Math.max(220, width), 36);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean dark = UITheme.isDarkMode();
            
            Color bg = dark ? new Color(11, 16, 32) : Color.WHITE;
            Color fg = dark ? Color.WHITE : new Color(17, 17, 17);
            Color hoverBg = dark ? new Color(14, 165, 233) : new Color(224, 242, 254);
            Color hoverFg = dark ? Color.WHITE : Color.BLACK;

            boolean active = isHovered || isSelected();

            g2.setColor(active ? hoverBg : bg);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(active ? hoverFg : fg);
            g2.setFont(getFont());
            
            if (getIcon() != null) {
                int iy = (getHeight() - getIcon().getIconHeight()) / 2;
                getIcon().paintIcon(this, g2, 14, iy);
            }
            
            FontMetrics fm = g2.getFontMetrics();
            int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText() == null ? "" : getText(), 48, ty);
            g2.drawString(">", getWidth() - 20, ty);
            g2.dispose();
        }
    }

    private class ModernSeparator extends JSeparator {
        public ModernSeparator() {
            setOpaque(true);
        }
        @Override public Dimension getPreferredSize() {
            return new Dimension(0, 1);
        }
        @Override protected void paintComponent(Graphics g) {
            boolean dark = UITheme.isDarkMode();
            g.setColor(dark ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 30));
            g.fillRect(0, 0, getWidth(), 1);
        }
    }

    private void installStatusPopup() {
        ModernPopupMenu popupMenu = new ModernPopupMenu();
        
        ModernMenuItem viewItem = new ModernMenuItem("View Full Details", UITheme.getIcon("reports", UITheme.ACCENT_CYAN, 16));
        viewItem.addActionListener(e -> {
            int row = serviceTable.getSelectedRow();
            if(row >= 0) UITheme.showInfoDialog(this, "Service Details", "Viewing complete records for: " + serviceTable.getValueAt(row, 1));
        });

        ModernMenuItem editItem = new ModernMenuItem("Assign Mechanic", UITheme.getIcon("mechanic", UITheme.ACCENT_PURPLE, 16));
        editItem.addActionListener(e -> assignMechanicForSelectedRow());

        ModernMenu statusMenu = new ModernMenu("Update Service Status");
        statusMenu.setIcon(UITheme.getIcon("service", UITheme.ACCENT_AMBER, 16));
        
        // Force submenu popup styling dynamically
        JPopupMenu subPopup = statusMenu.getPopupMenu();
        subPopup.setOpaque(true);
        subPopup.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        statusMenu.addChangeListener(e -> {
            boolean dark = UITheme.isDarkMode();
            subPopup.setBackground(dark ? new Color(11, 16, 32) : Color.WHITE);
            subPopup.setBorder(BorderFactory.createLineBorder(dark ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 30), 1));
        });
        
        String[][] statuses = {
            {"Pending Request",          "Pending Request"},
            {"Vehicle Pickup Assigned",  "Vehicle Pickup Assigned"},
            {"Vehicle Picked Up",        "Vehicle Picked Up"},
            {"In Inspection",            "In Inspection"},
            {"Waiting for Parts",        "Waiting for Parts"},
            {"Service In Progress",      "Service In Progress"},
            {"Quality Check",            "Quality Check"},
            {"Ready for Delivery",       "Ready for Delivery"},
            {"Out for Delivery",         "Out for Delivery"},
            {"Completed",                "Completed"}
        };

        for (String[] st : statuses) {
            ModernMenuItem mi = new ModernMenuItem(st[0], UITheme.getStatusIcon(st[1]));
            mi.addActionListener(e -> updateStatus(st[1]));
            statusMenu.add(mi);
        }

        ModernMenuItem trackItem = new ModernMenuItem("Live Vehicle Tracker", UITheme.getIcon("vehicle", UITheme.ACCENT_GREEN, 16));
        trackItem.addActionListener(e -> UITheme.showInfoDialog(this, "Live Tracking", "Opening real-time tracking interface..."));

        ModernMenuItem customerDetailsItem = new ModernMenuItem("Customer Profile", UITheme.getIcon("user", UITheme.ACCENT_BLUE, 16));
        customerDetailsItem.addActionListener(e -> {
            int row = serviceTable.getSelectedRow();
            if (row < 0) return;
            int mr = serviceTable.convertRowIndexToModel(row);
            String custName = (String) tableModel.getValueAt(mr, 2);
            UITheme.showInfoDialog(this, "Customer Profile", "Primary Contact: " + custName + "\nStatus: Active Account");
        });

        ModernMenuItem invoiceItem = new ModernMenuItem("Generate Billing", UITheme.getIcon("billing", UITheme.ACCENT_GREEN, 16));
        invoiceItem.addActionListener(e -> openInvoiceForSelectedRow());

        ModernMenuItem delItem = new ModernMenuItem("Remove Request", UITheme.getIcon("notification", UITheme.ACCENT_RED, 16));
        delItem.addActionListener(e -> {
            if (UITheme.showConfirmDialog(this, "Confirm Deletion", "Are you sure you want to remove this service request?")) {
                UITheme.showSuccessDialog(this, "Deleted", "Service request record has been archived.");
            }
        });

        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.add(statusMenu);
        popupMenu.add(new ModernSeparator());
        popupMenu.add(trackItem);
        popupMenu.add(customerDetailsItem);
        popupMenu.add(invoiceItem);
        popupMenu.add(new ModernSeparator());
        popupMenu.add(delItem);

        serviceTable.setComponentPopupMenu(popupMenu);
        serviceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) { maybeSelectRow(e); }
            @Override public void mouseReleased(java.awt.event.MouseEvent e) { maybeSelectRow(e); }
            private void maybeSelectRow(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = serviceTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && !serviceTable.isRowSelected(row)) {
                        serviceTable.setRowSelectionInterval(row, row);
                    }
                }
            }
        });
    }

    private void openInvoiceForSelectedRow() {
        int row = serviceTable.getSelectedRow();
        if (row < 0) {
            UITheme.showInfoDialog(this, "No Selection", "Select a service row first.");
            return;
        }
        int modelRow = serviceTable.convertRowIndexToModel(row);
        int serviceId = (int) tableModel.getValueAt(modelRow, 0);
        new BillingFrame(serviceId);
    }

    private JPanel createModulePanel(String title, Runnable action, String message) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
                new EmptyBorder(24, 24, 24, 24)
        ));

        JLabel heading = new JLabel(title);
        heading.setFont(UITheme.FONT_SUBTITLE);
        heading.setForeground(UITheme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel desc = new JLabel("<html>" + message + "</html>");
        desc.setFont(UITheme.FONT_BODY);
        desc.setForeground(UITheme.TEXT_SECONDARY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton open = UITheme.accentButton("Open " + title, UITheme.ACCENT_CYAN);
        open.setAlignmentX(Component.LEFT_ALIGNMENT);
        open.addActionListener(e -> action.run());

        card.add(heading);
        card.add(Box.createVerticalStrut(10));
        card.add(desc);
        card.add(Box.createVerticalStrut(16));
        card.add(open);
        panel.add(card);
        return panel;
    }

    private JPanel createActionCard(String title, String subtitle, Color accent, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_HEADING);
        t.setForeground(UITheme.TEXT_PRIMARY);
        JLabel s = new JLabel("<html>" + subtitle + "</html>");
        s.setFont(UITheme.FONT_BODY);
        s.setForeground(UITheme.TEXT_SECONDARY);
        JButton open = UITheme.accentButton("Open", accent);
        open.addActionListener(e -> action.run());
        card.add(t, BorderLayout.NORTH);
        card.add(s, BorderLayout.CENTER);
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createMetricsPanel() {
        java.util.Map<String, Integer> counts = dashboardDAO.getStatusCounts();
        Map<String, Integer> stats = dashboardDAO.getStats();

        JPanel panel = new JPanel(new GridLayout(1, 5, 12, 0));
        panel.setOpaque(false);

        panel.add(createMetricCard("Revenue", UITheme.formatCurrency(stats.getOrDefault("Total Revenue", 0)), UITheme.ACCENT_GREEN));
        
        int pending = counts.getOrDefault("Pending", 0) + counts.getOrDefault("Pending Request", 0);
        panel.add(createMetricCard("Pending Services", String.valueOf(pending), UITheme.ACCENT_AMBER));
        
        int ongoing = counts.getOrDefault("In Progress", 0) + counts.getOrDefault("Pickup", 0) + counts.getOrDefault("Ready", 0);
        panel.add(createMetricCard("Ongoing Services", String.valueOf(ongoing), UITheme.ACCENT_BLUE));
        
        panel.add(createMetricCard("Completed Services", String.valueOf(counts.getOrDefault("Completed", 0)), new Color(34, 197, 94)));
        panel.add(createMetricCard("Waiting for Parts", String.valueOf(counts.getOrDefault("Waiting Parts", 0)), new Color(249, 115, 22)));
        
        return panel;
    }

    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 260));
        
        LineChartPanel revenueChart = new LineChartPanel("Monthly Revenue", dashboardDAO.getMonthlyRevenue(), UITheme.ACCENT_GREEN);
        revenueChart.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { activateNav("billing"); }
        });
        
        BarChartPanel servicesChart = new BarChartPanel("Monthly Services", dashboardDAO.getMonthlyServiceCounts(), UITheme.ACCENT_CYAN);
        servicesChart.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { activateNav("requests"); }
        });
        
        panel.add(revenueChart);
        panel.add(servicesChart);
        return panel;
    }

    private JPanel createMetricCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UITheme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT, 1),
                new EmptyBorder(14, 14, 14, 14)
        ));
        JLabel valueLabel = new JLabel(value == null || value.trim().isEmpty() ? "-" : value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(UITheme.TEXT_PRIMARY);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SMALL);
        titleLabel.setForeground(accent);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        // make interactive
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                // Direct navigation via CardLayout - no intermediate screens
                String tl = title.toLowerCase();
                if (tl.contains("revenue")) {
                    activateNav("billing");
                } else if (tl.contains("pending") || tl.contains("jobs") || tl.contains("service")) {
                    activateNav("requests");
                } else if (tl.contains("users") || tl.contains("customer")) {
                    activateNav("customers");
                } else if (tl.contains("inventory")) {
                    activateNav("inventory");
                } else if (tl.contains("mechanic")) {
                    activateNav("mechanics");
                } else if (tl.contains("vehicle")) {
                    activateNav("vehicles");
                } else {
                    activateNav("reports");
                }
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(UITheme.BG_CARD_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { card.setBackground(UITheme.BG_CARD); }
        });
        return card;
    }

    private void activateNav(String key) {
        cardLayout.show(contentCards, key);
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            boolean active = entry.getKey().equals(key);
            JButton button = entry.getValue();
            button.setBackground(active ? UITheme.ACCENT_CYAN : new Color(15, 23, 42));
            button.setForeground(active ? Color.WHITE : new Color(226, 232, 240));
        }
        // Auto-refresh dashboard data when navigating back to dashboard
        if ("dashboard".equals(key)) {
            refreshDashboardCounters();
            if (chartsPanelContainer != null) {
                chartsPanelContainer.removeAll();
                chartsPanelContainer.add(createChartsPanel(), java.awt.BorderLayout.CENTER);
                chartsPanelContainer.revalidate();
                chartsPanelContainer.repaint();
            }
        }
    }

    private String getActiveCard() {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            if (entry.getValue().getBackground().equals(UITheme.ACCENT_CYAN)) {
                return entry.getKey();
            }
        }
        return "dashboard";
    }

    private void refreshData() {
        if (tableModel == null) {
            return;
        }
        tableModel.setRowCount(0);
        List<Service> services = serviceManager.getAllServices();
        Map<Integer, Vehicle> vehicleCache = new LinkedHashMap<>();
        Map<Integer, Customer> customerCache = new LinkedHashMap<>();
        for (Service s : services) {
            Vehicle vehicle = vehicleCache.get(s.getVehicleId());
            if (vehicle == null) {
                vehicle = vehicleService.getVehicleById(s.getVehicleId());
                if (vehicle != null) {
                    vehicleCache.put(s.getVehicleId(), vehicle);
                }
            }
            String vehicleLabel = vehicle == null ? ("ID " + s.getVehicleId()) : (vehicle.getRegistrationNumber() + " (" + vehicle.getBrand() + ")");
            String customerLabel = "N/A";
            if (vehicle != null) {
                Customer customer = customerCache.get(vehicle.getCustomerId());
                if (customer == null) {
                    customer = customerService.getCustomerById(vehicle.getCustomerId());
                    if (customer != null) {
                        customerCache.put(vehicle.getCustomerId(), customer);
                    }
                }
                if (customer != null) {
                    customerLabel = customer.getName();
                }
            }
            tableModel.addRow(new Object[]{
                    s.getServiceId(), vehicleLabel, customerLabel, s.getServiceType(),
                    s.getServiceDate(), s.getStatus(),
                    (s.getMechanicName() == null || s.getMechanicName().trim().isEmpty()) ? "Unassigned" : s.getMechanicName(),
                    UITheme.formatCurrency(s.getCost())
            });
        }
    }

    private void updateStatus(String status) {
        int row = serviceTable.getSelectedRow();
        if (row < 0) {
            UITheme.showInfoDialog(this, "No Selection", "Please select a service row.");
            return;
        }
        int modelRow = serviceTable.convertRowIndexToModel(row);
        int id = (int) tableModel.getValueAt(modelRow, 0);
        if (serviceManager.updateServiceStatus(id, status)) {
            refreshData();
            refreshDashboardCounters();
            updateNotificationBadge();
            // Create notification for status change
            try {
                String vehicleLabel = (String) tableModel.getValueAt(modelRow, 1);
                new NotificationDAO().createServiceNotification(id, vehicleLabel, status);
            } catch (Exception ignored) {}
        }
    }

    private void assignMechanicForSelectedRow() {
        int row = serviceTable.getSelectedRow();
        if (row < 0) {
            UITheme.showInfoDialog(this, "No Selection", "Select a service row first.");
            return;
        }
        int modelRow = serviceTable.convertRowIndexToModel(row);
        int serviceId = (int) tableModel.getValueAt(modelRow, 0);
        Service service = serviceManager.getServiceById(serviceId);
        if (service == null) return;

        List<Mechanic> mechanics = new MechanicDAO().getAllMechanics();
        if (mechanics.isEmpty()) {
            UITheme.showInfoDialog(this, "No Mechanics", "No active mechanics available.");
            return;
        }
        String[] options = new String[mechanics.size()];
        int selected = 0;
        for (int i = 0; i < mechanics.size(); i++) {
            Mechanic m = mechanics.get(i);
            options[i] = m.getName() + " | " + m.getSpecialization() + " | Active: " + m.getActiveJobs();
            if (service.getMechanicId() != null && service.getMechanicId() == m.getMechanicId()) selected = i;
        }
        JComboBox<String> combo = UITheme.styledComboBox(options);
        combo.setSelectedIndex(selected);
        int choice = JOptionPane.showConfirmDialog(this, combo, "Assign Mechanic", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;
        service.setMechanicId(mechanics.get(combo.getSelectedIndex()).getMechanicId());
        if (new dao.ServiceDAO().updateService(service)) {
            refreshData();
            refreshDashboardCounters();
            updateNotificationBadge();
        } else {
            UITheme.showErrorDialog(this, "Error", "Failed to assign mechanic.");
        }
    }

    private void applySearch() {
        if (serviceSorter == null || searchField == null) {
            return;
        }
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            serviceSorter.setRowFilter(null);
            return;
        }
        serviceSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query)));
    }

    private void exportServicesCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Services CSV");
        chooser.setSelectedFile(new File("services_export.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File out = chooser.getSelectedFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(out))) {
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                writer.print(csvValue(tableModel.getColumnName(c)));
                if (c < tableModel.getColumnCount() - 1) {
                    writer.print(",");
                }
            }
            writer.println();

            for (int r = 0; r < tableModel.getRowCount(); r++) {
                for (int c = 0; c < tableModel.getColumnCount(); c++) {
                    Object value = tableModel.getValueAt(r, c);
                    writer.print(csvValue(value == null ? "" : String.valueOf(value)));
                    if (c < tableModel.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
            UITheme.showSuccessDialog(this, "Export Success", "Exported successfully: " + out.getAbsolutePath());
        } catch (Exception ex) {
            UITheme.showErrorDialog(this, "Export Error", "Export failed: " + ex.getMessage());
        }
    }

    private String csvValue(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
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
        NotificationDAO.NotificationResponse response = new NotificationDAO().getNotifications(userId, "ADMIN");
        int unread = response.unreadCount;
        notificationButton.setText(unread > 0 ? "🔔(" + unread + ")" : "🔔");
    }

    @Override
    public void dispose() {
        if (sessionTimeoutManager != null) sessionTimeoutManager.stop();
        super.dispose();
    }

    private static class BarChartPanel extends JPanel {
        private final String title;
        private final Map<String, Integer> data;
        private final Color accent;
        private int hoverIndex = -1;
        private final java.util.List<Rectangle> barRects = new java.util.ArrayList<>();
        private final java.util.List<String> barLabels = new java.util.ArrayList<>();
        private final java.util.List<Integer> barValues = new java.util.ArrayList<>();

        private BarChartPanel(String title, Map<String, Integer> data, Color accent) {
            this.title = title;
            this.data = data;
            this.accent = accent;
            setBackground(UITheme.BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int newHover = -1;
                    for (int i = 0; i < barRects.size(); i++) {
                        if (barRects.get(i).contains(e.getPoint())) {
                            newHover = i; break;
                        }
                    }
                    if (newHover != hoverIndex) {
                        hoverIndex = newHover;
                        if (hoverIndex >= 0 && hoverIndex < barLabels.size()) {
                            setToolTipText(barLabels.get(hoverIndex) + ": " + barValues.get(hoverIndex) + " Services");
                        } else {
                            setToolTipText("Click for details");
                        }
                        repaint();
                    }
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hoverIndex = -1; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.setFont(UITheme.FONT_HEADING);
            g2.drawString(title, 10, 22);
            if (data == null || data.isEmpty()) {
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.drawString("No data", 10, 44);
                g2.dispose(); return;
            }
            barRects.clear(); barLabels.clear(); barValues.clear();
            int max = 1;
            for (Integer v : data.values()) max = Math.max(max, v == null ? 0 : v);
            int chartX = 30, chartY = 40, chartW = getWidth() - 40, chartH = getHeight() - 60;
            
            // Draw axis
            g2.setColor(UITheme.BORDER_DEFAULT);
            g2.drawLine(chartX, chartY, chartX, chartY + chartH);
            g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

            int n = data.size();
            int gap = 12;
            int barW = Math.max(16, (chartW - gap * (n + 1)) / Math.max(1, n));
            int i = 0;
            g2.setFont(UITheme.FONT_SMALL);
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int value = entry.getValue() == null ? 0 : entry.getValue();
                int h = (int) ((value * 1.0 / max) * (chartH - 25));
                int x = chartX + gap + i * (barW + gap);
                int y = chartY + chartH - h;

                if (i == hoverIndex) {
                    g2.setPaint(new GradientPaint(x, y, accent.brighter(), x, y + h, accent));
                    g2.fillRoundRect(x, y, barW, h, 6, 6);
                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.drawString(String.valueOf(value), x + barW/2 - g2.getFontMetrics().stringWidth(String.valueOf(value))/2, y - 4);
                    g2.setFont(UITheme.FONT_SMALL);
                } else {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 185));
                    g2.fillRoundRect(x, y, barW, h, 6, 6);
                }

                barRects.add(new Rectangle(x, y, barW, h));
                barLabels.add(entry.getKey());
                barValues.add(value);

                g2.setColor(UITheme.TEXT_SECONDARY);
                String label = entry.getKey();
                if (label.length() > 3) label = label.substring(0, 3);
                g2.drawString(label, x + barW/2 - g2.getFontMetrics().stringWidth(label)/2, chartY + chartH + 14);
                i++;
            }
            g2.dispose();
        }
    }

    private static class LineChartPanel extends JPanel {
        private final String title;
        private final Map<String, Integer> data;
        private final Color accent;
        private int hoverIndex = -1;
        private final java.util.List<Point> points = new java.util.ArrayList<>();
        private final java.util.List<String> labels = new java.util.ArrayList<>();
        private final java.util.List<Integer> values = new java.util.ArrayList<>();

        private LineChartPanel(String title, Map<String, Integer> data, Color accent) {
            this.title = title;
            this.data = data;
            this.accent = accent;
            setBackground(UITheme.BG_CARD);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT),
                    new EmptyBorder(10, 10, 10, 10)
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    int newHover = -1;
                    for (int i = 0; i < points.size(); i++) {
                        if (points.get(i).distance(e.getPoint()) < 10) {
                            newHover = i; break;
                        }
                    }
                    if (newHover != hoverIndex) {
                        hoverIndex = newHover;
                        if (hoverIndex >= 0 && hoverIndex < labels.size()) {
                            setToolTipText(labels.get(hoverIndex) + ": " + UITheme.formatCurrency(values.get(hoverIndex)));
                        } else {
                            setToolTipText("Click for details");
                        }
                        repaint();
                    }
                }
            });
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hoverIndex = -1; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.setFont(UITheme.FONT_HEADING);
            g2.drawString(title, 10, 22);
            if (data == null || data.isEmpty()) {
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.drawString("No data", 10, 44);
                g2.dispose(); return;
            }
            points.clear(); labels.clear(); values.clear();
            int max = 1;
            for (Integer v : data.values()) max = Math.max(max, v == null ? 0 : v);
            int chartX = 50, chartY = 40, chartW = getWidth() - 70, chartH = getHeight() - 60;
            
            // Draw axis
            g2.setColor(UITheme.BORDER_DEFAULT);
            g2.drawLine(chartX, chartY, chartX, chartY + chartH);
            g2.drawLine(chartX, chartY + chartH, chartX + chartW, chartY + chartH);

            int n = data.size();
            int stepX = n > 1 ? chartW / (n - 1) : chartW;
            int i = 0;
            g2.setFont(UITheme.FONT_SMALL);
            
            // Collect points
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int value = entry.getValue() == null ? 0 : entry.getValue();
                int h = (int) ((value * 1.0 / max) * (chartH - 20));
                int x = chartX + i * stepX;
                int y = chartY + chartH - h;
                points.add(new Point(x, y));
                labels.add(entry.getKey());
                values.add(value);
                i++;
            }
            
            // Draw lines and area
            if (points.size() > 1) {
                java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
                java.awt.geom.GeneralPath area = new java.awt.geom.GeneralPath();
                path.moveTo(points.get(0).x, points.get(0).y);
                area.moveTo(points.get(0).x, chartY + chartH);
                area.lineTo(points.get(0).x, points.get(0).y);
                
                for (int j = 1; j < points.size(); j++) {
                    path.lineTo(points.get(j).x, points.get(j).y);
                    area.lineTo(points.get(j).x, points.get(j).y);
                }
                area.lineTo(points.get(points.size() - 1).x, chartY + chartH);
                area.closePath();
                
                g2.setPaint(new GradientPaint(0, chartY, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80), 0, chartY + chartH, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 5)));
                g2.fill(area);
                
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(path);
            }
            
            // Draw points & labels
            g2.setStroke(new BasicStroke(1.5f));
            for (int j = 0; j < points.size(); j++) {
                Point p = points.get(j);
                if (j == hoverIndex) {
                    g2.setColor(accent.brighter());
                    g2.fillOval(p.x - 5, p.y - 5, 10, 10);
                    g2.setColor(UITheme.TEXT_PRIMARY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    String val = UITheme.formatCurrency(values.get(j));
                    g2.drawString(val, p.x - g2.getFontMetrics().stringWidth(val)/2, p.y - 10);
                    g2.setFont(UITheme.FONT_SMALL);
                } else {
                    g2.setColor(UITheme.BG_CARD);
                    g2.fillOval(p.x - 4, p.y - 4, 8, 8);
                    g2.setColor(accent);
                    g2.drawOval(p.x - 4, p.y - 4, 8, 8);
                }
                
                g2.setColor(UITheme.TEXT_SECONDARY);
                String label = labels.get(j);
                if (label.length() > 3) label = label.substring(0, 3);
                g2.drawString(label, p.x - g2.getFontMetrics().stringWidth(label)/2, chartY + chartH + 14);
            }
            g2.dispose();
        }
    }
}

