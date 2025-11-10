import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
class MessageTest {
    @Test
    void testConstructorAndGetters_createMessage_setsFieldsAndMemento() {
        User alice = new User("Alice");
        User bob = new User("Bob");
        String content = "Hello Bob!";

        Message msg = new Message(alice, List.of(bob), content);

        assertEquals(alice, msg.getSender());
        assertEquals(1, msg.getRecipients().size());
        assertEquals(bob, msg.getRecipients().get(0));
        assertEquals(content, msg.getMessage());
        assertNotNull(msg.getTimestamp());
        assertTrue(msg.toString().contains(content), "toString should include content");
    }

    @Test
    void testSetContent_updatesContentAndTimestamp() throws InterruptedException {
        User alice = new User("Alice");
        User bob = new User("Bob");
        Message msg = new Message(alice, List.of(bob), "v1");
        LocalDateTime t0 = msg.getTimestamp();

        // ensure clock moves
        Thread.sleep(5);
        msg.setContent("v2");

        assertEquals("v2", msg.getMessage());
        assertTrue(msg.getTimestamp().isAfter(t0), "Timestamp should update on setContent");
    }

    @Test
    void testSaveToMementoAndUndoFromMemento_restoresSavedState() throws InterruptedException {
        User alice = new User("Alice");
        User bob = new User("Bob");
        Message msg = new Message(alice, List.of(bob), "original");

        // Save current state in memento
        msg.saveToMemento();
        LocalDateTime savedTs = msg.getTimestamp();

        Thread.sleep(5);
        msg.setContent("edited");
        assertEquals("edited", msg.getMessage());
        assertTrue(msg.getTimestamp().isAfter(savedTs));

        // Undo to saved state
        msg.undoFromMemento();

        assertEquals("original", msg.getMessage(), "Undo should restore saved content");
        assertEquals(savedTs, msg.getTimestamp(), "Undo should restore saved timestamp");
    }
}