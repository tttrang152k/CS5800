package com.startup.trucking.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadTest {

    private Load makeSample() {
        return new Load(
                "L-100", "ACME", "Requested", 1500.25f, "TRK-1",
                "RC-9", "EMP-42", "TRL-7",
                "123 Pickup", "456 Delivery", "2025-01-01", "2025-01-05"
        );
    }

    @Test
    void test_constructor_setsAllFields() {
        var l = makeSample();
        assertEquals("L-100", l.getId());
        assertEquals("ACME", l.getReferenceNo());
        assertEquals("Requested", l.getStatus());
        assertEquals(1500.25f, l.getRateAmount(), 0.0001);
        assertEquals("TRK-1", l.getTrackingId());
        assertEquals("RC-9", l.getRateConfirmationRef());
        assertEquals("EMP-42", l.getDriverId());
        assertEquals("TRL-7", l.getTrailerId());
        assertEquals("123 Pickup", l.getPickupAddress());
        assertEquals("456 Delivery", l.getDeliveryAddress());
        assertEquals("2025-01-01", l.getPickupDate());
        assertEquals("2025-01-05", l.getDeliveryDate());
    }

    @Test
    void test_updateStatus_changesStatus() {
        var l = makeSample();
        l.updateStatus("Dispatched");
        assertEquals("Dispatched", l.getStatus());
        l.updateStatus("Delivered");
        assertEquals("Delivered", l.getStatus());
    }


    @Test void test_getId() { assertEquals("L-100", makeSample().getId()); }

    @Test void test_getStatus() { assertEquals("Requested", makeSample().getStatus()); }

    @Test void test_getDriverId() { assertEquals("EMP-42", makeSample().getDriverId()); }

    @Test void test_getTrailerId() { assertEquals("TRL-7", makeSample().getTrailerId()); }

    @Test void test_getReferenceNo() { assertEquals("ACME", makeSample().getReferenceNo()); }

    @Test void test_getPickupAddress() { assertEquals("123 Pickup", makeSample().getPickupAddress()); }

    @Test void test_getDeliveryAddress() { assertEquals("456 Delivery", makeSample().getDeliveryAddress()); }

    @Test void test_getPickupDate() { assertEquals("2025-01-01", makeSample().getPickupDate()); }

    @Test void test_getDeliveryDate() { assertEquals("2025-01-05", makeSample().getDeliveryDate()); }

    @Test void test_getRateAmount() { assertEquals(1500.25f, makeSample().getRateAmount(), 0.0001); }

    @Test void test_getTrackingId() { assertEquals("TRK-1", makeSample().getTrackingId()); }

    @Test void test_getRateConfirmationRef() { assertEquals("RC-9", makeSample().getRateConfirmationRef()); }
}
