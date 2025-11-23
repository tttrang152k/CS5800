import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoritosDispenseHandlerTest {

    static class SpyHandler extends SnackDispenseHandler {
        boolean called; Snack last; int qty; SpyHandler(){ super(null); }
        @Override public void dispenseSnack(Snack snack, int quantity){ called=true; last=snack; qty=quantity; }
    }

    @Test
    void testDispenseSnack_doritosMatches_reducesQuantity_noDelegate() {
        Snack s = new Snack("Doritos", 2.50, 4);
        SpyHandler spy = new SpyHandler();
        DoritosDispenseHandler handler = new DoritosDispenseHandler(spy);

        handler.dispenseSnack(s, 3);

        assertEquals(1, s.getQuantity());
        assertFalse(spy.called);
    }

    @Test
    void testDispenseSnack_notDoritos_delegatesToNext() {
        Snack s = new Snack("Coke", 1.50, 4);
        SpyHandler spy = new SpyHandler();
        DoritosDispenseHandler handler = new DoritosDispenseHandler(spy);

        handler.dispenseSnack(s, 1);

        assertTrue(spy.called);
        assertSame(s, spy.last);
        assertEquals(1, spy.qty);
        assertEquals(4, s.getQuantity());
    }
}
