public class CokeDispenseHandler extends SnackDispenseHandler {
    public CokeDispenseHandler(SnackDispenseHandler handler) {
        super(handler);
    }

    public void dispenseSnack(Snack snack, int quantity) {
        if (snack.getName().equalsIgnoreCase("Coke")) {
            System.out.println("Dispensing: " + quantity + " " + snack.getName());
            snack.setQuantity(snack.getQuantity() - quantity);
        } else {
            super.dispenseSnack(snack, quantity);
        }
    }
}
