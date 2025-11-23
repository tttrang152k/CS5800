package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailChannelTest {

    @Test
    void test_send_doesNotThrow() {
        var ch = new EmailChannel();
        assertDoesNotThrow(() -> ch.send("user@example.com", "Hello from Email"));
    }
}
