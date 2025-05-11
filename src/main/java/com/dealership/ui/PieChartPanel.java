package com.dealership.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartPanel extends JPanel {
    private Map<String, Double> data;
    private List<Color> colors = List.of(
        new Color(255, 99, 132), new Color(54, 162, 235), new Color(255, 206, 86),
        new Color(75, 192, 192), new Color(153, 102, 255), new Color(255, 159, 64),
        new Color(199, 199, 199), new Color(83, 102, 83), new Color(140, 100, 100),
        new Color(230, 25, 75), new Color(60, 180, 75), new Color(255, 225, 25),
        new Color(0, 130, 200), new Color(245, 130, 48), new Color(145, 30, 180),
        new Color(70, 240, 240), new Color(240, 50, 230), new Color(210, 245, 60),
        new Color(250, 190, 212), new Color(0, 128, 128), new Color(220, 190, 255)
    );

    public PieChartPanel(Map<String, Double> data) {
        this.data = data;
        setPreferredSize(new Dimension(350, 350));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            String msg = "Pasta grafik için veri yok.";
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(msg);
            g.setColor(Color.BLACK);
            g.drawString(msg, (getWidth() - msgWidth) / 2 , getHeight()/2 - fm.getHeight()/2);
            return;
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int legendItemHeight = 15;
        int legendPadding = 5;
        int legendColorBoxSize = 10;
        Font legendFont = new Font("SansSerif", Font.PLAIN, 9);
        g2d.setFont(legendFont);
        FontMetrics fm = g2d.getFontMetrics();

        int maxLegendTextWidth = 0;
        for (String key : data.keySet()) {
             maxLegendTextWidth = Math.max(maxLegendTextWidth, fm.stringWidth(String.format("%s: %.1f%%", key, data.get(key))));
        }
        maxLegendTextWidth += legendColorBoxSize + legendPadding;

        int numItems = data.entrySet().stream().filter(entry -> entry.getValue() > 0.01).mapToInt(e -> 1).sum();
        int totalLegendHeight = numItems * legendItemHeight;


        int chartDiameter;
        int chartX, chartY;
        int legendX, legendYStart;
        int availableWidth = getWidth();
        int availableHeight = getHeight();

        boolean legendOnRight = availableWidth - maxLegendTextWidth - (legendPadding * 3) > availableHeight * 0.6;
        if (numItems * legendItemHeight > availableHeight * 0.8) legendOnRight = false;

        if (legendOnRight) {
            chartDiameter = Math.min(availableWidth - maxLegendTextWidth - (legendPadding * 3), availableHeight - (legendPadding * 2));
            chartX = legendPadding;
            chartY = (availableHeight - chartDiameter) / 2;
            legendX = chartX + chartDiameter + legendPadding * 2;
            legendYStart = Math.max(chartY, (availableHeight - totalLegendHeight) / 2);
             if (legendYStart < legendPadding) legendYStart = legendPadding;


        } else {
            chartDiameter = Math.min(availableWidth - (legendPadding * 2), availableHeight - totalLegendHeight - (legendPadding * 3));
            chartX = (availableWidth - chartDiameter) / 2;
            chartY = legendPadding;
            legendX = (availableWidth - maxLegendTextWidth + legendColorBoxSize + legendPadding) / 2;
             if (maxLegendTextWidth > availableWidth *0.8) legendX = legendPadding;
            legendYStart = chartY + chartDiameter + legendPadding * 2;
        }
        if (chartDiameter <= 0) {
             g.setColor(Color.RED);
             g.drawString("Grafik için yeterli alan yok!", 10, getHeight()/2);
             return;
        }

        double totalValue = 100.0;
        double currentAngle = 90.0;
        int colorIndex = 0;
        int legendCount = 0;

        List<Map.Entry<String, Double>> sortedData = data.entrySet().stream()
            .filter(entry -> entry.getValue() > 0.01)
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .collect(Collectors.toList());

        for (Map.Entry<String, Double> entry : sortedData) {
            double percentage = entry.getValue();
            double arcAngle = (percentage / totalValue) * 360.0;

            g2d.setColor(colors.get(colorIndex % colors.size()));
            g2d.fillArc(chartX, chartY, chartDiameter, chartDiameter, (int) currentAngle, -(int) Math.ceil(arcAngle));

            if (legendYStart + legendCount * legendItemHeight + legendItemHeight < availableHeight || legendOnRight) {
                 g2d.fillRect(legendX, legendYStart + legendCount * legendItemHeight, legendColorBoxSize, legendColorBoxSize);
                 g2d.setColor(Color.BLACK);
                 String legendText = String.format("%s: %.1f%%", entry.getKey(), percentage);
                 g2d.drawString(legendText, legendX + legendColorBoxSize + legendPadding, legendYStart + legendCount * legendItemHeight + fm.getAscent() -2);
                 legendCount++;
            }
            currentAngle -= arcAngle;
            colorIndex++;
        }
    }
}
