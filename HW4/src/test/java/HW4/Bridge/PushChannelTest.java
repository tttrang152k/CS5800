package HW4.Bridge;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class PushChannelTest {
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
    public void test_send_PushChannel() {
        NotificationChannel pushChannel = new PushChannel();
        pushChannel.send("ios123", "Push notification!");

        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("[Push] notification sending to: ios123"));
        assertTrue(consoleOutput.contains("[Push] notification message: Push notification!"));
    }

}