public class PaypalAdapter implements Payable {
    private Paypal paypal;

    public PaypalAdapter(Paypal paypal) {
        this.paypal = paypal;
    }

    @Override
    public void pay(double amount) {
        paypal.paypalPayment(amount);
    }
}
