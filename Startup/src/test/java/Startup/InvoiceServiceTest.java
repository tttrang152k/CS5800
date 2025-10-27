package Startup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import startup.*;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import startup.LoadService;
import startup.EventNotify;
import startup.DocumentService;
import startup.InvoiceService;
import startup.Load;
import startup.Invoice;
import startup.Payment;

class InvoiceServiceTest {
    private LoadService loadService;
    private EventNotify eventNotify;
    private DocumentService documentService;
    private InvoiceService service;

    @BeforeEach
    void setUp() {
        loadService = mock(LoadService.class);
        eventNotify = mock(EventNotify.class);
        documentService = mock(DocumentService.class);
        service = new InvoiceService(loadService, eventNotify, documentService);
    }

    private Load deliveredLoad(String id, float rate) {
        return new Load(
                id,
                "REF-" + id,
                "Delivered",
                rate,
                "TRK-" + id,
                "http://rc.pdf",
                null,
                null,
                "Pickup Addr",
                "Delivery Addr",
                "2025-10-01",
                "2025-10-05"
        );
    }

    private Load notDeliveredLoad(String id, String status) {
        return new Load(
                id,
                "REF-" + id,
                status,
                500f,
                "TRK-" + id,
                "http://rc.pdf",
                null,
                null,
                "Pickup Addr",
                "Delivery Addr",
                "2025-10-01",
                "2025-10-05"
        );
    }

    // ---------- createInvoice ----------
    @Test
    void createInvoice_createsDraftFromDeliveredLoad_andPublishesNotification() {
        when(loadService.getLoad("L-1")).thenReturn(deliveredLoad("L-1", 1234.5f));

        Invoice inv = service.createInvoice("L-1");

        assertNotNull(inv);
        assertEquals("Draft", inv.getStatus());        // computeTotals sets Draft because paid=0
        assertEquals("L-1", inv.getLoadId());
        assertEquals(1234.5f, inv.getTotal(), 0.0001f); // subtotal 1234.5 + tax 0 => total 1234.5

        // Verify publish with expected audience
        verify(eventNotify).notifyInvoiceSent(eq(inv.getId()), eq("customer:L-1"));
    }

    @Test
    void createInvoice_throws_whenLoadNotDelivered() {
        when(loadService.getLoad("L-2")).thenReturn(notDeliveredLoad("L-2", "EnRoute"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createInvoice("L-2"));

        assertTrue(ex.getMessage().contains("Delivered"));
        verifyNoInteractions(eventNotify);
    }

    // ---------- sendInvoice ----------
    @Test
    void sendInvoice_setsStatusSent_andNotifiesRecipient() {
        when(loadService.getLoad("L-3")).thenReturn(deliveredLoad("L-3", 1000f));
        Invoice inv = service.createInvoice("L-3");

        // reset notifier to isolate sendInvoice interaction
        reset(eventNotify);

        service.sendInvoice(inv.getId(), "to@example.com");

        assertEquals("Sent", service.getInvoice(inv.getId()).getStatus());
        verify(eventNotify).notifyInvoiceSent(eq(inv.getId()), eq("to@example.com"));
    }

    // ---------- recordPayment ----------
    @Test
    void recordPayment_appliesPayment_andPublishesPaymentRecordedEvent() {
        when(loadService.getLoad("L-4")).thenReturn(deliveredLoad("L-4", 600f));
        Invoice inv = service.createInvoice("L-4");

        // reset notifier to isolate
        reset(eventNotify);

        Payment p = new Payment("PMT-1", 200f, "Card", OffsetDateTime.now(), "R1", "Pending");
        service.recordPayment(inv.getId(), p);

        assertEquals(400f, inv.balance(), 0.0001f);
        verify(eventNotify).publish(eq("PaymentRecorded"), eq(inv.getId()), eq("billing"));
    }

    // ---------- getInvoice ----------
    @Test
    void getInvoice_returnsStoredInvoice() {
        when(loadService.getLoad("L-5")).thenReturn(deliveredLoad("L-5", 777f));
        Invoice inv = service.createInvoice("L-5");

        Invoice fetched = service.getInvoice(inv.getId());
        assertSame(inv, fetched);
    }

    @Test
    void getInvoice_throws_whenNotFound() {
        assertThrows(IllegalArgumentException.class, () -> service.getInvoice("INV-NOT-EXIST"));
    }
}