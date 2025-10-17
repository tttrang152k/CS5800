package HW4.Bridge;

public class Driver {
    public static void main(String[] args){
        // Channels
        NotificationChannel email = new EmailChannel();
        NotificationChannel sms =  new SmsChannel();
        NotificationChannel push = new PushChannel();

        // online & email, online & sms, cash & email, cash & sms
        System.out.println("=============== Part 1 ===============");
        PaymentNotification onlineEmail = new OnlinePaymentNotification(email, "Nima", "CREDIT_CARD");
        onlineEmail.notifyCustomer();
        PaymentNotification onlineSms = new OnlinePaymentNotification(sms, "Taylor", "PAYPAL");
        onlineSms.notifyCustomer();
        PaymentNotification cashEmail = new CashOnDeliveryPayment(email, "Travis", "O1998");
        cashEmail.notifyCustomer();
        PaymentNotification cashSms = new CashOnDeliveryPayment(sms, "Sabrina", "O2000");
        cashSms.notifyCustomer();

        // bitcoin & email, bitcoin & sms
        System.out.println("=============== Part 2 ===============");
        PaymentNotification bitcoinEmail = new BitcoinPayment(email, "Charlie", "B1123");
        bitcoinEmail.notifyCustomer();
        PaymentNotification bitcoinSms = new BitcoinPayment(sms, "Nicky", "B1240");
        bitcoinSms.notifyCustomer();

        // Scenarios: online & push, bitcoin & push
        System.out.println("=============== Part 3 ===============");
        PaymentNotification onlinePush = new OnlinePaymentNotification(push, "Halsey", "CREDIT_CARD");
        onlinePush.notifyCustomer();
        PaymentNotification bitcoinPush = new BitcoinPayment(push, "Sasha", "B7365");
        bitcoinPush.notifyCustomer();
    }
}
