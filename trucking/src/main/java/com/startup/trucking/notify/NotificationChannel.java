package com.startup.trucking.notify;

public interface NotificationChannel {
    void send(String to, String message);
}