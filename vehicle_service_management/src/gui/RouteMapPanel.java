package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class RouteMapPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color ROAD_COLOR       = new Color(60, 65, 78);
    private static final Color ROAD_LINE        = new Color(255, 210, 60, 120);
    private static final Color ROAD_EDGE        = new Color(80, 85, 95);
    private static final Color BUILDING_COLOR   = new Color(35, 40, 52);
    private static final Color PARK_COLOR       = new Color(28, 58, 38);
    private static final Color WATER_COLOR      = new Color(22, 42, 70);
    private static final Color MAP_BG           = new Color(25, 28, 38);
    private static final Color GRID_LINE        = new Color(35, 40, 50);

    private final Map<String, Point> locations = new LinkedHashMap<>();
    private String pickupName, dropName;
    private String serviceCenterName = "Service Center";
    private List<Point> routePath = new ArrayList<>();
    private float vehicleProgress = 0f;
    private javax.swing.Timer animTimer;

    public RouteMapPanel() {
        setBackground(MAP_BG);
        setPreferredSize(new Dimension(400, 280));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_DEFAULT),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Predefined locations on a road grid
        locations.put("City Center",    new Point(60,  200));
        locations.put("MG Road",        new Point(60,  120));
        locations.put("Station Road",   new Point(170, 120));
        locations.put("Service Center", new Point(170, 40));
        locations.put("Highway Exit",   new Point(300, 40));
        locations.put("Mall Road",      new Point(300, 120));
        locations.put("Airport Road",   new Point(300, 200));
        locations.put("Ring Road",      new Point(170, 200));

        pickupName = "City Center";
        dropName = "Airport Road";
        buildRoute();
        startAnimation();
    }

    // Road network as adjacency list
    private Map<String, List<String>> roadNetwork() {
        Map<String, List<String>> net = new LinkedHashMap<>();
        net.put("City Center",    Arrays.asList("MG Road", "Ring Road"));
        net.put("MG Road",        Arrays.asList("City Center", "Station Road"));
        net.put("Station Road",   Arrays.asList("MG Road", "Service Center", "Mall Road", "Ring Road"));
        net.put("Service Center", Arrays.asList("Station Road", "Highway Exit"));
        net.put("Highway Exit",   Arrays.asList("Service Center", "Mall Road"));
        net.put("Mall Road",      Arrays.asList("Station Road", "Highway Exit", "Airport Road"));
        net.put("Airport Road",   Arrays.asList("Mall Road", "Ring Road"));
        net.put("Ring Road",      Arrays.asList("City Center", "Station Road", "Airport Road"));
        return net;
    }

    // BFS shortest path on road network
    private void buildRoute() {
        routePath.clear();
        if (pickupName == null || dropName == null) return;
        Map<String, List<String>> net = roadNetwork();
        // BFS: pickup -> service center -> drop
        List<String> leg1 = bfs(net, pickupName, serviceCenterName);
        List<String> leg2 = bfs(net, serviceCenterName, dropName);
        if (leg1 != null) {
            for (String n : leg1) routePath.add(locations.get(n));
        }
        if (leg2 != null) {
            for (int i = 1; i < leg2.size(); i++) routePath.add(locations.get(leg2.get(i)));
        }
        if (routePath.isEmpty()) {
            Point p = locations.getOrDefault(pickupName, new Point(60, 200));
            Point d = locations.getOrDefault(dropName, new Point(300, 200));
            routePath.add(p); routePath.add(d);
        }
        vehicleProgress = 0f;
    }

    private List<String> bfs(Map<String, List<String>> net, String from, String to) {
        if (from.equals(to)) return Arrays.asList(from);
        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(Arrays.asList(from));
        visited.add(from);
        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String last = path.get(path.size() - 1);
            for (String next : net.getOrDefault(last, Collections.emptyList())) {
                if (next.equals(to)) { List<String> r = new ArrayList<>(path); r.add(next); return r; }
                if (!visited.contains(next)) { visited.add(next); List<String> np = new ArrayList<>(path); np.add(next); queue.add(np); }
            }
        }
        return null;
    }

    public String[] getLocationNames() { return locations.keySet().toArray(new String[0]); }
    public void setRoute(String pickup, String drop) { pickupName = pickup; dropName = drop; buildRoute(); repaint(); }
    public String getPickup() { return pickupName; }
    public String getDrop() { return dropName; }

    private void startAnimation() {
        animTimer = new javax.swing.Timer(40, e -> { vehicleProgress = (vehicleProgress + 0.003f) % 1.0f; repaint(); });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        // Scale to fit
        double sx = w / 380.0, sy = h / 260.0;
        g2.scale(sx, sy);

        // Grid
        g2.setColor(GRID_LINE);
        g2.setStroke(new BasicStroke(0.5f));
        for (int x = 0; x < 380; x += 30) g2.drawLine(x, 0, x, 260);
        for (int y = 0; y < 260; y += 30) g2.drawLine(0, y, 380, y);

        // Terrain
        drawPark(g2, 10, 145, 35, 40);
        drawPark(g2, 220, 150, 50, 35);
        drawWater(g2, 330, 45, 40, 30);
        drawBuilding(g2, 95, 55, 25, 22);
        drawBuilding(g2, 125, 150, 22, 18);
        drawBuilding(g2, 220, 55, 25, 22);
        drawBuilding(g2, 95, 180, 20, 18);
        drawBuilding(g2, 240, 180, 22, 18);

        // Draw roads
        Map<String, List<String>> net = roadNetwork();
        Set<String> drawn = new HashSet<>();
        g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (Map.Entry<String, List<String>> entry : net.entrySet()) {
            Point p1 = locations.get(entry.getKey());
            for (String neighbor : entry.getValue()) {
                String key = entry.getKey().compareTo(neighbor) < 0 ? entry.getKey() + neighbor : neighbor + entry.getKey();
                if (drawn.contains(key)) continue;
                drawn.add(key);
                Point p2 = locations.get(neighbor);
                // Road background
                g2.setColor(ROAD_EDGE);
                g2.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                // Road surface
                g2.setColor(ROAD_COLOR);
                g2.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                // Center dashes
                drawDashedLine(g2, p1, p2);
            }
        }

        // Route highlight
        if (routePath.size() >= 2) {
            g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(59, 130, 246, 180));
            for (int i = 0; i < routePath.size() - 1; i++) {
                Point a = routePath.get(i), b = routePath.get(i + 1);
                g2.drawLine(a.x, a.y, b.x, b.y);
            }
        }

        // Location markers
        for (Map.Entry<String, Point> entry : locations.entrySet()) {
            Point p = entry.getValue();
            Color markerColor;
            if (entry.getKey().equals(pickupName)) markerColor = new Color(34, 197, 94);
            else if (entry.getKey().equals(dropName)) markerColor = new Color(239, 68, 68);
            else if (entry.getKey().equals(serviceCenterName)) markerColor = new Color(59, 130, 246);
            else markerColor = new Color(120, 130, 150);
            drawMarkerPin(g2, p.x, p.y, markerColor, entry.getKey());
        }

        // Animated vehicle on route
        if (routePath.size() >= 2) {
            Point2D.Float vPos = getPositionOnPath(vehicleProgress);
            // Glow
            g2.setColor(new Color(59, 130, 246, 40));
            g2.fillOval((int) vPos.x - 12, (int) vPos.y - 12, 24, 24);
            // Vehicle body
            drawVehicleIcon(g2, (int) vPos.x, (int) vPos.y);

            // ETA label
            double totalDist = 0;
            for (int i = 0; i < routePath.size() - 1; i++) {
                totalDist += routePath.get(i).distance(routePath.get(i + 1));
            }
            int etaMin = Math.max(1, (int) ((1 - vehicleProgress) * totalDist * 0.1));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
            g2.setColor(new Color(200, 210, 225));
            g2.drawString("ETA: " + etaMin + " min", (int) vPos.x + 10, (int) vPos.y - 6);
        }

        // Legend
        drawLegend(g2);

        g2.dispose();
    }

    private void drawDashedLine(Graphics2D g2, Point p1, Point p2) {
        g2.setColor(ROAD_LINE);
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{6, 8}, 0));
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void drawPark(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(PARK_COLOR);
        g2.fillRoundRect(x, y, w, h, 6, 6);
        g2.setColor(new Color(50, 100, 60));
        g2.fillOval(x + 5, y + 5, 10, 10);
        g2.fillOval(x + w - 15, y + h - 15, 10, 10);
    }

    private void drawWater(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(WATER_COLOR);
        g2.fillRoundRect(x, y, w, h, 10, 10);
        g2.setColor(new Color(40, 70, 110));
        g2.setStroke(new BasicStroke(0.8f));
        for (int wy = y + 6; wy < y + h - 4; wy += 6) {
            g2.drawLine(x + 4, wy, x + w - 4, wy);
        }
    }

    private void drawBuilding(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(BUILDING_COLOR);
        g2.fillRect(x, y, w, h);
        g2.setColor(new Color(50, 60, 75));
        g2.drawRect(x, y, w, h);
        // Windows
        g2.setColor(new Color(70, 85, 105));
        for (int wy = y + 3; wy < y + h - 3; wy += 6) {
            for (int wx = x + 3; wx < x + w - 3; wx += 6) {
                g2.fillRect(wx, wy, 3, 3);
            }
        }
    }

    private void drawMarkerPin(Graphics2D g2, int x, int y, Color color, String name) {
        // Pin shadow
        g2.setColor(new Color(0, 0, 0, 40));
        g2.fillOval(x - 5, y + 2, 10, 4);
        // Pin body
        g2.setColor(color);
        int[] px = {x, x - 6, x + 6};
        int[] py = {y + 2, y - 12, y - 12};
        g2.fillPolygon(px, py, 3);
        g2.fillOval(x - 6, y - 16, 12, 12);
        // Inner circle
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 3, y - 13, 6, 6);
        // Label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 8));
        g2.setColor(new Color(200, 210, 225));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(name);
        // Label background
        g2.setColor(new Color(15, 18, 28, 200));
        g2.fillRoundRect(x - tw/2 - 3, y - 28, tw + 6, 12, 4, 4);
        g2.setColor(new Color(200, 210, 225));
        g2.drawString(name, x - tw/2, y - 19);
    }

    private void drawVehicleIcon(Graphics2D g2, int cx, int cy) {
        // Car body
        g2.setColor(new Color(59, 130, 246));
        g2.fillRoundRect(cx - 8, cy - 4, 16, 8, 4, 4);
        // Windshield
        g2.setColor(new Color(120, 180, 255));
        g2.fillRoundRect(cx + 2, cy - 3, 5, 6, 2, 2);
        // Wheels
        g2.setColor(new Color(30, 35, 45));
        g2.fillOval(cx - 6, cy + 3, 5, 5);
        g2.fillOval(cx + 2, cy + 3, 5, 5);
    }

    private Point2D.Float getPositionOnPath(float progress) {
        if (routePath.size() < 2) return new Point2D.Float(0, 0);
        float totalLen = 0;
        float[] segLens = new float[routePath.size() - 1];
        for (int i = 0; i < routePath.size() - 1; i++) {
            segLens[i] = (float) routePath.get(i).distance(routePath.get(i + 1));
            totalLen += segLens[i];
        }
        float target = progress * totalLen;
        float cumLen = 0;
        for (int i = 0; i < segLens.length; i++) {
            if (cumLen + segLens[i] >= target) {
                float t = (target - cumLen) / segLens[i];
                float x = routePath.get(i).x + (routePath.get(i + 1).x - routePath.get(i).x) * t;
                float y = routePath.get(i).y + (routePath.get(i + 1).y - routePath.get(i).y) * t;
                return new Point2D.Float(x, y);
            }
            cumLen += segLens[i];
        }
        Point last = routePath.get(routePath.size() - 1);
        return new Point2D.Float(last.x, last.y);
    }

    private void drawLegend(Graphics2D g2) {
        int lx = 10, ly = 232;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 8));
        // Pickup
        g2.setColor(new Color(34, 197, 94)); g2.fillOval(lx, ly, 6, 6);
        g2.setColor(new Color(180, 190, 200)); g2.drawString("Pickup", lx + 9, ly + 6);
        // Service Center
        g2.setColor(new Color(59, 130, 246)); g2.fillOval(lx + 50, ly, 6, 6);
        g2.setColor(new Color(180, 190, 200)); g2.drawString("Center", lx + 59, ly + 6);
        // Drop
        g2.setColor(new Color(239, 68, 68)); g2.fillOval(lx + 105, ly, 6, 6);
        g2.setColor(new Color(180, 190, 200)); g2.drawString("Drop", lx + 114, ly + 6);
        // Vehicle
        g2.setColor(new Color(59, 130, 246)); g2.fillRect(lx + 150, ly + 1, 8, 4);
        g2.setColor(new Color(180, 190, 200)); g2.drawString("Vehicle", lx + 161, ly + 6);
    }
}
