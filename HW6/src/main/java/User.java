import java.util.Iterator;

public class User implements IterableByUser {
    private final String name;
    private final ChatHistory chatHistory;

    public User(String name) {
        this.name = name;
        this.chatHistory = new ChatHistory();
    }

    public String getUserName() {
        return name;
    }

    public void sendMessage(Message message) {
        this.chatHistory.addMessage(message);
        System.out.println("[Sender] " + this.getUserName() + " sent: " + message);
    }

    public void receiveMessage(Message message) {
        this.chatHistory.addMessage(message);
        System.out.println("[Recipient] " + this.getUserName() + " received: " + message);
    }

    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    public void undoLastMessage() {
        Message lastMessage = this.chatHistory.getLastMessage(this);
        lastMessage.undoFromMemento();
    }

    @Override
    public Iterator<Message> iterator(User userToSearchWith){
        return this.chatHistory.iterator(userToSearchWith);
    }
}
