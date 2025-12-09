package com.startup.trucking.notify;

public class PaymentReceivedEvent extends NotificationEvent {
    private final String invoiceId; private final String amount; private final String method;
    public PaymentReceivedEvent(NotificationChannel channel, String to, String customerRef,
                                String invoiceId, String amount, String method) {
        super(channel, to, customerRef);
        this.invoiceId = invoiceId; this.amount = amount; this.method = method;
    }
    @Override public String buildMessage() {
        return "Payment received $" + amount + " (" + method + ") for Invoice " + invoiceId + ".";
    }
}