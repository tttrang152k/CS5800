package Startup;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import startup.Payment;

import java.time.OffsetDateTime;

class PaymentTest {
    @Test
    void getters_return_constructor_values() {
        OffsetDateTime ts = OffsetDateTime.now();
        Payment p = new Payment("PMT-1", 123.45f, "ACH", ts, "REF-1", "Pending");

        assertEquals("PMT-1", p.getId());
        assertEquals(123.45f, p.getAmount(), 0.0001f);
        assertEquals("ACH", p.getMethod());
        assertEquals(ts, p.getPaidAt());
        assertEquals("REF-1", p.getReference());
        assertEquals("Pending", p.getStatus());
    }

    @Test
    void isCleared_true_for_Cleared_or_Settled_caseInsensitive() {
        Payment p1 = new Payment("P1", 10f, "ACH", OffsetDateTime.now(), "R", "Cleared");
        Payment p2 = new Payment("P2", 10f, "ACH", OffsetDateTime.now(), "R", "Settled");
        Payment p3 = new Payment("P3", 10f, "ACH", OffsetDateTime.now(), "R", "cleared");

        assertTrue(p1.isCleared());
        assertTrue(p2.isCleared());
        assertTrue(p3.isCleared());
    }

    @Test
    void isCleared_false_for_nonCleared_statuses() {
        Payment p = new Payment("P4", 10f, "ACH", OffsetDateTime.now(), "R", "Pending");
        assertFalse(p.isCleared());
    }

    @Test
    void setStatus_updates_status() {
        Payment p = new Payment("P5", 10f, "ACH", OffsetDateTime.now(), "R", "Pending");
        p.setStatus("Settled");
        assertEquals("Settled", p.getStatus());
        assertTrue(p.isCleared());
    }
}