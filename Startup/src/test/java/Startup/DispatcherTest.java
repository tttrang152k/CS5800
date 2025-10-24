package Startup;

import org.junit.jupiter.api.Test;
import startup.Dispatcher;
import startup.Employee;

import static org.junit.jupiter.api.Assertions.*;


class DispatcherTest {

    @Test
    void dispatcher_has_position_dispatcher() {
        Dispatcher d = new Dispatcher("U-20", "Daisy", "d@ex.com", "555-1000", "EMP-20");
        assertEquals(Employee.Position.DISPATCHER, d.getPosition());
        assertEquals("EMP-20", d.getEmployeeId());
    }
}
