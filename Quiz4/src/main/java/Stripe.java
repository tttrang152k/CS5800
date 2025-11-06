public class Stripe {
    private String cardName;
    private String cardNumber;
    private String expirationDate;

    public Stripe(String cardName, String cardNumber, String expirationDate) {
        this.cardName = cardName;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
    }

    public void stripePayment(double amount){
        System.out.println("============= Stripe Payment =============");
        System.out.println("$" + amount + " from" );
        System.out.println("+ Card Name: " + cardName);
        System.out.println("+ Card Number: " + cardNumber);
        System.out.println("+ Expiration Date: " + expirationDate);
    }


}
