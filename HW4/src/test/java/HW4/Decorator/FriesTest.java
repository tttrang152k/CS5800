package HW4.Decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FriesTest {
    @Test
    public void testFriesCost() {
        Fries fries = new Fries();
        assertEquals(5.5, fries.price());
    }

    @Test
    public void testFriesDescription() {
        Fries fries = new Fries();
        assertEquals("Fries", fries.description());
    }
}