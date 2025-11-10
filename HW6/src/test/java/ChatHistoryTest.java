import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
class ChatHistoryTest {
    /* Helper to capture System.out */
    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture() { System.setOut(new java.io.PrintStream(buf)); }
        String out() { return buf.toString(); }
        @Override public void close() { System.setOut(orig); }
    }

    @Test
    void testConstructor_initializesEmptyList() {
        ChatHistory history = new ChatHistory();
        assertNotNull(history.getMessages());
        assertTrue(history.getMessages().isEmpty());
    }

    @Test
    void testAddMessageAndGetMessages_appendsInOrder() {
        ChatHistory history = new ChatHistory();
        User alice = new User("Alice");
        User bob = new User("Bob");
        Message m1 = new Message(alice, List.of(bob), "Hi");
        Message m2 = new Message(bob, List.of(alice), "Hey!");

        history.addMessage(m1);
        history.addMessage(m2);

        List<Message> messages = history.getMessages();
        assertEquals(2, messages.size());
        assertSame(m1, messages.get(0));
        assertSame(m2, messages.get(1));
    }

    @Test
    void testGetLastMessage_whenEmptyReturnsNullAndPrints() {
        ChatHistory history = new ChatHistory();
        User alice = new User("Alice");

        String out;
        try (OutCapture cap = new OutCapture()) {
            Message last = history.getLastMessage(alice);
            out = cap.out();
            assertNull(last);
        }
        assertTrue(out.contains("No messages in chat history"));
    }

    @Test
    void testGetLastMessage_returnsMostRecentMessage() throws InterruptedException {
        ChatHistory history = new ChatHistory();
        User alice = new User("Alice");
        User bob = new User("Bob");

        Message m1 = new Message(alice, List.of(bob), "First");
        history.addMessage(m1);
        Thread.sleep(2);
        Message m2 = new Message(bob, List.of(alice), "Second");
        history.addMessage(m2);

        Message last = history.getLastMessage(alice);
        assertSame(m2, last);
    }

    @Test
    void testIterator_filtersBySpecificUser() {
        ChatHistory history = new ChatHistory();
        User alice = new User("Alice");
        User bob   = new User("Bob");
        User cara  = new User("Cara");

        Message m1 = new Message(alice, List.of(bob), "A to B");
        Message m2 = new Message(bob,   List.of(alice), "B to A");
        Message m3 = new Message(alice, List.of(cara), "A to C");
        Message m4 = new Message(cara,  List.of(bob), "C to B");
        Message m5 = new Message(cara,  List.of(alice, bob), "C to A,B");

        history.addMessage(m1);
        history.addMessage(m2);
        history.addMessage(m3);
        history.addMessage(m4);
        history.addMessage(m5);

        Iterator<Message> it = history.iterator(bob);

        // Expected matches in the same underlying order
        assertTrue(it.hasNext());
        assertSame(m1, it.next());
        assertTrue(it.hasNext());
        assertSame(m2, it.next());
        assertTrue(it.hasNext());
        assertSame(m4, it.next());
        assertTrue(it.hasNext());
        assertSame(m5, it.next());
        assertFalse(it.hasNext());
    }
}