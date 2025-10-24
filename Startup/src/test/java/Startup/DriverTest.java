package Startup;

import org.junit.jupiter.api.Test;
import startup.Driver;
import startup.Employee;

import static org.junit.jupiter.api.Assertions.*;


class DriverTest {
    @Test
    void constructor_sets_position_driver_and_driver_fields() {
        Driver d = new Driver("U-40", "Devon", "devon@ex.com", "555-3000",
                "EMP-40", "CA-12345", "Available");

        assertEquals(Employee.Position.DRIVER, d.getPosition());
        assertEquals("EMP-40", d.getEmployeeId());
        assertEquals("CA-12345", d.getLicenseNo());
        assertEquals("Available", d.getDriverStatus());
    }

    @Test
    void isAvailable_true_when_status_available() {
        Driver d = new Driver("U-41", "Ava", "a@ex.com", "555-3001",
                "EMP-41", "CA-54321", "Available");
        assertTrue(d.isAvailable());

        d.setDriverStatus("OnTrip");
        assertFalse(d.isAvailable());
    }

    @Test
    void setters_update_driver_fields_and_validate_non_null() {
        Driver d = new Driver("U-42", "Dan", "dan@ex.com", "555-3002",
                "EMP-42", "CA-88888", "OffDuty");

        d.setLicenseNo("CA-99999");
        d.setDriverStatus("Assigned");

        assertEquals("CA-99999", d.getLicenseNo());
        assertEquals("Assigned", d.getDriverStatus());

        assertThrows(NullPointerException.class, () -> d.setLicenseNo(null));
        assertThrows(NullPointerException.class, () -> d.setDriverStatus(null));
    }
}