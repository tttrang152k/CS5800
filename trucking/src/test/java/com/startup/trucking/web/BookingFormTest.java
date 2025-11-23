package com.startup.trucking.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookingFormTest {

    @Test
    void test_defaults_and_setters_getters() {
        BookingForm f = new BookingForm();
        // default status
        assertEquals("Requested", f.getStatus());

        f.setId("L-ABC");
        f.setReferenceNo("ACME");
        f.setStatus("Dispatched");
        f.setRateAmount(123.45f);
        f.setTrackingId("TRK-1");
        f.setRateConfirmationRef("RC-9");
        f.setDriverId("EMP-1");
        f.setTrailerId("TRL-2");
        f.setPickupAddress("123 A St");
        f.setDeliveryAddress("789 B Ave");
        f.setPickupDate("2025-01-01");
        f.setDeliveryDate("2025-01-02");

        assertEquals("L-ABC", f.getId());
        assertEquals("ACME", f.getReferenceNo());
        assertEquals("Dispatched", f.getStatus());
        assertEquals(123.45f, f.getRateAmount(), 0.0001);
        assertEquals("TRK-1", f.getTrackingId());
        assertEquals("RC-9", f.getRateConfirmationRef());
        assertEquals("EMP-1", f.getDriverId());
        assertEquals("TRL-2", f.getTrailerId());
        assertEquals("123 A St", f.getPickupAddress());
        assertEquals("789 B Ave", f.getDeliveryAddress());
        assertEquals("2025-01-01", f.getPickupDate());
        assertEquals("2025-01-02", f.getDeliveryDate());
    }
}
