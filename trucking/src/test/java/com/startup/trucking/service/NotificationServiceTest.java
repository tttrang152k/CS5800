package com.startup.trucking.service;

import com.startup.trucking.notify.*;
import com.startup.trucking.persistence.Notification;
import com.startup.trucking.persistence.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    ChannelFactory channelFactory;

    @Mock
    NotificationRepository repo;

    @Test
    void test_sendLoadConfirmed_builds_and_persists() {
        NotificationChannel ch = mock(NotificationChannel.class);
        when(channelFactory.get(ChannelType.EMAIL)).thenReturn(ch);

        NotificationService svc = new NotificationService(channelFactory, repo);
        svc.sendLoadConfirmed(ChannelType.EMAIL, "ops@acme.com", "ACME", "L-100");

        verify(ch).send(eq("ops@acme.com"), contains("Load confirmed for ACME — Load L-100"));

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        Notification n = cap.getValue();

        assertEquals("LoadConfirmed", n.getType());
        assertEquals("EMAIL", n.getChannel());
        assertEquals("ops@acme.com", n.getRecipient());
        assertEquals("ACME", n.getCustomerRef());
        assertEquals("Load", n.getRefType());
        assertEquals("L-100", n.getRefId());
        assertNotNull(n.getId());
        assertNotNull(n.getCreatedAt());
        assertTrue(n.getMessage().contains("Load confirmed"));
    }

    @Test
    void test_sendInvoiceCreated_builds_and_persists() {
        NotificationChannel ch = mock(NotificationChannel.class);
        when(channelFactory.get(ChannelType.SMS)).thenReturn(ch);

        NotificationService svc = new NotificationService(channelFactory, repo);
        svc.sendInvoiceCreated(ChannelType.SMS, "15551230000", "ACME", "INV-1", "L-1", "1500.00");

        verify(ch).send(eq("15551230000"), contains("Invoice INV-1 created for Load L-1 — Total $1500.00"));

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        Notification n = cap.getValue();
        assertEquals("InvoiceCreated", n.getType());
        assertEquals("Invoice", n.getRefType());
        assertEquals("INV-1", n.getRefId());
    }

    @Test
    void test_sendPaymentReceived_builds_and_persists() {
        NotificationChannel ch = mock(NotificationChannel.class);
        when(channelFactory.get(ChannelType.PUSH)).thenReturn(ch);

        NotificationService svc = new NotificationService(channelFactory, repo);
        svc.sendPaymentReceived(ChannelType.PUSH, "pushToken123", "ACME", "INV-2", "500.00", "CARD");

        verify(ch).send(eq("pushToken123"), contains("Payment received $500.00 (CARD) for Invoice INV-2"));

        ArgumentCaptor<Notification> cap = ArgumentCaptor.forClass(Notification.class);
        verify(repo).save(cap.capture());
        Notification n = cap.getValue();
        assertEquals("PaymentReceived", n.getType());
        assertEquals("Invoice", n.getRefType());
        assertEquals("INV-2", n.getRefId());
    }
}
