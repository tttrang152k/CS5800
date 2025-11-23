import java.util.HashMap;
import java.util.Map;

public class Driver {
    public static Map<String, Snack> fillSnacks() {
        Map<String, Snack> snacks = new HashMap<>();
        snacks.put("Coke", new Snack("Coke",1.50, 2));
        snacks.put("Pepsi", new Snack("Pepsi",  1.45, 5));
        snacks.put("Cheetos", new Snack("Cheetos",2.25, 1));
        snacks.put("Doritos", new Snack("Doritos",2.50, 3));
        snacks.put("KitKat", new Snack("KitKat", 1.25, 2));
        snacks.put("Snickers", new Snack("Snickers",1.00, 4));
        return snacks;
    }
    public static void main(String[] args) {
        // Fill Vending Machine with snacks first
        Map<String, Snack> snacks = fillSnacks();
        VendingMachine machine = new VendingMachine();
        machine.setSnacks(snacks);

        machine.selectSnack("Coke", 2);
        machine.insertMoney(9);
        machine.dispenseSnack();
        System.out.println("-----------------------");

        machine.selectSnack("Doritos", 2);
        machine.insertMoney(5);
        machine.dispenseSnack();
        System.out.println("-----------------------");

        System.out.println("----------- Not Enough Stock -----------");
        machine.selectSnack("Cheetos", 2);
        machine.insertMoney(5);
        machine.dispenseSnack();
        System.out.println("-----------------------");

        System.out.println("----------- Not Enough Money Inserted -----------");
        machine.selectSnack("Cheetos", 1);
        machine.insertMoney(2);
        machine.dispenseSnack();
        System.out.println("-----------------------");

        machine.selectSnack("KitKat", 1);
        machine.insertMoney(3);
        machine.dispenseSnack();
        System.out.println("-----------------------");

        machine.selectSnack("Snickers", 4);
        machine.insertMoney(10);
        machine.dispenseSnack();
        System.out.println("-----------------------");

        System.out.println("----------- No more snickers in stock -----------");
        machine.selectSnack("Snickers", 2);
        machine.insertMoney(5);
        machine.dispenseSnack();
        System.out.println("-----------------------");

    }
}
