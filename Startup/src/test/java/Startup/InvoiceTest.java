package Startup;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import startup.Invoice;
import startup.Payment;

import java.time.OffsetDateTime;

class InvoiceTest {
    private Invoice newInvoice(String id, String status, float subtotal, float tax, float total, String loadId) {
        return new Invoice(id, status, subtotal, tax, total, loadId);
    }

    @Test
    void computeTotals_setsTotal_and_setsDraft_whenPaidZero() {
        Invoice inv = newInvoice("INV-1", "Sent", 1000f, 100f, 0f, "L-1");

        inv.computeTotals();

        assertEquals(1100f, inv.getTotal(), 0.0001f);
        assertEquals("Draft", inv.getStatus()); // because paid == 0
    }

    @Test
    void computeTotals_doesNotOverrideStatus_whenAlreadyPartiallyPaid() {
        Invoice inv = newInvoice("INV-2", "Draft", 1000f, 0f, 1000f, "L-1");
        // Make it partially paid first
        inv.applyPayment(new Payment("PMT-1", 100f, "ACH", OffsetDateTime.now(), "R1", "Pending"));

        inv.computeTotals();

        assertEquals(1000f, inv.getTotal(), 0.0001f);
        assertEquals("PartiallyPaid", inv.getStatus()); // should NOT revert to Draft
    }

    @Test
    void applyPayment_reducesBalance_and_setsPartiallyPaid() {
        Invoice inv = newInvoice("INV-3", "Draft", 1000f, 0f, 1000f, "L-1");

        inv.applyPayment(new Payment("PMT-2", 400f, "Card", OffsetDateTime.now(), "R2", "Pending"));

        assertEquals(600f, inv.balance(), 0.0001f);
        assertEquals("PartiallyPaid", inv.getStatus());
    }

    @Test
    void applyPayment_exactAmount_setsPaid_and_zeroBalance() {
        Invoice inv = newInvoice("INV-4", "Draft", 500f, 0f, 500f, "L-1");

        inv.applyPayment(new Payment("PMT-3", 500f, "ACH", OffsetDateTime.now(), "R3", "Pending"));

        assertEquals(0f, inv.balance(), 0.0001f);
        assertEquals("Paid", inv.getStatus());
    }

    @Test
    void applyPayment_overpay_throws() {
        Invoice inv = newInvoice("INV-5", "Draft", 500f, 0f, 500f, "L-1");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inv.applyPayment(new Payment("PMT-4", 600f, "ACH", OffsetDateTime.now(), "R4", "Pending")));

        assertTrue(ex.getMessage().toLowerCase().contains("exceeds"));
    }

    @Test
    void applyPayment_nonPositiveAmount_throws() {
        Invoice inv = newInvoice("INV-6", "Draft", 500f, 0f, 500f, "L-1");

        assertThrows(IllegalArgumentException.class,
                () -> inv.applyPayment(new Payment("PMT-5", 0f, "ACH", OffsetDateTime.now(), "R5", "Pending")));
        assertThrows(IllegalArgumentException.class,
                () -> inv.applyPayment(new Payment("PMT-6", -10f, "ACH", OffsetDateTime.now(), "R6", "Pending")));
    }

    @Test
    void applyPayment_whenAlreadyPaid_throws() {
        Invoice inv = newInvoice("INV-7", "Paid", 100f, 0f, 0f, "L-1"); // status Paid is terminal

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inv.applyPayment(new Payment("PMT-7", 10f, "ACH", OffsetDateTime.now(), "R7", "Pending")));

        assertTrue(ex.getMessage().toLowerCase().contains("already paid"));
    }

    @Test
    void balance_accumulates_multiple_payments_correctly() {
        Invoice inv = newInvoice("INV-8", "Draft", 0f, 0f, 500f, "L-1");

        inv.applyPayment(new Payment("P1", 100f, "ACH", OffsetDateTime.now(), "R", "Pending"));
        inv.applyPayment(new Payment("P2", 200f, "ACH", OffsetDateTime.now(), "R", "Pending"));

        assertEquals(200f, inv.balance(), 0.0001f);
        assertEquals("PartiallyPaid", inv.getStatus());
    }

    @Test
    void getters_and_setStatus_work() {
        Invoice inv = newInvoice("INV-9", "Draft", 100f, 10f, 110f, "L-77");
        inv.setStatus("Overdue");

        assertEquals("INV-9", inv.getId());
        assertEquals("Overdue", inv.getStatus());
        assertEquals(110f, inv.getTotal(), 0.0001f);
        assertEquals("L-77", inv.getLoadId());
    }
}