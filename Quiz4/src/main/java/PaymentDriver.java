public class PaymentDriver {
    public static void main(String[] args) {
        Paypal paypal = new Paypal("thitt@cpp.edu");
        Payable paypalAdapter = new PaypalAdapter(paypal);
        paypalAdapter.pay(123.56);
        System.out.println("\n");

        Stripe stripe = new Stripe("Trang Tran", "1234-567-8910", "04/25");
        Payable stripeAdapter = new StripeAdapter(stripe);
        stripeAdapter.pay(789.10);
        System.out.println("\n");

        Square square = new Square("loc-123");
        Payable squareAdatper = new SquareAdatper(square);
        squareAdatper.pay(1112.13);
    }
}
