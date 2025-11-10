import java.util.Iterator;
import java.util.List;

public class MessageDriver {
    private static final ChatServer chatServer = new ChatServer();

    public static void main(String[] args) {
        // Register users
        User Winter = new User("Winter");
        User JK =  new User("JK");
        User Jimin = new User("Jimin");
        chatServer.registerUser(Winter);
        chatServer.registerUser(JK);
        chatServer.registerUser(Jimin);

        // Send messages
        Message msg = new Message(Winter, List.of(Jimin, JK), "Hi there, I'm Winter!");
        Message msg1 = new Message(Winter, List.of(JK), "I really like your solo album");
        Message msg2 = new Message(Jimin, List.of(JK), "Do you want to go out tonight?");
        chatServer.sendMessage(msg);
        chatServer.sendMessage(msg1);
        chatServer.sendMessage(msg2);
        System.out.println();

        // Undo last message
        Message msg3 = new Message(Winter, List.of(JK), "Please do a live stream once you're free");
        Message msg4 = new Message(Winter, List.of(JK), "Last stream was fun!");
        Message msg5 = new Message(Winter, List.of(JK), "Even tho it's long but it's not boring at all");
        chatServer.sendMessage(msg3);
        chatServer.sendMessage(msg4);
        chatServer.sendMessage(msg5);
        msg5.saveToMemento();
        msg5.setContent("It's long but really fun");

        System.out.println("======== SENDER ========");
        System.out.println("Winter's chat:");
        Iterator<Message> iterator = Winter.iterator(Winter);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("======== RECEIVER ========");
        System.out.println("JK's chat:");
        iterator = Winter.iterator(JK);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("=========== Winter undo the last message ===========");
        Winter.undoLastMessage();
        System.out.println("======== SENDER ========");
        System.out.println("Winter's chat:");
        iterator = Winter.iterator(Winter);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("======== RECEIVER ========");
        System.out.println("JK's chat:");
        iterator = Winter.iterator(JK);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println();

        // Block users
        System.out.println("=========== Jimin blocked Winter ===========");
        chatServer.blockUser(Jimin, Winter);
        Message msg6 = new Message(Winter, List.of(Jimin), "Are you here?");
        chatServer.sendMessage(msg6);
        System.out.println();

        // Receive messages
        System.out.println("=========== JK replied to Jimin ===========");
        Message msg7 = new Message(JK, List.of(Jimin), "Sure! What do you want to do?");
        Message msg8 = new Message(JK, List.of(Jimin), "Wanna go eat?");
        chatServer.sendMessage(msg7);
        chatServer.sendMessage(msg8);
        System.out.println("=========== Jimin's Chat ===========");
        iterator = Jimin.iterator(Jimin);
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
