package in.edu.ssn.ssnapp.message_utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ChatHelper {

    Context context;
    FirebaseFirestore db;
    FirebaseUser user;
    MessageListener messageListener;

    public ChatHelper(Context context, FirebaseFirestore db, FirebaseUser user, MessageListener messageListener) {
        this.context = context;
        this.db = db;
        this.user = user;
        this.messageListener = messageListener;
    }

    public void sendMessage(String text, boolean replyMode, Message replyMessage){
        Message message = new Message();
        message.setSenderId(user.getUid());
        String senderName = user.getDisplayName();
        try {
            System.out.println("Sender name before: " + senderName);
            assert senderName != null;
            senderName = senderName.replaceAll("[0-9]", "");
            String s1 = senderName.substring(0, 1).toUpperCase();
            String nameCapitalized = s1 + senderName.substring(1);
            System.out.println("Sender name after: " + nameCapitalized);
            message.setSenderName(nameCapitalized);
        }catch (Exception e){
            e.printStackTrace();
            message.setSenderName(CommonUtils.getNameFromEmail(user.getEmail()));
        }
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
        sendPushNotification(message);
    }

    private void sendPushNotification(Message message){
        RequestQueue mRequestQue = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + Constants.GLOBAL_CHAT);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("message", message.getMessage());
            notificationObj.put("sender_name", message.getSenderName());
            notificationObj.put("PostType", 8);
            json.put("data", notificationObj);
            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("FCM Push Notif", "onResponse: "+response.toString());
                    }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("FCM Push Notif", "onError: "+error.getMessage());
                        }
                    }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> header = new HashMap<>();
                        header.put("content-type", "application/json");
                        header.put("authorization", "key=AIzaSyAc8HvX2uqX9k59Tb_Q89NlR8ERRLvEJ0c");
                        return header;
                    }
            };
            mRequestQue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void listenForMessages(){
        CollectionReference messageRef = db.collection("global_chat");
        messageRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Message event listener", "Listen failed.", e);
                    return;
                }
                for (int i=0;i<queryDocumentSnapshots.getDocumentChanges().size();i++) {
                    DocumentChange dc = queryDocumentSnapshots.getDocumentChanges().get(i);
                    switch (dc.getType()) {
                        case ADDED: {
                            // handle added documents...
                            Message newMessage = dc.getDocument().toObject(Message.class);
                            if(newMessage!=null) {
                                newMessage.setMessageId(dc.getDocument().getId());
                                if(newMessage.getSenderId()!=null){
                                    if (!newMessage.getSenderId().equals(user.getUid()) && newMessage.getType() == 1) {
                                        newMessage.setType(0);
                                    } else if (!newMessage.getSenderId().equals(user.getUid()) && newMessage.getType() == 3) {
                                        newMessage.setType(2);
                                    }
                                    messageListener.onMessageAdded(newMessage);
                                }
                            }
                            break;
                        }
                        case MODIFIED: {
                            // handle modified documents...
                            String id = dc.getDocument().getId();
                            Message changedMsg = dc.getDocument().toObject(Message.class);
                            if(changedMsg!=null) {
                                changedMsg.setMessageId(id);
                                if(changedMsg.getSenderId()!=null) {
                                    if (!changedMsg.getSenderId().equals(user.getUid()) && changedMsg.getType() == 1) {
                                        changedMsg.setType(0);
                                    } else if (!changedMsg.getSenderId().equals(user.getUid()) && changedMsg.getType() == 3) {
                                        changedMsg.setType(2);
                                    }
                                    messageListener.onMessageModified(id, changedMsg);
                                }
                            }
                            break;
                        }
                        case REMOVED: {
                            // handle removed documents...
                            String id = dc.getDocument().getId();
                            messageListener.onMessageRemoved(id);
                            break;
                        }
                    }
                }
            }
        });
    }

    public int findMessageById(String id, List<Message> messageList){
        int pos = -1;
        for(int i=0;i<messageList.size();i++){
            if(messageList.get(i).getMessageId().equals(id)){
                pos = i;
                break;
            }
        }
        return pos;
    }

}
