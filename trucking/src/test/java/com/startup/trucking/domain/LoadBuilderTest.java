package com.startup.trucking.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadBuilderTest {

    @Test
    void test_createLoad_success_setsAllProvidedFields() {
        var l = new LoadBuilder()
                .setId("L-200")
                .setReferenceNo("Landstar")
                .setStatus("Assigned")
                .setRateAmount(2100.0f)
                .setTrackingId("TRK-9")
                .setRateConfirmationRef("RC-22")
                .setDriverId("EMP-77")
                .setTrailerId("TRL-22")
                .setPickupAddress("10 Port Rd")
                .setDeliveryAddress("99 Warehouse")
                .setPickupDate("2025-02-01")
                .setDeliveryDate("2025-02-05")
                .createLoad();

        assertEquals("L-200", l.getId());
        assertEquals("Landstar", l.getReferenceNo());
        assertEquals("Assigned", l.getStatus());
        assertEquals(2100.0f, l.getRateAmount(), 0.0001);
        assertEquals("TRK-9", l.getTrackingId());
        assertEquals("RC-22", l.getRateConfirmationRef());
        assertEquals("EMP-77", l.getDriverId());
        assertEquals("TRL-22", l.getTrailerId());
        assertEquals("10 Port Rd", l.getPickupAddress());
        assertEquals("99 Warehouse", l.getDeliveryAddress());
        assertEquals("2025-02-01", l.getPickupDate());
        assertEquals("2025-02-05", l.getDeliveryDate());
    }

    @Test
    void test_createLoad_throwsWhenIdMissing() {
        var b = new LoadBuilder()
                .setReferenceNo("ACME")
                .setRateAmount(1000f);
        var ex = assertThrows(IllegalArgumentException.class, b::createLoad);
        assertTrue(ex.getMessage().toLowerCase().contains("id"));
    }

    @Test
    void test_defaultStatus_isRequested_whenNotSet() {
        var l = new LoadBuilder()
                .setId("L-300")
                .setReferenceNo("ACME")
                .setRateAmount(500f)
                .createLoad();

        assertEquals("Requested", l.getStatus());
    }

    @Test
    void test_builder_isFluent_returnsSameBuilderInstance() {
        var b = new LoadBuilder();
        assertSame(b, b.setId("L-1"));
        assertSame(b, b.setReferenceNo("Ref"));
        assertSame(b, b.setStatus("Assigned"));
        assertSame(b, b.setRateAmount(1.23f));
        assertSame(b, b.setTrackingId("T"));
        assertSame(b, b.setRateConfirmationRef("RC"));
        assertSame(b, b.setDriverId("D"));
        assertSame(b, b.setTrailerId("TR"));
        assertSame(b, b.setPickupAddress("P"));
        assertSame(b, b.setDeliveryAddress("D"));
        assertSame(b, b.setPickupDate("2025-01-01"));
        assertSame(b, b.setDeliveryDate("2025-01-02"));
    }
}
