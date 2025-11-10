import java.util.*;

public class ChatServer {
    private List<User> users;
    private HashMap<User, Set<User>> blockedUsers;

    public ChatServer() {
        users = new ArrayList<>();
        blockedUsers = new HashMap<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public HashMap<User, Set<User>> getBlockedUsers() {
        return blockedUsers;
    }

    public void registerUser(User newUser) {
        users.add(newUser);
        blockedUsers.put(newUser, new HashSet<>());
        System.out.println("[Server] Successfully registered user: " + newUser.getUserName());
    }

    public void unregiserUser(User user) {
        users.remove(user);
        blockedUsers.remove(user);
        System.out.println("[Server] Successfully unregistered user: " + user.getUserName());
    }

    public void blockUser(User user, User blockedUser) {
        blockedUsers.get(user).add(blockedUser);
        System.out.println("[Server]: " + user.getUserName() +  " has blocked " + blockedUser.getUserName());
    }

    public void unblockUser(User user, User unblockedUser) {
        blockedUsers.get(user).remove(unblockedUser);
        System.out.println("[Server]: " + user.getUserName() +  " has unblocked " + unblockedUser.getUserName());
    }

    public void sendMessage(Message message) {
        User sender = message.getSender();
        List<User> recipients = message.getRecipients();

        // Check if sender is registered
        if(!users.contains(sender)) {
            System.out.println("[Server]: Cannot send message. User " + sender.getUserName() + " isn't registered");
            return;
        }

        // Check if recipients are registered
        List<User> validUsers = new ArrayList<>();
        for (User recipient : recipients) {
            if (!users.contains(recipient)) {
                System.out.println("[Server]: Cannot send message. Recipient user " + recipient.getUserName() + " isn't registered");
            }
            else {
                validUsers.add(recipient);
            }
        }
        sender.sendMessage(message);

        // Send if not blocked
        for (User recipient : validUsers) {
            if (blockedUsers.get(recipient).contains(sender)) {
                System.out.println("[Server]: Cannot send message.");
                System.out.println("[Server]: Recipient " + recipient.getUserName() + " has blocked sender " + sender.getUserName());
            }
            else if (blockedUsers.get(sender).contains(recipient)) {
                System.out.println("[Server]: Cannot send message.");
                System.out.println("[Server]: Sender " + recipient.getUserName() + " has blocked recipient " + sender.getUserName());
            }
            else {
                recipient.receiveMessage(message);
            }
        }
    }
}
