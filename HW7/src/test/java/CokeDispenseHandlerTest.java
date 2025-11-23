import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CokeDispenseHandlerTest {

    static class SpyHandler extends SnackDispenseHandler {
        Snack lastSnack;
        int lastQty;
        boolean called;

        SpyHandler() { super(null); }

        @Override
        public void dispenseSnack(Snack snack, int quantity) {
            called = true;
            lastSnack = snack;
            lastQty = quantity;
        }
    }

    @Test
    void testDispenseSnack_cokeMatches_reducesQuantity_noDelegate() {
        Snack coke = new Snack("Coke", 1.50, 3);
        SpyHandler spy = new SpyHandler();
        CokeDispenseHandler handler = new CokeDispenseHandler(spy);

        handler.dispenseSnack(coke, 2);

        assertEquals(1, coke.getQuantity(), "Quantity should be decremented by 2");
        assertFalse(spy.called, "Should not delegate when name matches");
    }

    @Test
    void testDispenseSnack_notCoke_delegatesToNext() {
        Snack pepsi = new Snack("Pepsi", 1.45, 5);
        SpyHandler spy = new SpyHandler();
        CokeDispenseHandler handler = new CokeDispenseHandler(spy);

        handler.dispenseSnack(pepsi, 2);

        assertTrue(spy.called, "Should delegate when name does not match");
        assertSame(pepsi, spy.lastSnack);
        assertEquals(2, spy.lastQty);
        assertEquals(5, pepsi.getQuantity(), "Quantity unchanged by Coke handler for non-Coke");
    }
}
