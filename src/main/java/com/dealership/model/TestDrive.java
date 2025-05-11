package com.dealership.model;

import java.time.LocalDateTime;

public class TestDrive {
    private String testDriveId;
    private String customerIdFk;
    private String vehicleChassisNumberFk;
    private LocalDateTime scheduledDateTime;
    private TestDriveStatus status;
    private int durationMinutes;


    public TestDrive(String testDriveId, String customerIdFk, String vehicleChassisNumberFk, LocalDateTime scheduledDateTime, TestDriveStatus status) {
        this(testDriveId, customerIdFk, vehicleChassisNumberFk, scheduledDateTime, status, 60);
    }

    public TestDrive(String testDriveId, String customerIdFk, String vehicleChassisNumberFk, LocalDateTime scheduledDateTime, TestDriveStatus status, int durationMinutes) {
        this.testDriveId = testDriveId;
        this.customerIdFk = customerIdFk;
        this.vehicleChassisNumberFk = vehicleChassisNumberFk;
        this.scheduledDateTime = scheduledDateTime;
        this.status = status;
        this.durationMinutes = durationMinutes;
    }

    public String getTestDriveId() { return testDriveId; }
    public String getCustomerIdFk() { return customerIdFk; }
    public String getVehicleChassisNumberFk() { return vehicleChassisNumberFk; }
    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }
    public TestDriveStatus getStatus() { return status; }
    public void setStatus(TestDriveStatus status) { this.status = status; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public LocalDateTime getEndDateTime() {
        return scheduledDateTime.plusMinutes(durationMinutes);
    }

    @Override
    public String toString() {
        String vehiclePart = vehicleChassisNumberFk != null && vehicleChassisNumberFk.length() > 6 ? vehicleChassisNumberFk.substring(vehicleChassisNumberFk.length() - 6) : vehicleChassisNumberFk;
         return "TD ID: " + testDriveId + " (Müşteri: " + customerIdFk + ", Araç: " + vehiclePart + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TestDrive testDrive = (TestDrive) obj;
        return testDriveId != null ? testDriveId.equals(testDrive.testDriveId) : testDrive.testDriveId == null;
    }

    @Override
    public int hashCode() {
        return testDriveId != null ? testDriveId.hashCode() : 0;
    }
}
