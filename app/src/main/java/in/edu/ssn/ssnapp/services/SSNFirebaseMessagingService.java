package in.edu.ssn.ssnapp.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.ClubPostDetailsActivity;
import in.edu.ssn.ssnapp.GroupChatActivity;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.SplashActivity;
import in.edu.ssn.ssnapp.message_utils.NewMessageEvent;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;


public class SSNFirebaseMessagingService extends FirebaseMessagingService {

    final String TAG = "SSNFireBaseMessaging";
    String pdfUrl;
    String vac, vca;
    int type;
    String collectionName;
    Post post;
    RemoteMessage rmMessage;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "new token generated is " + s);
        SharedPref.putString(getApplicationContext(), "dont_delete", "FCMToken", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        this.rmMessage = remoteMessage;

        // handling data+notification messages
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey("PostType")) {
                try {
                    type = Integer.parseInt(remoteMessage.getData().get("PostType"));
                    collectionName = CommonUtils.getCollectionName(type);
                } catch (Exception e) {
                    e.printStackTrace();
                    type = 1;
                    collectionName = Constants.collection_post;
                }
            }

            if (remoteMessage.getData().containsKey("PostUrl"))
                pdfUrl = remoteMessage.getData().get("PostUrl");

            if (remoteMessage.getData().containsKey("vac"))
                vac = remoteMessage.getData().get("vac");
            if (remoteMessage.getData().containsKey("vca"))
                vca = remoteMessage.getData().get("vca");


            if (pdfUrl != null && !pdfUrl.equals("")) {
                Intent intent = new Intent(this, PdfViewerActivity.class);
                intent.putExtra(Constants.PDF_URL, pdfUrl);
                FCMHelper.showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), this, intent);
            } else if (collectionName.equals(Constants.collection_post_club)) {
                // http://ssnportal.cf/share.html?vca =     K1gFiFwA3A2Y2O30PJUA & type=4  & acv=5d &  vac=43
                // http://ssnportal.cf/share.html?club_id = K1gFiFwA3A2Y2O30PJUA & type=4  & time=5d & post_id=43

                //vca ==> id [club_id]
                //acv ==> id [time]
                //vac ==> id [post_id]
                HashMap<String, Object> hmp = new HashMap<>();
                hmp.put("post_id", vac);
                FetchPostById(vca, collectionName, hmp, type, this);
            } else if (collectionName.equals(Constants.COLLECTION_GLOBAL_CHAT)) {
                try {
                    int new_message_count = SharedPref.getInt(getApplicationContext(), "new_message_count");
                    new_message_count += 1;
                    SharedPref.putInt(getApplicationContext(), "new_message_count", new_message_count);
                    Intent intent = new Intent(this, GroupChatActivity.class);
                    String title = "Global Chat";
                    String msg = "";
                    if (new_message_count == 1) {
                        msg = "1 new message";
                    } else {
                        msg = new_message_count + " new messages";
                    }
                    if (SharedPref.getBoolean(getApplicationContext(), "isStudHomeActive")) {
                        //update message count in student home activity and show notification
                        EventBus.getDefault().post(new NewMessageEvent(new_message_count));
                        FCMHelper.showChatNotification(title, msg, this, intent);
                    } else if (!SharedPref.getBoolean(getApplicationContext(), "isChatActive")) {
                        FCMHelper.showChatNotification(title, msg, this, intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                FetchPostById(vca, collectionName, new HashMap<String, Object>(), type, this);
            }
        }
        // handling notification message
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Intent intent = new Intent(this, SplashActivity.class);
            FCMHelper.showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), this, intent);
        }
    }

    public void FetchPostById(final String postId, final String collectionName, final HashMap<String, Object> data, final int type, final Context context) {
        String collection = collectionName;
        if (collectionName.equals(Constants.collection_post_club))
            collection = Constants.collection_club;

        FirebaseFirestore.getInstance().collection(collection).document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (collectionName.equals(Constants.collection_club) || collectionName.equals(Constants.collection_post_club)) {
                    final Club club = CommonUtils.getClubFromSnapshot(getApplicationContext(), snapshot);
                    if (collectionName.equals(Constants.collection_post_club)) {
                        try {
                            String post_id = data.get("post_id").toString();
                            Intent intent = new Intent(context, ClubPostDetailsActivity.class);
                            intent.putExtra("data", post_id);
                            intent.putExtra("club", club);
                            FCMHelper.showNotification(rmMessage.getNotification().getTitle(), rmMessage.getNotification().getBody(), context, intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(context, ClubPageActivity.class);
                        intent.putExtra("data", club);
                        FCMHelper.showNotification(rmMessage.getNotification().getTitle(), rmMessage.getNotification().getBody(), context, intent);
                    }
                } else {
                    Post post = CommonUtils.getPostFromSnapshot(getApplicationContext(), snapshot);
                    post.setId(postId);
                    if (type == Constants.placement) {
                        post.setAuthor_image_url("placement@ssn.edu.in");
                        post.setAuthor("SSN Career Development Centre");
                        post.setPosition("Placement Coordinator");
                    } else if (type == Constants.exam_cell) {
                        post.setAuthor_image_url("examcell@ssn.edu.in");
                        post.setAuthor("SSNCE COE");
                        post.setPosition("Exam cell Coordinator");
                    } else if (type == Constants.event) {
                        post.setAuthor_image_url("eventmanagement@ssn.edu.in");
                        post.setAuthor("SSN Event Management");
                        post.setPosition("Event Coordinator");
                    }

                    Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                    intent.putExtra("post", post);
                    intent.putExtra("type", type);
                    FCMHelper.showNotification(rmMessage.getNotification().getTitle(), rmMessage.getNotification().getBody(), context, intent);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "failed to fetch the post");
            }
        });
    }
}
