package in.edu.ssn.ssnapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.LayoutTransition;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hendraanggrian.appcompat.widget.SocialEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.edu.ssn.ssnapp.message_utils.ChatHelper;
import in.edu.ssn.ssnapp.message_utils.ISwipeControllerActions;
import in.edu.ssn.ssnapp.message_utils.Message;
import in.edu.ssn.ssnapp.message_utils.MessageAdapter;
import in.edu.ssn.ssnapp.message_utils.ReceivedReplyHolder;
import in.edu.ssn.ssnapp.message_utils.SentReplyHolder;
import in.edu.ssn.ssnapp.message_utils.SwipeController;
import in.edu.ssn.ssnapp.message_utils.Utils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class GroupChatActivity extends AppCompatActivity {

    //Views
    RecyclerView chatRV;
    SocialEditText messageET;
    ImageView sendIV;
    LinearLayout replyMessageLL, editMessageLL;
    TextView replyNameTV, replyMessageTV;
    ImageView closeIV;
    LinearLayout appbarContentLL;
    LinearLayout messageOptionsLL;
    ViewGroup appbarRL;
    ImageView closeOptionIV,copyIV,replyIV,deleteIV,backIV;
    TextView newMessageTV;
    ProgressBar loadingPB;
    CounterFab newMessageFAB;

    //Vars
    ChatHelper chatHelper;
    MessageAdapter adapter;
    boolean replyMode = false;
    Message replyMessage = null;
    FirebaseFirestore db;
    FirebaseUser user;
    Vibrator vibrator;
    List<Message> messageList = new ArrayList<>();
    private int lastScrollPosition=-1;
    private int prevLastScrollPos=-1;
    LinearLayoutManager layoutManager;
    int newMessageCount=0;
    int newMessagePos=-1;
    boolean firstRun=false;
    //int lastSeenMessagePosition=-1;
    private boolean fullChatRead=false;
    boolean pageLoaded=true;
    int page=0;
    Query next;
    int pageSize = 20;
    boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        darkMode = SharedPref.getBoolean(getApplicationContext(),"dark_mode");
        if(darkMode){
            setContentView(R.layout.activity_group_chat_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.appbar_color1_chat));
        }else{
            setContentView(R.layout.activity_group_chat);
        }
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        chatHelper = new ChatHelper(getApplicationContext(),db,user);
        initUI();
        initMessages();
        sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(messageET.getText().toString().trim())) {
                    chatHelper.sendMessage(messageET.getText().toString(),replyMode,replyMessage);
                    messageET.setText("");
                    closeReplyUI();
                }
            }
        });
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeReplyUI();
            }
        });
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void initMessages(){
        Query first = db.collection("global_chat").orderBy("timestamp", Query.Direction.DESCENDING).limit(pageSize);
        getMessages(first);
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
                            if (lastScrollPosition != -1) {
                                if (Math.abs(lastScrollPosition - messageList.size()) == 1) {
                                    Message newMessage = dc.getDocument().toObject(Message.class);
                                    newMessage.setMessageId(dc.getDocument().getId());
                                    if (!newMessage.getSenderId().equals(user.getUid()) && newMessage.getType() == 1) {
                                        newMessage.setType(0);
                                    } else if (!newMessage.getSenderId().equals(user.getUid()) && newMessage.getType() == 3) {
                                        newMessage.setType(2);
                                    }
                                    messageList.add(newMessage);
                                    adapter.notifyItemInserted(messageList.size() - 1);
                                    chatRV.smoothScrollToPosition(messageList.size() - 1);
                                } else {
                                    Message newMessage = dc.getDocument().toObject(Message.class);
                                    newMessage.setMessageId(dc.getDocument().getId());
                                    if (!newMessage.getSenderId().equals(user.getUid()) && newMessage.getType() == 1) {
                                        newMessage.setType(0);
                                    } else if (!newMessage.getSenderId().equals(user.getUid()) && newMessage.getType() == 3) {
                                        newMessage.setType(2);
                                    }
                                    messageList.add(newMessage);
                                    adapter.notifyItemInserted(messageList.size() - 1);
                                    if (newMessage.getSenderId().equals(user.getUid())) {
                                        chatRV.smoothScrollToPosition(messageList.size() - 1);
                                    } else {
                                        newMessageCount++;
                                        prevNewMsgCount = newMessageCount;
                                        if (newMessageCount == 1) {
                                            newMessagePos = messageList.size() - 1;
                                        }
                                        updateNewMessageUI();
                                    }
                                }
                            }
                            break;
                        }
                        case MODIFIED: {
                            // handle modified documents...
                            String id = dc.getDocument().getId();
                            Message changedMsg = dc.getDocument().toObject(Message.class);
                            changedMsg.setMessageId(id);
                            if (!changedMsg.getSenderId().equals(user.getUid()) && changedMsg.getType() == 1) {
                                changedMsg.setType(0);
                            } else if (!changedMsg.getSenderId().equals(user.getUid()) && changedMsg.getType() == 3) {
                                changedMsg.setType(2);
                            }
                            int pos = findMessageById(id);
                            if (pos != -1) {
                                messageList.set(pos, changedMsg);
                                adapter.notifyItemChanged(pos);
                            }
                            break;
                        }
                        case REMOVED: {
                            // handle removed documents...
                            String id = dc.getDocument().getId();
                            int pos = findMessageById(id);
                            if (pos != -1) {
                                messageList.remove(pos);
                                adapter.notifyItemRemoved(pos);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    int findMessageById(String id){
        int pos = -1;
        for(int i=0;i<messageList.size();i++){
            if(messageList.get(i).getMessageId().equals(id)){
                pos = i;
                break;
            }
        }
        return pos;
    }

    void addMessagesToChatRV(List<DocumentSnapshot> documentSnapshots){
        List<Message> messagesTemp = new ArrayList<>();
        for(DocumentSnapshot ds: documentSnapshots){
            Message temp = ds.toObject(Message.class);
            temp.setMessageId(ds.getId());
            if(!temp.getSenderId().equals(user.getUid())&&temp.getType()==1){
                temp.setType(0);
            }else if(!temp.getSenderId().equals(user.getUid())&&temp.getType()==3){
                temp.setType(2);
            }
            messagesTemp.add(temp);
        }
        Collections.reverse(messagesTemp);
        messageList.addAll(0,messagesTemp);
        adapter.notifyItemRangeInserted(0,messagesTemp.size());
        if(messageList.size()>messagesTemp.size()){
            messageList.get(messagesTemp.size()).setShowDivider(false);
            adapter.notifyItemChanged(messagesTemp.size());
        }

    }

    public void getMessages(Query query){
        loadingPB.setVisibility(View.VISIBLE);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                loadingPB.setVisibility(View.INVISIBLE);
                if(queryDocumentSnapshots.size()>0) {
                    DocumentSnapshot lastVisible = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                    addMessagesToChatRV(queryDocumentSnapshots.getDocuments());
                    next = db.collection("global_chat")
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .startAfter(lastVisible)
                            .limit(pageSize);
                }else{
                    fullChatRead=true;
                }
                pageLoaded=true;
            }
        });
    }


    int prevNewMsgCount=0;
    void updateNewMessageUI(){
        newMessageFAB.setCount(newMessageCount);
        newMessageFAB.show();
        newMessageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessageFAB.hide();
                newMessageFAB.setCount(0);
                try {
                    //((Message) adapter.getItem(newMessagePos)).newMessageBlink = true;
                    //((Message) adapter.getItem(newMessagePos)).newMessageCount = prevNewMsgCount;
                    messageList.get(newMessagePos).newMessageBlink = true;
                    messageList.get(newMessagePos).newMessageCount = newMessageCount;
                    adapter.notifyItemChanged(newMessagePos);
                    if(prevNewMsgCount>1){
                        chatRV.smoothScrollToPosition(newMessagePos+1);
                    }else{
                        chatRV.smoothScrollToPosition(newMessagePos);
                    }
                }catch (Exception e){
                    chatRV.smoothScrollToPosition(adapter.getItemCount()-1);
                }
                prevNewMsgCount=0;
                newMessageCount=0;
                newMessagePos=-1;
                newMessageFAB.setOnClickListener(scrollToBottomListener);
            }
        });
        if(newMessageCount==0){
            newMessageFAB.hide();
            newMessageFAB.setCount(0);
            try {
                messageList.get(newMessagePos).newMessageBlink = true;
                messageList.get(newMessagePos).newMessageCount = prevNewMsgCount;
                adapter.notifyItemChanged(newMessagePos);
            }catch (Exception e){
                chatRV.smoothScrollToPosition(adapter.getItemCount()-1);
            }
            prevNewMsgCount=0;
            newMessagePos=-1;
            newMessageFAB.setOnClickListener(scrollToBottomListener);
        }
    }



    void showMessageOptionsMenu(View v, final Message message, final int pos){
        optionsMode=true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDarkChat));

        if(user.getUid().equals(message.getSenderId())&& !message.isMessageDeleted() && Utils.canDeleteMsg(message.getTimestamp())){
            deleteIV.setVisibility(View.VISIBLE);
        }else{
            deleteIV.setVisibility(View.GONE);
        }
        if(message.isMessageDeleted()){
            copyIV.setVisibility(View.GONE);
        }else{
            copyIV.setVisibility(View.VISIBLE);
        }
        if(selectedMessageView!=null)
            selectedMessageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        selectedMessageView = v;
        selectedMessageView.setBackgroundColor(getResources().getColor(R.color.colorAccentAlpha3Chat));
        appbarContentLL.setVisibility(View.GONE);
        messageOptionsLL.setVisibility(View.VISIBLE);
        closeOptionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMessageOptionUI();
            }
        });
        copyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("SSN_Global_Chat",message.getMessage());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),"Message copied",Toast.LENGTH_SHORT).show();
                closeMessageOptionUI();
            }
        });
        replyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSwiped(message);
            }
        });
        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chatHelper.removeMessage(message.getMessageId());
                chatHelper.removeMessage(message);
                closeMessageOptionUI();
            }
        });

    }

    void messageSwiped(Message message){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        }
        closeMessageOptionUI();
        replyMessage = message;
        replyMode=true;
        updateReplyUI();
    }


    private void updateReplyUI(){

        if(replyMessage.getSenderId().equals(user.getUid()))
            replyNameTV.setText("You");
        else
            replyNameTV.setText(replyMessage.getSenderName());

        if(replyMessage.isMessageDeleted()) {
            if(user.getUid().equals(replyMessage.getSenderId())) {
                replyMessageTV.setText("You deleted this message");
            }
            else{
                replyMessageTV.setText("This message was deleted");
            }
        }else{
            replyMessageTV.setText(replyMessage.getMessage());
        }

        replyMessageLL.setVisibility(View.VISIBLE);
        messageET.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(messageET, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeReplyUI(){
        replyMode = false;
        replyMessage = null;
        replyMessageLL.setVisibility(View.GONE);
    }

    private void closeMessageOptionUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(darkMode){
                getWindow().setStatusBarColor(getResources().getColor(R.color.appbar_color1_chat));
            }else{
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.white));
            }
        }
        appbarContentLL.setVisibility(View.VISIBLE);
        messageOptionsLL.setVisibility(View.GONE);
        try {
            if (selectedMessageView != null) {
                selectedMessageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                selectedMessageView = null;
            }
        }catch (Exception e){
            selectedMessageView=null;
        }
        optionsMode=false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //SharedPref.putInt(getApplicationContext(), "lastSeenMessagePosition", lastSeenMessagePosition);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SharedPref.putInt(getApplicationContext(), "lastSeenMessagePosition", lastSeenMessagePosition);
        //adapter.stopListening();
    }

    boolean optionsMode=false;
    View selectedMessageView=null;
    @Override
    public void onBackPressed() {
        if(optionsMode){
            closeMessageOptionUI();
        }else{
            //SharedPref.putInt(getApplicationContext(),"lastSeenMessagePosition",lastSeenMessagePosition);
            super.onBackPressed();
        }
    }

    View.OnClickListener scrollToBottomListener;
    private void initUI(){
        newMessageFAB = findViewById(R.id.newMessageFAB);
        newMessageFAB.hide();
        appbarRL = findViewById(R.id.appbarRL);
        loadingPB = findViewById(R.id.loadingMessagePB);
        LayoutTransition layoutTransition1 = appbarRL.getLayoutTransition();
        layoutTransition1.enableTransitionType(LayoutTransition.CHANGING);
        messageOptionsLL = findViewById(R.id.messageOptionsLL);
        appbarContentLL = findViewById(R.id.appbarContentLL);
        backIV = findViewById(R.id.backIV);
        closeOptionIV = findViewById(R.id.closeOptionIV);
        copyIV = findViewById(R.id.copyIV);
        replyIV = findViewById(R.id.replyIV);
        deleteIV = findViewById(R.id.deleteIV);
        chatRV = findViewById(R.id.chatRV);
        messageET = findViewById(R.id.messageET);
        sendIV = findViewById(R.id.sendIV);
        replyMessageLL = findViewById(R.id.replyMessageLL);
        replyNameTV = findViewById(R.id.replyNameTV);
        replyMessageTV = findViewById(R.id.replyMessageTV);
        closeIV = findViewById(R.id.closeIV);
        newMessageTV = findViewById(R.id.newMessageTV);
        final ViewGroup layout = findViewById(R.id.messageEditLL);
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {}
            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                messageET.requestFocus();
            }
        });
        layoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        layoutManager.setStackFromEnd(true);
        chatRV.setLayoutManager(layoutManager);
        //ScaleInAnimator animator = new ScaleInAnimator();
        //animator.setAddDuration(100);
        //chatRV.setItemAnimator(animator);
        chatRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastScrollPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if(lastScrollPosition<messageList.size()-1){
                    //newMessageTV.setVisibility(View.VISIBLE);
                    newMessageFAB.show();
                }else{
                    //newMessageTV.setVisibility(View.GONE);
                    newMessageFAB.hide();
                }
                if(newMessageCount!=0 && lastScrollPosition==newMessagePos){
                    newMessageCount=0;
                    updateNewMessageUI();
                }
                int id = layoutManager.findFirstCompletelyVisibleItemPosition();
                if(id==0 && !fullChatRead && pageLoaded){
                    pageLoaded=false;
                    getMessages(next);
                }
            }
        });
        scrollToBottomListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageList.size()-1>=0){
                    newMessageFAB.hide();
                    chatRV.scrollToPosition(messageList.size()-1);
                }
            }
        };
        newMessageFAB.setOnClickListener(scrollToBottomListener);
        chatRV.setHasFixedSize(true);
        SwipeController controller = new SwipeController(getApplicationContext(), new ISwipeControllerActions() {
            @Override
            public void onSwipePerformed(int position) {
                //System.out.println("Swiped pos "+position);
                messageSwiped(messageList.get(position));
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(controller);
        itemTouchHelper.attachToRecyclerView(chatRV);
        adapter = new MessageAdapter(
                getApplicationContext(),
                messageList,
                new MessageAdapter.OnItemLongClickListener() {
                    @Override
                    public void onMessageLongClicked(View view, int position) {
                        showMessageOptionsMenu(view, messageList.get(position), position);
                    }
                },
                new MessageAdapter.OnReplyItemClickListener() {
                    @Override
                    public void onReplyMessageClicked(final View view, int position) {
                        try{
                            final int pos = findMessageById(messageList.get(position).getReplyMessage().getMessageId());
                            if(pos!=-1){
                                chatRV.scrollToPosition(pos);
                                messageList.get(pos).setBlinkReply(true);
                                adapter.notifyItemChanged(pos);
                            }else{
                                //TODO Expand message and show time on reply message
                                RecyclerView.ViewHolder holder = chatRV.findViewHolderForLayoutPosition(position);
                                switch(holder.getItemViewType()){
                                    case 2:{
                                        if(((ReceivedReplyHolder) holder).replyMessageTV.getMaxLines()==Integer.MAX_VALUE){
                                            ((ReceivedReplyHolder) holder).replyMessageTV.setMaxLines(3);
                                            ((ReceivedReplyHolder) holder).replyMessageTV.setEllipsize(TextUtils.TruncateAt.END);
                                            ((ReceivedReplyHolder) holder).replyTimeTV.setVisibility(View.GONE);
                                        }else{
                                            ((ReceivedReplyHolder) holder).replyMessageTV.setMaxLines(Integer.MAX_VALUE);
                                            ((ReceivedReplyHolder) holder).replyMessageTV.setEllipsize(null);
                                            ((ReceivedReplyHolder) holder).replyTimeTV.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    }
                                    case 3:{
                                        if(((SentReplyHolder) holder).replyMessageTV.getMaxLines()==Integer.MAX_VALUE){
                                            ((SentReplyHolder) holder).replyMessageTV.setMaxLines(3);
                                            ((SentReplyHolder) holder).replyMessageTV.setEllipsize(TextUtils.TruncateAt.END);
                                            ((SentReplyHolder) holder).replyTimeTV.setVisibility(View.GONE);
                                        }else{
                                            ((SentReplyHolder) holder).replyMessageTV.setMaxLines(Integer.MAX_VALUE);
                                            ((SentReplyHolder) holder).replyMessageTV.setEllipsize(null);
                                            ((SentReplyHolder) holder).replyTimeTV.setVisibility(View.VISIBLE);
                                        }
                                        break;
                                    }
                                }
                            }
                        }catch (Exception e){
                            System.out.println("Error occurred while navigating to reply message");
                            e.printStackTrace();
                        }

                    }
                });
        chatRV.setAdapter(adapter);
    }
}
