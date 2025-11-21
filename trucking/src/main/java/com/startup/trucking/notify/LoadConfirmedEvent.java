package com.startup.trucking.notify;

public class LoadConfirmedEvent extends NotificationEvent {
    private final String loadId;
    public LoadConfirmedEvent(NotificationChannel ch, String to, String customerRef, String loadId) {
        super(ch, to, customerRef);
        this.loadId = loadId;
    }
    @Override public String buildMessage() {
        return "Load confirmed for " + customerRef + " — Load " + loadId + ".";
    }
}