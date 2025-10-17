package HW4.Bridge;

public class HelperChannel implements NotificationChannel {
    private String recipient;
    private String message;

    @Override
    public void send(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

}
