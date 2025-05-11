package com.dealership.service.impl;

import com.dealership.db.DatabaseUtil;
import com.dealership.model.*;
import com.dealership.service.CustomerService;
import com.dealership.service.StockService;
import com.dealership.service.TestDriveService;

import javax.swing.JOptionPane;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostgresTestDriveService implements TestDriveService {
    private final CustomerService customerService;
    private final StockService stockService;

    public PostgresTestDriveService(CustomerService customerService, StockService stockService) {
        this.customerService = customerService;
        this.stockService = stockService;
    }

    @Override
    public boolean hasOverlappingTestDrive(String vehicleChassisNumber, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        String sql = "SELECT COUNT(*) FROM TestDrives " +
                     "WHERE vehicleChassisNumberFk = ? " +
                     "AND status = ? " +
                     "AND scheduledDateTime < ? " +
                     "AND (scheduledDateTime + (durationMinutes * INTERVAL '1 minute')) > ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleChassisNumber);
            pstmt.setString(2, TestDriveStatus.SCHEDULED.name());
            pstmt.setTimestamp(3, Timestamp.valueOf(newEndTime));
            pstmt.setTimestamp(4, Timestamp.valueOf(newStartTime));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Çakışan test sürüşü kontrol edilirken SQL Hatası: " + e.getMessage());
        }
        return false;
    }

    @Override
    public TestDrive scheduleTestDrive(String customerId, String vehicleChassisNumber, LocalDateTime scheduledDateTime, int durationMinutes) {
        Vehicle vehicle = stockService.findVehicleByChassisNumber(vehicleChassisNumber);
        if (vehicle == null || (vehicle.getStatus() != VehicleStatus.IN_STOCK && vehicle.getStatus() != VehicleStatus.IN_SHOWROOM_DISPLAY)) {
            System.err.println("Test sürüşü için araç uygun değil veya bulunamadı. Araç Durumu: " + (vehicle != null ? vehicle.getStatus() : "Bulunamadı"));
            return null;
        }

        LocalDateTime scheduledEndTime = scheduledDateTime.plusMinutes(durationMinutes);
        if (hasOverlappingTestDrive(vehicleChassisNumber, scheduledDateTime, scheduledEndTime)) {
            System.err.println("Bu araç için belirtilen zamanda zaten planlanmış bir test sürüşü var.");
            JOptionPane.showMessageDialog(null, "Bu araç için belirtilen zamanda zaten planlanmış bir test sürüşü var.", "Test Sürüşü Çakışması", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String testDriveId = "TD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        TestDriveStatus status = TestDriveStatus.SCHEDULED;
        String sql = "INSERT INTO TestDrives (testDriveId, customerIdFk, vehicleChassisNumberFk, scheduledDateTime, status, durationMinutes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, testDriveId);
            pstmt.setString(2, customerId);
            pstmt.setString(3, vehicleChassisNumber);
            pstmt.setTimestamp(4, Timestamp.valueOf(scheduledDateTime));
            pstmt.setString(5, status.name());
            pstmt.setInt(6, durationMinutes);
            if (pstmt.executeUpdate() > 0) {
                String eventId = "EVT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
                InteractionEvent event = new InteractionEvent(eventId, customerId, InteractionEventType.TEST_DRIVE_SCHEDULED, LocalDateTime.now(), vehicleChassisNumber, "Test sürüşü planlandı: " + scheduledDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ", Süre: " + durationMinutes + " dk");
                customerService.addInteractionEvent(event);
                return new TestDrive(testDriveId, customerId, vehicleChassisNumber, scheduledDateTime, status, durationMinutes);
            }
        } catch (SQLException e) {
            System.err.println("Test sürüşü planlanırken SQL Hatası: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Test sürüşü planlanırken bir veritabanı hatası oluştu: " + e.getMessage(), "Veritabanı Hatası", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

     @Override
    public boolean updateTestDriveStatus(String testDriveId, TestDriveStatus newStatus) {
        TestDrive testDrive = findTestDriveById(testDriveId);
        if (testDrive == null) return false;

        String sql = "UPDATE TestDrives SET status = ? WHERE testDriveId = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, testDriveId);
            if (pstmt.executeUpdate() > 0) {
                if (newStatus == TestDriveStatus.COMPLETED) {
                    String eventId = "EVT-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
                    InteractionEvent event = new InteractionEvent(eventId, testDrive.getCustomerIdFk(), InteractionEventType.TEST_DRIVE_COMPLETED, LocalDateTime.now(), testDrive.getVehicleChassisNumberFk(), "Test sürüşü tamamlandı.");
                    customerService.addInteractionEvent(event);
                }
                return true;
            }
        } catch (SQLException e) { System.err.println("Test sürüşü durumu güncellenirken SQL Hatası: " + e.getMessage()); }
        return false;
    }
    @Override
    public TestDrive findTestDriveById(String testDriveId) {
        String sql = "SELECT * FROM TestDrives WHERE testDriveId = ?";
        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, testDriveId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new TestDrive(
                    rs.getString("testDriveId"),
                    rs.getString("customerIdFk"),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getTimestamp("scheduledDateTime").toLocalDateTime(),
                    TestDriveStatus.valueOf(rs.getString("status")),
                    rs.getInt("durationMinutes")
                );
            }
        } catch (SQLException e) { System.err.println("Test sürüşü bulunurken SQL Hatası: " + e.getMessage()); }
        return null;
    }
    @Override
    public List<TestDrive> getAllTestDrives() {
         List<TestDrive> testDrives = new ArrayList<>();
        String sql = "SELECT * FROM TestDrives ORDER BY scheduledDateTime DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                 testDrives.add(new TestDrive(
                    rs.getString("testDriveId"),
                    rs.getString("customerIdFk"),
                    rs.getString("vehicleChassisNumberFk"),
                    rs.getTimestamp("scheduledDateTime").toLocalDateTime(),
                    TestDriveStatus.valueOf(rs.getString("status")),
                    rs.getInt("durationMinutes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Tüm test sürüşleri alınırken SQL Hatası: " + e.getMessage());
        }
        return testDrives;
    }
}
