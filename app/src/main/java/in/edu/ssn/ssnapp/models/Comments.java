package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Comments implements Parcelable {

    private String id;
    private String author;
    private String message;
    private ArrayList<String> reply;



    public Comments() { }

    protected Comments(Parcel in) {
        id = in.readString();
        author = in.readString();
        message = in.readString();
        reply = in.createStringArrayList();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public ArrayList<String> getReply() {
        return reply;
    }

    public void setReply(ArrayList<String> reply) {
        this.reply = reply;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(message);
        parcel.writeStringList(reply);

    }
}
