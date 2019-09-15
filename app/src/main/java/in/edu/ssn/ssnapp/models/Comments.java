package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Comments {

    String author;
    String message;
    ArrayList<HashMap<String,String>> reply;

    public Comments(String author, String message, ArrayList<HashMap<String, String>> reply) {
        this.author = author;
        this.message = message;
        this.reply = reply;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<HashMap<String, String>> getReply() {
        return reply;
    }

    public void setReply(ArrayList<HashMap<String, String>> reply) {
        this.reply = reply;
    }
}
