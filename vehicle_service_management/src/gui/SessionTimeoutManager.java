package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SessionTimeoutManager {
    private static final long TIMEOUT_MS = 10L * 60L * 1000L;
    private static final long WARN_BEFORE_MS = 60L * 1000L;

    private long lastActivity = System.currentTimeMillis();
    private final JFrame frame;
    private final Runnable onTimeout;
    private Timer timer;
    private boolean warningShown = false;
    private final AWTEventListener listener;

    public SessionTimeoutManager(JFrame frame, Runnable onTimeout) {
        this.frame = frame;
        this.onTimeout = onTimeout;
        this.listener = event -> {
            if (event instanceof MouseEvent || event instanceof KeyEvent) {
                lastActivity = System.currentTimeMillis();
                warningShown = false;
            }
        };
    }

    public void start() {
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
        timer = new Timer(1000, e -> checkTimeout());
        timer.start();
    }

    public void stop() {
        if (timer != null) timer.stop();
        Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
    }

    private void checkTimeout() {
        long inactiveMs = System.currentTimeMillis() - lastActivity;
        long remaining = TIMEOUT_MS - inactiveMs;
        if (remaining <= 0) {
            stop();
            SwingUtilities.invokeLater(onTimeout);
            return;
        }
        if (remaining <= WARN_BEFORE_MS && !warningShown) {
            warningShown = true;
            JOptionPane.showMessageDialog(frame, "Session will timeout in 1 minute due to inactivity.", "Session Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
