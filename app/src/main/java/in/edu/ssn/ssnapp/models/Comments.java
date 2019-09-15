package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Comments implements Parcelable{

    String author;
    String message;
    ArrayList<HashMap<String,String>> reply;

    public Comments() {
    }

    protected Comments(Parcel in) {
        author = in.readString();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel in) {
            return new Comments(in);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };

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
