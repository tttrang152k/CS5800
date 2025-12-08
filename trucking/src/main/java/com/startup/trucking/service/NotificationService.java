package com.startup.trucking.service;

import com.startup.trucking.notify.ChannelFactory;
import com.startup.trucking.notify.ChannelType;
import com.startup.trucking.notify.InvoiceCreatedEvent;
import com.startup.trucking.notify.LoadConfirmedEvent;
import com.startup.trucking.notify.NotificationChannel;
import com.startup.trucking.notify.NotificationEvent;
import com.startup.trucking.notify.PaymentReceivedEvent;
import com.startup.trucking.persistence.Notification;
import com.startup.trucking.persistence.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class NotificationService {

    private static final String TYPE_LOAD_CONFIRMED = "LoadConfirmed";
    private static final String TYPE_INVOICE_CREATED = "InvoiceCreated";
    private static final String TYPE_PAYMENT_RECEIVED = "PaymentReceived";

    private static final String REF_TYPE_LOAD = "Load";
    private static final String REF_TYPE_INVOICE = "Invoice";

    private final ChannelFactory channelFactory;
    private final NotificationRepository notificationRepository;

    public NotificationService(ChannelFactory channelFactory,
                               NotificationRepository notificationRepository) {
        this.channelFactory = channelFactory;
        this.notificationRepository = notificationRepository;
    }

    public void sendLoadConfirmed(ChannelType channelType,
                                  String recipient,
                                  String customerRef,
                                  String loadId) {
        NotificationChannel channel = channelFactory.get(channelType);
        NotificationEvent event = new LoadConfirmedEvent(channel, recipient, customerRef, loadId);

        sendAndRecord(
                TYPE_LOAD_CONFIRMED,
                REF_TYPE_LOAD,
                loadId,
                channelType,
                recipient,
                customerRef,
                event
        );
    }

    public void sendInvoiceCreated(ChannelType channelType,
                                   String recipient,
                                   String customerRef,
                                   String invoiceId,
                                   String loadId,
                                   String total) {
        NotificationChannel channel = channelFactory.get(channelType);
        NotificationEvent event =
                new InvoiceCreatedEvent(channel, recipient, customerRef, invoiceId, loadId, total);

        sendAndRecord(
                TYPE_INVOICE_CREATED,
                REF_TYPE_INVOICE,
                invoiceId,
                channelType,
                recipient,
                customerRef,
                event
        );
    }

    public void sendPaymentReceived(ChannelType channelType,
                                    String recipient,
                                    String customerRef,
                                    String invoiceId,
                                    String amount,
                                    String method) {
        NotificationChannel channel = channelFactory.get(channelType);
        NotificationEvent event =
                new PaymentReceivedEvent(channel, recipient, customerRef, invoiceId, amount, method);

        sendAndRecord(
                TYPE_PAYMENT_RECEIVED,
                REF_TYPE_INVOICE,
                invoiceId,
                channelType,
                recipient,
                customerRef,
                event
        );
    }

    private void sendAndRecord(String type,
                               String refType,
                               String refId,
                               ChannelType channelType,
                               String recipient,
                               String customerRef,
                               NotificationEvent event) {
        String message = event.buildMessage();
        event.send();

        Notification notification = createNotification(
                type,
                channelType.name(),
                recipient,
                customerRef,
                refType,
                refId,
                message
        );
        notificationRepository.save(notification);
    }

    private Notification createNotification(String type,
                                            String channelName,
                                            String recipient,
                                            String customerRef,
                                            String refType,
                                            String refId,
                                            String message) {
        Notification notification = new Notification();
        notification.setId("NT-" + UUID.randomUUID());
        notification.setType(type);
        notification.setChannel(channelName);
        notification.setRecipient(recipient);
        notification.setCustomerRef(customerRef);
        notification.setRefType(refType);
        notification.setRefId(refId);
        notification.setMessage(message);
        notification.setCreatedAt(OffsetDateTime.now());
        return notification;
    }
}
