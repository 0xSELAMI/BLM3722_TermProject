package com.dealership.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testGettersAndToString() {
        String orderId = "O01";
        String customerId = "C01";
        String chassis = "CH001";
        LocalDateTime date = LocalDateTime.of(2025, 5, 14, 12, 0);
        double price = 12345.67;
        OrderStatus status = OrderStatus.values()[0];
        Order order = new Order(orderId, customerId, chassis, date, price, status);

        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerIdFk());
        assertEquals(chassis, order.getVehicleChassisNumberFk());
        assertEquals(date, order.getOrderDate());
        assertEquals(price, order.getTotalPrice());
        assertEquals(status, order.getStatus());
        assertEquals("Sipariş ID: " + orderId + " (Müşteri: " + customerId + ")", order.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Order a = new Order("O02", "C02", "CH002", now, 100.0, OrderStatus.values()[0]);
        Order b = new Order("O02", "C02", "CH002", now, 100.0, OrderStatus.values()[0]);
        Order c = new Order("O03", "C03", "CH003", now, 200.0, OrderStatus.values()[0]);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }

    @Test
    void testSetStatus() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order("O04", "C04", "CH004", now, 500.0, OrderStatus.values()[0]);
        OrderStatus newStatus = OrderStatus.values()[1];
        order.setStatus(newStatus);
        assertEquals(newStatus, order.getStatus());
    }
}