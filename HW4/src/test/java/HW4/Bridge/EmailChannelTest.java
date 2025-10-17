package HW4.Bridge;

import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmailChannelTest {
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
    public void test_send_EmailChannel() {
        NotificationChannel emailChannel = new EmailChannel();
        emailChannel.send("nima@gmail.com", "Email sent!");

        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("[Email] notification sending to: nima@gmail.com"));
        assertTrue(consoleOutput.contains("[Email] notification message: Email sent!"));
    }

}