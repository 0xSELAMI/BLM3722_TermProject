package com.dealership.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void testGetAndSetMethods() {
        Customer customer = new Customer("C01", "Ayse", "ayse@ornek.com");
        assertEquals("C01", customer.getCustomerId());
        assertEquals("Ayse", customer.getName());
        assertEquals("ayse@ornek.com", customer.getContactInfo());

        customer.setCustomerId("C02");
        customer.setName("Mehmet");
        customer.setContactInfo("mehmet@ornek.com");
        assertEquals("C02", customer.getCustomerId());
        assertEquals("Mehmet", customer.getName());
        assertEquals("mehmet@ornek.com", customer.getContactInfo());
    }

    @Test
    void testToString() {
        Customer customer = new Customer("C10", "Can", "can@ornek.com");
        assertEquals("Can (C10)", customer.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Customer a = new Customer("X1", "Ali", "ali@x.com");
        Customer b = new Customer("X1", "Ali", "ali@x.com");
        Customer c = new Customer("Y2", "Veli", "veli@y.com");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());

        assertEquals(a, a);

        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }
}