package HW4.Bridge;

public class OnlinePaymentNotification extends PaymentNotification {
    private final String payMethod;
    public OnlinePaymentNotification(NotificationChannel channel, String customerName, String method) {
        super(channel, customerName);
        this.payMethod = method;
    }
    @Override
    public void notifyCustomer() {
        channel.send(customerName, "[ONLINE] " + payMethod + " payment sent!");
    }
}
