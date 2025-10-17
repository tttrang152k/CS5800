package HW4.Decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class HotDogTest {
    @Test
    public void testHotdogCost() {
        HotDog hotdog = new HotDog();
        assertEquals(4.25, hotdog.price());
    }

    @Test
    public void testHotdogDescription() {
        HotDog hotdog = new HotDog();
        assertEquals("Hot Dog", hotdog.description());
    }
}