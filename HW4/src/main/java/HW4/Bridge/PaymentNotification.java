package HW4.Bridge;

public abstract class PaymentNotification {
    protected NotificationChannel channel;
    protected String customerName;
     public PaymentNotification(NotificationChannel channel, String customerName) {
         this.channel = channel;
         this.customerName = customerName;
     }

     public abstract void notifyCustomer();
}
