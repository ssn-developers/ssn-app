package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class ClubPost implements Parcelable {
//    private Date time;
//    private String author;
//    private String pid;
//    private String title;
//    private ArrayList<String> imageUrl;
//    private ArrayList<String> fileName;
//    private ArrayList<String> fileUrl;
//    private ArrayList<String> comments;
//    private String position;
//    private int like;
//    private int Comment;

    private String id;
    private String dp_url;
    private String cover_url;
    private String name;
    private Long followers;
    private String contact;
    private ArrayList<String> head;
    private String description;

    public ClubPost() { }

    protected ClubPost(Parcel in) {
        id = in.readString();
        dp_url = in.readString();
        cover_url = in.readString();
        name = in.readString();
        followers = in.readLong();
        contact = in.readString();
        head = in.createStringArrayList();
        description = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDp_url() {
        return dp_url;
    }

    public void setDp_url(String dp_url) {
        this.dp_url = dp_url;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFollowers() {
        return followers;
    }

    public void setFollowers(Long followers) {
        this.followers = followers;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ArrayList<String> getHead() {
        return head;
    }

    public void setHead(ArrayList<String> head) {
        this.head = head;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(dp_url);
        parcel.writeString(cover_url);
        parcel.writeString(name);
        parcel.writeLong(followers);
        parcel.writeString(contact);
        parcel.writeStringList(head);
        parcel.writeString(description);
    }
}
