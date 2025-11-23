public class DispensingState implements StateOfVendingMachine {
    private final VendingMachine vendingMachine;
    private final SnackDispenseHandler snackDispenseHandler;

    public DispensingState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
        this.snackDispenseHandler = new CokeDispenseHandler(new PepsiDispenseHandler(new CheetosDispenseHandler(new DoritosDispenseHandler(
                new KitKatDispenseHandler(new SnickersDispenseHandler(null))))));
    }

    @Override
    public void selectSnack(String name, int quantity) {
        System.out.println("Waiting for snack dispensing. Cannot select another snack.");
    }

    @Override
    public void insertMoney(double amount) {
        System.out.println("Waiting for snack dispensing. Cannot insert money.");
    }

    @Override
    public void dispenseSnack() {
        snackDispenseHandler.dispenseSnack(vendingMachine.getSelectedSnack(), vendingMachine.getSelectedSnackQuantity());
        vendingMachine.clearSelection();
        vendingMachine.setMachineState(new IdleState(vendingMachine));
    }
}
