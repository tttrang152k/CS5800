public class WaitingForMoneyState implements StateOfVendingMachine {
    private final VendingMachine vendingMachine;

    public WaitingForMoneyState(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void selectSnack(String name, int quantity) {
        System.out.println("You already selected a snack. Cannot select another snack until you pay your current one.");
    }

    @Override
    public void insertMoney(double amount) {
        double price = vendingMachine.getSelectedSnack().getPrice();
        double quantity = vendingMachine.getSelectedSnackQuantity();
        double inStockQuantity = vendingMachine.getSelectedSnack().getQuantity();
        double total = price * quantity;

        vendingMachine.insertMoreMoney(amount);
        System.out.printf("Money inserted: $%.2f%n", amount);

        if (amount < total) {
            System.out.println("Not enough money. Transaction rejected. Ejecting money back!");
            vendingMachine.setMachineState(new IdleState(vendingMachine));
            return;
        }
        else if (inStockQuantity < quantity) {
            System.out.printf("Not enough %s in stock. Transaction rejected. Ejecting money back!\n",
                    vendingMachine.getSelectedSnack().getName());
            vendingMachine.setMachineState(new IdleState(vendingMachine));
            return;
        }
        else if (amount >= total) {
            double changes = amount - total;
            System.out.printf("Money accepted. Here is your changes: $%.2f \n", changes);
            vendingMachine.setInsertedAmount(0);
            vendingMachine.setMachineState(new DispensingState(vendingMachine));
        }
    }

    @Override
    public void dispenseSnack() {
        System.out.println("Pease insert money to pay for the snack");
    }
}
