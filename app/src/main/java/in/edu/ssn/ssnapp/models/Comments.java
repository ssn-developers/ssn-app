package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comments implements Parcelable, Comparable {

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
    String author;
    String message;
    Date time;
    ArrayList<HashMap<String, Object>> reply;

    public Comments() {
    }

    public Comments(String author, String message, Date time, ArrayList<HashMap<String, Object>> reply) {
        this.author = author;
        this.message = message;
        this.time = time;
        this.reply = reply;
        //Collections.sort(reply,new MapComparator("time"));
    }


    public Comments(String author, String message, ArrayList<HashMap<String, Object>> reply) {
        this.author = author;
        this.message = message;
        this.reply = reply;
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

    public ArrayList<HashMap<String, Object>> getReply() {
        return reply;
    }

    public void setReply(ArrayList<HashMap<String, Object>> reply) {
        this.reply = reply;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public int compareTo(Object o) {

        if (this.getTime().compareTo(((Comments) o).getTime()) > 0)
            return -1;
        else
            return 1;

    }

    class MapComparator implements Comparator<Map<String, Object>> {
        private final String key;

        public MapComparator(String key) {
            this.key = key;
        }

        public int compare(Map<String, Object> first, Map<String, Object> second) {
            //((Timestamp)i.get("time")).toDate()
            Date firstValue = ((Timestamp) first.get(key)).toDate();
            Date secondValue = ((Timestamp) first.get(key)).toDate();
            if (firstValue.compareTo(secondValue) > 0)
                return -1;
            else
                return 1;
        }
    }
}
