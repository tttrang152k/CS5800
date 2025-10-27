package Startup;

import org.junit.jupiter.api.Test;
import startup.EventNotify;
import startup.Notification;

import static org.junit.jupiter.api.Assertions.*;

class EventNotifyTest {
    @Test
    void publish_withValidAudience_returnsNotificationWithIdsAndFields() {
        EventNotify notifier = new EventNotify("sns");
        Notification n = notifier.publish("LoadDispatched", "L-1", "customer:123");

        assertNotNull(n);
        assertNotNull(n.getId());
        assertFalse(n.getId().isBlank());
        assertEquals("LoadDispatched", n.getType());
        assertEquals("customer:123", n.getRecipientId());
        assertEquals("L-1", n.getRefId());
    }

    @Test
    void publish_withNullOrBlankAudience_throws() {
        EventNotify notifier = new EventNotify("sns");
        assertThrows(IllegalArgumentException.class,
                () -> notifier.publish("X", "REF", null));
        assertThrows(IllegalArgumentException.class,
                () -> notifier.publish("X", "REF", "   "));
    }

    @Test
    void notifyLoadStatusChanged_delegatesToPublish_andSetsTypeAndRef() {
        EventNotify notifier = new EventNotify("sns");
        Notification n = notifier.notifyLoadStatusChanged("L-22", "broker:777");

        assertEquals("LoadStatusChanged", n.getType());
        assertEquals("L-22", n.getRefId());
        assertEquals("broker:777", n.getRecipientId());
    }

    @Test
    void notifyInvoiceSent_delegatesToPublish_andSetsTypeAndRef() {
        EventNotify notifier = new EventNotify("sns");
        Notification n = notifier.notifyInvoiceSent("INV-9", "customer:xyz");

        assertEquals("InvoiceSent", n.getType());
        assertEquals("INV-9", n.getRefId());
        assertEquals("customer:xyz", n.getRecipientId());
    }
}