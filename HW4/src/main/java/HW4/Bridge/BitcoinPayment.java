package HW4.Bridge;

public class BitcoinPayment extends PaymentNotification {
    private final String transactionID;

    public BitcoinPayment(NotificationChannel channel, String customerName, String transactionID){
        super(channel, customerName);
        this.transactionID = transactionID;
    }

    @Override
    public void notifyCustomer() {
        channel.send(customerName, "[BITCOIN] Crypto payment for transaction: " + transactionID + " sent!");
    }
}
