package in.edu.ssn.ssnapp.models;

public class Feedback {
    String time,email,text;

    public Feedback(String time, String email, String text) {
        this.time = time;
        this.email = email;
        this.text = text;
    }

    public Feedback() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
