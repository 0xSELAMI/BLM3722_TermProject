package com.dealership.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class QuoteTest {

    @Test
    void testGettersAndToStringWithChassis() {
        String quoteId = "Q01";
        String customerId = "C01";
        String chassis = "CH001";
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 14, 14, 30);
        double price = 2500.50;
        LocalDate validUntil = LocalDate.of(2025, 6, 14);

        Quote quote = new Quote(
                quoteId,
                customerId,
                chassis,
                dateTime,
                price,
                validUntil
        );

        assertEquals(quoteId, quote.getQuoteId());
        assertEquals(customerId, quote.getCustomerIdFk());
        assertEquals(chassis, quote.getVehicleChassisNumberFk());
        assertEquals(dateTime, quote.getQuoteDate());
        assertEquals(price, quote.getPrice());
        assertEquals(validUntil, quote.getValidUntil());

        String output = quote.toString();
        String datePart = dateTime.toLocalDate().toString();
        String validPart = validUntil.toString();

        assertTrue(output.contains("ID=" + quoteId));
        assertTrue(output.contains("Müşteri ID=" + customerId));
        assertTrue(output.contains("Araç Şase No=" + chassis));
        assertTrue(output.contains("Tarih=" + datePart));
        assertTrue(output.contains("Fiyat=" + price));
        assertTrue(output.contains("Geçerlilik=" + validPart));
    }

    @Test
    void testToStringWithoutChassis() {
        Quote quote = new Quote(
                "Q02",
                "C02",
                null,
                LocalDateTime.of(2025, 5, 15, 9, 0),
                1800.0,
                LocalDate.of(2025, 6, 15)
        );

        String output = quote.toString();

        assertTrue(output.contains("ID=Q02"));
        assertTrue(output.contains("Müşteri ID=C02"));
        assertFalse(output.contains("Araç Şase No="));
        assertTrue(output.contains("Fiyat=1800.0"));
        assertTrue(output.contains("Geçerlilik=2025-06-15"));
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate until = LocalDate.now().plusDays(30);

        Quote a = new Quote("Q03", "C03", "CH003", now, 1000.0, until);
        Quote b = new Quote("Q03", "C03", "CH003", now, 1200.0, until);
        Quote c = new Quote("Q04", "C04", "CH004", now, 1000.0, until);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }
}