public abstract class SnackDispenseHandler {
    private final SnackDispenseHandler next;

    public SnackDispenseHandler(SnackDispenseHandler next) {
        this.next = next;
    }

    public void dispenseSnack(Snack snack, int quantity){
        if (next != null) {
            next.dispenseSnack(snack, quantity);
        }
        else if (snack.getQuantity() <= quantity) {
            System.out.println(snack.getName() + ": OUT OF STOCK!");
        }
    }
}
