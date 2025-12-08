package com.startup.trucking.service;

import com.startup.trucking.notify.ChannelFactory;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.notify.NotificationChannel;
import com.startup.trucking.persistence.Notification;
import com.startup.trucking.persistence.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Unit Tests")
class NotificationServiceTest {

    private static final String CUSTOMER_ACME = "ACME";
    private static final String LOAD_ID = "L-1";
    private static final String INVOICE_ID = "INV-1";
    private static final String RECIPIENT_EMAIL = "ops@acme.com";

    @Mock
    ChannelFactory channelFactory;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    NotificationChannel notificationChannel;

    @Test
    @DisplayName("sendLoadConfirmed() - Sends notification and persists record")
    void test_sendLoadConfirmed_sends_and_persists() {
        when(channelFactory.get(ChannelType.EMAIL)).thenReturn(notificationChannel);

        NotificationService service = new NotificationService(channelFactory, notificationRepository);

        service.sendLoadConfirmed(ChannelType.EMAIL, RECIPIENT_EMAIL, CUSTOMER_ACME, LOAD_ID);

        verify(channelFactory).get(ChannelType.EMAIL);
        verify(notificationChannel).send(eq(RECIPIENT_EMAIL), any());

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertNotNull(saved.getId());
        assertEquals("LoadConfirmed", saved.getType());
        assertEquals("EMAIL", saved.getChannel());
        assertEquals(RECIPIENT_EMAIL, saved.getRecipient());
        assertEquals(CUSTOMER_ACME, saved.getCustomerRef());
        assertEquals("Load", saved.getRefType());
        assertEquals(LOAD_ID, saved.getRefId());
        assertNotNull(saved.getMessage());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @DisplayName("sendInvoiceCreated() - Sends notification and persists record")
    void test_sendInvoiceCreated_sends_and_persists() {
        when(channelFactory.get(ChannelType.SMS)).thenReturn(notificationChannel);

        NotificationService service = new NotificationService(channelFactory, notificationRepository);

        service.sendInvoiceCreated(
                ChannelType.SMS,
                "+15551234567",
                CUSTOMER_ACME,
                INVOICE_ID,
                LOAD_ID,
                "1250.00"
        );

        verify(channelFactory).get(ChannelType.SMS);
        verify(notificationChannel).send(eq("+15551234567"), any());

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertEquals("InvoiceCreated", saved.getType());
        assertEquals("SMS", saved.getChannel());
        assertEquals("Invoice", saved.getRefType());
        assertEquals(INVOICE_ID, saved.getRefId());
    }

    @Test
    @DisplayName("sendPaymentReceived() - Sends notification and persists record")
    void test_sendPaymentReceived_sends_and_persists() {
        when(channelFactory.get(ChannelType.PUSH)).thenReturn(notificationChannel);

        NotificationService service = new NotificationService(channelFactory, notificationRepository);

        service.sendPaymentReceived(
                ChannelType.PUSH,
                "pushToken123",
                CUSTOMER_ACME,
                INVOICE_ID,
                "500.00",
                "CARD"
        );

        verify(channelFactory).get(ChannelType.PUSH);
        verify(notificationChannel).send(eq("pushToken123"), any());

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(notificationCaptor.capture());

        Notification saved = notificationCaptor.getValue();
        assertEquals("PaymentReceived", saved.getType());
        assertEquals("Invoice", saved.getRefType());
        assertEquals(INVOICE_ID, saved.getRefId());
    }
}
