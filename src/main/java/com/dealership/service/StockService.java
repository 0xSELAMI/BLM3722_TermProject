package com.dealership.service;

import com.dealership.model.Vehicle;
import com.dealership.model.VehicleStatus;
import java.util.List;

public interface StockService {
    boolean addVehicleToStock(Vehicle vehicle);
    Vehicle findVehicleByChassisNumber(String chassisNumber);
    List<Vehicle> queryStock(String brand, String model, Integer year, String carPackage);
    boolean updateVehicleStatus(String chassisNumber, VehicleStatus newStatus);
    List<Vehicle> getAllVehicles();
}
