import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
class SearchMessagesByUserTest {
    @Test
    void testHasNextAndNext_onlyReturnsMessagesInvolvingTargetUser() {
        User alice = new User("Alice");
        User bob   = new User("Bob");
        User cara  = new User("Cara");

        List<Message> messages = new ArrayList<>();
        Message m1 = new Message(alice, List.of(bob), "A to B");
        Message m2 = new Message(bob,   List.of(alice), "B to A");
        Message m3 = new Message(alice, List.of(cara), "A to C");
        Message m4 = new Message(cara,  List.of(bob), "C to B");
        Message m5 = new Message(cara,  List.of(alice, bob), "C to A,B");

        messages.add(m1);
        messages.add(m2);
        messages.add(m3);
        messages.add(m4);
        messages.add(m5);

        Iterator<Message> it = new SearchMessagesByUser(messages, bob);
        assertTrue(it.hasNext()); assertSame(m1, it.next());
        assertTrue(it.hasNext()); assertSame(m2, it.next());
        assertTrue(it.hasNext()); assertSame(m4, it.next());
        assertTrue(it.hasNext()); assertSame(m5, it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void testNextReturnsNullWhenNoMoreMatches() {
        User alice = new User("Alice");
        User bob   = new User("Bob");

        List<Message> messages = List.of(
                new Message(alice, List.of(bob), "A→B")
        );

        SearchMessagesByUser it = new SearchMessagesByUser(messages, bob);
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertFalse(it.hasNext());
        assertNull(it.next());
    }

    @Test
    void testEmptyMessageList_hasNextFalse() {
        User bob = new User("Bob");
        SearchMessagesByUser it = new SearchMessagesByUser(List.of(), bob);
        assertFalse(it.hasNext());
        assertNull(it.next());
    }
}