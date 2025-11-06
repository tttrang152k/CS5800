public class Paypal {
    private String email;

    public Paypal(String email) {
        this.email = email;
    }

    public void paypalPayment(double amount) {
        System.out.println("============= Paypal Payment =============");
        System.out.println("$" + amount + " from Paypal account: " + email);
    }

}
