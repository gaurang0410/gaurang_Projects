package gui;

import dao.NotificationDAO;
import model.NotificationItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NotificationsPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private final int userId;
    private final String role;  // "ADMIN", "CUSTOMER", or null
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private JPanel listPanel;
    private JLabel unreadLabel;
    private JScrollPane scrollPane;

    /** Backward-compat constructor (no role filtering) */
    public NotificationsPanel(int userId) {
        this(userId, null);
    }

    public NotificationsPanel(int userId, String role) {
        this.userId = userId;
        this.role = role;
        setTitle("VehicleFlow - Notification Center");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 640);
        setLocationRelativeTo(null);
        initComponents();
        loadData();
        setVisible(true);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        // Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(UITheme.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_DEFAULT),
            new EmptyBorder(14, 18, 14, 18)
        ));
        JLabel title = new JLabel("Notifications", UITheme.getIcon("notification", UITheme.ACCENT_CYAN, 20), SwingConstants.LEFT);
        title.setFont(UITheme.FONT_SUBTITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setIconTextGap(10);
        header.add(title, BorderLayout.WEST);

        unreadLabel = new JLabel("0 unread");
        unreadLabel.setFont(UITheme.FONT_SMALL);
        unreadLabel.setForeground(UITheme.ACCENT_AMBER);

        JButton markAll = UITheme.ghostButton("Mark All Read", UITheme.ACCENT_CYAN);
        markAll.addActionListener(e -> {
            notificationDAO.markAllRead(userId);
            loadData();
        });

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        headerRight.setOpaque(false);
        headerRight.add(unreadLabel);
        headerRight.add(markAll);
        header.add(headerRight, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Notification list (card-based, not table)
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UITheme.BG_DARK);
        listPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadData() {
        listPanel.removeAll();
        NotificationDAO.NotificationResponse response = notificationDAO.getNotifications(userId, role);
        unreadLabel.setText(response.unreadCount + " unread");
        List<NotificationItem> items = response.notifications;

        if (items.isEmpty()) {
            JLabel empty = new JLabel("No notifications yet", SwingConstants.CENTER);
            empty.setFont(UITheme.FONT_BODY);
            empty.setForeground(UITheme.TEXT_SECONDARY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalStrut(80));
            listPanel.add(empty);
        } else {
            for (NotificationItem item : items) {
                listPanel.add(createNotificationCard(item));
                listPanel.add(Box.createVerticalStrut(4));
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createNotificationCard(NotificationItem item) {
        boolean isRead = item.isRead();
        String type = item.getType() == null ? "info" : item.getType().toLowerCase();

        // Determine accent color based on type
        Color accent;
        String icon;
        if (type.contains("booking") || type.contains("request")) {
            accent = UITheme.ACCENT_GREEN; icon = "service";
        } else if (type.contains("complete")) {
            accent = UITheme.ACCENT_CYAN; icon = "service";
        } else if (type.contains("waiting") || type.contains("parts")) {
            accent = new Color(249, 115, 22); icon = "inventory";
        } else if (type.contains("feedback")) {
            accent = UITheme.ACCENT_PURPLE; icon = "feedback";
        } else if (type.contains("pickup") || type.contains("delivery")) {
            accent = UITheme.ACCENT_BLUE; icon = "vehicle";
        } else {
            accent = UITheme.ACCENT_AMBER; icon = "notification";
        }

        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isRead ? UITheme.BG_CARD : new Color(UITheme.BG_CARD.getRed() + 8, UITheme.BG_CARD.getGreen() + 8, UITheme.BG_CARD.getBlue() + 15));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                if (!isRead) {
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left: icon
        JLabel iconLabel = new JLabel(UITheme.getIcon(icon, accent, 20));
        iconLabel.setPreferredSize(new Dimension(28, 28));
        card.add(iconLabel, BorderLayout.WEST);

        // Center: title + message
        JPanel textPanel = new JPanel(new BorderLayout(0, 2));
        textPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(item.getTitle() != null ? item.getTitle() : "Notification");
        titleLabel.setFont(new Font("Segoe UI", isRead ? Font.PLAIN : Font.BOLD, 13));
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        JLabel msgLabel = new JLabel("<html>" + (item.getMessage() != null ? item.getMessage() : "") + "</html>");
        msgLabel.setFont(UITheme.FONT_SMALL);
        msgLabel.setForeground(UITheme.TEXT_SECONDARY);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(msgLabel, BorderLayout.CENTER);
        card.add(textPanel, BorderLayout.CENTER);

        // Right: time + read status
        JPanel rightPanel = new JPanel(new BorderLayout(0, 2));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(90, 0));
        String timeStr = item.getCreatedAt() != null ? item.getCreatedAt() : "";
        if (timeStr.length() > 16) timeStr = timeStr.substring(5, 16); // trim to readable
        JLabel timeLabel = new JLabel(timeStr);
        timeLabel.setFont(UITheme.FONT_TINY);
        timeLabel.setForeground(UITheme.TEXT_SECONDARY);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        rightPanel.add(timeLabel, BorderLayout.NORTH);
        if (!isRead) {
            JLabel dot = new JLabel("\u25CF");
            dot.setForeground(accent);
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            dot.setHorizontalAlignment(SwingConstants.RIGHT);
            rightPanel.add(dot, BorderLayout.SOUTH);
        }
        card.add(rightPanel, BorderLayout.EAST);

        // Click to mark as read
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!isRead) {
                    notificationDAO.markRead(item.getNotificationId());
                    loadData();
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60)),
                    new EmptyBorder(9, 13, 9, 13)
                ));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(new EmptyBorder(10, 14, 10, 14));
            }
        });

        return card;
    }
}
