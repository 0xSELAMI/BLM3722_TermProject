package com.dealership.service.impl;

import com.dealership.db.DatabaseUtil;
import com.dealership.model.InteractionEvent;
import com.dealership.model.OrderStatus;
import com.dealership.service.CustomerService;
import com.dealership.service.OrderService;
import com.dealership.service.ReportService;
import com.dealership.service.StockService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresReportService implements ReportService {
    private final OrderService orderService;

    public PostgresReportService(OrderService orderService) {
        this.orderService = orderService;
    }
     public PostgresReportService() {
        this.orderService = null; // Bu durumda bazı raporlar eksik çalışabilir
        System.err.println("UYARI: PostgresReportService, OrderService olmadan başlatıldı. Bazı raporlar düzgün çalışmayabilir.");
    }

    @Override
    public List<Map<String, Object>> generateSalesReport(Map<String, String> filters) {
        List<Map<String, Object>> reportData = new ArrayList<>();
        String sql = "SELECT o.orderId, o.customerIdFk, c.name as customerName, o.vehicleChassisNumberFk, v.brand, v.model, o.orderDate, o.totalPrice " +
                     "FROM Orders o " +
                     "JOIN Vehicles v ON o.vehicleChassisNumberFk = v.chassisNumber " +
                     "JOIN Customers c ON o.customerIdFk = c.customerId " +
                     "WHERE o.status = ? ORDER BY o.orderDate DESC";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, OrderStatus.DELIVERED.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("orderId", rs.getString("orderId"));
                row.put("customerId", rs.getString("customerIdFk"));
                row.put("customerName", rs.getString("customerName"));
                row.put("vehicleChassisNumber", rs.getString("vehicleChassisNumberFk"));
                row.put("brand", rs.getString("brand"));
                row.put("model", rs.getString("model"));
                row.put("orderDate", rs.getTimestamp("orderDate").toLocalDateTime());
                row.put("totalPrice", rs.getDouble("totalPrice"));
                reportData.add(row);
            }
        } catch (SQLException e) { System.err.println("Satış raporu oluşturulurken SQL Hatası: " + e.getMessage()); }
        return reportData;
    }

    @Override
    public double generateSalesForecast(List<Double> pastSalesPrices, int n) {
         if (pastSalesPrices == null || pastSalesPrices.isEmpty() || n <= 0 || pastSalesPrices.size() < n) {
             System.err.println("Satış tahmini için yetersiz veri veya geçersiz periyot. Veri boyutu: " + (pastSalesPrices != null ? pastSalesPrices.size() : 0) + ", İstenen periyot: " + n);
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < n; i++) {
            sum += pastSalesPrices.get(i);
        }
        return sum / n;
    }

    @Override
    public List<InteractionEvent> generateCustomerInteractionReport(String customerId, Map<String, String> filters, CustomerService customerService) {
        if (customerService != null) {
            return customerService.getInteractionHistoryForCustomer(customerId);
        }
        System.err.println("CustomerService generateCustomerInteractionReport için sağlanmadı.");
        return new ArrayList<>();
    }

    @Override
    public Map<String, Double> getSalesPercentageByModel(StockService stockService) {
        Map<String, Long> salesCountByModel = new HashMap<>();
        String sql = "SELECT v.model, COUNT(o.orderId) as model_count " +
                     "FROM Orders o JOIN Vehicles v ON o.vehicleChassisNumberFk = v.chassisNumber " +
                     "WHERE o.status = ? GROUP BY v.model";
        long totalSales = 0;

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, OrderStatus.DELIVERED.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String model = rs.getString("model");
                long count = rs.getLong("model_count");
                salesCountByModel.put(model, count);
                totalSales += count;
            }
        } catch (SQLException e) { System.err.println("Model bazlı satış yüzdesi alınırken SQL Hatası: " + e.getMessage()); }

        Map<String, Double> salesPercentage = new HashMap<>();
        if (totalSales > 0) {
            for (Map.Entry<String, Long> entry : salesCountByModel.entrySet()) {
                salesPercentage.put(entry.getKey(), (double) entry.getValue() * 100.0 / totalSales);
            }
        }
        return salesPercentage;
    }

    @Override
    public List<Map<String, Object>> generateDetailedVehicleSalesReport() {
        List<Map<String, Object>> detailedReportData = new ArrayList<>();
        long totalDeliveredSales = 0;
        if (this.orderService != null) {
            totalDeliveredSales = this.orderService.getTotalDeliveredSalesCount();
        } else {
            System.err.println("UYARI: OrderService, PostgresReportService içinde null. Toplam satış sayısı DB'den çekilecek.");
             try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rsTotal = stmt.executeQuery("SELECT COUNT(*) FROM Orders WHERE status = '" + OrderStatus.DELIVERED.name() + "'")) {
                if (rsTotal.next()) {
                    totalDeliveredSales = rsTotal.getLong(1);
                }
            } catch (SQLException e) {
                System.err.println("Detaylı rapor için toplam satış sayısı alınırken acil durum SQL hatası: " + e.getMessage());
            }
        }

        String sql = "SELECT v.brand, v.model, v.year, v.carPackage, COUNT(o.orderId) as quantity_sold " +
                     "FROM Orders o " +
                     "JOIN Vehicles v ON o.vehicleChassisNumberFk = v.chassisNumber " +
                     "WHERE o.status = ? " +
                     "GROUP BY v.brand, v.model, v.year, v.carPackage " +
                     "ORDER BY quantity_sold DESC, v.brand, v.model, v.year";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, OrderStatus.DELIVERED.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("brand", rs.getString("brand"));
                row.put("model", rs.getString("model"));
                row.put("year", rs.getInt("year"));
                row.put("carPackage", rs.getString("carPackage"));
                long quantitySold = rs.getLong("quantity_sold");
                row.put("quantitySold", quantitySold);

                double percentage = 0.0;
                if (totalDeliveredSales > 0) {
                    percentage = (double) quantitySold * 100.0 / totalDeliveredSales;
                }
                row.put("salesPercentage", percentage);
                detailedReportData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Detaylı araç satış raporu oluşturulurken SQL Hatası: " + e.getMessage());
        }
        return detailedReportData;
    }
}
