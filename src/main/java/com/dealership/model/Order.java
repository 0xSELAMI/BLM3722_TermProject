package com.dealership.model;

import java.time.LocalDateTime;

public class Order {
    private String orderId;
    private String customerIdFk;
    private String vehicleChassisNumberFk;
    private LocalDateTime orderDate;
    private double totalPrice;
    private OrderStatus status;

    public Order(String orderId, String customerIdFk, String vehicleChassisNumberFk, LocalDateTime orderDate, double totalPrice, OrderStatus status) {
        this.orderId = orderId;
        this.customerIdFk = customerIdFk;
        this.vehicleChassisNumberFk = vehicleChassisNumberFk;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerIdFk() { return customerIdFk; }
    public String getVehicleChassisNumberFk() { return vehicleChassisNumberFk; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public double getTotalPrice() { return totalPrice; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Sipariş ID: " + orderId + " (Müşteri: " + customerIdFk + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Order order = (Order) obj;
        return orderId != null ? orderId.equals(order.orderId) : order.orderId == null;
    }

    @Override
    public int hashCode() {
        return orderId != null ? orderId.hashCode() : 0;
    }
}
