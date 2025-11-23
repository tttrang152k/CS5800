package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChannelFactoryTest {

    private final ChannelFactory factory = new ChannelFactory();

    @Test
    void test_get_EMAIL_returnsEmailChannel() {
        NotificationChannel ch = factory.get(ChannelType.EMAIL);
        assertNotNull(ch);
        assertEquals(EmailChannel.class, ch.getClass());
    }

    @Test
    void test_get_SMS_returnsSmsChannel() {
        NotificationChannel ch = factory.get(ChannelType.SMS);
        assertNotNull(ch);
        assertEquals(SmsChannel.class, ch.getClass());
    }

    @Test
    void test_get_PUSH_returnsPushChannel() {
        NotificationChannel ch = factory.get(ChannelType.PUSH);
        assertNotNull(ch);
        assertEquals(PushChannel.class, ch.getClass());
    }
}
