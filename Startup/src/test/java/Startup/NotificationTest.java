package Startup;

import org.junit.jupiter.api.Test;
import startup.Notification;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    @Test
    void getters_return_constructor_values() {
        Notification n = new Notification(
                "N-1",
                "InvoiceSent",
                "Your invoice is ready",
                "Email",
                "customer:123",
                "Invoice",
                "INV-9"
        );

        assertEquals("N-1", n.getId());
        assertEquals("InvoiceSent", n.getType());
        assertEquals("customer:123", n.getRecipientId());
        assertEquals("INV-9", n.getRefId());
    }

    @Test
    void markDelivered_sets_deliveredAt_timestamp() throws Exception {
        Notification n = new Notification("N-2", "LoadDispatched", "msg",
                "Email", "user:1", "Load", "L-1");

        // deliveredAt should be null before call
        Field deliveredAt = Notification.class.getDeclaredField("deliveredAt");
        deliveredAt.setAccessible(true);
        assertNull(deliveredAt.get(n));

        n.markDelivered();

        Object ts = deliveredAt.get(n);
        assertNotNull(ts);
        assertTrue(ts instanceof OffsetDateTime);
    }

    @Test
    void markRead_sets_readAt_timestamp() throws Exception {
        Notification n = new Notification("N-3", "DocVerified", "msg",
                "Email", "user:2", "Document", "DOC-1");

        Field readAt = Notification.class.getDeclaredField("readAt");
        readAt.setAccessible(true);
        assertNull(readAt.get(n));

        n.markRead();

        Object ts = readAt.get(n);
        assertNotNull(ts);
        assertTrue(ts instanceof OffsetDateTime);
    }
}