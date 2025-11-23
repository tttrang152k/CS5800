public class PepsiDispenseHandler extends SnackDispenseHandler{
    public PepsiDispenseHandler(SnackDispenseHandler handler) {
        super(handler);
    }

    public void dispenseSnack(Snack snack, int quantity) {
        if (snack.getName().equalsIgnoreCase("Pepsi")) {
            System.out.println("Dispensing: " + quantity + " " + snack.getName());
            snack.setQuantity(snack.getQuantity() - quantity);
        } else {
            super.dispenseSnack(snack, quantity);
        }
    }
}
