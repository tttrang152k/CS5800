package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class LoadConfirmedEventTest {

    @Test
    void test_buildMessage_includesCustomerAndLoad() {
        var ch = Mockito.mock(NotificationChannel.class);
        var ev = new LoadConfirmedEvent(ch, "ops@acme.com", "ACME", "L-200");
        assertEquals("Load confirmed for ACME — Load L-200.", ev.buildMessage());
    }

    @Test
    void test_send_callsChannelSend() {
        var ch = Mockito.mock(NotificationChannel.class);
        var ev = new LoadConfirmedEvent(ch, "ops@acme.com", "ACME", "L-200");

        ev.send();

        verify(ch).send("ops@acme.com", "Load confirmed for ACME — Load L-200.");
    }
}
