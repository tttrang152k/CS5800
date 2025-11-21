package com.startup.trucking.notify;

import org.springframework.stereotype.Component;

@Component
public class ChannelFactory {
    public NotificationChannel get(ChannelType type) {
        return switch (type) {
            case EMAIL -> new EmailChannel();
            case SMS   -> new SmsChannel();
            case PUSH  -> new PushChannel();
        };
    }
}