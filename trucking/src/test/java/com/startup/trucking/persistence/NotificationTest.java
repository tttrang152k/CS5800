package com.startup.trucking.persistence;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void test_settersAndGetters_roundTrip() {
        Notification n = new Notification();
        OffsetDateTime t = OffsetDateTime.now();

        n.setId("NT-1");
        n.setCustomerRef("ACME");
        n.setType("InvoiceCreated");
        n.setChannel("EMAIL");
        n.setRecipient("billing@acme.com");
        n.setMessage("Invoice INV-1 created…");
        n.setRefType("Invoice");
        n.setRefId("INV-1");
        n.setCreatedAt(t);

        assertEquals("NT-1", n.getId());
        assertEquals("ACME", n.getCustomerRef());
        assertEquals("InvoiceCreated", n.getType());
        assertEquals("EMAIL", n.getChannel());
        assertEquals("billing@acme.com", n.getRecipient());
        assertEquals("Invoice INV-1 created…", n.getMessage());
        assertEquals("Invoice", n.getRefType());
        assertEquals("INV-1", n.getRefId());
        assertEquals(t, n.getCreatedAt());
    }
}
