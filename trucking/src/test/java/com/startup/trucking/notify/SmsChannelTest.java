package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SmsChannelTest {

    @Test
    void test_send_doesNotThrow() {
        var ch = new SmsChannel();
        assertDoesNotThrow(() -> ch.send("+15551234567", "Hello via SMS"));
    }
}
