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
import java.util.Date;

import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Post;

public class FCMHelper {

    final static String TAG="FCMHelper";

    public static String getToken(final Context context){

        String token="null";

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }

                String token = task.getResult().getToken();
                Log.d(TAG, token);
                SharedPref.putString(context,"FCMToken",token);
                //UpdateFCM(context,token);
                //SubscribeToTopic(context,SharedPref.getString(context,"dept"));
            }
        });

        return token;
    }

    public static void UpdateFCM(final Context context,final String fcmToken){

        String id=SharedPref.getString(context,"id");
        FirebaseFirestore.getInstance().collection("user").document(id).update("FCMToken",fcmToken)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Added the FCM token successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to add the FCM token ");
                    }
                });
    }

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
                        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
                    .setContentTitle("hello")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText(message)
                    .setChannelId("1")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1,nbuilder.build());

        }else {
            Notification.Builder nbuilder=new Notification.Builder(context)
                    .setContentTitle("hello")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1,nbuilder.build());
        }

    }


    public static void FetchPostById(final Context context,String postId){

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("post").document(postId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot postSnapShot) {
                Log.d(TAG,"post fetched successfully");
                final Post post = new Post();
                Log.d(TAG,postSnapShot.getString("title"));
                post.setTitle(postSnapShot.getString("title"));
                post.setDescription(postSnapShot.getString("description"));
                post.setTime(postSnapShot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) postSnapShot.get("img_urls");
                if(images != null){
                    post.setImageUrl(images);
                }

                String id = postSnapShot.getString("author");

                post.setAuthor(SharedPref.getString(context,"faculty",id + "_name"));
                post.setAuthor_image_url(SharedPref.getString(context,"faculty",id + "_dp_url"));
                post.setPosition(SharedPref.getString(context,"faculty",id + "_position"));


                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post",post);
                intent.putExtra("time",getTime(post.getTime()));
                FCMHelper.showNotification(post.getDescription(),context,intent);
                Log.d(TAG,"success fetching the post of type 1");

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


}
