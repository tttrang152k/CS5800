import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
class MessageMementoTest {
    @Test
    void testConstructorAndGetters_storeMessageAndTimestamp() {
        LocalDateTime ts = LocalDateTime.now();
        MessageMemento mem = new MessageMemento("hello", ts);

        assertEquals("hello", mem.getMessage());
        assertEquals(ts, mem.getTimestamp());
    }

    @Test
    void testSetMessageMemento_updatesFields() throws InterruptedException {
        LocalDateTime ts1 = LocalDateTime.now();
        MessageMemento mem = new MessageMemento("v1", ts1);

        Thread.sleep(2);
        LocalDateTime ts2 = LocalDateTime.now();
        mem.setMessageMemento("v2", ts2);

        assertEquals("v2", mem.getMessage());
        assertEquals(ts2, mem.getTimestamp());
        assertTrue(ts2.isAfter(ts1));
    }
}