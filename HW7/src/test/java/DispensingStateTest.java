import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DispensingStateTest {

    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture(){ System.setOut(new java.io.PrintStream(buf)); }
        String out(){ return buf.toString(); }
        @Override public void close(){ System.setOut(orig); }
    }

    private VendingMachine machineWithInventory() {
        Map<String, Snack> inv = new HashMap<>();
        inv.put("Coke", new Snack("Coke", 1.50, 3));
        inv.put("Pepsi", new Snack("Pepsi", 1.45, 2));
        inv.put("Cheetos", new Snack("Cheetos", 2.25, 1));
        inv.put("Doritos", new Snack("Doritos", 2.50, 5));
        inv.put("KitKat", new Snack("KitKat", 1.25, 3));
        inv.put("Snickers", new Snack("Snickers", 1.00, 4));

        VendingMachine vm = new VendingMachine();
        vm.setSnacks(inv);
        return vm;
    }

    @Test
    void testSelectSnack_printsBusyMessage() {
        VendingMachine vm = machineWithInventory();
        vm.setMachineState(new DispensingState(vm));
        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().selectSnack("Coke", 1);
            out = cap.out();
        }
        assertTrue(out.contains("Waiting for snack dispensing. Cannot select another snack."));
    }

    @Test
    void testInsertMoney_printsBusyMessage() {
        VendingMachine vm = machineWithInventory();
        vm.setMachineState(new DispensingState(vm));
        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().insertMoney(1.0);
            out = cap.out();
        }
        assertTrue(out.contains("Waiting for snack dispensing. Cannot insert money."));
    }

    @Test
    void testDispenseSnack_invokesChain_decrementsQty_clearsSelection_setsIdle() {
        VendingMachine vm = machineWithInventory();

        vm.selectSnack("Coke", 2);
        vm.setMachineState(new DispensingState(vm));

        Snack coke = vm.getSnacks().get("Coke");
        int before = coke.getQuantity();

        vm.getMachineState().dispenseSnack();

        assertEquals(before - 2, coke.getQuantity());
        assertNull(vm.getSelectedSnack());
        assertEquals(0, vm.getSelectedSnackQuantity());
        assertTrue(vm.getMachineState() instanceof IdleState);
    }
}
