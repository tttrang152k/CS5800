package HW4.Decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BurgerTest {

    @Test
    public void testBurgerCost() {
        Burger burger = new Burger();
        assertEquals(6.0, burger.price());
    }

    @Test
    public void testBurgerDescription() {
        Burger burger = new Burger();
        assertEquals("Burger", burger.description());
    }
}