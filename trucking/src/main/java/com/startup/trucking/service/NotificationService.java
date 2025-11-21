package com.startup.trucking.service;

import com.startup.trucking.notify.*;
import com.startup.trucking.persistence.Notification;
import com.startup.trucking.persistence.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class NotificationService {
    private final ChannelFactory channels;
    private final NotificationRepository repo;

    public NotificationService(ChannelFactory channels, NotificationRepository repo) {
        this.channels = channels; this.repo = repo;
    }

    public void sendLoadConfirmed(ChannelType channel, String recipient, String customerRef, String loadId) {
        var ch = channels.get(channel);
        var event = new LoadConfirmedEvent(ch, recipient, customerRef, loadId);
        var message = event.buildMessage();
        event.send();
        save("LoadConfirmed", channel.name(), recipient, customerRef, "Load", loadId, message);
    }

    public void sendInvoiceCreated(ChannelType channel, String recipient, String customerRef,
                                   String invoiceId, String loadId, String total) {
        var ch = channels.get(channel);
        var event = new InvoiceCreatedEvent(ch, recipient, customerRef, invoiceId, loadId, total);
        var message = event.buildMessage();
        event.send();
        save("InvoiceCreated", channel.name(), recipient, customerRef, "Invoice", invoiceId, message);
    }

    public void sendPaymentReceived(ChannelType channel, String recipient, String customerRef,
                                    String invoiceId, String amount, String method) {
        var ch = channels.get(channel);
        var event = new PaymentReceivedEvent(ch, recipient, customerRef, invoiceId, amount, method);
        var message = event.buildMessage();
        event.send();
        save("PaymentReceived", channel.name(), recipient, customerRef, "Invoice", invoiceId, message);
    }

    private void save(String type, String channel, String to, String customerRef,
                      String refType, String refId, String message) {
        var n = new Notification();
        n.setId("NT-" + UUID.randomUUID());
        n.setType(type);
        n.setChannel(channel);
        n.setRecipient(to);
        n.setCustomerRef(customerRef);
        n.setRefType(refType);
        n.setRefId(refId);
        n.setMessage(message);
        n.setCreatedAt(OffsetDateTime.now());
        repo.save(n);
    }
}