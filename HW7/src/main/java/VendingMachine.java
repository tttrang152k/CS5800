import java.util.HashMap;
import java.util.Map;

public class VendingMachine {
    private StateOfVendingMachine state;
    private Snack selectedSnack;
    private int selectedSnackQuantity;
    private double insertedAmount;
    private Map<String, Snack> snacks;

    public VendingMachine() {
        state = new IdleState(this);
        snacks = new HashMap<>();
        insertedAmount = 0;
    }

    public Map<String, Snack> getSnacks() {
        return snacks;
    }

    public void setSnacks(Map<String, Snack> snacks) {
        this.snacks = snacks;
    }

    public void setMachineState(StateOfVendingMachine state) {
        this.state = state;
    }

    public StateOfVendingMachine getMachineState() {
        return state;
    }

    public void selectSnack(String name, int quantity) {
        if (!snacks.containsKey(name)) {
            System.out.println("Snack " + name + " not found");
            return;
        }

        Snack pickedSnack = snacks.get(name);
        if (pickedSnack.getQuantity() < quantity) {
            System.out.println("Not enough stock for snack " + name);
            System.out.println("Available stock: " + pickedSnack.getQuantity());
            System.out.println("Transaction rejected. Ejecting money back!");
            return;
        }

        selectedSnack = pickedSnack;
        selectedSnackQuantity = quantity;
        System.out.printf("You selected %d %s with $%.2f each. Total: $%.2f \n",
                quantity, name, selectedSnack.getPrice(), selectedSnack.getPrice() * quantity);

        state.selectSnack(name, quantity);
    }

    public void insertMoney(double amount) {
        state.insertMoney(amount);
    }

    public void dispenseSnack() {
        state.dispenseSnack();
    }

    public void insertMoreMoney(double amount) {
        insertedAmount += amount;
    }

    public void setInsertedAmount(double amount) {
        insertedAmount = amount;
    }

    public double getInsertedAmount() {
        return insertedAmount;
    }

    public Snack getSelectedSnack() {
        return selectedSnack;
    }

    public int getSelectedSnackQuantity() {
        return selectedSnackQuantity;
    }

    public void setSelectedSnack(Snack selectedSnack) {
        this.selectedSnack = selectedSnack;
        this.selectedSnackQuantity = selectedSnack.getQuantity();
    }

    public void clearSelection() {
        selectedSnack = null;
        selectedSnackQuantity = 0;
    }
}
