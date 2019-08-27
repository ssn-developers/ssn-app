package in.edu.ssn.ssnapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import net.sqlcipher.Cursor;
import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.Constants;

public class DataBaseHelper extends SQLiteOpenHelper {

    final static String TAG="DataBaseHelper";
    public static DataBaseHelper instance;

    public DataBaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME,null,Constants.DATABASE_VERSION);
        SQLiteDatabase.loadLibs(context);
    }

    public static DataBaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DataBaseHelper.class) {
                instance = new DataBaseHelper(context);
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SavedPost.SQL_CREATE_SAVED_POST_ENTRIES);
        db.execSQL(Notification.SQL_CREATE_NOTIFICATION_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // add a post
    public void addPost(Post post){
        SQLiteDatabase db=this.getWritableDatabase(Constants.DATABASE_PWD);
        ContentValues cv=new ContentValues();
        cv.put(SavedPost.SavedPostEntry.COLUMN_NAME_POST_ID, post.getId());
        String json=new Gson().toJson(post);
        cv.put(SavedPost.SavedPostEntry.COLUMN_NAME_POST,json);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

        String date = sdf.format(new Date());
        cv.put(SavedPost.SavedPostEntry.COLUMN_NAME_TIME,date);

        long id=db.insert(SavedPost.SavedPostEntry.TABLE_NAME,null,cv);

        Log.d(TAG,"insertion id "+id);
    }


    // check if a post is saved in DB
    public boolean checkPost(String postId){
        SQLiteDatabase db=this.getReadableDatabase(Constants.DATABASE_PWD);
        Cursor cursor=db.query(SavedPost.SavedPostEntry.TABLE_NAME, new String[]{SavedPost.SavedPostEntry.COLUMN_NAME_POST},SavedPost.SavedPostEntry.COLUMN_NAME_POST_ID+"= ?",new String[]{postId},null,null,null,null);
        return cursor.moveToFirst();
    }

    // delete a post
    public void deletePost(String postID){
        SQLiteDatabase db=this.getWritableDatabase(Constants.DATABASE_PWD);
        int rowsDeleted=db.delete(SavedPost.SavedPostEntry.TABLE_NAME,SavedPost.SavedPostEntry.COLUMN_NAME_POST_ID+"= ?",new String[]{postID});
        Log.d(TAG,"deleted rows cnt "+rowsDeleted);
    }

    // list the post
    public ArrayList<Post> getSavedPostList(){

        SQLiteDatabase db=this.getReadableDatabase(Constants.DATABASE_PWD);
        ArrayList<Post> PostList=new ArrayList<>();

        long rowCount =DatabaseUtils.queryNumEntries(db,SavedPost.SavedPostEntry.TABLE_NAME);
        Log.d(TAG,"no of rows "+rowCount);
        String query = "select * from " + SavedPost.SavedPostEntry.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        Log.d(TAG,"column count: "+c.getColumnCount());
        Log.d(TAG,c.getColumnName(0)+" "+c.getColumnName(1)+" "+c.getColumnName(2));

        int i=0;
        if (c.moveToFirst())
        {
            do {
                String json=c.getString(1);
                Post post= new Gson().fromJson(json, Post.class);
                Log.d(TAG,i +" "+post.getAuthor_image_url());
                PostList.add(post);
                i++;
            }while (c.moveToNext());
        }

        return PostList;
    }



    // Add notification
    public void addNotification(Notification notification){

        SQLiteDatabase db=this.getWritableDatabase(Constants.DATABASE_PWD);

        ContentValues cv=new ContentValues();
        cv.put(Notification.NotificationEntry.COLUMN_NAME_ROW_ID,System.currentTimeMillis());
        cv.put(Notification.NotificationEntry.COLUMN_NAME_POST_TYPE,notification.postType);

        if(notification.postType.equals("1"))
        {
            String json=new Gson().toJson(notification.post);
            cv.put(Notification.NotificationEntry.COLUMN_NAME_POST,json);
        }else{
            String json=new Gson().toJson(notification.post);
            cv.put(Notification.NotificationEntry.COLUMN_NAME_POST,json);
            cv.put(Notification.NotificationEntry.COLUMN_NAME_POST_URL,notification.postUrl);
        }

        long id=db.insert(Notification.NotificationEntry.TABLE_NAME,null,cv);
        //Log.d(TAG,"insertion id "+id);
    }

    public ArrayList<Post> getNotificationList() {
        ArrayList<Post> postList=new ArrayList<Post>();
        SQLiteDatabase db=this.getReadableDatabase(Constants.DATABASE_PWD);
        long rowCount =DatabaseUtils.queryNumEntries(db,SavedPost.SavedPostEntry.TABLE_NAME);
        //Log.d(TAG,"no of rows "+rowCount);
        String query = "select * from " + Notification.NotificationEntry.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        //Log.d(TAG,"column count: "+c.getColumnCount());
        //Log.d(TAG,c.getColumnName(0)+" "+c.getColumnName(1)+" "+c.getColumnName(2));

        int i=0;
        if (c.moveToFirst())
        {
            do {
                String json=c.getString(2);
                Post post= new Gson().fromJson(json, Post.class);
                postList.add(post);
                Log.d(TAG,i  + " " + post.getAuthor_image_url());
                i++;
            }while (c.moveToNext());
        }

        return postList;
    }





}
