package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Club implements Parcelable {
    private Date time;
    private String id;
    private String pid;
    private String title;
    private String description;
    private ArrayList<String> imageUrl;
    private ArrayList<String> fileName;
    private ArrayList<String> fileUrl;
    private ArrayList<String> heads;
    private ArrayList<String> comments;
    private String author;
    private String club_image_url;
    private String club_cover_image_url;
    private String club_name;
    private long contact;
    private int followers;
    private String position;
    private int like;
    private int Comment;



    public Club() { }

    public Club(String id, String description, ArrayList<String> heads, String club_image_url, String club_name,int followers,String club_cover_image_url,long contact) {
        this.id = id;
        this.description = description;
        this.heads = heads;
        this.club_image_url = club_image_url;
        this.club_name = club_name;
        this.followers = followers;
        this.club_cover_image_url = club_cover_image_url;
        this.contact=contact;
    }

    public Club(String title, String club_image_url, Date time, String id, String FileUrl, int like, int comment) {
        this.title = title;
        this.club_image_url = club_image_url;
        this.time = time;
        this.id=id;
        this.fileUrl=new ArrayList<>();
        this.fileUrl.add(FileUrl);
        this.like = like;
        this.Comment = comment;
    }

    protected Club(Parcel in) {
        id = in.readString();
        title = in.readString();
        club_name = in.readString();
        pid = in.readString();

        description = in.readString();
        imageUrl = in.createStringArrayList();

        fileName = in.createStringArrayList();
        fileUrl = in.createStringArrayList();

        like = in.readInt();
        Comment =in.readInt();
        contact = in.readLong();
        club_cover_image_url = in.readString();

        author = in.readString();
        club_image_url = in.readString();
        position = in.readString();
        time=new Date(in.readLong());
    }

    public static final Creator<Club> CREATOR = new Creator<Club>() {
        @Override
        public Club createFromParcel(Parcel in) {
            return new Club(in);
        }

        @Override
        public Club[] newArray(int size) {
            return new Club[size];
        }
    };

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public String getClub_cover_image_url() {
        return club_cover_image_url;
    }

    public void setClub_cover_image_url(String club_cover_image_url) {
        this.club_cover_image_url = club_cover_image_url;
    }

    public String getPid() {
        return pid;
    }

    public ArrayList<String> getHeads() {
        return heads;
    }

    public void setHeads(ArrayList<String> heads) {
        this.heads = heads;
    }


    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getContact() {
        return contact;
    }

    public String getClub_name() {
        return club_name;
    }

    public void setClub_name(String club_name) {
        this.club_name = club_name;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getComment() {
        return Comment;
    }

    public void setComment(int comment) {
        Comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ArrayList<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ArrayList<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getClub_image_url() {
        return club_image_url;
    }

    public void setClub_image_url(String club_image_url) {
        this.club_image_url = club_image_url;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeStringList(this.imageUrl);
        dest.writeStringList(this.fileName);
        dest.writeStringList(this.fileUrl);
        dest.writeString(this.pid);
        dest.writeLong(this.contact);
        dest.writeString(this.club_cover_image_url);
        dest.writeString(this.author);
        dest.writeString(this.club_name);
        dest.writeString(this.club_image_url);
        dest.writeString(this.position);
        dest.writeLong(this.time.getTime());
        dest.writeInt(this.like);
        dest.writeInt(this.Comment);
    }
}
