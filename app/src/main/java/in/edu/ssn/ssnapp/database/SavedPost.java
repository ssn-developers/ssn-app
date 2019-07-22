package in.edu.ssn.ssnapp.database;

import android.provider.BaseColumns;


import java.text.SimpleDateFormat;
import java.util.Date;


public class SavedPost {

    String rowId,post,time;

    public static class SavedPostEntry implements BaseColumns {
        public static final String TABLE_NAME = "SavedPost";
        public static final String COLUMN_NAME_POST_ID = "postid";
        public static final String COLUMN_NAME_POST = "post";
        public static final String COLUMN_NAME_TIME = "time";
    }


    // src: https://stackoverflow.com/questions/8434819/android-sqlite-auto-increment/30798224#30798224
    public static final String SQL_CREATE_SAVED_POST_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SavedPostEntry.TABLE_NAME + " (" +
                    SavedPostEntry.COLUMN_NAME_POST_ID + " TEXT PRIMARY KEY," +
                    SavedPostEntry.COLUMN_NAME_POST + " TEXT,"
                    + SavedPostEntry.COLUMN_NAME_TIME+" TEXT"+")";
}
