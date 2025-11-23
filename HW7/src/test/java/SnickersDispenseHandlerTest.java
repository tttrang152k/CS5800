import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnickersDispenseHandlerTest {

    static class SpyHandler extends SnackDispenseHandler {
        boolean called; Snack snack; int qty;
        SpyHandler(){ super(null); }
        @Override
        public void dispenseSnack(Snack snack, int quantity){
            called = true; this.snack = snack; this.qty = quantity;
        }
    }

    @Test
    void testDispenseSnack_snickersMatch_reducesQuantity_noDelegate() {
        Snack s = new Snack("Snickers", 1.0, 5);
        SpyHandler spy = new SpyHandler();
        SnickersDispenseHandler handler = new SnickersDispenseHandler(spy);

        handler.dispenseSnack(s, 2);

        assertEquals(3, s.getQuantity());
        assertFalse(spy.called, "Should not delegate when handler matches");
    }

    @Test
    void testDispenseSnack_nonMatching_delegates() {
        Snack s = new Snack("Coke", 1.5, 4);
        SpyHandler spy = new SpyHandler();
        SnickersDispenseHandler handler = new SnickersDispenseHandler(spy);

        handler.dispenseSnack(s, 1);

        assertTrue(spy.called, "Should delegate when name does not match");
        assertSame(s, spy.snack);
        assertEquals(1, spy.qty);
        assertEquals(4, s.getQuantity(), "Quantity unchanged by Snickers handler for non-Snickers");
    }
}
