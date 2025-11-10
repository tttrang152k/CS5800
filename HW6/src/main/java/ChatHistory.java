import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatHistory implements IterableByUser {
    private List<Message> messages;

    public ChatHistory() {
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public Message getLastMessage(User user) {
        if (this.messages.isEmpty()) {
            System.out.println("[Chat History] No messages in chat history");
            return null;
        }
        else {
            return this.messages.getLast();
        }
    }

    @Override
    public Iterator<Message> iterator(User userToSearchWith) {
        return new SearchMessagesByUser(this.messages, userToSearchWith);
    }
}
