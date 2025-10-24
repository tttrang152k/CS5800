package Startup;

import org.junit.jupiter.api.Test;
import startup.Billing;
import startup.Employee;

import static org.junit.jupiter.api.Assertions.*;

class BillingTest {
    @Test
    void billing_has_position_billing() {
        Billing b = new Billing("U-30", "Bill", "bill@ex.com", "555-2000", "EMP-30");
        assertEquals(Employee.Position.BILLING, b.getPosition());
        assertEquals("EMP-30", b.getEmployeeId());
    }
}