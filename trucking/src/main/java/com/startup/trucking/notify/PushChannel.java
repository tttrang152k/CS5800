package com.startup.trucking.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushChannel implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(PushChannel.class);
    @Override public void send(String to, String message) {
        log.info("[Push] to={} msg={}", to, message);
    }
}