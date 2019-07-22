package in.edu.ssn.ssnapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.value.ReferenceValue;

import java.lang.ref.Reference;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;

public class Post implements Parcelable {
    Date time;

    String id;
    String title;
    String description;

    ArrayList<String> dept;
    ArrayList<String> year;
    ArrayList<String> imageUrl;

    ArrayList<String> fileName;
    ArrayList<String> fileUrl;

    String author;
    String author_image_url;
    String position;

    public Post() { }

    public Post(String title, String author_image_url, Date time,String id,String FileUrl) {
        this.title = title;
        this.author_image_url = author_image_url;
        this.time = time;
        this.id=id;
        this.fileUrl=new ArrayList<>();
        this.fileUrl.add(FileUrl);
    }

    protected Post(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();

        dept = in.createStringArrayList();
        year = in.createStringArrayList();
        imageUrl = in.createStringArrayList();

        fileName = in.createStringArrayList();
        fileUrl = in.createStringArrayList();

        author = in.readString();
        author_image_url = in.readString();
        position = in.readString();
        time=new Date(in.readLong());
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

    public String getAuthor_image_url() {
        return author_image_url;
    }

    public void setAuthor_image_url(String author_image_url) {
        this.author_image_url = author_image_url;
    }

    public ArrayList<String> getDept() {
        return dept;
    }

    public void setDept(ArrayList<String> dept) {
        this.dept = dept;
    }

    public ArrayList<String> getYear() {
        return year;
    }

    public void setYear(ArrayList<String> year) {
        this.year = year;
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

        dest.writeStringList(this.dept);
        dest.writeStringList(this.year);
        dest.writeStringList(this.imageUrl);

        dest.writeStringList(this.fileName);
        dest.writeStringList(this.fileUrl);

        dest.writeString(this.author);
        dest.writeString(this.author_image_url);
        dest.writeString(this.position);
        dest.writeLong(this.time.getTime());
    }
}
