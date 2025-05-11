package com.dealership.service.impl;

import com.dealership.db.DatabaseUtil;
import com.dealership.model.*;
import com.dealership.service.CustomerService;
import com.dealership.service.OrderService;
import com.dealership.service.StockService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresOrderService implements OrderService {
    private final StockService stockService;
    private final CustomerService customerService;

    public PostgresOrderService(StockService stockService, CustomerService customerService) {
        this.stockService = stockService;
        this.customerService = customerService;
    }

    @Override
    public Order placeOrder(String customerId, String vehicleChassisNumber, double orderPrice) {
        Vehicle vehicle = stockService.findVehicleByChassisNumber(vehicleChassisNumber);
        if (vehicle == null || (vehicle.getStatus() != VehicleStatus.IN_STOCK && vehicle.getStatus() != VehicleStatus.IN_SHOWROOM_ORDER && vehicle.getStatus() != VehicleStatus.IN_SHOWROOM_DISPLAY)) {
            System.err.println("Sipariş için araç uygun değil. Şase: " + vehicleChassisNumber + ", Durum: " + (vehicle != null ? vehicle.getStatus() : "BULUNAMADI"));
            return null;
        }
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime orderDate = LocalDateTime.now();
        OrderStatus status = OrderStatus.PLACED;

        String sql = "INSERT INTO Orders (orderId, customerIdFk, vehicleChassisNumberFk, orderDate, totalPrice, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, customerId);
            pstmt.setString(3, vehicleChassisNumber);
            pstmt.setTimestamp(4, Timestamp.valueOf(orderDate));
            pstmt.setDouble(5, orderPrice);
            pstmt.setString(6, status.name());

            if (pstmt.executeUpdate() > 0) {
                stockService.updateVehicleStatus(vehicleChassisNumber, VehicleStatus.RESERVED);
                if (customerService != null) {
                    customerService.addInteractionEvent(new InteractionEvent(
                        "EVT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase(),
                        customerId,
                        InteractionEventType.ORDER_PLACED,
                        orderDate,
                        vehicleChassisNumber,
                        "Sipariş oluşturuldu. Sipariş ID: " + orderId + ", Tutar: " + orderPrice
                    ));
                }
                return new Order(orderId, customerId, vehicleChassisNumber, orderDate, orderPrice, status);
            }
        } catch (SQLException e) { System.err.println("Sipariş oluşturulurken SQL Hatası: " + e.getMessage()); }
        return null;
    }

    @Override
    public boolean updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderById(orderId);
        if (order == null) return false;

        String sql = "UPDATE Orders SET status = ? WHERE orderId = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, orderId);
            if (pstmt.executeUpdate() > 0) {
                if (newStatus == OrderStatus.DELIVERED) {
                    stockService.updateVehicleStatus(order.getVehicleChassisNumberFk(), VehicleStatus.SOLD);
                    if (customerService != null) {
                         customerService.addInteractionEvent(new InteractionEvent(
                            "EVT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase(),
                            order.getCustomerIdFk(),
                            InteractionEventType.VEHICLE_DELIVERED,
                            LocalDateTime.now(),
                            order.getVehicleChassisNumberFk(),
                            "Araç teslim edildi. Sipariş ID: " + order.getOrderId()
                        ));
                    }
                } else if (newStatus == OrderStatus.CANCELLED) {
                     Vehicle vehicle = stockService.findVehicleByChassisNumber(order.getVehicleChassisNumberFk());
                     if (vehicle != null && vehicle.getStatus() == VehicleStatus.RESERVED) {
                         stockService.updateVehicleStatus(order.getVehicleChassisNumberFk(), VehicleStatus.IN_STOCK);
                     }
                }
                return true;
            }
        } catch (SQLException e) { System.err.println("Sipariş durumu güncellenirken SQL Hatası: " + e.getMessage()); }
        return false;
    }

    @Override
    public Order findOrderById(String orderId) {
        String sql = "SELECT * FROM Orders WHERE orderId = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Order(
                    rs.getString("orderId"),
                    rs.getString("customerIdFk"),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getTimestamp("orderDate").toLocalDateTime(),
                    rs.getDouble("totalPrice"),
                    OrderStatus.valueOf(rs.getString("status"))
                );
            }
        } catch (SQLException e) { System.err.println("Sipariş bulunurken SQL Hatası: " + e.getMessage()); }
        return null;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY orderDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                 orders.add(new Order(
                    rs.getString("orderId"),
                    rs.getString("customerIdFk"),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getTimestamp("orderDate").toLocalDateTime(),
                    rs.getDouble("totalPrice"),
                    OrderStatus.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Tüm siparişler alınırken SQL Hatası: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public long getTotalDeliveredSalesCount() {
        String sql = "SELECT COUNT(*) FROM Orders WHERE status = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, OrderStatus.DELIVERED.name());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.err.println("Toplam teslim edilen satış sayısı alınırken SQL Hatası: " + e.getMessage());
        }
        return 0;
    }
}
