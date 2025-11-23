public class KitKatDispenseHandler extends SnackDispenseHandler {
    public KitKatDispenseHandler(SnackDispenseHandler handler) {
        super(handler);
    }

    public void dispenseSnack(Snack snack, int quantity) {
        if (snack.getName().equalsIgnoreCase("Kitkat")) {
            System.out.println("Dispensing: " + quantity + " " + snack.getName());
            snack.setQuantity(snack.getQuantity() - quantity);
        } else {
            super.dispenseSnack(snack, quantity);
        }
    }
}
