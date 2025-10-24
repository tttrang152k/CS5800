package Startup;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import startup.User;

class UserTest {
    @Test
    void constructor_and_getters_work() {
        User u = new User("U-1", "Alex", "alex@ex.com", "555-0000");
        assertEquals("U-1", u.getId());
        assertEquals("Alex", u.getName());
        assertEquals("alex@ex.com", u.getEmail());
        assertEquals("555-0000", u.getPhone());
    }

    @Test
    void setters_update_fields() {
        User u = new User("U-2", "Old", "old@ex.com", "555-1111");
        u.setName("New");
        u.setEmail("new@ex.com");
        u.setPhone("555-2222");
        assertAll(
                () -> assertEquals("New", u.getName()),
                () -> assertEquals("new@ex.com", u.getEmail()),
                () -> assertEquals("555-2222", u.getPhone())
        );
    }

    @Test
    void updateContact_sets_email_and_phone_and_throws_on_null() {
        User u = new User("U-3", "Pat", "p@ex.com", "555-1234");
        u.updateContact("p2@ex.com", "555-9999");
        assertEquals("p2@ex.com", u.getEmail());
        assertEquals("555-9999", u.getPhone());

        assertThrows(IllegalArgumentException.class, () -> u.updateContact(null, "x"));
        assertThrows(IllegalArgumentException.class, () -> u.updateContact("x@ex.com", null));
    }
}