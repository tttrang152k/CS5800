package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class PaymentReceivedEventTest {

    @Test
    void test_buildMessage_formatsAmountMethodAndInvoice() {
        var ch = Mockito.mock(NotificationChannel.class);
        var ev = new PaymentReceivedEvent(ch, "bill@acme.com", "ACME", "INV-9", "250.00", "CARD");
        assertEquals("Payment received $250.00 (CARD) for Invoice INV-9.", ev.buildMessage());
    }

    @Test
    void test_send_invokesChannel() {
        var ch = Mockito.mock(NotificationChannel.class);
        var ev = new PaymentReceivedEvent(ch, "bill@acme.com", "ACME", "INV-9", "250.00", "ACH");

        ev.send();

        verify(ch).send("bill@acme.com", "Payment received $250.00 (ACH) for Invoice INV-9.");
    }
}
