package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubPost implements Parcelable{
    String author;
    String cid;
    ArrayList<Comments> comment;
    String description;
    private ArrayList<String> fileName;
    private ArrayList<String> fileUrl;
    ArrayList<String> img_urls;
    ArrayList<String> like;
    String time;
    String title;
    String id;

    public ClubPost() { }

    protected ClubPost(Parcel in) {
        author = in.readString();
        cid = in.readString();
        comment = in.createTypedArrayList(Comments.CREATOR);
        description = in.readString();
        fileName = in.createStringArrayList();
        fileUrl = in.createStringArrayList();
        img_urls = in.createStringArrayList();
        like = in.createStringArrayList();
        time = in.readString();
        title = in.readString();
        id = in.readString();
    }

    public static final Creator<ClubPost> CREATOR = new Creator<ClubPost>() {
        @Override
        public ClubPost createFromParcel(Parcel in) {
            return new ClubPost(in);
        }

        @Override
        public ClubPost[] newArray(int size) {
            return new ClubPost[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public ArrayList<Comments> getComment() {
        return comment;
    }

    public void setComment(ArrayList<Comments> comment) {
        this.comment = comment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(ArrayList<String> img_urls) {
        this.img_urls = img_urls;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(author);
        parcel.writeString(cid);
        parcel.writeTypedList(comment);
        parcel.writeString(description);
        parcel.writeStringList(fileName);
        parcel.writeStringList(fileUrl);
        parcel.writeStringList(img_urls);
        parcel.writeStringList(like);
        parcel.writeString(time);
        parcel.writeString(title);
        parcel.writeString(id);
    }
}
