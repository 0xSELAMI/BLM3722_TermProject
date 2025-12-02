package com.dealership.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TestDriveTest {

    @Test
    void testGettersDefaultDurationAndToString() {
        String id = "TD01";
        String customerId = "C01";
        String chassis = "ABCDEFG123456";
        LocalDateTime scheduled = LocalDateTime.of(2025, 5, 14, 9, 0);
        TestDriveStatus status = TestDriveStatus.SCHEDULED;
        TestDrive td = new TestDrive(id, customerId, chassis, scheduled, status);

        assertEquals(id, td.getTestDriveId());
        assertEquals(customerId, td.getCustomerIdFk());
        assertEquals(chassis, td.getVehicleChassisNumberFk());
        assertEquals(scheduled, td.getScheduledDateTime());
        assertEquals(status, td.getStatus());
        assertEquals(60, td.getDurationMinutes());

        String str = td.toString();
        assertTrue(str.contains("TD ID: " + id));
        assertTrue(str.contains("Müşteri: " + customerId));
        assertTrue(str.contains("Araç: 123456"));
    }

    @Test
    void testCustomConstructorAndEndDateTime() {
        String id = "TD02";
        String customerId = "C02";
        String chassis = "CH02";
        LocalDateTime scheduled = LocalDateTime.of(2025, 5, 15, 10, 0);
        TestDriveStatus status = TestDriveStatus.COMPLETED;
        int duration = 90;
        TestDrive td = new TestDrive(id, customerId, chassis, scheduled, status, duration);

        assertEquals(duration, td.getDurationMinutes());
        assertEquals(scheduled.plusMinutes(duration), td.getEndDateTime());
    }

    @Test
    void testEqualsHashCodeAndSetters() {
        LocalDateTime now = LocalDateTime.of(2025, 5, 16, 11, 0);
        TestDrive a = new TestDrive("TD03", "C03", "CH03", now, TestDriveStatus.SCHEDULED, 45);
        TestDrive b = new TestDrive("TD03", "CXX", "CHXX", now.plusDays(1), TestDriveStatus.COMPLETED, 120);
        TestDrive c = new TestDrive("TD04", "C03", "CH03", now, TestDriveStatus.SCHEDULED, 45);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a, null);
        assertNotEquals(a, "string");

        a.setStatus(TestDriveStatus.CANCELLED);
        assertEquals(TestDriveStatus.CANCELLED, a.getStatus());
        a.setDurationMinutes(30);
        assertEquals(30, a.getDurationMinutes());
    }
}