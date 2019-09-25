package in.edu.ssn.ssnapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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

        if(Constants.debug_mode)
            topic = "debug_" + topic;

        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribe success";
                        if (!task.isSuccessful()) {
                            msg = "subscribe failed";
                        }
                        //Log.d(TAG, msg);
                    }
                });
    }

    public static void UnSubscribeToTopic(final Context context, String topic){

        if(Constants.debug_mode)
            topic="debug_"+topic;

        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "unsubscribe success";
                        if (!task.isSuccessful()) {
                            msg = "unsubscribe failed";
                        }
                        //Log.d(TAG, msg);
                    }
                });
    }

    public static void showNotification(String message, Context context, Intent intent){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("1","general",NotificationManager.IMPORTANCE_HIGH));
            Notification.Builder nbuilder=new Notification.Builder(context,"1")
                    .setContentTitle("New post")
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentText(message)
                    .setChannelId("1")
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1,nbuilder.build());
        }
        else {
            Notification.Builder nbuilder=new Notification.Builder(context)
                    .setContentTitle("New post")
                    .setSmallIcon(R.mipmap.app_icon)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1,nbuilder.build());
        }
    }
}
