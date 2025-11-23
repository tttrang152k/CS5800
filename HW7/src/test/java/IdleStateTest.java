import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdleStateTest {

    static class OutCapture implements AutoCloseable {
        private final java.io.PrintStream orig = System.out;
        private final java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        OutCapture(){ System.setOut(new java.io.PrintStream(buf)); }
        String out(){ return buf.toString(); }
        @Override public void close(){ System.setOut(orig); }
    }

    @Test
    void testSelectSnack_movesToWaitingForMoney_printsSelecting() {
        VendingMachine vm = new VendingMachine();
        vm.setMachineState(new IdleState(vm));

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().selectSnack("Coke", 2);
            out = cap.out();
        }

        assertTrue(out.contains("Selecting snack: 2 Coke"));
        assertTrue(vm.getMachineState() instanceof WaitingForMoneyState,
                "After selecting, state should be WaitingForMoneyState");
    }

    @Test
    void testInsertMoney_printsPrompt() {
        VendingMachine vm = new VendingMachine();
        vm.setMachineState(new IdleState(vm));

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().insertMoney(2.0);
            out = cap.out();
        }
        assertTrue(out.contains("Please select a snack first before inserting money"));
    }

    @Test
    void testDispenseSnack_printsPrompt() {
        VendingMachine vm = new VendingMachine();
        vm.setMachineState(new IdleState(vm));

        String out;
        try (OutCapture cap = new OutCapture()) {
            vm.getMachineState().dispenseSnack();
            out = cap.out();
        }
        assertTrue(out.contains("Please select a snack first"));
    }
}
