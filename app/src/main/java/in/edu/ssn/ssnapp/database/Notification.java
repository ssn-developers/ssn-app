package in.edu.ssn.ssnapp.database;

import android.provider.BaseColumns;
import android.util.Log;

import in.edu.ssn.ssnapp.models.Post;

public class Notification {

    String rowId,postType,postId,postUrl;
    Post post;

    public Notification(String postType, String postId, String postUrl, Post post) {
        this.postType = postType;
        this.postId = postId;
        this.postUrl = postUrl;
        this.post = post;
    }

    public static class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "ReceivedNotification";
        public static final String COLUMN_NAME_ROW_ID = "rowId";
        public static final String COLUMN_NAME_POST_TYPE = "postType";
        public static final String COLUMN_NAME_POST = "post";
        public static final String COLUMN_NAME_POST_URL = "postUrl";
    }


    public static final String SQL_CREATE_NOTIFICATION_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + Notification.NotificationEntry.TABLE_NAME + " (" +
                    Notification.NotificationEntry.COLUMN_NAME_ROW_ID + " TEXT PRIMARY KEY," +
                    Notification.NotificationEntry.COLUMN_NAME_POST_TYPE + " TEXT,"
                    +Notification.NotificationEntry.COLUMN_NAME_POST + " TEXT,"
                    + NotificationEntry.COLUMN_NAME_POST_URL+" TEXT"+")";


}
