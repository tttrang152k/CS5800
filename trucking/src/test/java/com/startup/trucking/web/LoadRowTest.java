package com.startup.trucking.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadRowTest {

    @Test
    void test_constructor_sets_all_fields() {
        LoadRow r = new LoadRow(
                "L-1", "ACME", "Delivered", 100.5f, "EMP-1",
                "PU", "DEL", "2025-01-01", "2025-01-02",
                "https://doc", true, "INV-1"
        );

        assertEquals("L-1", r.id);
        assertEquals("ACME", r.customerName);
        assertEquals("Delivered", r.status);
        assertEquals(100.5f, r.rateAmount, 0.0001);
        assertEquals("EMP-1", r.driver);
        assertEquals("PU", r.pickupAddress);
        assertEquals("DEL", r.deliveryAddress);
        assertEquals("2025-01-01", r.pickupDate);
        assertEquals("2025-01-02", r.deliveryDate);
        assertEquals("https://doc", r.documentUrl);
        assertTrue(r.invoiced);
        assertEquals("INV-1", r.invoiceId);
    }
}
