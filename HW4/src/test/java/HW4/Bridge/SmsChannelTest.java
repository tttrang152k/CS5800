package HW4.Bridge;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class SmsChannelTest {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private PrintStream original;

    @BeforeEach
    void setUp() {
        original = System.out;
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(original);
    }

    @Test
    public void test_send_SmsChannel() {
        NotificationChannel smsChannel = new SmsChannel();
        smsChannel.send("123-456-789", "Sms sent!");

        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("[SMS] notification sending to: 123-456-789"));
        assertTrue(consoleOutput.contains("[SMS] notification message: Sms sent!"));
    }

}