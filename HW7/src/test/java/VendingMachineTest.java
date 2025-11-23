import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VendingMachineTest {

    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture(){ System.setOut(new java.io.PrintStream(buf)); }
        String out(){ return buf.toString(); }
        @Override public void close(){ System.setOut(orig); }
    }

    static class TestState implements StateOfVendingMachine {
        boolean selectCalled, insertCalled, dispenseCalled;
        String lastName; int lastQty; double lastAmount;

        @Override public void selectSnack(String name, int quantity) {
            selectCalled = true; lastName = name; lastQty = quantity;
        }
        @Override public void insertMoney(double amount) {
            insertCalled = true; lastAmount = amount;
        }
        @Override public void dispenseSnack() { dispenseCalled = true; }
    }

    private Map<String, Snack> sampleInventory() {
        Map<String, Snack> inv = new HashMap<>();
        inv.put("Coke", new Snack("Coke", 1.50, 2));
        inv.put("Doritos", new Snack("Doritos", 2.50, 3));
        inv.put("Cheetos", new Snack("Cheetos", 2.25, 1));
        return inv;
    }

    @Test
    void testGetSetSnacks() {
        VendingMachine vm = new VendingMachine();
        Map<String, Snack> inv = sampleInventory();
        vm.setSnacks(inv);
        assertSame(inv, vm.getSnacks());
    }

    @Test
    void testGetSetMachineState() {
        VendingMachine vm = new VendingMachine();
        TestState ts = new TestState();
        vm.setMachineState(ts);
        assertSame(ts, vm.getMachineState());
    }

    @Test
    void testSelectSnack_snackNotFound_printsMessageNoStateCall() {
        VendingMachine vm = new VendingMachine();
        vm.setSnacks(sampleInventory());

        TestState ts = new TestState();
        vm.setMachineState(ts);

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.selectSnack("Pepsi", 1);
            out = cap.out();
        }
        assertTrue(out.contains("Snack Pepsi not found"));
        assertFalse(ts.selectCalled, "State.selectSnack should not be called when not found");
    }

    @Test
    void testSelectSnack_notEnoughStock_printsMessageNoStateCall() {
        VendingMachine vm = new VendingMachine();
        vm.setSnacks(sampleInventory()); // Cheetos qty=1

        TestState ts = new TestState();
        vm.setMachineState(ts);

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.selectSnack("Cheetos", 2);
            out = cap.out();
        }
        assertTrue(out.contains("Not enough stock for snack Cheetos"));
        assertTrue(out.contains("Available stock: 1"));
        assertTrue(out.contains("Transaction rejected"));
        assertFalse(ts.selectCalled);
    }

    @Test
    void testSelectSnack_success_setsSelection_prints_movesState() {
        VendingMachine vm = new VendingMachine();
        vm.setSnacks(sampleInventory());

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.selectSnack("Coke", 2);
            out = cap.out();
        }
        assertTrue(out.contains("You selected 2 Coke with $1.50 each. Total: $3.00"));
        assertNotNull(vm.getSelectedSnack());
        assertEquals("Coke", vm.getSelectedSnack().getName());
        assertEquals(2, vm.getSelectedSnackQuantity());

        assertTrue(vm.getMachineState() instanceof WaitingForMoneyState);
    }

    @Test
    void testInsertMoney_delegatesToState() {
        VendingMachine vm = new VendingMachine();
        TestState ts = new TestState();
        vm.setMachineState(ts);

        vm.insertMoney(4.25);
        assertTrue(ts.insertCalled);
        assertEquals(4.25, ts.lastAmount);
    }

    @Test
    void testDispenseSnack_delegatesToState() {
        VendingMachine vm = new VendingMachine();
        TestState ts = new TestState();
        vm.setMachineState(ts);

        vm.dispenseSnack();
        assertTrue(ts.dispenseCalled);
    }

    @Test
    void testInsertMoreAndGetSetInsertedAmount() {
        VendingMachine vm = new VendingMachine();
        assertEquals(0.0, vm.getInsertedAmount());

        vm.insertMoreMoney(1.50);
        vm.insertMoreMoney(0.75);
        assertEquals(2.25, vm.getInsertedAmount(), 1e-9);

        vm.setInsertedAmount(10.0);
        assertEquals(10.0, vm.getInsertedAmount(), 1e-9);
    }

    @Test
    void testGettersForSelectedSnack() {
        VendingMachine vm = new VendingMachine();
        vm.setSnacks(sampleInventory());
        vm.selectSnack("Doritos", 2);

        assertEquals("Doritos", vm.getSelectedSnack().getName());
        assertEquals(2, vm.getSelectedSnackQuantity());
    }

    @Test
    void testSetSelectedSnack_setsSelectionQuantityToStockQuantity() {
        VendingMachine vm = new VendingMachine();
        Snack s = new Snack("Coke", 1.5, 7);
        vm.setSelectedSnack(s);

        assertSame(s, vm.getSelectedSnack());
        assertEquals(7, vm.getSelectedSnackQuantity(),
                "Per implementation, setSelectedSnack() sets quantity to stock quantity");
    }

    @Test
    void testClearSelection_resetsSelectionAndQuantity() {
        VendingMachine vm = new VendingMachine();
        vm.setSnacks(sampleInventory());
        vm.selectSnack("Coke", 2);

        vm.clearSelection();
        assertNull(vm.getSelectedSnack());
        assertEquals(0, vm.getSelectedSnackQuantity());
    }
}
