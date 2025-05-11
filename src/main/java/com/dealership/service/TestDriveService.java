package com.dealership.service;

import com.dealership.model.TestDrive;
import com.dealership.model.TestDriveStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface TestDriveService {
    TestDrive scheduleTestDrive(String customerId, String vehicleChassisNumber, LocalDateTime scheduledDateTime, int durationMinutes);
    boolean updateTestDriveStatus(String testDriveId, TestDriveStatus status);
    List<TestDrive> getAllTestDrives();
    TestDrive findTestDriveById(String testDriveId);
    boolean hasOverlappingTestDrive(String vehicleChassisNumber, LocalDateTime newStartTime, LocalDateTime newEndTime);
}
