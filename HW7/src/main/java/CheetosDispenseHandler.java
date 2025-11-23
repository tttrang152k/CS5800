public class CheetosDispenseHandler extends SnackDispenseHandler{
    public CheetosDispenseHandler(SnackDispenseHandler handler) {
        super(handler);
    }

    public void dispenseSnack(Snack snack, int quantity) {
        if (snack.getName().equalsIgnoreCase("Cheetos")) {
            System.out.println("Dispensing: " + quantity + " " + snack.getName());
            snack.setQuantity(snack.getQuantity() - quantity);
        } else {
            super.dispenseSnack(snack, quantity);
        }
    }
}
