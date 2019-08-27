package in.edu.ssn.ssnapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Post;

public class FCMHelper {

    final static String TAG="test_set";

    public static void SubscribeToTopic(final Context context,String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribe success";
                        if (!task.isSuccessful()) {
                            msg = "subscribe failed";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    public static void UnSubscribeToTopic(final Context context, String topic){

        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "unsubscribe success";
                        if (!task.isSuccessful()) {
                            msg = "unsubscribe failed";
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    public static void showNotification(String message, Context context, Intent intent){

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("1","general",NotificationManager.IMPORTANCE_HIGH));
            Notification.Builder nbuilder=new Notification.Builder(context,"1")
                    .setContentTitle("New post")
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentText(message)
                    .setChannelId("1")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1,nbuilder.build());

        }else {
            Notification.Builder nbuilder=new Notification.Builder(context)
                    .setContentTitle("New post")
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1,nbuilder.build());
        }

    }


    public static void FetchPostById(final Context context, final String postId, final int operationId){
        FirebaseFirestore.getInstance().collection("post").document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Post post = new Post();
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
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
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
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
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
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
                }

                String email = snapshot.getString("author");

                post.setAuthor_image_url(SharedPref.getString(context,"faculty",email + "_dp_url"));

                String name = SharedPref.getString(context,"faculty",email + "_name");
                if(name!=null && !name.equals(""))
                    post.setAuthor(name);
                else
                    post.setAuthor("SSN Institutions");

                String position = SharedPref.getString(context,"faculty",email + "_position");
                if(position!=null && !position.equals(""))
                    post.setPosition(position);
                else
                    post.setPosition("Admin");

                DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(context);
                dataBaseHelper.addNotification(new in.edu.ssn.ssnapp.database.Notification("1",postId,"",post));
                RunFunction(context,operationId,post);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failed to fetch the post");
            }
        });
    }

    public static String getTime(Date time){

        Date now = new Date();
        Long t = now.getTime() - time.getTime();

        String diff_time;
        if(t < 60000)
            return Long.toString(t / 1000) + "s ago";
        else if(t < 3600000)
            return Long.toString(t / 60000) + "m ago";
        else if(t < 86400000)
            return Long.toString(t / 3600000) + "h ago";
        else if(t < 604800000)
            return Long.toString(t/86400000) + "d ago";
        else if(t < 2592000000L)
            return Long.toString(t/604800000) + "w ago";
        else if(t < 31536000000L)
            return Long.toString(t/2592000000L) + "M ago";
        else
            return Long.toString(t/31536000000L) + "y ago";
    }


    private static void RunFunction(final Context context,final int type,Post post)
    {
        Intent intent;

        switch (type)
        {
            case 1:
                intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post",post);
                intent.putExtra("time",getTime(post.getTime()));
                FCMHelper.showNotification(post.getDescription(),context,intent);
                break;
            case 2:
                intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post",post);
                intent.putExtra("time",getTime(post.getTime()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
        }
    }

}
