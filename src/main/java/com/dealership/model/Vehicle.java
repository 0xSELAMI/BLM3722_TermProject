package com.dealership.model;

public class Vehicle {
    private String chassisNumber;
    private String brand;
    private String model;
    private int year;
    private String carPackage;
    private VehicleStatus status;
    private double price;

    public Vehicle(String chassisNumber, String brand, String model, int year, String carPackage, double price) {
        this.chassisNumber = chassisNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.carPackage = carPackage;
        this.status = VehicleStatus.IN_STOCK;
        this.price = price;
    }
    public Vehicle(String chassisNumber, String brand, String model, int year, String carPackage, VehicleStatus status, double price) {
        this.chassisNumber = chassisNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.carPackage = carPackage;
        this.status = status;
        this.price = price;
    }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getCarPackage() { return carPackage; }
    public void setCarPackage(String carPackage) { this.carPackage = carPackage; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        if ("NO_VEHICLE_ID".equals(chassisNumber)) {
            return model;
        }
        return brand + " " + model + " (" + (chassisNumber != null && chassisNumber.length() > 6 ? chassisNumber.substring(chassisNumber.length() - 6) : chassisNumber) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return chassisNumber != null ? chassisNumber.equals(vehicle.chassisNumber) : vehicle.chassisNumber == null;
    }

    @Override
    public int hashCode() {
        return chassisNumber != null ? chassisNumber.hashCode() : 0;
    }
}
