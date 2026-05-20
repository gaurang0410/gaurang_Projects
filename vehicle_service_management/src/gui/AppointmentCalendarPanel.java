package gui;

import model.Service;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class AppointmentCalendarPanel extends JPanel {
    private YearMonth currentMonth = YearMonth.now();
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private final JPanel grid = new JPanel(new GridLayout(0, 7, 3, 3));
    private java.util.List<Service> allAppointments = new java.util.ArrayList<>();
    private java.util.Map<Integer, java.util.List<Service>> servicesByDay = new java.util.HashMap<>();

    public AppointmentCalendarPanel() {
        setLayout(new BorderLayout(6, 6));
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        JButton prev = UITheme.ghostButton("<", UITheme.ACCENT_CYAN);
        prev.setPreferredSize(new Dimension(36, 30));
        JButton next = UITheme.ghostButton(">", UITheme.ACCENT_CYAN);
        next.setPreferredSize(new Dimension(36, 30));
        prev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); buildServicesForMonth(); render(); });
        next.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); buildServicesForMonth(); render(); });
        monthLabel.setFont(UITheme.FONT_HEADING);
        monthLabel.setForeground(UITheme.TEXT_PRIMARY);
        top.add(prev, BorderLayout.WEST);
        top.add(monthLabel, BorderLayout.CENTER);
        top.add(next, BorderLayout.EAST);

        JPanel weekdays = new JPanel(new GridLayout(1, 7, 3, 3));
        weekdays.setOpaque(false);
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        Color[] dayColors = {UITheme.TEXT_SECONDARY, UITheme.TEXT_SECONDARY, UITheme.TEXT_SECONDARY, UITheme.TEXT_SECONDARY, UITheme.TEXT_SECONDARY, UITheme.ACCENT_CYAN, UITheme.ACCENT_RED};
        for (int i = 0; i < dayNames.length; i++) {
            JLabel l = new JLabel(dayNames[i], SwingConstants.CENTER);
            l.setForeground(dayColors[i]);
            l.setFont(new Font("Segoe UI", Font.BOLD, 11));
            weekdays.add(l);
        }

        grid.setOpaque(false);
        add(top, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 4));
        middle.setOpaque(false);
        middle.add(weekdays, BorderLayout.NORTH);
        middle.add(grid, BorderLayout.CENTER);
        add(middle, BorderLayout.CENTER);

        buildServicesForMonth();
        render();
    }

    public void setAppointments(List<Service> services) {
        allAppointments = services == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(services);
        buildServicesForMonth();
        render();
    }

    private void buildServicesForMonth() {
        servicesByDay.clear();
        for (Service s : allAppointments) {
            LocalDate d = parseDate(s.getServiceDate());
            if (d == null) continue;
            if (d.getYear() == currentMonth.getYear() && d.getMonthValue() == currentMonth.getMonthValue()) {
                servicesByDay.computeIfAbsent(d.getDayOfMonth(), k -> new java.util.ArrayList<>()).add(s);
            }
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null) return null;
        try { return LocalDate.parse(value.trim()); } catch (Exception e) { return null; }
    }

    private void render() {
        monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
        grid.removeAll();
        LocalDate first = currentMonth.atDay(1);
        int start = first.getDayOfWeek().getValue();
        int days = currentMonth.lengthOfMonth();
        for (int i = 1; i < start; i++) {
            grid.add(emptyCell());
        }
        for (int d = 1; d <= days; d++) {
            grid.add(dayCell(d));
        }
        int fill = (start - 1 + days) % 7;
        if (fill != 0) {
            for (int i = fill; i < 7; i++) {
                grid.add(emptyCell());
            }
        }
        revalidate();
        repaint();
    }

    private JComponent emptyCell() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }

    private JComponent dayCell(int day) {
        java.util.List<Service> list = servicesByDay.get(day);
        LocalDate cellDate = currentMonth.atDay(day);
        boolean isPast = cellDate.isBefore(LocalDate.now());
        boolean isToday = cellDate.equals(LocalDate.now());
        int count = (list == null) ? 0 : list.size();
        boolean isFullyBooked = count >= 5;

        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isToday) {
                    g2.setColor(new Color(UITheme.ACCENT_CYAN.getRed(), UITheme.ACCENT_CYAN.getGreen(), UITheme.ACCENT_CYAN.getBlue(), 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(UITheme.ACCENT_CYAN);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                } else {
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        p.setPreferredSize(new Dimension(44, 50));

        JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
        dayLabel.setFont(new Font("Segoe UI", isToday ? Font.BOLD : Font.PLAIN, 13));

        // Determine color coding
        if (isPast) {
            // Past dates: muted
            p.setBackground(new Color(30, 35, 50));
            dayLabel.setForeground(new Color(80, 90, 110));
            p.setToolTipText("Past date");
        } else if (count >= 8) {
            // High load: red
            p.setBackground(new Color(60, 20, 25));
            dayLabel.setForeground(new Color(255, 100, 100));
            p.setToolTipText("High Load: " + count + " services");
        } else if (count >= 4 && count <= 7) {
            // Medium load: yellow
            p.setBackground(new Color(50, 40, 15));
            dayLabel.setForeground(new Color(255, 190, 50));
            p.setToolTipText("Medium Load: " + count + " services");
        } else if (count >= 1 && count <= 3) {
            // Low load: green
            p.setBackground(new Color(15, 45, 25));
            dayLabel.setForeground(new Color(80, 220, 120));
            p.setToolTipText("Low Load: " + count + " service(s)");
        } else {
            // Empty: neutral
            p.setBackground(new Color(20, 25, 35));
            dayLabel.setForeground(new Color(100, 110, 130));
            p.setToolTipText("No bookings");
        }

        p.add(dayLabel, BorderLayout.CENTER);

        // Appointment count dot indicator
        if (count > 0 && !isPast) {
            JPanel indicator = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Color dotColor = isFullyBooked ? UITheme.ACCENT_RED : (count > 3 ? new Color(255, 140, 50) : UITheme.ACCENT_AMBER);
                    g2.setColor(dotColor);
                    // Draw small count text
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
                    FontMetrics fm = g2.getFontMetrics();
                    String txt = String.valueOf(count);
                    g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2, getHeight() - 1);
                    g2.dispose();
                }
            };
            indicator.setPreferredSize(new Dimension(0, 10));
            indicator.setOpaque(false);
            p.add(indicator, BorderLayout.SOUTH);
        }

        // Click interaction (only for non-past dates)
        if (!isPast) {
            p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final int finalDay = day;
            p.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (count >= 8) {
                        UITheme.showInfoDialog(AppointmentCalendarPanel.this, 
                            "Load Alert", 
                            "Warning: High Load (" + count + " services) on " + currentMonth.getMonth() + " " + finalDay + ".\nNeed extra mechanics!");
                        return;
                    }
                    if (list == null || list.isEmpty()) {
                        UITheme.showInfoDialog(AppointmentCalendarPanel.this, "Calendar Info", "No appointments on " + currentMonth.getMonth() + " " + finalDay + ". Perfect for booking!");
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Appointments for ").append(currentMonth.getMonth()).append(" ").append(finalDay).append(":\n\n");
                    for (Service s : list) {
                        sb.append("\u2022 ").append(s.getServiceType())
                          .append(" (").append(s.getStatus()).append(")\n");
                    }
                    UITheme.showInfoDialog(AppointmentCalendarPanel.this, "Daily Schedule", sb.toString());
                }
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UITheme.ACCENT_CYAN, 1),
                        BorderFactory.createEmptyBorder(1, 1, 1, 1)
                    ));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    p.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                }
            });
        }
        return p;
    }
}
