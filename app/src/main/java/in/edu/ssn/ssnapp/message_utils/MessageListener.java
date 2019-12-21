package in.edu.ssn.ssnapp.message_utils;

public interface MessageListener {
    public void onMessageAdded(Message message);
    public void onMessageRemoved(String id);
    public void onMessageModified(String id, Message message);
}
