package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubPost{
    String author;
    String cid;
    ArrayList<Comments> comment;
    String description;
    ArrayList<HashMap<String,String>> file_urls;
    ArrayList<String> img_urls;
    HashMap<String,Long> like;
    String time;
    String title;
    String id;


    public ClubPost(String author, String cid, ArrayList<Comments> comment, String descrption, ArrayList<HashMap<String, String>> file_urls, ArrayList<String> img_urls, HashMap<String, Long> like, String time, String title, String id) {
        this.author = author;
        this.cid = cid;
        this.comment = comment;
        this.description = descrption;
        this.file_urls = file_urls;
        this.img_urls = img_urls;
        this.like = like;
        this.time = time;
        this.title = title;
        this.id = id;
    }

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

    public ArrayList<HashMap<String, String>> getFile_urls() {
        return file_urls;
    }

    public void setFile_urls(ArrayList<HashMap<String, String>> file_urls) {
        this.file_urls = file_urls;
    }

    public ArrayList<String> getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(ArrayList<String> img_urls) {
        this.img_urls = img_urls;
    }

    public HashMap<String, Long> getLike() {
        return like;
    }

    public void setLike(HashMap<String, Long> like) {
        this.like = like;
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
}
