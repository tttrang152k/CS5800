package com.startup.trucking.persistence;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    @Test
    void test_settersAndGetters_roundTrip() {
        Invoice i = new Invoice();
        OffsetDateTime now = OffsetDateTime.now();

        i.setId("INV-123");
        i.setLoadId("L-100");
        i.setIssuedAt(now);
        i.setStatus("Draft");
        i.setSubtotal(new BigDecimal("1500.00"));
        i.setTax(new BigDecimal("0.00"));
        i.setTotal(new BigDecimal("1500.00"));
        i.setCustomerRef("ACME");

        assertEquals("INV-123", i.getId());
        assertEquals("L-100", i.getLoadId());
        assertEquals(now, i.getIssuedAt());
        assertEquals("Draft", i.getStatus());
        assertEquals(new BigDecimal("1500.00"), i.getSubtotal());
        assertEquals(new BigDecimal("0.00"), i.getTax());
        assertEquals(new BigDecimal("1500.00"), i.getTotal());
        assertEquals("ACME", i.getCustomerRef());
    }

    @Test
    void test_status_update() {
        Invoice i = new Invoice();
        i.setStatus("Draft");
        assertEquals("Draft", i.getStatus());
        i.setStatus("Sent");
        assertEquals("Sent", i.getStatus());
    }
}
