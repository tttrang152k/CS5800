public class Square {
    private String locationId;

    public Square(String locationId) {
        this.locationId = locationId;
    }

    public void squarePayment(double amount) {
        System.out.println("============= Square Payment =============");
        System.out.println("$" + amount + " from location ID: " + locationId);
    }
}
