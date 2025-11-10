import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
class ChatServerTest {
    /* Helper for System.Out outputs */
    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture() { System.setOut(new java.io.PrintStream(buf)); }
        String out() { return buf.toString(); }
        @Override public void close() { System.setOut(orig); }
    }

    @Test
    void testConstructor_initializesCollections() {
        ChatServer server = new ChatServer();
        assertNotNull(server.getUsers());
        assertNotNull(server.getBlockedUsers());
        assertTrue(server.getUsers().isEmpty());
        assertTrue(server.getBlockedUsers().isEmpty());
    }

    @Test
    void testRegisterUser_addsUserAndBlockEntry() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");

        server.registerUser(alice);

        assertEquals(1, server.getUsers().size());
        assertTrue(server.getUsers().contains(alice));
        assertTrue(server.getBlockedUsers().containsKey(alice));
        assertEquals(0, server.getBlockedUsers().get(alice).size());
    }

    @Test
    void testUnregisterUser_removesUserAndBlockEntry() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");
        server.registerUser(alice);

        server.unregiserUser(alice);

        assertFalse(server.getUsers().contains(alice));
        assertFalse(server.getBlockedUsers().containsKey(alice));
    }

    @Test
    void testBlockAndUnblockUser_updatesBlockedMap() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");
        User bob = new User("Bob");
        server.registerUser(alice);
        server.registerUser(bob);

        server.blockUser(bob, alice);
        Set<User> blockedForBob = server.getBlockedUsers().get(bob);
        assertTrue(blockedForBob.contains(alice), "Bob should have Alice in block list");

        server.unblockUser(bob, alice);
        assertFalse(blockedForBob.contains(alice), "Bob should have Alice removed from block list");
    }

    @Test
    void testSendMessage_successfulDelivery_senderAndRecipientLogs() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");
        User bob = new User("Bob");
        server.registerUser(alice);
        server.registerUser(bob);

        Message m = new Message(alice, List.of(bob), "Hello!");

        String out;
        try (OutCapture cap = new OutCapture()) {
            server.sendMessage(m);
            out = cap.out();
        }

        assertTrue(out.contains("[Sender] Alice sent: "), "Sender log should appear");
        assertTrue(out.contains("[Recipient] Bob received: "), "Recipient log should appear");
        assertTrue(out.contains("\"Hello!\""), "Message content should be printed");
    }

    @Test
    void testSendMessage_senderNotRegistered_printsWarning() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice"); // not registered
        User bob = new User("Bob");
        server.registerUser(bob);

        Message m = new Message(alice, List.of(bob), "Hello from unknown");

        String out;
        try (OutCapture cap = new OutCapture()) {
            server.sendMessage(m);
            out = cap.out();
        }

        assertTrue(out.contains("isn't registered"), "Should warn about unregistered sender");
        assertFalse(out.contains("[Recipient] Bob received"), "Recipient should not receive message");
    }

    @Test
    void testSendMessage_recipientNotRegistered_onlyWarnsAndNoDelivery() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");
        User bob = new User("Bob"); // not registered
        server.registerUser(alice);

        Message m = new Message(alice, List.of(bob), "Hi Bob");

        String out;
        try (OutCapture cap = new OutCapture()) {
            server.sendMessage(m);
            out = cap.out();
        }

        assertTrue(out.contains("Recipient user Bob isn't registered"), "Should warn about invalid recipient");
        assertTrue(out.contains("[Sender] Alice sent:"), "Sender still logs sending on server");
        assertFalse(out.contains("[Recipient] Bob received"), "No delivery to unregistered user");
    }

    @Test
    void testSendMessage_blockedByRecipient_messageDropped() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");
        User bob = new User("Bob");
        server.registerUser(alice);
        server.registerUser(bob);

        server.blockUser(bob, alice);

        Message m = new Message(alice, List.of(bob), "Are you there?");

        String out;
        try (OutCapture cap = new OutCapture()) {
            server.sendMessage(m);
            out = cap.out();
        }

        assertTrue(out.contains("Recipient Bob has blocked sender Alice"), "Should indicate block drop");
        assertTrue(out.contains("[Sender] Alice sent:"), "Sender log should still appear");
        assertFalse(out.contains("[Recipient] Bob received"), "Blocked recipient should not receive");
    }

    @Test
    void testSendMessage_senderBlocksRecipient_messageNotDelivered() {
        ChatServer server = new ChatServer();
        User alice = new User("Alice");
        User bob = new User("Bob");
        server.registerUser(alice);
        server.registerUser(bob);

        server.blockUser(alice, bob);

        Message m = new Message(alice, List.of(bob), "Won't go through by design");

        String out;
        try (OutCapture cap = new OutCapture()) {
            server.sendMessage(m);
            out = cap.out();
        }

        assertTrue(out.contains("Sender Bob has blocked recipient Alice"), "Should indicate sender-side block");
        assertFalse(out.contains("[Recipient] Bob received"), "No delivery when sender blocks recipient");
    }

    @Test
    void testGetters_returnLiveCollections() {
        ChatServer server = new ChatServer();
        assertNotNull(server.getUsers());
        assertNotNull(server.getBlockedUsers());

        User alice = new User("Alice");
        server.registerUser(alice);
        assertTrue(server.getUsers().contains(alice));
        assertTrue(server.getBlockedUsers().containsKey(alice));
    }
}