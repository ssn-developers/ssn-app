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
    Date time;
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
        title = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(cid);
        dest.writeTypedList(comment);
        dest.writeString(description);
        dest.writeStringList(fileName);
        dest.writeStringList(fileUrl);
        dest.writeStringList(img_urls);
        dest.writeStringList(like);
        dest.writeString(title);
        dest.writeString(id);
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
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

    public ArrayList<String> getFileName() {
        return fileName;
    }

    public void setFileName(ArrayList<String> fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(ArrayList<String> fileUrl) {
        this.fileUrl = fileUrl;
    }

    public ArrayList<String> getLike() {
        return like;
    }

    public void setLike(ArrayList<String> like) {
        this.like = like;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
