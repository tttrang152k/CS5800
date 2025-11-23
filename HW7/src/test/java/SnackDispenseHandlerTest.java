import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnackDispenseHandlerTest {

    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture(){ System.setOut(new java.io.PrintStream(buf)); }
        String out(){ return buf.toString(); }
        @Override public void close(){ System.setOut(orig); }
    }

    static class SpyHandler extends SnackDispenseHandler {
        boolean called = false;
        Snack snack;
        int qty;
        SpyHandler() { super(null); }
        @Override
        public void dispenseSnack(Snack snack, int quantity) {
            called = true;
            this.snack = snack;
            this.qty = quantity;
        }
    }

    static class HeadHandler extends SnackDispenseHandler {
        HeadHandler(SnackDispenseHandler next) { super(next); }
        @Override
        public void dispenseSnack(Snack snack, int quantity) {
            super.dispenseSnack(snack, quantity);
        }
    }

    @Test
    void testDispenseSnack_delegatesToNextWhenPresent() {
        SpyHandler tail = new SpyHandler();
        HeadHandler head = new HeadHandler(tail);

        Snack s = new Snack("Anything", 1.0, 10);
        head.dispenseSnack(s, 3);

        assertTrue(tail.called, "Should delegate to next when present");
        assertSame(s, tail.snack);
        assertEquals(3, tail.qty);
    }

    @Test
    void testDispenseSnack_printsOutOfStockWhenNoNextAndNotEnough() {
        HeadHandler head = new HeadHandler(null);
        Snack s = new Snack("Cheetos", 2.25, 2);

        String out;
        try (OutCapture cap = new OutCapture()) {
            head.dispenseSnack(s, 2);
            out = cap.out();
        }
        assertTrue(out.contains("Cheetos: OUT OF STOCK!"),
                "Should print OUT OF STOCK! when next==null and qty <= requested");
    }
}
