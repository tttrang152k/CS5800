package com.startup.trucking.persistence;

import com.startup.trucking.domain.Load;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoadMapperTest {

    @Test
    void test_toEntity_scalesRateToTwoDecimals_HALF_UP() {
        Load domain = new Load(
                "L-100", "ACME", "Dispatched",
                1234.5678f, "TRK-1", "RC-9", "EMP-1", "TRL-1",
                "PU", "DEL", "2025-01-01", "2025-01-02"
        );

        LoadEntity e = LoadMapper.toEntity(domain);

        assertEquals("L-100", e.getId());
        assertEquals("ACME", e.getReferenceNo());
        assertEquals("Dispatched", e.getStatus());
        assertEquals(new BigDecimal("1234.57"), e.getRateAmount());
        assertEquals("TRK-1", e.getTrackingId());
        assertEquals("RC-9", e.getRateConfirmationRef());
        assertEquals("EMP-1", e.getDriverId());
        assertEquals("TRL-1", e.getTrailerId());
        assertEquals("PU", e.getPickupAddress());
        assertEquals("DEL", e.getDeliveryAddress());
        assertEquals("2025-01-01", e.getPickupDate());
        assertEquals("2025-01-02", e.getDeliveryDate());
    }

    @Test
    void test_toDomain_handlesNullRateAmountAsZero() {
        LoadEntity e = new LoadEntity();
        e.setId("L-101");
        e.setReferenceNo("ACME");
        e.setStatus("Requested");
        e.setRateAmount(null);
        e.setTrackingId("TRK-2");
        e.setRateConfirmationRef("RC-10");
        e.setDriverId("EMP-2");
        e.setTrailerId("TRL-2");
        e.setPickupAddress("A");
        e.setDeliveryAddress("B");
        e.setPickupDate("2025-02-01");
        e.setDeliveryDate("2025-02-02");

        Load d = LoadMapper.toDomain(e);

        assertNotNull(d);
        assertEquals("L-101", d.getId());
        assertEquals("ACME", d.getReferenceNo());
        assertEquals("Requested", d.getStatus());
        assertEquals(0.0f, d.getRateAmount(), 0.0001f);
        assertEquals("TRK-2", d.getTrackingId());
        assertEquals("RC-10", d.getRateConfirmationRef());
        assertEquals("EMP-2", d.getDriverId());
        assertEquals("TRL-2", d.getTrailerId());
        assertEquals("A", d.getPickupAddress());
        assertEquals("B", d.getDeliveryAddress());
        assertEquals("2025-02-01", d.getPickupDate());
        assertEquals("2025-02-02", d.getDeliveryDate());
    }

    @Test
    void test_roundTrip_domainToEntityToDomain_preservesCoreFields() {
        Load original = new Load(
                "L-200", "Landstar", "Delivered",
                2100.0f, "TRK-9", "RC-77", "EMP-9", "TRL-77",
                "PU-Addr", "DEL-Addr", "2025-03-01", "2025-03-03"
        );

        LoadEntity entity = LoadMapper.toEntity(original);
        Load mappedBack = LoadMapper.toDomain(entity);

        assertEquals(original.getId(), mappedBack.getId());
        assertEquals(original.getReferenceNo(), mappedBack.getReferenceNo());
        assertEquals(original.getStatus(), mappedBack.getStatus());
        assertEquals(original.getTrackingId(), mappedBack.getTrackingId());
        assertEquals(original.getRateConfirmationRef(), mappedBack.getRateConfirmationRef());
        assertEquals(original.getDriverId(), mappedBack.getDriverId());
        assertEquals(original.getTrailerId(), mappedBack.getTrailerId());
        assertEquals(original.getPickupAddress(), mappedBack.getPickupAddress());
        assertEquals(original.getDeliveryAddress(), mappedBack.getDeliveryAddress());
        assertEquals(original.getPickupDate(), mappedBack.getPickupDate());
        assertEquals(original.getDeliveryDate(), mappedBack.getDeliveryDate());
        assertEquals(original.getRateAmount(), mappedBack.getRateAmount(), 0.0001f);
    }

    @Test
    void test_toDomain_nullEntity_returnsNull() {
        assertNull(LoadMapper.toDomain(null));
    }
}
