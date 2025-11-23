package com.startup.trucking.notify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PushChannelTest {

    @Test
    void test_send_doesNotThrow() {
        var ch = new PushChannel();
        assertDoesNotThrow(() -> ch.send("push-token-123", "Hello via Push"));
    }
}
