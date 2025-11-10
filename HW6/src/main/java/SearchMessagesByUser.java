import java.util.Iterator;
import java.util.List;

public class SearchMessagesByUser implements Iterator<Message> {
    private List<Message> messages;
    private User userToSearchWith;
    private int interator;

    public SearchMessagesByUser(List<Message> messages, User user) {
        this.messages = messages;
        this.userToSearchWith = user;
        this.interator = 0;
    }

    @Override
    public boolean hasNext() {
        while (interator < messages.size()) {
            Message message = messages.get(interator);
            if (message.getSender().equals(userToSearchWith) || message.getRecipients().contains(userToSearchWith)) {
                return true;
            }
            interator++;
        }
        return false;
    }

    @Override
    public Message next() {
        if (hasNext()) {
            Message message = messages.get(interator);
            interator++;
            return message;
        }
        return null;
    }
}
