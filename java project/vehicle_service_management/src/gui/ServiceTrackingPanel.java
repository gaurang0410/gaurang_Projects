package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServiceTrackingPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final String[] steps = {
        "Request Pending",
        "Pickup Assigned",
        "Vehicle Picked Up",
        "Inspection",
        "Waiting for Parts",
        "Service In Progress",
        "Quality Check",
        "Ready for Delivery",
        "Out for Delivery",
        "Service Completed"
    };

    private int currentStage = 0;
    private float pulseAlpha = 0.5f;
    private boolean pulseUp = true;
    private final Timer pulseTimer;
    private final JProgressBar progressBar;

    public ServiceTrackingPanel() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel timelinePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTimeline((Graphics2D) g);
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(350, steps.length * 60 + 40);
            }
        };
        timelinePanel.setOpaque(false);

        JScrollPane scrollPane = UITheme.styledScrollPane(timelinePanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(0, 14));
        progressBar.setStringPainted(true);
        UITheme.styleProgressBar(progressBar);
        add(progressBar, BorderLayout.SOUTH);

        pulseTimer = new Timer(50, e -> {
            if (pulseUp) {
                pulseAlpha += 0.04f;
                if (pulseAlpha >= 0.8f) { pulseAlpha = 0.8f; pulseUp = false; }
            } else {
                pulseAlpha -= 0.04f;
                if (pulseAlpha <= 0.3f) { pulseAlpha = 0.3f; pulseUp = true; }
            }
            timelinePanel.repaint();
        });
        pulseTimer.start();
    }

    private void drawTimeline(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int startX = 40;
        int stepY = 60;
        int circleSize = 24;

        // Draw connecting lines
        for (int i = 0; i < steps.length - 1; i++) {
            int y1 = 30 + i * stepY + circleSize/2;
            int y2 = 30 + (i + 1) * stepY + circleSize/2;
            
            g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (i < currentStage) {
                g2.setColor(UITheme.ACCENT_GREEN);
            } else {
                g2.setColor(UITheme.BORDER_DEFAULT);
                float[] dash = {5.0f};
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f));
            }
            g2.drawLine(startX + circleSize/2, y1, startX + circleSize/2, y2);
        }

        for (int i = 0; i < steps.length; i++) {
            int y = 30 + i * stepY;
            boolean completed = i < currentStage;
            boolean active = i == currentStage;

            if (completed) {
                // Outer ring
                g2.setColor(new Color(UITheme.ACCENT_GREEN.getRed(), UITheme.ACCENT_GREEN.getGreen(), UITheme.ACCENT_GREEN.getBlue(), 40));
                g2.fillOval(startX - 4, y - 4, circleSize + 8, circleSize + 8);
                // Inner circle
                g2.setColor(UITheme.ACCENT_GREEN);
                g2.fillOval(startX, y, circleSize, circleSize);
                // Checkmark
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(startX + 7, y + 12, startX + 11, y + 16);
                g2.drawLine(startX + 11, y + 16, startX + 17, y + 8);
                
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
            } else if (active) {
                // Pulsing Active Glow
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.fillOval(startX - 8, y - 8, circleSize + 16, circleSize + 16);
                
                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.fillOval(startX, y, circleSize, circleSize);
                g2.setColor(Color.WHITE);
                g2.fillOval(startX + 8, y + 8, 8, 8);
                
                g2.setColor(UITheme.ACCENT_CYAN);
                g2.setFont(UITheme.FONT_BODY.deriveFont(Font.BOLD));
            } else {
                // Upcoming
                g2.setColor(UITheme.BORDER_DEFAULT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(startX, y, circleSize, circleSize);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.setFont(UITheme.FONT_BODY);
            }

            g2.drawString(steps[i], startX + circleSize + 20, y + 17);
            
            // Subtle status label for active
            if (active) {
                g2.setFont(UITheme.FONT_TINY.deriveFont(Font.ITALIC));
                g2.drawString("Currently processing...", startX + circleSize + 20, y + 34);
            }
        }
    }

    public void updateByStatus(String status) {
        if (status == null) { updateStage(0); return; }
        String s = status.toLowerCase();
        int stage = 0;
        if (s.contains("pending"))           stage = 0;
        else if (s.contains("pickup assigned"))      stage = 1;
        else if (s.contains("picked up"))            stage = 2;
        else if (s.contains("inspection"))           stage = 3;
        else if (s.contains("waiting") || s.contains("parts")) stage = 4;
        else if (s.contains("in progress"))          stage = 5;
        else if (s.contains("quality"))              stage = 6;
        else if (s.contains("ready"))                stage = 7;
        else if (s.contains("out for delivery"))     stage = 8;
        else if (s.contains("completed") || s.contains("delivered")) stage = 9;
        updateStage(stage);
    }

    public void updateStage(int stage) {
        this.currentStage = Math.min(steps.length - 1, Math.max(0, stage));
        int percent = (int)((currentStage + 1) * 100.0 / steps.length);
        if (stage >= steps.length) percent = 100;
        
        progressBar.setValue(percent);
        progressBar.setString(percent + "% Complete");
        
        if (percent >= 100) progressBar.setForeground(UITheme.ACCENT_GREEN);
        else if (percent >= 50) progressBar.setForeground(UITheme.ACCENT_CYAN);
        else progressBar.setForeground(UITheme.ACCENT_AMBER);
        
        repaint();
    }
}
