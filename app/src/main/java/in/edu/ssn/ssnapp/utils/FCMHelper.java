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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import in.edu.ssn.ssnapp.R;

public class FCMHelper {

    final static String TAG = "test_set";

    public static void SubscribeToTopic(final Context context, String topic) {

        if (Constants.debug_mode && !topic.startsWith("debug_")) {
            topic = "debug_" + topic;
        }

        final String finalTopic = topic;
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = finalTopic + " subscribe success";
                        System.out.println(msg);
                        if (!task.isSuccessful()) {
                            msg = finalTopic + " subscribe failed";
                            System.out.println(msg);
                        }
                    }
                });
    }

    public static void UnSubscribeToTopic(final Context context, String topic) {

        if (Constants.debug_mode && !topic.startsWith("debug_")) {
            topic = "debug_" + topic;
        }

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

    public static void showNotification(String title, String message, Context context, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("1", "general", NotificationManager.IMPORTANCE_HIGH));
            Notification.Builder nbuilder = new Notification.Builder(context, "1")
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ssn_logo)
                    .setContentText(message)
                    .setChannelId("1")
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1, nbuilder.build());
        } else {
            Notification.Builder nbuilder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ssn_logo)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1, nbuilder.build());
        }
    }

    public static void showChatNotification(String title, String message, Context context, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("7698", Constants.GLOBAL_CHAT, NotificationManager.IMPORTANCE_HIGH));
            Notification.Builder nbuilder = new Notification.Builder(context, "7698")
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ssn_logo)
                    .setContentText(message)
                    .setChannelId("7698")
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(1, nbuilder.build());
        } else {
            Notification.Builder nbuilder = new Notification.Builder(context)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ssn_logo)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);

            notificationManager.notify(7698, nbuilder.build());
        }
    }
}
