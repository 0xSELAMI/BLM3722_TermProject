package com.dealership.service;

import com.dealership.model.Order;
import com.dealership.model.OrderStatus;
import java.util.List;

public interface OrderService {
    Order placeOrder(String customerId, String vehicleChassisNumber, double orderPrice);
    boolean updateOrderStatus(String orderId, OrderStatus newStatus);
    Order findOrderById(String orderId);
    List<Order> getAllOrders();
    long getTotalDeliveredSalesCount();
}
