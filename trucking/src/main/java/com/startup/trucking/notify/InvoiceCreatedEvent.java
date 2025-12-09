package com.startup.trucking.notify;

public class InvoiceCreatedEvent extends NotificationEvent {
    private final String invoiceId;
    private final String loadId;
    private final String total;
    public InvoiceCreatedEvent(NotificationChannel channel, String to, String customerRef,
                               String invoiceId, String loadId, String total) {
        super(channel, to, customerRef);
        this.invoiceId = invoiceId; this.loadId = loadId; this.total = total;
    }
    @Override public String buildMessage() {
        return "Invoice " + invoiceId + " created for Load " + loadId + " — Total $" + total + ".";
    }
}