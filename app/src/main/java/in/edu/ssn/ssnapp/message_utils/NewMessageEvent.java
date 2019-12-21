package in.edu.ssn.ssnapp.message_utils;

public class NewMessageEvent {
    int count;

    public NewMessageEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
