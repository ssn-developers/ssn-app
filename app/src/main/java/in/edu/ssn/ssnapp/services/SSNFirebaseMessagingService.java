package in.edu.ssn.ssnapp.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.StudentHomeActivity;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.database.Notification;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;


public class SSNFirebaseMessagingService extends FirebaseMessagingService {

    final String TAG="SSNFireBaseMessaging";
    String postType,postId,pdfUrl;
    Post post;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG,"new token generated is "+s);
        SharedPref.putString(getApplicationContext(),"dont_delete","FCMToken",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(this);

        // handling data+notification messages
        if (remoteMessage.getData().size() > 0) {

            if(remoteMessage.getData().containsKey("PostType"))
                postType=remoteMessage.getData().get("PostType");
            if(remoteMessage.getData().containsKey("PostId"))
                postId=remoteMessage.getData().get("PostId");
            if(remoteMessage.getData().containsKey("PostUrl"))
                pdfUrl=remoteMessage.getData().get("PostUrl");

            if(postType.equalsIgnoreCase("1")) {
                FetchPostById(this,postId);
            }
            else{
                Intent intent=new Intent(this, PdfViewerActivity.class);
                intent.putExtra(Constants.PDF_URL,pdfUrl);
                FCMHelper.showNotification(remoteMessage.getNotification().getBody(),this,intent);
                dataBaseHelper.addNotification(new Notification("2",postId,pdfUrl,new Post(remoteMessage.getNotification().getTitle(),"",new Date(),"2",pdfUrl)));
            }

        }

        // handling notification message
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Intent intent=new Intent(this, StudentHomeActivity.class);
            FCMHelper.showNotification("test 1 "+remoteMessage.getNotification().getBody(),this,intent);
        }
    }

    void FetchPostById(final Context context, final String postId){
        FirebaseFirestore.getInstance().collection("post").document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Post post = new Post();
                post.setId(postId);
                post.setTitle(snapshot.getString("title"));
                post.setDescription(snapshot.getString("description"));
                post.setTime(snapshot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if(images != null && images.size() > 0)
                    post.setImageUrl(images);
                else
                    post.setImageUrl(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            fileName.add(files.get(i).get("name"));
                            fileUrl.add(files.get(i).get("url"));
                        }
                        post.setFileName(fileName);
                        post.setFileUrl(fileUrl);
                    }
                    else {
                        post.setFileName(null);
                        post.setFileUrl(null);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    post.setFileName(null);
                    post.setFileUrl(null);
                }

                try {
                    ArrayList<String> dept = (ArrayList<String>) snapshot.get("dept");
                    if (dept != null && dept.size() != 0)
                        post.setDept(dept);
                    else
                        post.setDept(null);
                }
                catch (Exception e){
                    e.printStackTrace();
                    post.setDept(null);
                }

                try {
                    ArrayList<String> years = new ArrayList<>();
                    Map<Object, Boolean> year = (HashMap<Object, Boolean>) snapshot.get("year");
                    for (Map.Entry<Object, Boolean> entry : year.entrySet()) {
                        if (entry.getValue().booleanValue())
                            years.add((String) entry.getKey());
                    }
                    Collections.sort(years);
                    post.setYear(years);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                String email = snapshot.getString("author");

                post.setAuthor_image_url(email);

                String name = SharedPref.getString(getApplicationContext(),"faculty_name",email);
                if(name!=null && !name.equals(""))
                    post.setAuthor(name);
                else
                    post.setAuthor(email.split("@")[0]);

                String position = SharedPref.getString(getApplicationContext(),"faculty_position",email);
                if(position!=null && !position.equals(""))
                    post.setPosition(position);
                else
                    post.setPosition("Faculty");

                DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(getApplicationContext());
                dataBaseHelper.addNotification(new in.edu.ssn.ssnapp.database.Notification("1",postId,"",post));

                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post",post);
                intent.putExtra("time",FCMHelper.getTime(post.getTime()));
                FCMHelper.showNotification(post.getDescription(),context,intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failed to fetch the post");
            }
        });
    }
}
