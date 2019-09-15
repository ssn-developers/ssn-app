package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ClubPost implements Parcelable {
    private Date time;
    private String author;
    private String id;
    private String title;
    private ArrayList<String> image_Urls;
    private ArrayList<String> file_urls;
    private ArrayList<String> file_name;
    private ArrayList<String> comment;
    private ArrayList<String> like;
    private String cid;
    private String description;



    public ClubPost() { }

    protected ClubPost(Parcel in) {
        id = in.readString();
        cid = in.readString();
        title = in.readString();
        description = in.readString();
        author = in.readString();
        image_Urls = in.createStringArrayList();
        file_urls = in.createStringArrayList();
        file_name = in.createStringArrayList();
        comment =  in.createStringArrayList();
        like = in.createStringArrayList();
        time = new Date(in.readLong());
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

    public Date getTime() {
        return time;
    }

    public ArrayList<String> getFile_name() {
        return file_name;
    }

    public void setFile_name(ArrayList<String> file_name) {
        this.file_name = file_name;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getImage_Urls() {
        return image_Urls;
    }

    public void setImage_Urls(ArrayList<String> image_Urls) {
        this.image_Urls = image_Urls;
    }

    public ArrayList<String> getFile_urls() {
        return file_urls;
    }

    public void setFile_urls(ArrayList<String> file_urls) {
        this.file_urls = file_urls;
    }

    public ArrayList<String> getComment() {
        return comment;
    }

    public void setComment(ArrayList<String> comment) {
        this.comment = comment;
    }

    public ArrayList<String> getLike() {
        return like;
    }

    public void setLike(ArrayList<String> like) {
        this.like = like;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
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
        parcel.writeString(cid);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(author);
        parcel.writeStringList(image_Urls);
        parcel.writeStringList(file_urls);
        parcel.writeStringList(file_name);
        parcel.writeStringList(comment);
        parcel.writeStringList(like);
        parcel.writeLong(this.time.getTime());

    }
}
