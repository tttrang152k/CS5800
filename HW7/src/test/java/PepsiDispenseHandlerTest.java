import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PepsiDispenseHandlerTest {

    static class SpyHandler extends SnackDispenseHandler {
        boolean called; Snack last; int qty; SpyHandler(){ super(null); }
        @Override public void dispenseSnack(Snack snack, int quantity){ called=true; last=snack; qty=quantity; }
    }

    @Test
    void testDispenseSnack_pepsiMatches_reducesQuantity_noDelegate() {
        Snack pepsi = new Snack("Pepsi", 1.45, 4);
        SpyHandler spy = new SpyHandler();
        PepsiDispenseHandler handler = new PepsiDispenseHandler(spy);

        handler.dispenseSnack(pepsi, 3);

        assertEquals(1, pepsi.getQuantity());
        assertFalse(spy.called);
    }

    @Test
    void testDispenseSnack_notPepsi_delegatesToNext() {
        Snack other = new Snack("KitKat", 1.25, 7);
        SpyHandler spy = new SpyHandler();
        PepsiDispenseHandler handler = new PepsiDispenseHandler(spy);

        handler.dispenseSnack(other, 5);

        assertTrue(spy.called);
        assertSame(other, spy.last);
        assertEquals(5, spy.qty);
        assertEquals(7, other.getQuantity());
    }
}
