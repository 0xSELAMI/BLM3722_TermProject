package com.dealership.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void testGettersSettersAndToString() {
        Vehicle v = new Vehicle("1234567890", "Toyota", "Corolla", 2020, "Premium", 30000.0);
        assertEquals("1234567890", v.getChassisNumber());
        assertEquals("Toyota", v.getBrand());
        assertEquals("Corolla", v.getModel());
        assertEquals(2020, v.getYear());
        assertEquals("Premium", v.getCarPackage());
        assertEquals(VehicleStatus.IN_STOCK, v.getStatus());
        assertEquals(30000.0, v.getPrice());
        assertEquals("Toyota Corolla (567890)", v.toString());

        v.setChassisNumber("CHID");
        v.setBrand("Honda");
        v.setModel("Civic");
        v.setYear(2021);
        v.setCarPackage("Standard");
        v.setStatus(VehicleStatus.SOLD);
        v.setPrice(25000.0);
        assertEquals("CHID", v.getChassisNumber());
        assertEquals("Honda", v.getBrand());
        assertEquals("Civic", v.getModel());
        assertEquals(2021, v.getYear());
        assertEquals("Standard", v.getCarPackage());
        assertEquals(VehicleStatus.SOLD, v.getStatus());
        assertEquals(25000.0, v.getPrice());
        assertEquals("Honda Civic (CHID)", v.toString());
    }

    @Test
    void testToStringNoVehicleId() {
        Vehicle v = new Vehicle("NO_VEHICLE_ID", "Ford", "Focus", 2019, "Eco", 20000.0);
        assertEquals("Focus", v.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        Vehicle a = new Vehicle("CH001", "BMW", "X5", 2022, "Luxury", 60000.0);
        Vehicle b = new Vehicle("CH001", "Audi", "Q7", 2023, "Sport", 65000.0);
        Vehicle c = new Vehicle("CH002", "BMW", "X5", 2022, "Luxury", 60000.0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertNotEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
    }
}