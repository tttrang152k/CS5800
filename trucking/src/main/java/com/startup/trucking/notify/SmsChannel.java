package com.startup.trucking.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsChannel implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);
    @Override public void send(String to, String message) {
        log.info("[SMS] to={} msg={}", to, message);
    }
}