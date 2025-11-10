import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private User sender;
    private List<User> recipients;
    private String content;
    private LocalDateTime timestamp;
    private MessageMemento memento;

    public Message(User sender, List<User> recipients, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.content = message;
        this.timestamp = LocalDateTime.now();
        this.memento = new MessageMemento(message, timestamp);
    }

    public User getSender() {
        return sender;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public String getMessage() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void saveToMemento() {
        memento.setMessageMemento(content, timestamp);
    }

    public void undoFromMemento() {
        this.content = memento.getMessage();
        this.timestamp = memento.getTimestamp();
    }

    @Override
    public String toString() {
        return String.format("\"%s\" (%s)", content, timestamp.toString());
    }
}
