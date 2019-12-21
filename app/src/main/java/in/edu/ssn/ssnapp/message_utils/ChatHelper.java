package in.edu.ssn.ssnapp.message_utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class ChatHelper {

    Context context;
    FirebaseFirestore db;
    FirebaseUser user;

    public ChatHelper(Context context, FirebaseFirestore db, FirebaseUser user) {
        this.context = context;
        this.db = db;
        this.user = user;
    }

    public void sendMessage(String text, boolean replyMode, Message replyMessage){
        Message message = new Message();
        message.setSenderId(user.getUid());
        message.setSenderName(user.getDisplayName());
        message.setMessage(text);
        message.setTimestamp(String.valueOf(new Date().getTime()));
        if(replyMode){
            message.setType(3);
            message.setReplyMessage(replyMessage);
        }else{
            message.setType(1);
            message.setReplyMessage(null);
        }
        db.collection("global_chat").add(message).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error when sending message "+e.getMessage());
            }
        });
    }

    public void removeMessage(Message message){
        //db.collection("global_chat").document(id).delete();
        String id = message.getMessageId();
        message.setMessageDeleted(true);
        db.collection("global_chat").document(id).set(message);
    }

    public void removeMessages(List<String> ids){
        for(String id:ids){
            db.collection("global_chat").document(id).delete();
        }
    }

}
