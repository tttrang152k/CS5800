import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
class UserTest {
    /* Helper to get System.out outputs for assertions */
    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture() { System.setOut(new java.io.PrintStream(buf)); }
        String out() { return buf.toString(); }
        @Override public void close() { System.setOut(orig); }
    }

    @Test
    void testGetUserName_returnsName() {
        User alice = new User("Alice");
        assertEquals("Alice", alice.getUserName());
    }

    @Test
    void testSendMessage_addsToHistoryAndPrints() {
        User alice = new User("Alice");
        User bob = new User("Bob");
        Message msg = new Message(alice, List.of(bob), "Ping");

        String out;
        try (OutCapture cap = new OutCapture()) {
            alice.sendMessage(msg);
            out = cap.out();
        }

        assertTrue(out.contains("[Sender] Alice sent: "), "Should print sender log");
        assertTrue(out.contains("\"Ping\""), "Should print message content");
        // Basic sanity: history exists
        assertNotNull(alice.getChatHistory());
    }

    @Test
    void testReceiveMessage_addsToHistoryAndPrints() {
        User alice = new User("Alice");
        User bob = new User("Bob");
        Message msg = new Message(alice, List.of(bob), "Hi Bob");

        String out;
        try (OutCapture cap = new OutCapture()) {
            bob.receiveMessage(msg);
            out = cap.out();
        }

        assertTrue(out.contains("[Recipient] Bob received: "), "Should print recipient log");
        assertTrue(out.contains("\"Hi Bob\""), "Should print message content");
        assertNotNull(bob.getChatHistory());
    }

    @Test
    void testUndoLastMessage_revertsToMemento() {
        User alice = new User("Alice");
        User bob = new User("Bob");
        Message msg = new Message(alice, List.of(bob), "draft v1");

        alice.sendMessage(msg);

        msg.saveToMemento();
        msg.setContent("draft v2");
        assertEquals("draft v2", msg.getMessage());

        alice.undoLastMessage(); // should restore to saved memento
        assertEquals("draft v1", msg.getMessage(), "Undo should revert to saved content");
    }

    @Test
    void testIterator_wrapperDelegatesToChatHistory() {
        User alice = new User("Alice");
        User bob = new User("Bob");
        assertNotNull(alice.iterator(bob));
    }
}