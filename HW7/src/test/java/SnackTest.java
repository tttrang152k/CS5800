import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnackTest {

    @Test
    void testConstructorAndGetters() {
        Snack s = new Snack("Coke", 1.50, 3);
        assertEquals("Coke", s.getName());
        assertEquals(1.50, s.getPrice());
        assertEquals(3, s.getQuantity());
    }

    @Test
    void testSettersMutateFields() {
        Snack s = new Snack("X", 0.0, 0);
        s.setName("Pepsi");
        s.setPrice(1.45);
        s.setQuantity(5);

        assertEquals("Pepsi", s.getName());
        assertEquals(1.45, s.getPrice());
        assertEquals(5, s.getQuantity());
    }
}
