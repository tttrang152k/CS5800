package HW4.Bridge;

public class EmailChannel implements NotificationChannel {
    @Override
    public void send(String to, String message) {
        System.out.println("[Email] notification sending to: " + to);
        System.out.println("[Email] notification message: " + message);
    }
}
