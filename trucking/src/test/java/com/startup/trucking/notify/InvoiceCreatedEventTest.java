package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class InvoiceCreatedEventTest {

    @Test
    void test_buildMessage_formatsAllFields() {
        var ch = Mockito.mock(NotificationChannel.class);
        var ev = new InvoiceCreatedEvent(ch, "to@example.com", "ACME", "INV-1", "L-100", "1,500.00");
        assertEquals("Invoice INV-1 created for Load L-100 — Total $1,500.00.", ev.buildMessage());
    }

    @Test
    void test_send_invokesChannelWithBuiltMessage() {
        var ch = Mockito.mock(NotificationChannel.class);
        var ev = new InvoiceCreatedEvent(ch, "to@example.com", "ACME", "INV-1", "L-100", "1500.00");

        ev.send();

        verify(ch).send("to@example.com", "Invoice INV-1 created for Load L-100 — Total $1500.00.");
    }
}
