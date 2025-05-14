package com.dealership.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScatterPlotPanel extends JPanel {
    private final Map<Integer, Double> data; // x=Yıl, y=OrtalamaFiyat

    public ScatterPlotPanel(Map<Integer, Double> data) {
        this.data = data;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 300));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            g.drawString("Veri yok", 10, 20);
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth() - 60, h = getHeight() - 60;
        int minX = data.keySet().stream().mapToInt(i -> i).min().orElse(0);
        int maxX = data.keySet().stream().mapToInt(i -> i).max().orElse(1);
        double maxY = data.values().stream().mapToDouble(v -> v).max().orElse(1);

        // eksenler
        g2.drawLine(40, 20, 40, h + 20);
        g2.drawLine(40, h + 20, w + 40, h + 20);

        for (var e : data.entrySet()) {
            int year = e.getKey();
            double price = e.getValue();
            int x = 40 + (int) ((year - minX) / (double) (maxX - minX) * w);
            int y = 20 + (int) ((h) * (1 - price / maxY));
            g2.fillOval(x - 4, y - 4, 8, 8);
            g2.drawString(Integer.toString(year), x - 10, h + 40);
        }
    }
}