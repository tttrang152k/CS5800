package HW4.Bridge;

public class CashOnDeliveryPayment extends PaymentNotification {
    private final String orderID;

    public CashOnDeliveryPayment(NotificationChannel channel, String customerName, String orderID){
        super(channel, customerName);
        this.orderID = orderID;
    }

    public void notifyCustomer(){
        channel.send(customerName, "[CASH] Payment on delivery for order " + orderID + " sent!");
    }


}
