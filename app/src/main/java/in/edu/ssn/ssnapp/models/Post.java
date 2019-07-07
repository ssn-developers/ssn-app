package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.value.ReferenceValue;

import java.lang.ref.Reference;
import java.util.Date;
import java.util.List;

public class Post implements Parcelable {
    String id;
    Date time;
    String title;
    String description;
    List<String> imageUrl;
    List<String> fileUrl;
    String author;
    String author_image_url;
    String position;
    //private int likes;
    //private int comments;

    public Post() { }

    public Post(String title, String author_image_url, Date time) {
        this.title = title;
        this.author_image_url = author_image_url;
        this.time = time;
    }

    protected Post(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        imageUrl = in.createStringArrayList();
        fileUrl = in.createStringArrayList();
        author = in.readString();
        author_image_url = in.readString();
        position = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

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

    public List<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(List<String> fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor_image_url() {
        return author_image_url;
    }

    public void setAuthor_image_url(String author_image_url) {
        this.author_image_url = author_image_url;
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
        dest.writeStringList(this.fileUrl);
        dest.writeString(this.author);
        dest.writeString(this.author_image_url);
        dest.writeString(this.position);
    }
}
