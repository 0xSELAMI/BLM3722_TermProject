package com.dealership.service.impl;

import com.dealership.db.DatabaseUtil;
import com.dealership.model.InteractionEvent;
import com.dealership.model.InteractionEventType;
import com.dealership.model.Quote;
import com.dealership.service.CustomerService;
import com.dealership.service.QuoteService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresQuoteService implements QuoteService {
    private final CustomerService customerService;

    public PostgresQuoteService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public Quote createQuote(String customerId, String vehicleChassisNumber, double price, LocalDate validUntil) {
        String quoteId = "QT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime quoteDate = LocalDateTime.now();
        String sql = "INSERT INTO Quotes (quoteId, customerIdFk, vehicleChassisNumberFk, quoteDate, price, validUntil) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quoteId);
            pstmt.setString(2, customerId);
            if (vehicleChassisNumber != null) {
                pstmt.setString(3, vehicleChassisNumber);
            } else {
                pstmt.setNull(3, Types.VARCHAR);
            }
            pstmt.setTimestamp(4, Timestamp.valueOf(quoteDate));
            pstmt.setDouble(5, price);
            pstmt.setDate(6, Date.valueOf(validUntil));
            if (pstmt.executeUpdate() > 0) {
                String eventId = "EVT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
                InteractionEvent event = new InteractionEvent(eventId, customerId, InteractionEventType.QUOTE_GIVEN, LocalDateTime.now(), vehicleChassisNumber, "Fiyat teklifi verildi. Fiyat: " + price + ", Geçerlilik: " + validUntil.toString());
                customerService.addInteractionEvent(event);
                return new Quote(quoteId, customerId, vehicleChassisNumber, quoteDate, price, validUntil);
            }
        } catch (SQLException e) { System.err.println("Fiyat teklifi oluşturulurken SQL Hatası: " + e.getMessage()); }
        return null;
    }
    @Override
    public Quote findQuoteById(String quoteId) {
         String sql = "SELECT * FROM Quotes WHERE quoteId = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quoteId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Quote(
                    rs.getString("quoteId"),
                    rs.getString("customerIdFk"),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getTimestamp("quoteDate").toLocalDateTime(),
                    rs.getDouble("price"),
                    rs.getDate("validUntil").toLocalDate()
                );
            }
        } catch (SQLException e) { System.err.println("Fiyat teklifi bulunurken SQL Hatası: " + e.getMessage()); }
        return null;
    }
    @Override
    public List<Quote> getAllQuotes() {
        List<Quote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM Quotes ORDER BY quoteDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quotes.add(new Quote(
                    rs.getString("quoteId"),
                    rs.getString("customerIdFk"),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getTimestamp("quoteDate").toLocalDateTime(),
                    rs.getDouble("price"),
                    rs.getDate("validUntil").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            System.err.println("Tüm fiyat teklifleri alınırken SQL Hatası: " + e.getMessage());
        }
        return quotes;
    }
}
