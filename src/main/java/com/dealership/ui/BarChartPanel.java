package com.dealership.ui;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

public class BarChartPanel extends JPanel {
    private final Map<String, Double> sales;
    private final Map<String, Long> counts;
    private final DecimalFormat fmt;
    private static final int TOP    = 20;
    private static final int BOTTOM = 40;
    private static final int LEFT   = 50;
    private static final int RIGHT  = 20;
    private static final int GAP    = 10;

    public BarChartPanel(Map<String, Double> sales, Map<String, Long> counts) {
        this.sales  = sales;
        this.counts = counts;
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator('.');
        s.setDecimalSeparator(',');
        fmt = new DecimalFormat("#,###", s);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sales == null || sales.isEmpty()) {
            g.drawString("Veri yok", LEFT, TOP + 15);
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        double max = sales.values().stream().mapToDouble(d -> d).max().orElse(1);
        int n = sales.size();
        int cw = w - LEFT - RIGHT, ch = h - TOP - BOTTOM;
        int bw = Math.max(1, (cw - (n + 1) * GAP) / n);

        g2.setColor(Color.BLACK);
        g2.drawLine(LEFT, TOP, LEFT, TOP + ch);
        g2.drawLine(LEFT, TOP + ch, LEFT + cw, TOP + ch);

        g2.drawString("0", LEFT - 15, TOP + ch);
        g2.drawString(fmt.format(max) + " TL", LEFT - 60, TOP + 5);

        int x = LEFT + GAP, i = 0;
        FontMetrics fm = g2.getFontMetrics();
        for (Map.Entry<String, Double> e : sales.entrySet()) {
            double v = e.getValue();
            int bh = (int) ((v / max) * ch), by = TOP + ch - bh;

            g2.setColor(Color.getHSBColor((float) i++ / n, 0.7f, 0.8f));
            g2.fillRect(x, by, bw, bh);

            g2.setColor(Color.BLACK);
            g2.drawRect(x, by, bw, bh);

            String year = e.getKey();
            int xt = x + (bw - fm.stringWidth(year)) / 2;
            g2.drawString(year, xt, TOP + ch + 15);

            String price = fmt.format(v) + " TL";
            int px = x + (bw - fm.stringWidth(price)) / 2;
            g2.drawString(price, px, by - 5);

            String cnt = counts.get(year) + " araç";
            int cx = x + (bw - fm.stringWidth(cnt)) / 2;
            g2.drawString(cnt, cx, by - 20);

            x += bw + GAP;
        }
    }
}