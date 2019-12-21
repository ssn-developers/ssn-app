package in.edu.ssn.ssnapp.message_utils;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

/*
 type = 0 -> received
 type = 1 -> sent
 type = 2 -> received reply
 type = 3 -> sent reply
 */

@IgnoreExtraProperties
public class Message {

    @Exclude public boolean newMessageBlink=false;
    @Exclude public int newMessageCount=0;
    @Exclude boolean showDivider=false;
    @Exclude boolean blinkReply=false;
    @Exclude String dividerText;

    boolean messageDeleted=false;
    int type;
    String messageId;
    String senderId;
    String senderName;
    String message;
    String timestamp;
    Message replyMessage=null;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Message getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(Message replyMessage) {
        this.replyMessage = replyMessage;
    }

    @Exclude
    public boolean isShowDivider() {
        return showDivider;
    }

    @Exclude
    public void setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
    }

    @Exclude
    public String getDividerText() {
        return dividerText;
    }

    @Exclude
    public void setDividerText(String dividerText) {
        this.dividerText = dividerText;
    }

    public boolean isMessageDeleted() {
        return messageDeleted;
    }

    public void setMessageDeleted(boolean messageDeleted) {
        this.messageDeleted = messageDeleted;
    }

    @Exclude
    public boolean isNewMessageBlink() {
        return newMessageBlink;
    }

    @Exclude
    public void setNewMessageBlink(boolean newMessageBlink) {
        this.newMessageBlink = newMessageBlink;
    }

    @Exclude
    public int getNewMessageCount() {
        return newMessageCount;
    }

    @Exclude
    public void setNewMessageCount(int newMessageCount) {
        this.newMessageCount = newMessageCount;
    }

    @Exclude
    public boolean isBlinkReply() {
        return blinkReply;
    }

    @Exclude
    public void setBlinkReply(boolean blinkReply) {
        this.blinkReply = blinkReply;
    }



}
