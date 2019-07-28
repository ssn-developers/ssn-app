package in.edu.ssn.ssnapp.services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

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
        SharedPref.putString(getApplicationContext(),"FCMToken","FCMToken",s);
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
                FCMHelper.FetchPostById(this,postId,1);
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
}
