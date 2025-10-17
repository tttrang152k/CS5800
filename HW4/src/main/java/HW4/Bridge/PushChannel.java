package HW4.Bridge;

public class PushChannel implements NotificationChannel {
    @Override
    public void send(String to, String message) {
        System.out.println("[Push] notification sending to: " + to);
        System.out.println("[Push] notification message: " + message);
    }
}
