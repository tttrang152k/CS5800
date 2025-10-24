package Startup;

import org.junit.jupiter.api.Test;
import startup.Employee;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {
    @Test
    void constructor_sets_employeeId_and_position() {
        Employee e = new Employee("U-10", "Kim", "k@ex.com", "555-0001",
                "EMP-10", Employee.Position.BILLING);

        assertEquals("EMP-10", e.getEmployeeId());
        assertEquals(Employee.Position.BILLING, e.getPosition());
        assertEquals("Kim", e.getName()); // inherited
    }

    @Test
    void setPosition_updates_position() {
        Employee e = new Employee("U-11", "Sam", "s@ex.com", "555-0002",
                "EMP-11", Employee.Position.DISPATCHER);
        e.setPosition(Employee.Position.DRIVER);
        assertEquals(Employee.Position.DRIVER, e.getPosition());
    }
}