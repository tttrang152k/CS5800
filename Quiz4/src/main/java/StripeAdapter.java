public class StripeAdapter implements Payable{
    private Stripe stripe;

    public StripeAdapter(Stripe stripe){
        this.stripe = stripe;
    }

    @Override
    public void pay(double amount) {
        stripe.stripePayment(amount);
    }
}
