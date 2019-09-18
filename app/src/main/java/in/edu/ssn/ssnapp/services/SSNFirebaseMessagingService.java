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

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.ClubPostDetailsActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.SplashActivity;
import in.edu.ssn.ssnapp.StudentHomeActivity;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.database.Notification;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;


public class SSNFirebaseMessagingService extends FirebaseMessagingService {

    final String TAG="SSNFireBaseMessaging";
    String postId,pdfUrl;
    String vac,vca,acv;
    int type;
    String collectionName;
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

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);

        // handling data+notification messages
        if (remoteMessage.getData().size() > 0) {
            if(remoteMessage.getData().containsKey("PostType")) {
                try {
                    type = Integer.parseInt(remoteMessage.getData().get("PostType"));
                    collectionName = CommonUtils.getCollectionName(type);
                }
                catch (Exception e){
                    e.printStackTrace();
                    type=1;
                    collectionName = "post";
                }
            }
            if(remoteMessage.getData().containsKey("PostId"))
                postId=remoteMessage.getData().get("PostId");
            if(remoteMessage.getData().containsKey("PostUrl"))
                pdfUrl=remoteMessage.getData().get("PostUrl");

            if(remoteMessage.getData().containsKey("vac"))
                vac=remoteMessage.getData().get("vac");
            if(remoteMessage.getData().containsKey("vca"))
                vca=remoteMessage.getData().get("vca");
            if(remoteMessage.getData().containsKey("acv"))
                acv=remoteMessage.getData().get("acv");

            if(pdfUrl!=null && !pdfUrl.equals("")){
                Intent intent = new Intent(this, PdfViewerActivity.class);
                intent.putExtra(Constants.PDF_URL, pdfUrl);
                FCMHelper.showNotification(remoteMessage.getNotification().getBody(),this,intent);
                dataBaseHelper.addNotification(new Notification("7",postId,pdfUrl,new Post(remoteMessage.getNotification().getTitle(),"",new Date(),"7",pdfUrl)));
            }
            else if (collectionName.equals("post_club")) {
                // http://ssnportal.cf/share.html?vca =     K1gFiFwA3A2Y2O30PJUA & type=4  & acv=5d &  vac=43
                // http://ssnportal.cf/share.html?club_id = K1gFiFwA3A2Y2O30PJUA & type=4  & time=5d & post_id=43

                //vca ==> id [club_id]
                //acv ==> id [time]
                //vac ==> id [post_id]
                HashMap<String, Object> hmp = new HashMap<>();
                hmp.put("time", acv);
                hmp.put("post_id", vac);
                FetchPostById(vca, collectionName, hmp, type, this);
            }
            else{
                FetchPostById(postId, collectionName, new HashMap<String, Object>(),type, this);
            }
        }
        // handling notification message
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Intent intent=new Intent(this, SplashActivity.class);
            FCMHelper.showNotification(remoteMessage.getNotification().getBody(),this,intent);
        }
    }

    public void FetchPostById(final String postId, final String collectionName, final HashMap<String,Object> data, final int type, final Context context){
        String collection = collectionName;
        if(collectionName.equals("post_club"))
            collection = "club";

        FirebaseFirestore.getInstance().collection(collection).document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(collectionName.equals("club") || collectionName.equals("post_club")){
                    final Club club = new Club();
                    club.setId(snapshot.getString("id"));
                    club.setName(snapshot.getString("name"));
                    club.setDp_url(snapshot.getString("dp_url"));
                    club.setCover_url(snapshot.getString("cover_url"));
                    club.setContact(snapshot.getString("contact"));
                    club.setDescription(snapshot.getString("description"));
                    try {
                        club.setFollowers((ArrayList<String>) snapshot.get("followers"));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        club.setFollowers(null);
                    }
                    try {
                        club.setHead((ArrayList<String>) snapshot.get("head"));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        club.setHead(null);
                    }

                    if(collectionName.equals("post_club")){
                        try{
                            String time=data.get("time").toString();
                            String post_id=data.get("post_id").toString();

                            Intent intent=new Intent(context, ClubPostDetailsActivity.class);
                            intent.putExtra("data", post_id);
                            intent.putExtra("time", time);
                            intent.putExtra("club", club);
                            FCMHelper.showNotification(post.getDescription(),context,intent);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        Intent intent = new Intent(context, ClubPageActivity.class);
                        intent.putExtra("data",club);
                        FCMHelper.showNotification(post.getDescription(),context,intent);
                    }
                }
                else{
                    Post post = new Post();
                    post.setId(postId);
                    post.setTitle(snapshot.getString("title"));
                    post.setDescription(snapshot.getString("description"));
                    DocumentSnapshot.ServerTimestampBehavior behavior = ESTIMATE;
                    post.setTime(snapshot.getDate("time", behavior));

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

                    try {
                        String email = snapshot.getString("author");
                        post.setAuthor_image_url(email);

                        String name = SharedPref.getString(getApplicationContext(), "faculty_name", email);
                        if (name != null && !name.equals(""))
                            post.setAuthor(name);
                        else
                            post.setAuthor(email.split("@")[0]);

                        String position = SharedPref.getString(getApplicationContext(), "faculty_position", email);
                        if (position != null && !position.equals(""))
                            post.setPosition(position);
                        else
                            post.setPosition("Faculty");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        post.setAuthor_image_url("");
                        post.setAuthor("");
                        post.setPosition("Faculty");
                    }

                    DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(getApplicationContext());
                    dataBaseHelper.addNotification(new Notification("1",postId,"",post));

                    Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.putExtra("post",post);
                    intent.putExtra("time", CommonUtils.getTime(post.getTime()));
                    intent.putExtra("type",type);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failed to fetch the post");
            }
        });
    }
}
