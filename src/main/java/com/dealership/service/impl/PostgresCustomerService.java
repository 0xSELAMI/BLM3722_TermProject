package com.dealership.service.impl;

import com.dealership.db.DatabaseUtil;
import com.dealership.model.Customer;
import com.dealership.model.InteractionEvent;
import com.dealership.model.InteractionEventType;
import com.dealership.service.CustomerService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresCustomerService implements CustomerService {
    @Override
    public boolean registerCustomer(Customer customer) {
        String sql = "INSERT INTO Customers (customerId, name, contactInfo) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getContactInfo());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Müşteri kaydedilirken SQL Hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Customer findCustomerById(String customerId) {
        String sql = "SELECT * FROM Customers WHERE customerId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(rs.getString("customerId"), rs.getString("name"), rs.getString("contactInfo"));
            }
        } catch (SQLException e) {
            System.err.println("Müşteri bulunurken SQL Hatası: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY name";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(rs.getString("customerId"), rs.getString("name"), rs.getString("contactInfo")));
            }
        } catch (SQLException e) {
            System.err.println("Tüm müşteriler alınırken SQL Hatası: " + e.getMessage());
        }
        return customers;
    }

    @Override
    public boolean addInteractionEvent(InteractionEvent event) {
        String sql = "INSERT INTO InteractionEvents (eventId, customerIdFk, eventType, eventDate, vehicleChassisNumberFk, details) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.getEventId());
            pstmt.setString(2, event.getCustomerIdFk());
            pstmt.setString(3, event.getEventType().name());
            pstmt.setTimestamp(4, Timestamp.valueOf(event.getEventDate()));
            if (event.getVehicleChassisNumberFk() != null) {
                pstmt.setString(5, event.getVehicleChassisNumberFk());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }
            pstmt.setString(6, event.getDetails());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Etkileşim eklenirken SQL Hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<InteractionEvent> getInteractionHistoryForCustomer(String customerId) {
        List<InteractionEvent> history = new ArrayList<>();
        String sql = "SELECT * FROM InteractionEvents WHERE customerIdFk = ? ORDER BY eventDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
                history.add(new InteractionEvent(
                    rs.getString("eventId"),
                    rs.getString("customerIdFk"),
                    InteractionEventType.valueOf(rs.getString("eventType")),
                    rs.getTimestamp("eventDate").toLocalDateTime(),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getString("details")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Etkileşim geçmişi alınırken SQL Hatası: " + e.getMessage());
        }
        return history;
    }
}
