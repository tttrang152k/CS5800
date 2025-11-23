public class DoritosDispenseHandler extends SnackDispenseHandler {
    public DoritosDispenseHandler(SnackDispenseHandler handler) {
        super(handler);
    }

    public void dispenseSnack(Snack snack, int quantity) {
        if (snack.getName().equalsIgnoreCase("Doritos")) {
            System.out.println("Dispensing: " + quantity + " " + snack.getName());
            snack.setQuantity(snack.getQuantity() - quantity);
        } else {
            super.dispenseSnack(snack, quantity);
        }
    }
}
