package com.startup.trucking.notify;

public abstract class NotificationEvent {
    protected final NotificationChannel channel;
    protected final String recipient;
    protected final String customerRef; // customer name or id

    protected NotificationEvent(NotificationChannel channel, String recipient, String customerRef) {
        this.channel = channel;
        this.recipient = recipient;
        this.customerRef = customerRef;
    }

    public abstract String buildMessage();
    public void send() {
        channel.send(recipient, buildMessage());
    }
}