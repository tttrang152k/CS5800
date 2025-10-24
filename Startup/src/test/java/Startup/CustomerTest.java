package Startup;

import org.junit.jupiter.api.Test;
import startup.Customer;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    @Test
    void customer_inherits_user_fields_and_behavior() {
        Customer c = new Customer("C-1", "Acme", "acct@acme.com", "555-4000");

        assertEquals("C-1", c.getId());
        assertEquals("Acme", c.getName());
        assertEquals("acct@acme.com", c.getEmail());
        assertEquals("555-4000", c.getPhone());

        c.updateContact("new@acme.com", "555-4999");
        assertEquals("new@acme.com", c.getEmail());
        assertEquals("555-4999", c.getPhone());
    }
}