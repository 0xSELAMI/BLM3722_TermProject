package com.dealership.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class LineChartPanel extends JPanel {
    private final Map<String, Double> data;

    public LineChartPanel(Map<String, Double> data) {
        this.data = data;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.size() < 2) {
            g.drawString("Yeterli veri yok", 10, 20);
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth() - 60, h = getHeight() - 60;
        double max = data.values().stream().mapToDouble(v -> v).max().orElse(1);
        // eksen çizgisi
        g2.drawLine(40, 20, 40, h + 20);
        g2.drawLine(40, h + 20, w + 40, h + 20);

        List<String> keys = data.keySet().stream().collect(Collectors.toList());
        int n = keys.size();
        int gap = w / (n - 1);
        int idx = 0;
        Point prev = null;
        for (String k : keys) {
            double val = data.get(k);
            int x = 40 + idx * gap;
            int y = 20 + (int) ((h) * (1 - val / max));
            // nokta
            g2.fillOval(x - 3, y - 3, 6, 6);
            // etiket
            g2.drawString(k, x - 10, h + 40);
            // çizgi
            if (prev != null) {
                g2.drawLine(prev.x, prev.y, x, y);
            }
            prev = new Point(x, y);
            idx++;
        }
    }
}