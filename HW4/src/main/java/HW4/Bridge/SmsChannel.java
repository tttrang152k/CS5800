package HW4.Bridge;

public class SmsChannel implements NotificationChannel {
    @Override
    public void send(String to, String message) {
        System.out.println("[SMS] notification sending to: " + to);
        System.out.println("[SMS] notification message: " + message);
    }
}
