public class IdleState implements StateOfVendingMachine{
    private final VendingMachine vendingMachine;

    public IdleState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectSnack(String name, int quantity) {
        System.out.println("Selecting snack: " + quantity + " " + name);
        vendingMachine.setMachineState(new WaitingForMoneyState(vendingMachine));
    }

    @Override
    public void insertMoney(double amount) {
        System.out.println("Please select a snack first before inserting money");
    }

    @Override
    public void dispenseSnack() {
        System.out.println("Please select a snack first");
    }
}
