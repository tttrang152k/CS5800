package com.startup.trucking.notify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailChannel implements NotificationChannel {
    private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);
    @Override public void send(String to, String message) {
        log.info("[Email] to={} msg={}", to, message);
    }
}