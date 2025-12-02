package com.dealership.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class InteractionEventTest {

    @Test
    void testGetters() {
        String eventId = "E01";
        String customerIdFk = "C01";
        InteractionEventType type = InteractionEventType.values()[0];
        LocalDateTime date = LocalDateTime.of(2025, 5, 14, 10, 30);
        String chassis = "CH123";
        String details = "Test details";

        InteractionEvent event = new InteractionEvent(
                eventId,
                customerIdFk,
                type,
                date,
                chassis,
                details
        );

        assertEquals(eventId, event.getEventId());
        assertEquals(customerIdFk, event.getCustomerIdFk());
        assertEquals(type, event.getEventType());
        assertEquals(date, event.getEventDate());
        assertEquals(chassis, event.getVehicleChassisNumberFk());
        assertEquals(details, event.getDetails());
    }

    @Test
    void testToStringWithChassis() {
        String eventId = "E02";
        String customerIdFk = "C02";
        InteractionEventType type = InteractionEventType.values()[0];
        LocalDateTime date = LocalDateTime.of(2025, 5, 14, 16, 45);
        String chassis = "CH456";
        String details = "Service completed";

        InteractionEvent event = new InteractionEvent(
                eventId,
                customerIdFk,
                type,
                date,
                chassis,
                details
        );

        String output = event.toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = date.format(formatter);

        assertTrue(output.contains(eventId));
        assertTrue(output.contains(type.getDisplayName()));
        assertTrue(output.contains("Tarih=" + formattedDate));
        assertTrue(output.contains("Araç Şase No=" + chassis));
        assertTrue(output.contains("Detay=" + details));
    }

    @Test
    void testToStringWithoutChassis() {
        String eventId = "E03";
        String customerIdFk = "C03";
        InteractionEventType type = InteractionEventType.values()[0];
        LocalDateTime date = LocalDateTime.of(2025, 5, 15, 9, 0);
        String details = "Initial contact";

        InteractionEvent event = new InteractionEvent(
                eventId,
                customerIdFk,
                type,
                date,
                null,
                details
        );

        String output = event.toString();

        assertTrue(output.contains(eventId));
        assertFalse(output.contains("Araç Şase No="));
        assertTrue(output.contains("Detay=" + details));
    }
}