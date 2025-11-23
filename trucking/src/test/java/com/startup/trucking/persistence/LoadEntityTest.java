package com.startup.trucking.persistence;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoadEntityTest {

    @Test
    void test_settersAndGetters_allFields() {
        LoadEntity e = new LoadEntity();

        e.setId("L-100");
        e.setReferenceNo("ACME");
        e.setStatus("Delivered");
        e.setRateAmount(new BigDecimal("2100.50"));
        e.setTrackingId("TRK-1");
        e.setRateConfirmationRef("RC-123");
        e.setDriverId("EMP-42");
        e.setTrailerId("TRL-9");
        e.setPickupAddress("123 Pickup St");
        e.setDeliveryAddress("456 Delivery Ave");
        e.setPickupDate("2025-10-01");
        e.setDeliveryDate("2025-10-05");

        assertEquals("L-100", e.getId());
        assertEquals("ACME", e.getReferenceNo());
        assertEquals("Delivered", e.getStatus());
        assertEquals(new BigDecimal("2100.50"), e.getRateAmount());
        assertEquals("TRK-1", e.getTrackingId());
        assertEquals("RC-123", e.getRateConfirmationRef());
        assertEquals("EMP-42", e.getDriverId());
        assertEquals("TRL-9", e.getTrailerId());
        assertEquals("123 Pickup St", e.getPickupAddress());
        assertEquals("456 Delivery Ave", e.getDeliveryAddress());
        assertEquals("2025-10-01", e.getPickupDate());
        assertEquals("2025-10-05", e.getDeliveryDate());
    }
}
