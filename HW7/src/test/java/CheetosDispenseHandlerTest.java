import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheetosDispenseHandlerTest {

    static class SpyHandler extends SnackDispenseHandler {
        boolean called; Snack last; int qty; SpyHandler(){ super(null); }
        @Override public void dispenseSnack(Snack snack, int quantity){ called=true; last=snack; qty=quantity; }
    }

    @Test
    void testDispenseSnack_cheetosMatches_reducesQuantity_noDelegate() {
        Snack cheetos = new Snack("Cheetos", 2.25, 5);
        SpyHandler spy = new SpyHandler();
        CheetosDispenseHandler handler = new CheetosDispenseHandler(spy);

        handler.dispenseSnack(cheetos, 2);

        assertEquals(3, cheetos.getQuantity());
        assertFalse(spy.called);
    }

    @Test
    void testDispenseSnack_notCheetos_delegatesToNext() {
        Snack doritos = new Snack("Doritos", 2.50, 6);
        SpyHandler spy = new SpyHandler();
        CheetosDispenseHandler handler = new CheetosDispenseHandler(spy);

        handler.dispenseSnack(doritos, 2);

        assertTrue(spy.called);
        assertSame(doritos, spy.last);
        assertEquals(2, spy.qty);
        assertEquals(6, doritos.getQuantity());
    }
}
