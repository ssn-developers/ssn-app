package in.edu.ssn.ssnapp.models;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.model.value.ReferenceValue;

import java.lang.ref.Reference;
import java.util.Date;
import java.util.List;

public class Post {
    String id;
    Date time;
    String title;
    String description;
    List<String> imageUrl;
    //private int likes;
    //private int comments;
    DocumentReference userRef;

    public Post() { }

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

    public DocumentReference getDocs() {
        return userRef;
    }

    public void setDocs(DocumentReference userRef) {
        this.userRef = userRef;
    }
}
