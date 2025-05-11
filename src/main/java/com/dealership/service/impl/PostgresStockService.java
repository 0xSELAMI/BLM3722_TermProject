package com.dealership.service.impl;

import com.dealership.db.DatabaseUtil;
import com.dealership.model.Vehicle;
import com.dealership.model.VehicleStatus;
import com.dealership.service.StockService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresStockService implements StockService {
    @Override
    public boolean addVehicleToStock(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicles (chassisNumber, brand, model, year, carPackage, status, price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getChassisNumber());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setInt(4, vehicle.getYear());
            pstmt.setString(5, vehicle.getCarPackage());
            pstmt.setString(6, vehicle.getStatus().name());
            pstmt.setDouble(7, vehicle.getPrice());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Araç eklenirken SQL Hatası: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Vehicle findVehicleByChassisNumber(String chassisNumber) {
        String sql = "SELECT * FROM Vehicles WHERE chassisNumber = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chassisNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Vehicle(
                    rs.getString("chassisNumber"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("carPackage"),
                    VehicleStatus.valueOf(rs.getString("status")),
                    rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            System.err.println("Araç bulunurken SQL Hatası: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicles ORDER BY brand, model";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                 vehicles.add(new Vehicle(
                    rs.getString("chassisNumber"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("carPackage"),
                    VehicleStatus.valueOf(rs.getString("status")),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Tüm araçlar alınırken SQL Hatası: " + e.getMessage());
        }
        return vehicles;
    }

    @Override
    public List<Vehicle> queryStock(String brand, String model, Integer year, String carPackage) {
        List<Vehicle> vehicles = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM Vehicles WHERE (status = ? OR status = ? OR status = ?)");
        List<Object> params = new ArrayList<>();
        params.add(VehicleStatus.IN_STOCK.name());
        params.add(VehicleStatus.IN_SHOWROOM_DISPLAY.name());
        params.add(VehicleStatus.IN_SHOWROOM_ORDER.name());

        if (brand != null && !brand.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(brand) LIKE LOWER(?)");
            params.add("%" + brand.trim() + "%");
        }
        if (model != null && !model.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(model) LIKE LOWER(?)");
            params.add("%" + model.trim() + "%");
        }
        if (year != null && year > 0) {
            sqlBuilder.append(" AND year = ?");
            params.add(year);
        }
        if (carPackage != null && !carPackage.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(carPackage) LIKE LOWER(?)");
            params.add("%" + carPackage.trim() + "%");
        }
        sqlBuilder.append(" ORDER BY brand, model");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getString("chassisNumber"),
                    rs.getString("brand"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getString("carPackage"),
                    VehicleStatus.valueOf(rs.getString("status")),
                    rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Stok sorgulanırken SQL Hatası: " + e.getMessage());
        }
        return vehicles;
    }

    @Override
    public boolean updateVehicleStatus(String chassisNumber, VehicleStatus newStatus) {
        String sql = "UPDATE Vehicles SET status = ? WHERE chassisNumber = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, chassisNumber);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Araç durumu güncellenirken SQL Hatası: " + e.getMessage());
            return false;
        }
    }
}
