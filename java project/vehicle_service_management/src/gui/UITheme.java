package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Premium design system for VehicleFlow — Professional Dark/Light Theme
 * Optimized for professional dynamic theming and high readability.
 */
public final class UITheme {

    private UITheme() {}

    // ─── Color Palette (Dynamic) ──────────────────────────────────────────────
    public static Color BG_DARK        = new Color(8, 12, 22);
    public static Color BG_CARD        = new Color(18, 24, 40);
    public static Color BG_CARD_HOVER  = new Color(26, 34, 55);
    public static Color BG_INPUT       = new Color(22, 32, 52);
    public static Color TEXT_INPUT     = new Color(240, 245, 255);

    public static Color ACCENT_CYAN    = new Color(0, 200, 220);
    public static Color ACCENT_GREEN   = new Color(20, 210, 100);
    public static Color ACCENT_AMBER   = new Color(250, 170, 20);
    public static Color ACCENT_RED     = new Color(245, 60, 75);
    public static Color ACCENT_PURPLE  = new Color(150, 100, 255);
    public static Color ACCENT_BLUE    = new Color(50, 140, 255);

    public static Color TEXT_PRIMARY   = new Color(240, 245, 255);
    public static Color TEXT_SECONDARY = new Color(140, 160, 190);
    public static Color TEXT_MUTED     = new Color(80, 100, 130);

    public static Color BORDER_DEFAULT = new Color(35, 50, 80);
    public static Color BORDER_FOCUS   = new Color(0, 200, 220);

    // Light Mode Reference Colors
    public static final Color LIGHT_BG_DARK        = new Color(242, 246, 255);
    public static final Color LIGHT_BG_CARD        = new Color(255, 255, 255);
    public static final Color LIGHT_BG_CARD_HOVER  = new Color(240, 245, 255);
    public static final Color LIGHT_BG_INPUT       = new Color(255, 255, 255);
    public static final Color LIGHT_TEXT_PRIMARY   = new Color(15, 20, 40);
    public static final Color LIGHT_TEXT_SECONDARY = new Color(70, 85, 110);
    public static final Color LIGHT_TEXT_MUTED     = new Color(130, 145, 170);
    public static final Color LIGHT_BORDER_DEFAULT = new Color(200, 215, 235);

    private static boolean darkMode = true;
    private static final File THEME_PREF_FILE = new File("theme.pref");

    static {
        Boolean saved = loadThemePreference();
        if (saved != null) darkMode = saved;
        updateThemeColors(darkMode);
    }

    // ─── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_TINY     = new Font("Segoe UI", Font.PLAIN, 11);

    private static final NumberFormat INR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    // ─── Icon System ─────────────────────────────────────────────────────────
    public static Icon getIcon(String name, Color color, int size) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int s = size, cx = x + s/2, cy = y + s/2;
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                switch (name.toLowerCase()) {
                    case "user":
                        g2.drawOval(cx - s/5, cy - s/3, 2*s/5, 2*s/5);
                        g2.drawArc(x + s/6, cy + 2, 2*s/3, s/2, 0, 180);
                        break;
                    case "lock":
                        g2.drawRoundRect(x + s/4, cy - s/8, s/2, s/2, 4, 4);
                        g2.drawArc(x + s/3, y + s/6, s/3, s/2, 0, 180);
                        break;
                    case "login":
                        g2.drawLine(x + s/5, cy, x + 4*s/5, cy);
                        g2.drawLine(x + 3*s/5, cy - s/5, x + 4*s/5, cy);
                        g2.drawLine(x + 3*s/5, cy + s/5, x + 4*s/5, cy);
                        g2.drawArc(x + s/8, y + s/8, 3*s/4, 3*s/4, 45, 270);
                        break;
                    case "logout":
                        g2.drawLine(x + 3*s/5, cy, x + s/8, cy);
                        g2.drawLine(x + s/4, cy - s/5, x + s/8, cy);
                        g2.drawLine(x + s/4, cy + s/5, x + s/8, cy);
                        g2.drawArc(x + s/4, y + s/8, 3*s/5, 3*s/4, -135, 270);
                        break;
                    case "vehicle":
                        g2.drawRoundRect(x + s/10, cy - s/6, 4*s/5, s/3, 6, 6);
                        g2.drawRect(x + s/4, y + s/5, s/2, s/3);
                        g2.drawOval(x + s/5, cy + s/6, s/6, s/6);
                        g2.drawOval(x + 3*s/5, cy + s/6, s/6, s/6);
                        break;
                    case "service":
                    case "settings":
                        g2.drawOval(cx - s/6, cy - s/6, s/3, s/3);
                        for (int i = 0; i < 8; i++) {
                            double ang = Math.toRadians(i * 45);
                            int x1 = (int)(cx + Math.cos(ang) * s / 4);
                            int y1 = (int)(cy + Math.sin(ang) * s / 4);
                            int x2 = (int)(cx + Math.cos(ang) * s * 0.42);
                            int y2 = (int)(cy + Math.sin(ang) * s * 0.42);
                            g2.drawLine(x1, y1, x2, y2);
                        }
                        break;
                    case "billing":
                        g2.drawRoundRect(x + s/6, y + s/10, 2*s/3, 4*s/5, 4, 4);
                        g2.drawLine(x + s/4, y + s/3, x + 3*s/4, y + s/3);
                        g2.drawLine(x + s/4, cy, x + 3*s/4, cy);
                        g2.drawLine(x + s/4, y + 2*s/3, x + 3*s/4, y + 2*s/3);
                        break;
                    case "mechanic":
                        g2.drawOval(cx - s/5, cy - s/3, 2*s/5, 2*s/5);
                        g2.drawLine(cx, cy - 2, cx, cy + 5*s/12);
                        g2.drawLine(cx - s/4, cy + s/6, cx + s/4, cy + s/6);
                        break;
                    case "notification":
                        g2.drawArc(x + s/4, y + s/6, s/2, s/2, 0, 180);
                        g2.drawLine(x + s/4, cy + 2, x + 3*s/4, cy + 2);
                        g2.drawOval(cx - 2, cy + 4, 4, 4);
                        break;
                    case "feedback":
                        g2.drawRoundRect(x + s/8, y + s/8, 3*s/4, s/2, 6, 6);
                        g2.drawLine(x + s/4, cy + s/8, x + s/6, cy + s/3);
                        break;
                    case "charts":
                        g2.drawLine(x + s/8, y + s/8, x + s/8, y + 7*s/8);
                        g2.drawLine(x + s/8, y + 7*s/8, x + 7*s/8, y + 7*s/8);
                        g2.drawRect(x + s/4, cy, s/6, s/4);
                        g2.drawRect(x + s/2, cy - s/6, s/6, 5*s/12);
                        break;
                    case "reports":
                        g2.drawRoundRect(x + s/5, y + s/8, 3*s/5, 3*s/4, 6, 6);
                        g2.drawLine(x + s/3, y + s/3, x + 2*s/3, y + s/3);
                        g2.drawLine(x + s/3, cy, x + 2*s/3, cy);
                        g2.drawLine(x + s/3, y + 2*s/3, x + 2*s/3, y + 2*s/3);
                        break;
                    case "dashboard":
                        int gs = s/5;
                        g2.fillRoundRect(x + s/6, y + s/6, gs*2, gs*2, 3, 3);
                        g2.fillRoundRect(x + s/2 + 1, y + s/6, gs*2, gs*2, 3, 3);
                        g2.fillRoundRect(x + s/6, y + s/2 + 1, gs*2, gs*2, 3, 3);
                        g2.fillRoundRect(x + s/2 + 1, y + s/2 + 1, gs*2, gs*2, 3, 3);
                        break;
                    case "calendar":
                        g2.drawRoundRect(x + s/8, y + s/5, 3*s/4, 3*s/5, 4, 4);
                        g2.drawLine(x + s/8, y + s/3 + 2, x + 7*s/8, y + s/3 + 2);
                        g2.drawLine(x + s/3, y + s/8, x + s/3, y + s/4);
                        g2.drawLine(x + 2*s/3, y + s/8, x + 2*s/3, y + s/4);
                        g2.fillOval(x + s/4, cy + 2, 3, 3);
                        g2.fillOval(cx - 1, cy + 2, 3, 3);
                        g2.fillOval(x + 5*s/8, cy + 2, 3, 3);
                        break;
                    case "inventory":
                        g2.drawRoundRect(x + s/8, y + s/4, 3*s/4, 3*s/5, 4, 4);
                        g2.drawLine(x + s/3, y + s/6, x + 2*s/3, y + s/6);
                        g2.drawLine(x + s/3, y + s/6, x + s/3, y + s/4);
                        g2.drawLine(x + 2*s/3, y + s/6, x + 2*s/3, y + s/4);
                        break;
                    case "customer":
                        g2.drawOval(cx - s/5, cy - s/3, 2*s/5, 2*s/5);
                        g2.drawArc(x + s/6, cy + 2, 2*s/3, s/2, 0, 180);
                        break;
                    default:
                        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, size - 2));
                        g2.drawString("?", x + size/3, y + 2*size/3);
                }
                g2.dispose();
            }
            @Override public int getIconWidth()  { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }

    // ─── Background Utilities ─────────────────────────────────────────────────
    public static class GradientPanel extends JPanel {
        private Color from, to;
        public GradientPanel(Color from, Color to) { this.from = from; this.to = to; setOpaque(false); }
        public void setColors(Color from, Color to) { this.from = from; this.to = to; repaint(); }
        public Color getFrom() { return from; }
        public Color getTo() { return to; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, from, getWidth(), getHeight(), to));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static JPanel gradientPanel(Color from, Color to) {
        return new GradientPanel(from, to);
    }

    public static class BrandPanel extends GradientPanel {
        public BrandPanel(Color from, Color to) { super(from, to); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            g2.setColor(Color.WHITE);
            g2.fillOval(-80, -80, 300, 300);
            g2.fillOval(getWidth() - 160, getHeight() - 180, 280, 280);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
            for (int dx = 0; dx <= getWidth(); dx += 28) {
                for (int dy = 0; dy <= getHeight(); dy += 28) {
                    g2.fillOval(dx - 1, dy - 1, 3, 3);
                }
            }
            g2.dispose();
        }
    }

    public static JPanel brandPanel(Color from, Color to) {
        return new BrandPanel(from, to);
    }

    // ─── Buttons ──────────────────────────────────────────────────────────────
    public static JButton accentButton(String text, Color color) { return accentButton(text, color, null); }
    public static JButton accentButton(String text, Color color, Icon icon) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                boolean pressed = getModel().isPressed(), hover = getModel().isRollover();
                if (pressed) { g2.setColor(color.darker()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); }
                else if (hover) { g2.setPaint(new GradientPaint(0, 0, color.brighter(), getWidth(), getHeight(), color)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); }
                else { g2.setPaint(new GradientPaint(0, 0, color, getWidth(), getHeight(), color.darker())); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); }
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int totalW = fm.stringWidth(getText()), iconGap = 10;
                if (getIcon() != null) totalW += getIcon().getIconWidth() + iconGap;
                int startX = (getWidth() - totalW) / 2, textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                if (getIcon() != null) { getIcon().paintIcon(this, g2, startX, (getHeight() - getIcon().getIconHeight()) / 2); startX += getIcon().getIconWidth() + iconGap; }
                g2.drawString(getText(), startX, textY);
            }
        };
        btn.setFont(FONT_BUTTON); btn.setForeground(Color.WHITE); btn.setIcon(icon);
        btn.setBorderPainted(false); btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 46));
        return btn;
    }

    public static JButton ghostButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isPressed()) { g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12); }
                g2.setColor(color); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
                g2.setFont(getFont()); FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText()))/2, (getHeight() + fm.getAscent() - fm.getDescent())/2);
            }
        };
        btn.setFont(FONT_BUTTON); btn.setForeground(color); btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 46));
        return btn;
    }

    // ─── Inputs ───────────────────────────────────────────────────────────────
    // Standard input height used across all form components
    public static final int INPUT_HEIGHT = 42;
    public static final int INPUT_MIN_WIDTH = 180;

    public static void styleInput(JComponent comp) {
        if (comp == null) return;
        if (comp instanceof JTextComponent) {
            JTextComponent tc = (JTextComponent) comp;
            // Always set explicit colors — never inherit from L&F
            tc.setBackground(BG_INPUT);
            tc.setForeground(TEXT_INPUT);
            tc.setCaretColor(ACCENT_CYAN);
            tc.setFont(FONT_BODY);
            tc.setOpaque(true);
            tc.putClientProperty("caretAspectRatio", 0.1f);
            // Enforce uniform height
            Dimension pref = tc.getPreferredSize();
            tc.setPreferredSize(new Dimension(Math.max(pref.width, INPUT_MIN_WIDTH), INPUT_HEIGHT));
        } else if (comp instanceof JComboBox) {
            @SuppressWarnings("unchecked") JComboBox<Object> combo = (JComboBox<Object>) comp;
            // Capture current theme colors in locals so lambda closures capture the right values
            final Color bgInput   = BG_INPUT;
            final Color textInput = TEXT_INPUT;
            final Color accentC   = ACCENT_CYAN;

            combo.setBackground(bgInput);
            combo.setForeground(textInput);
            combo.setFont(FONT_BODY);
            combo.setOpaque(true);
            combo.setLightWeightPopupEnabled(false);
            // Enforce uniform height
            combo.setPreferredSize(new Dimension(INPUT_MIN_WIDTH, INPUT_HEIGHT));
            combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));

            try {
                combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
                    @Override protected JButton createArrowButton() {
                        JButton btn = new JButton("▼");
                        btn.setBackground(bgInput);
                        btn.setForeground(textInput);
                        btn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        btn.setOpaque(true);
                        btn.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 6));
                        btn.setContentAreaFilled(false);
                        return btn;
                    }
                    @Override public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean hasFocus) {
                        // Always fill with our bg color so text is readable
                        g.setColor(bgInput);
                        g.fillRect(r.x, r.y, r.width, r.height);
                    }
                    @Override public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                        // Override to ensure text is painted in the correct foreground color
                        ListCellRenderer<Object> renderer = comboBox.getRenderer();
                        Component c = renderer.getListCellRendererComponent(
                            listBox, comboBox.getSelectedItem(), -1, false, false);
                        c.setFont(comboBox.getFont());
                        c.setBackground(bgInput);
                        c.setForeground(textInput);
                        if (c instanceof JComponent) {
                            ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
                        }
                        currentValuePane.paintComponent(g, c, comboBox,
                            bounds.x, bounds.y, bounds.width, bounds.height, c instanceof JPanel);
                    }
                });
            } catch (Exception ignored) {}

            combo.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int idx, boolean sel, boolean focus) {
                    // Force list colors before calling super
                    list.setBackground(bgInput);
                    list.setForeground(textInput);
                    list.setSelectionBackground(accentC);
                    list.setSelectionForeground(Color.WHITE);
                    JLabel c = (JLabel) super.getListCellRendererComponent(list, value, idx, sel, focus);
                    c.setBackground(sel ? accentC : bgInput);
                    c.setForeground(sel ? Color.WHITE : textInput);
                    c.setFont(FONT_BODY);
                    c.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                    c.setOpaque(true);
                    return c;
                }
            });

            // Fix the editable combo editor component colors
            Component editor = combo.getEditor().getEditorComponent();
            if (editor instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) editor;
                tc.setBackground(bgInput);
                tc.setForeground(textInput);
                tc.setOpaque(true);
                tc.setCaretColor(accentC);
            } else if (editor != null) {
                editor.setBackground(bgInput);
                editor.setForeground(textInput);
            }
        }
    }

    public static JTextField styledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                // Always paint our own background first (avoids white flash on LAF changes)
                g.setColor(BG_INPUT);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    // Placeholder is always TEXT_MUTED (visible on both themes)
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets(); FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, ins.left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        styleInput(field);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_DEFAULT, 1),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        // Enforce uniform height
        field.setPreferredSize(new Dimension(INPUT_MIN_WIDTH, INPUT_HEIGHT));
        attachFocusBorder(field);
        return field;
    }

    public static JPasswordField styledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    Insets ins = getInsets(); FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, ins.left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g2.dispose();
                }
            }
        };
        styleInput(field);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_DEFAULT, 1), BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        attachFocusBorder(field); return field;
    }
    public static JPasswordField styledPasswordField() { return styledPasswordField("Enter password"); }

    private static void attachFocusBorder(JComponent f) {
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT_CYAN, 2), BorderFactory.createEmptyBorder(9, 13, 9, 13))); }
            public void focusLost(java.awt.event.FocusEvent e) { f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_DEFAULT, 1), BorderFactory.createEmptyBorder(10, 14, 10, 14))); }
        });
    }

    // ─── Labels ───────────────────────────────────────────────────────────────
    public static JLabel styledLabel(String text) { return styledLabel(text, FONT_HEADING, TEXT_PRIMARY); }
    public static JLabel styledLabel(String text, Font font, Color color) { JLabel l = new JLabel(text); l.setFont(font); l.setForeground(color); return l; }
    public static JLabel bodyLabel(String text) { return styledLabel(text, FONT_BODY, TEXT_SECONDARY); }
    public static JLabel headingLabel(String text) { return styledLabel(text, FONT_HEADING, TEXT_PRIMARY); }

    public static JComboBox<String> styledComboBox() { return styledComboBox(new String[0]); }
    public static JComboBox<String> styledComboBox(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setOpaque(true);
        c.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_DEFAULT, 1),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        c.setFocusable(true);
        styleInput(c);
        // Enforce uniform size
        c.setPreferredSize(new Dimension(INPUT_MIN_WIDTH, INPUT_HEIGHT));
        return c;
    }

    public static void styleProgressBar(JProgressBar bar) {
        bar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            @Override protected void paintDeterminate(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth(), h = c.getHeight(), progress = (int)(w * bar.getPercentComplete());
                g2.setColor(BG_INPUT); g2.fillRoundRect(0, 0, w, h, h, h);
                if (progress > 0) { Color accent = bar.getForeground(); g2.setPaint(new GradientPaint(0, 0, accent, progress, 0, accent.darker())); g2.fillRoundRect(0, 0, progress, h, h, h); }
                if (bar.isStringPainted()) { g2.setColor(TEXT_PRIMARY); g2.setFont(FONT_TINY.deriveFont(Font.BOLD)); String s = bar.getString(); FontMetrics fm = g2.getFontMetrics(); g2.drawString(s, (w - fm.stringWidth(s))/2, (h + fm.getAscent() - fm.getDescent())/2); }
                g2.dispose();
            }
        });
        bar.setBorder(BorderFactory.createEmptyBorder()); bar.setBackground(BG_INPUT);
    }

    // ─── Premium Card Components ──────────────────────────────────────────────
    public static JPanel accentCard(Color topAccent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(topAccent); g2.fillRoundRect(0, 0, getWidth(), 4, 4, 4); g2.fillRect(0, 2, getWidth(), 2);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BORDER_DEFAULT); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        card.setBackground(BG_CARD); card.setOpaque(false); return card;
    }

    public static JPanel metricCard(String title, String value, Color accent) {
        JPanel card = accentCard(accent); card.setLayout(new BorderLayout(0, 8)); card.setBorder(new EmptyBorder(20, 18, 16, 18));
        JLabel valLabel = new JLabel(value == null || value.isBlank() ? "—" : value);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); valLabel.setForeground(TEXT_PRIMARY);
        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); titleLabel.setForeground(accent);
        JPanel content = new JPanel(new BorderLayout(0, 4)); content.setOpaque(false); content.add(valLabel, BorderLayout.CENTER); content.add(titleLabel, BorderLayout.SOUTH);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35)); g2.fillOval(0, 0, 34, 34);
                g2.setColor(accent); g2.fillOval(8, 8, 18, 18); g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(34, 34); }
        };
        dot.setOpaque(false); JPanel top = new JPanel(new BorderLayout(8, 0)); top.setOpaque(false); top.add(content, BorderLayout.CENTER); top.add(dot, BorderLayout.EAST);
        card.add(top, BorderLayout.CENTER); return card;
    }

    public static JButton sidebarNavButton(String text, Color accentColor) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                boolean active = Boolean.TRUE.equals(getClientProperty("active")), hover = getModel().isRollover();
                if (active) {
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 25)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(accentColor); g2.fillRoundRect(0, 8, 3, getHeight() - 16, 3, 3);
                    g2.setColor(TEXT_PRIMARY);
                } else if (hover) {
                    g2.setColor(new Color(255, 255, 255, 10)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(TEXT_SECONDARY);
                } else { g2.setColor(TEXT_MUTED); }
                g2.setFont(getFont()); FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 16, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BODY); btn.setHorizontalAlignment(SwingConstants.LEFT); btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44)); return btn;
    }

    // ─── Table Styling ────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD); table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 80));
        table.setSelectionForeground(TEXT_PRIMARY); table.setGridColor(BORDER_DEFAULT);
        table.setRowHeight(46); table.setFont(FONT_BODY); table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false); table.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader header = table.getTableHeader(); header.setOpaque(true);
        header.setBackground(darkMode ? new Color(12, 20, 38) : new Color(232, 240, 255));
        header.setForeground(darkMode ? ACCENT_CYAN : new Color(40, 70, 140));
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, darkMode ? ACCENT_CYAN : new Color(160, 200, 235)));
        header.setPreferredSize(new Dimension(0, 42));
        DefaultTableCellRenderer hdr = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(darkMode ? new Color(12, 20, 38) : new Color(232, 240, 255));
                c.setForeground(darkMode ? ACCENT_CYAN : new Color(40, 70, 140));
                c.setFont(new Font("Segoe UI", Font.BOLD, 12)); ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                ((JComponent) c).setOpaque(true); return c;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setHeaderRenderer(hdr);
        DefaultTableCellRenderer row = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int r, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, r, col);
                Integer hover = (Integer) t.getClientProperty("hover-row");
                if (!sel) {
                    if (hover != null && hover == r) { c.setBackground(darkMode ? new Color(28, 48, 78) : new Color(218, 230, 252)); }
                    else { c.setBackground(r % 2 == 0 ? BG_CARD : (darkMode ? new Color(12, 18, 32) : new Color(245, 249, 255))); }
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };
        row.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(row);
    }

    public static JScrollPane styledScrollPane(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        applyScrollPaneStyle(sp); return sp;
    }

    public static void applyScrollPaneStyle(JScrollPane sp) {
        sp.setBackground(BG_DARK); sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_DEFAULT));
        sp.getVerticalScrollBar().setUnitIncrement(16); sp.setWheelScrollingEnabled(true);
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 100); this.trackColor = BG_DARK; }
            @Override protected JButton createDecreaseButton(int orientation) { return invisibleBtn(); }
            @Override protected JButton createIncreaseButton(int orientation) { return invisibleBtn(); }
            private JButton invisibleBtn() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty()) return; Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor); g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8); g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { g.setColor(trackColor); g.fillRect(r.x, r.y, r.width, r.height); }
        });
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        sp.getHorizontalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 100); this.trackColor = BG_DARK; }
            @Override protected JButton createDecreaseButton(int orientation) { return invisibleBtn(); }
            @Override protected JButton createIncreaseButton(int orientation) { return invisibleBtn(); }
            private JButton invisibleBtn() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty()) return; Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor); g2.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8); g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { g.setColor(trackColor); g.fillRect(r.x, r.y, r.width, r.height); }
        });
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
    }

    public static JTabbedPane styledTabbedPane() {
        JTabbedPane tp = new JTabbedPane();
        styleTabbedPane(tp);
        return tp;
    }

    public static void styleTabbedPane(JTabbedPane tp) {
        tp.setBackground(BG_CARD); tp.setForeground(TEXT_PRIMARY); tp.setFont(FONT_BODY); tp.setOpaque(true);
        tp.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void installDefaults() {
                super.installDefaults();
                tabInsets = new Insets(10, 20, 10, 20);
                selectedTabPadInsets = new Insets(2, 2, 2, 2);
                tabAreaInsets = new Insets(4, 8, 0, 8);
                contentBorderInsets = new Insets(8, 0, 0, 0);
                highlight = BG_CARD; lightHighlight = BG_CARD;
                shadow = BORDER_DEFAULT; darkShadow = BORDER_DEFAULT;
                focus = ACCENT_CYAN;
            }
            @Override protected void paintTabBackground(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (sel) {
                    g2.setColor(new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 35));
                    g2.fillRoundRect(x + 1, y + 1, w - 2, h, 10, 10);
                    g2.setColor(ACCENT_CYAN); g2.fillRect(x + 6, y + h - 3, w - 12, 3);
                } else {
                    g2.setColor(BG_CARD); g2.fillRoundRect(x + 1, y + 1, w - 2, h, 10, 10);
                }
                g2.dispose();
            }
            @Override protected void paintTabBorder(Graphics g, int tp, int idx, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(sel ? ACCENT_CYAN : BORDER_DEFAULT); g2.drawRoundRect(x + 1, y + 1, w - 3, h - 1, 10, 10);
                g2.dispose();
            }
            @Override protected void paintContentBorder(Graphics g, int tp, int si) {}
            @Override protected void paintFocusIndicator(Graphics g, int tp, Rectangle[] r, int idx, Rectangle ir, Rectangle tr, boolean sel) {}
        });
        for (int i = 0; i < tp.getTabCount(); i++) tp.setForegroundAt(i, TEXT_PRIMARY);
    }

    // ─── Theme Management ─────────────────────────────────────────────────────
    public static boolean isDarkMode() { return darkMode; }

    public static void updateThemeColors(boolean dark) {
        if (dark) {
            BG_DARK = new Color(8, 12, 22); BG_CARD = new Color(18, 24, 40); BG_CARD_HOVER = new Color(26, 34, 55);
            BG_INPUT = new Color(22, 32, 52); TEXT_INPUT = new Color(240, 245, 255);
            TEXT_PRIMARY = new Color(240, 245, 255); TEXT_SECONDARY = new Color(140, 160, 190); TEXT_MUTED = new Color(80, 100, 130);
            BORDER_DEFAULT = new Color(35, 50, 80); ACCENT_CYAN = new Color(0, 200, 220); ACCENT_GREEN = new Color(20, 210, 100);
        } else {
            BG_DARK = LIGHT_BG_DARK; BG_CARD = LIGHT_BG_CARD; BG_CARD_HOVER = LIGHT_BG_CARD_HOVER;
            BG_INPUT = LIGHT_BG_INPUT; TEXT_INPUT = LIGHT_TEXT_PRIMARY;
            TEXT_PRIMARY = LIGHT_TEXT_PRIMARY; TEXT_SECONDARY = LIGHT_TEXT_SECONDARY; TEXT_MUTED = LIGHT_TEXT_MUTED;
            BORDER_DEFAULT = LIGHT_BORDER_DEFAULT; ACCENT_CYAN = new Color(0, 140, 170); ACCENT_GREEN = new Color(10, 160, 80);
        }
        setUIManagerDefaults(dark);
    }

    private static void setUIManagerDefaults(boolean dark) {
        UIManager.put("Panel.background", BG_DARK); UIManager.put("Panel.foreground", TEXT_PRIMARY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", BG_INPUT); UIManager.put("TextField.foreground", TEXT_INPUT);
        UIManager.put("TextField.caretForeground", ACCENT_CYAN);
        UIManager.put("PasswordField.background", BG_INPUT); UIManager.put("PasswordField.foreground", TEXT_INPUT);
        UIManager.put("ComboBox.background", BG_INPUT); UIManager.put("ComboBox.foreground", TEXT_INPUT);
        UIManager.put("Table.background", BG_CARD); UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER_DEFAULT);
        UIManager.put("TableHeader.background", dark ? new Color(12, 20, 38) : new Color(232, 240, 255));
        UIManager.put("TableHeader.foreground", dark ? ACCENT_CYAN : new Color(40, 70, 140));
        UIManager.put("ScrollPane.background", BG_DARK); UIManager.put("Viewport.background", BG_DARK);
        UIManager.put("TabbedPane.background", BG_CARD); UIManager.put("TabbedPane.foreground", TEXT_PRIMARY);
        UIManager.put("List.background", BG_INPUT); UIManager.put("List.foreground", TEXT_INPUT);
        UIManager.put("List.selectionBackground", ACCENT_CYAN); UIManager.put("List.selectionForeground", Color.WHITE);
        
        // OptionPane Styling
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("OptionPane.messageFont", FONT_BODY);
        UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
        UIManager.put("Button.background", ACCENT_CYAN);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", FONT_BUTTON);
        
        // Popup & Menu Styling
        UIManager.put("PopupMenu.background", BG_CARD);
        UIManager.put("PopupMenu.foreground", TEXT_PRIMARY);
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDER_DEFAULT, 1));
        
        UIManager.put("MenuItem.background", BG_CARD);
        UIManager.put("MenuItem.foreground", TEXT_PRIMARY);
        UIManager.put("MenuItem.selectionBackground", new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 80));
        UIManager.put("MenuItem.selectionForeground", dark ? Color.WHITE : Color.BLACK);
        UIManager.put("MenuItem.borderPainted", false);
        UIManager.put("MenuItem.font", FONT_BODY);

        UIManager.put("Menu.background", BG_CARD);
        UIManager.put("Menu.foreground", TEXT_PRIMARY);
        UIManager.put("Menu.selectionBackground", new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 80));
        UIManager.put("Menu.selectionForeground", dark ? Color.WHITE : Color.BLACK);
        UIManager.put("Menu.font", FONT_BODY);
    }

    public static void styleMenu(Component comp) {
        if (comp == null) return;
        comp.setBackground(BG_CARD); comp.setForeground(TEXT_PRIMARY);
        if (comp instanceof JPopupMenu) {
            JPopupMenu pm = (JPopupMenu) comp;
            pm.setOpaque(true);
            pm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_DEFAULT, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
            ));
            for (Component c : pm.getComponents()) styleMenu(c);
        } else if (comp instanceof JMenu) {
            JMenu m = (JMenu) comp;
            styleMenuItem(m);
            for (Component c : m.getMenuComponents()) styleMenu(c);
        } else if (comp instanceof JMenuItem) {
            styleMenuItem((JMenuItem) comp);
        }
    }

    private static void styleMenuItem(JMenuItem mi) {
        mi.setOpaque(true);
        mi.setBackground(BG_CARD);
        mi.setForeground(TEXT_PRIMARY);
        mi.setFont(FONT_BODY);
        mi.setIconTextGap(14);
        mi.setMargin(new Insets(8, 12, 8, 12));
        mi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        mi.setUI(new javax.swing.plaf.basic.BasicMenuItemUI() {
            @Override
            protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(menuItem.getBackground());
                g2.fillRect(0, 0, menuItem.getWidth(), menuItem.getHeight());
                if (menuItem.getModel().isArmed() || menuItem.getModel().isSelected()) {
                    g2.setColor(new Color(ACCENT_CYAN.getRed(), ACCENT_CYAN.getGreen(), ACCENT_CYAN.getBlue(), 60));
                    g2.fillRoundRect(4, 2, menuItem.getWidth() - 8, menuItem.getHeight() - 4, 8, 8);
                }
                g2.dispose();
            }
            @Override
            protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                boolean active = menuItem.getModel().isArmed() || menuItem.getModel().isSelected();
                if (active) {
                    g2.setColor(darkMode ? Color.WHITE : Color.BLACK);
                    g2.setFont(menuItem.getFont().deriveFont(Font.BOLD));
                } else {
                    g2.setColor(TEXT_PRIMARY);
                    g2.setFont(menuItem.getFont());
                }
                
                FontMetrics fm = g2.getFontMetrics();
                int y = textRect.y + fm.getAscent();
                g2.drawString(text, textRect.x, y);
                g2.dispose();
            }
        });
    }

    public static Icon getStatusIcon(String status) {
        String s = status.toLowerCase();
        Color c;
        String iconName;
        if (s.contains("completed")) { c = ACCENT_GREEN; iconName = "✔"; }
        else if (s.contains("in progress")) { c = ACCENT_BLUE; iconName = "🔧"; }
        else if (s.contains("waiting")) { c = ACCENT_AMBER; iconName = "🟠"; }
        else if (s.contains("quality")) { c = ACCENT_PURPLE; iconName = "🧪"; }
        else if (s.contains("ready")) { c = ACCENT_CYAN; iconName = "✅"; }
        else if (s.contains("delivery")) { c = new Color(20, 184, 166); iconName = "🚗"; }
        else if (s.contains("pickup assigned")) { c = ACCENT_BLUE; iconName = "🚚"; }
        else if (s.contains("picked up")) { c = ACCENT_BLUE; iconName = "📦"; }
        else if (s.contains("inspection")) { c = ACCENT_PURPLE; iconName = "🔍"; }
        else { c = ACCENT_AMBER; iconName = "🟡"; }
        
        return new Icon() {
            @Override public void paintIcon(Component comp, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
                g2.drawString(iconName, x, y + 14);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 20; }
            @Override public int getIconHeight() { return 18; }
        };
    }

    public static void applyGlobalTheme(boolean dark) {
        Color oBgDark = BG_DARK, oBgCard = BG_CARD, oBgCardHover = BG_CARD_HOVER, oBgInput = BG_INPUT;
        Color oTextPrimary = TEXT_PRIMARY, oTextSecondary = TEXT_SECONDARY, oTextMuted = TEXT_MUTED;
        Color oAccentCyan = ACCENT_CYAN, oAccentGreen = ACCENT_GREEN;

        darkMode = dark; saveThemePreference(dark); updateThemeColors(dark);
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
            translateTheme(window, oBgDark, oBgCard, oBgCardHover, oBgInput, oTextPrimary, oTextSecondary, oTextMuted, oAccentCyan, oAccentGreen);
            window.repaint();
        }
    }

    private static void translateTheme(Component comp, Color oBgDark, Color oBgCard, Color oBgCardHover, Color oBgInput,
                                       Color oTextPrimary, Color oTextSecondary, Color oTextMuted, Color oAccentCyan, Color oAccentGreen) {
        if (comp == null) return;
        Color bg = comp.getBackground(), fg = comp.getForeground();
        
        // Background translation (including common hardcoded dark colors)
        if (bg != null) {
            if (bg.equals(oBgDark) || isColorClose(bg, new Color(10, 16, 30)) || isColorClose(bg, new Color(8, 13, 26))) 
                comp.setBackground(BG_DARK);
            else if (bg.equals(oBgCard) || isColorClose(bg, new Color(15, 23, 42))) 
                comp.setBackground(BG_CARD);
            else if (bg.equals(oBgCardHover) || isColorClose(bg, new Color(30, 41, 59))) 
                comp.setBackground(BG_CARD_HOVER);
            else if (bg.equals(oBgInput)) 
                comp.setBackground(BG_INPUT);
        }
        
        // Foreground translation
        if (fg != null) {
            if (fg.equals(oTextPrimary) || isColorClose(fg, Color.WHITE)) 
                comp.setForeground(TEXT_PRIMARY);
            else if (fg.equals(oTextSecondary) || isColorClose(fg, new Color(148, 163, 184))) 
                comp.setForeground(TEXT_SECONDARY);
            else if (fg.equals(oTextMuted) || isColorClose(fg, new Color(71, 85, 105))) 
                comp.setForeground(TEXT_MUTED);
            else if (fg.equals(oAccentCyan)) 
                comp.setForeground(ACCENT_CYAN);
            else if (fg.equals(oAccentGreen)) 
                comp.setForeground(ACCENT_GREEN);
        }

        if (comp instanceof JTextComponent || comp instanceof JComboBox) {
            styleInput((JComponent) comp);
            if (comp instanceof JTextComponent) {
                JTextComponent tc = (JTextComponent) comp;
                if (!tc.isFocusOwner()) {
                    tc.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_DEFAULT, 1), BorderFactory.createEmptyBorder(10, 14, 10, 14)));
                }
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_DEFAULT, 1), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            }
        }
        else if (comp instanceof JTable) styleTable((JTable) comp);
        else if (comp instanceof JScrollPane) applyScrollPaneStyle((JScrollPane) comp);
        else if (comp instanceof JTabbedPane) styleTabbedPane((JTabbedPane) comp);
        else if (comp instanceof JPopupMenu) {
            comp.setBackground(BG_CARD); comp.setForeground(TEXT_PRIMARY);
            ((JComponent)comp).setOpaque(true);
            ((JPopupMenu)comp).setBorder(BorderFactory.createLineBorder(BORDER_DEFAULT));
        }
        else if (comp instanceof JMenuItem) {
            comp.setBackground(BG_CARD); comp.setForeground(TEXT_PRIMARY);
            ((JComponent)comp).setOpaque(true);
            if (comp instanceof JMenu) {
                JMenu menu = (JMenu) comp;
                for (Component item : menu.getMenuComponents()) translateTheme(item, oBgDark, oBgCard, oBgCardHover, oBgInput, oTextPrimary, oTextSecondary, oTextMuted, oAccentCyan, oAccentGreen);
            }
        }
        else if (comp instanceof JSeparator) {
            comp.setForeground(BORDER_DEFAULT); comp.setBackground(BG_CARD);
        }
        else if (comp instanceof GradientPanel) {
            GradientPanel gp = (GradientPanel) comp;
            Color nFrom = gp.getFrom(), nTo = gp.getTo();
            if (nFrom.equals(oBgDark)) nFrom = BG_DARK; else if (nFrom.equals(oBgCard)) nFrom = BG_CARD;
            if (nTo.equals(oBgDark)) nTo = BG_DARK; else if (nTo.equals(oBgCard)) nTo = BG_CARD;
            gp.setColors(nFrom, nTo);
        }
        
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents())
                translateTheme(child, oBgDark, oBgCard, oBgCardHover, oBgInput, oTextPrimary, oTextSecondary, oTextMuted, oAccentCyan, oAccentGreen);
        }
    }

    private static boolean isColorClose(Color c1, Color c2) {
        if (c1 == null || c2 == null) return false;
        int threshold = 15;
        return Math.abs(c1.getRed() - c2.getRed()) < threshold &&
               Math.abs(c1.getGreen() - c2.getGreen()) < threshold &&
               Math.abs(c1.getBlue() - c2.getBlue()) < threshold;
    }

    public static void showFullscreenMap(RouteMapPanel originalMap) {
        JDialog dialog = new JDialog((Frame)null, "Live Tracking Fullscreen", true); dialog.setSize(1000, 800); dialog.setLocationRelativeTo(null);
        RouteMapPanel fsMap = new RouteMapPanel(); fsMap.setRoute(originalMap.getPickup(), originalMap.getDrop());
        JPanel container = new JPanel(new BorderLayout()); container.setBackground(BG_DARK); container.setBorder(new EmptyBorder(20, 20, 20, 20));
        JButton close = accentButton("Close Preview", ACCENT_RED); close.addActionListener(e -> dialog.dispose());
        container.add(fsMap, BorderLayout.CENTER); container.add(close, BorderLayout.SOUTH);
        dialog.add(container); applyGlobalTheme(darkMode); dialog.setVisible(true);
    }

    private static void saveThemePreference(boolean dark) { try (BufferedWriter w = new BufferedWriter(new FileWriter(THEME_PREF_FILE))) { w.write(dark ? "dark" : "light"); } catch (Exception ignored) {} }
    private static Boolean loadThemePreference() {
        if (!THEME_PREF_FILE.exists()) return null;
        try (BufferedReader r = new BufferedReader(new FileReader(THEME_PREF_FILE))) { String v = r.readLine(); return "dark".equalsIgnoreCase(v) ? Boolean.TRUE : Boolean.FALSE; } catch (Exception ignored) {}
        return null;
    }
    public static String formatCurrency(double amount) { return INR_FORMAT.format(amount); }
    public static String formatCurrency(int amount) { return INR_FORMAT.format(amount); }

    public enum AlertType { SUCCESS, ERROR, WARNING, INFO, CONFIRM }

    public static void showAlert(Component parent, String title, String message, AlertType type) {
        showCustomDialog(parent, title, message, type, false);
    }

    public static boolean showConfirm(Component parent, String title, String message) {
        return showCustomDialog(parent, title, message, AlertType.CONFIRM, true);
    }

    // --- Reusable Centralized Alert Methods ---
    public static boolean showConfirmDialog(Component parent, String title, String message) {
        return showConfirm(parent, title, message);
    }

    public static void showSuccessDialog(Component parent, String title, String message) {
        showAlert(parent, title, message, AlertType.SUCCESS);
    }

    public static void showErrorDialog(Component parent, String title, String message) {
        showAlert(parent, title, message, AlertType.ERROR);
    }

    public static void showInfoDialog(Component parent, String title, String message) {
        showAlert(parent, title, message, AlertType.INFO);
    }

    private static boolean showCustomDialog(Component parent, String title, String message, AlertType type, boolean isConfirm) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel card = new JPanel(new BorderLayout(0, 20)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                Color accent = getAlertColor(type);
                g2.setColor(accent); g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 24, 24);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 30, 30, 30));

        Icon alertIcon = getAlertIcon(type, 54);
        JLabel iconLabel = new JLabel(alertIcon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_SUBTITLE);
        titleLbl.setForeground(TEXT_PRIMARY);
        titleLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel msgLbl = new JLabel("<html><center>" + message + "</center></html>");
        msgLbl.setFont(FONT_BODY);
        msgLbl.setForeground(TEXT_SECONDARY);
        msgLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.add(titleLbl, BorderLayout.NORTH);
        content.add(msgLbl, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);

        final boolean[] result = {false};
        if (isConfirm) {
            JButton yesBtn = accentButton("Confirm", getAlertColor(type));
            yesBtn.setPreferredSize(new Dimension(130, 42));
            JButton noBtn = ghostButton("Cancel", TEXT_SECONDARY);
            noBtn.setPreferredSize(new Dimension(130, 42));
            yesBtn.addActionListener(e -> { result[0] = true; dialog.dispose(); });
            noBtn.addActionListener(e -> { result[0] = false; dialog.dispose(); });
            btnPanel.add(noBtn);
            btnPanel.add(yesBtn);
        } else {
            JButton okBtn = accentButton("OK", getAlertColor(type));
            okBtn.setPreferredSize(new Dimension(130, 42));
            okBtn.addActionListener(e -> { result[0] = true; dialog.dispose(); });
            btnPanel.add(okBtn);
        }

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(card);
        dialog.pack();
        dialog.setSize(Math.max(400, dialog.getWidth()), Math.max(280, dialog.getHeight()));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return result[0];
    }

    private static Color getAlertColor(AlertType type) {
        switch (type) {
            case SUCCESS: return ACCENT_GREEN;
            case ERROR:   return ACCENT_RED;
            case WARNING: return ACCENT_AMBER;
            case CONFIRM: return ACCENT_PURPLE;
            default:      return ACCENT_CYAN;
        }
    }

    private static Icon getAlertIcon(AlertType type, int size) {
        Color c = getAlertColor(type);
        return new Icon() {
            @Override public void paintIcon(Component comp, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c); g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int s = size, cx = x + s/2, cy = y + s/2;
                switch (type) {
                    case SUCCESS:
                        g2.drawOval(x + 2, y + 2, s - 4, s - 4);
                        g2.drawLine(cx - s/5, cy, cx - s/10, cy + s/6);
                        g2.drawLine(cx - s/10, cy + s/6, cx + s/4, cy - s/6);
                        break;
                    case ERROR:
                        g2.drawOval(x + 2, y + 2, s - 4, s - 4);
                        g2.drawLine(cx - s/6, cy - s/6, cx + s/6, cy + s/6);
                        g2.drawLine(cx + s/6, cy - s/6, cx - s/6, cy + s/6);
                        break;
                    case WARNING:
                        int[] px = {cx, x + 2, x + s - 2};
                        int[] py = {y + 2, y + s - 2, y + s - 2};
                        g2.drawPolygon(px, py, 3);
                        g2.drawLine(cx, cy - s/6, cx, cy + s/10);
                        g2.fillOval(cx - 2, cy + s/5, 4, 4);
                        break;
                    case CONFIRM:
                    case INFO:
                        g2.drawOval(x + 2, y + 2, s - 4, s - 4);
                        g2.drawLine(cx, cy - s/8, cx, cy + s/4);
                        g2.fillOval(cx - 2, cy - s/4, 4, 4);
                        break;
                }
                g2.dispose();
            }
            @Override public int getIconWidth()  { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
}

