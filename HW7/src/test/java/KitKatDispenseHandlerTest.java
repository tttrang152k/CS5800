import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KitKatDispenseHandlerTest {

    static class SpyHandler extends SnackDispenseHandler {
        boolean called; Snack last; int qty; SpyHandler(){ super(null); }
        @Override public void dispenseSnack(Snack snack, int quantity){ called=true; last=snack; qty=quantity; }
    }

    @Test
    void testDispenseSnack_kitkatCaseInsensitive_reducesQuantity_noDelegate() {
        Snack s = new Snack("KitKat", 1.25, 3);
        SpyHandler spy = new SpyHandler();
        KitKatDispenseHandler handler = new KitKatDispenseHandler(spy);

        // Handler checks equalsIgnoreCase("Kitkat"), so verify case-insensitivity
        handler.dispenseSnack(s, 2);

        assertEquals(1, s.getQuantity());
        assertFalse(spy.called);
    }

    @Test
    void testDispenseSnack_notKitkat_delegatesToNext() {
        Snack s = new Snack("Snickers", 1.00, 8);
        SpyHandler spy = new SpyHandler();
        KitKatDispenseHandler handler = new KitKatDispenseHandler(spy);

        handler.dispenseSnack(s, 5);

        assertTrue(spy.called);
        assertSame(s, spy.last);
        assertEquals(5, spy.qty);
        assertEquals(8, s.getQuantity());
    }
}
