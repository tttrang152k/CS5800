import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WaitingForMoneyStateTest {

    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture(){ System.setOut(new java.io.PrintStream(buf)); }
        String out(){ return buf.toString(); }
        @Override public void close(){ System.setOut(orig); }
    }

    private VendingMachine vmWith(String name, double price, int qty, int requestedQty) {
        VendingMachine vm = new VendingMachine();
        Map<String, Snack> inv = new HashMap<>();
        inv.put(name, new Snack(name, price, qty));
        vm.setSnacks(inv);
        vm.selectSnack(name, requestedQty);
        assertTrue(vm.getMachineState() instanceof WaitingForMoneyState);
        return vm;
    }

    @Test
    void testSelectSnack_printsBlockMessage() {
        VendingMachine vm = vmWith("Coke", 1.50, 5, 2);
        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().selectSnack("Pepsi", 1);
            out = cap.out();
        }
        assertTrue(out.contains("You already selected a snack. Cannot select another snack"));
    }

    @Test
    void testInsertMoney_amountLessThanTotal_transitionsToIdle_printsReject() {
        VendingMachine vm = vmWith("Coke", 1.50, 5, 2);

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().insertMoney(2.00);
            out = cap.out();
        }
        assertTrue(out.contains("Money inserted: $2.00"));
        assertTrue(out.contains("Not enough money. Transaction rejected"));
        assertTrue(vm.getMachineState() instanceof IdleState);
    }

    @Test
    void testInsertMoney_amountGreaterOrEqualTotal_transitionsToDispensing_setsInsertedToZero_printsChange() {
        VendingMachine vm = vmWith("Doritos", 2.50, 4, 2);

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().insertMoney(6.00);
            out = cap.out();
        }
        assertTrue(out.contains("Money inserted: $6.00"));
        assertTrue(out.contains("Money accepted. Here is your changes: $1.00"),
                "Should print change (amount-total)");
        assertEquals(0.0, vm.getInsertedAmount(), 1e-9);
        assertTrue(vm.getMachineState() instanceof DispensingState);
    }

    @Test
    void testDispenseSnack_printsPrompt() {
        VendingMachine vm = vmWith("Coke", 1.50, 5, 1);
        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().dispenseSnack();
            out = cap.out();
        }
        assertTrue(out.contains("Pease insert money to pay for the snack"));
    }
}
