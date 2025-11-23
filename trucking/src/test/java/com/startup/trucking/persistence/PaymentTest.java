package com.startup.trucking.persistence;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void test_settersAndGetters_roundTrip() {
        Payment p = new Payment();
        OffsetDateTime paidAt = OffsetDateTime.now();

        p.setId("PMT-1");
        p.setInvoiceId("INV-1");
        p.setAmount(new BigDecimal("500.00"));
        p.setMethod("CARD");
        p.setPaidAt(paidAt);
        p.setReference("AUTH-123");
        p.setStatus("Cleared");
        p.setProvider("CardGateway");
        p.setAuthCode("AUTH-123");

        assertEquals("PMT-1", p.getId());
        assertEquals("INV-1", p.getInvoiceId());
        assertEquals(new BigDecimal("500.00"), p.getAmount());
        assertEquals("CARD", p.getMethod());
        assertEquals(paidAt, p.getPaidAt());
        assertEquals("AUTH-123", p.getReference());
        assertEquals("Cleared", p.getStatus());
        assertEquals("CardGateway", p.getProvider());
        assertEquals("AUTH-123", p.getAuthCode());
    }
}
