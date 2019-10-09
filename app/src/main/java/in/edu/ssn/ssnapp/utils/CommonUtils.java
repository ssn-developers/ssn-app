package in.edu.ssn.ssnapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.io.BaseEncoding;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.models.Post;

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

public class CommonUtils {

    private static Boolean is_blocked = false;
    public static Typeface regular, bold, semi_bold;
    public static void initFonts(Context context, View view){
        regular = ResourcesCompat.getFont(context, R.font.open_sans);
        bold = ResourcesCompat.getFont(context, R.font.open_sans_bold);
        semi_bold = ResourcesCompat.getFont(context, R.font.open_sans_semi_bold);

        FontChanger fontChanger = new FontChanger(bold);
    }

    /************************************************************************/

    //Hides Keyboard
    public static void hideKeyboard(Activity activity){
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (view == null) {
                view = new View(activity);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Checks for Internet Connectivity
    public static boolean alerter(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
        }
        return false;
    }

    /************************************************************************/
    // checks if wifi is connected to a particular network

    public static boolean checkWifiOnAndConnected(Context context,String ssid) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if( wifiInfo.getNetworkId() == -1 )
                return false;   // Not connected to an access point
            else{
                // connected to the required wifi network
                String temp=findSSIDForWifiInfo(wifiMgr,wifiInfo);

                if(temp.toLowerCase().equalsIgnoreCase("\""+ssid+"\""))
                    return true;
                else
                    return false;
            }
        }
        else
            return false; // Wi-Fi adapter is OFF
    }

    private static String findSSIDForWifiInfo(WifiManager manager, WifiInfo wifiInfo) {
        List<WifiConfiguration> listOfConfigurations = manager.getConfiguredNetworks();
        for (int index = 0; index < listOfConfigurations.size(); index++) {
            WifiConfiguration configuration = listOfConfigurations.get(index);
            if (configuration.networkId == wifiInfo.getNetworkId()) {
                return configuration.SSID;
            }
        }

        return null;
    }

    /************************************************************************/

    public static String getYear(String val){
        if(val.equals("4"))
            return Constants.fourth;
        else if(val.equals("3"))
            return Constants.third;
        else if(val.equals("2"))
            return Constants.second;
        else
            return Constants.first;
    }

    public static String getNameFromEmail(String email){
        String name=" ";

        email = email.substring(0, email.indexOf("@"));
        for (int j = 0; j < email.length(); j++) {
            if (Character.isDigit(email.charAt(j))) {
                name = email.substring(0, j);
                break;
            }
        }
        if (name.isEmpty())
            name = email;

        name = name.substring(0,1).toUpperCase() + name.substring(1);

        return name;
    }

    public static String getTime(Date time){
        Date now = new Date();
        Long t = now.getTime() - time.getTime();

        String diff_time;
        if(t < 60000)
            return Long.toString(t / 1000) + "s ago";
        else if(t < 3600000)
            return Long.toString(t / 60000) + "m ago";
        else if(t < 86400000)
            return Long.toString(t / 3600000) + "h ago";
        else if(t < 604800000)
            return Long.toString(t/86400000) + "d ago";
        else if(t < 2592000000L)
            return Long.toString(t/604800000) + "w ago";
        else if(t < 31536000000L)
            return Long.toString(t/2592000000L) + "M ago";
        else
            return Long.toString(t/31536000000L) + "y ago";
    }

    public static String getCollectionName(int type){
        switch (type){
            case 2 : return "placement";
            case 3 : return "club";
            case 4 : return "post_club";
            case 5 : return "exam_cell";
            case 6 : return "workshop";
            case 7 : return "post_bus";
            default : return "post";
        }
    }

    /************************************************************************/
    //Return Object

    public static Post getPostFromSnapshot(Context context, DocumentSnapshot snapshot){
        Post post = new Post();
        post.setId(snapshot.getString("id"));
        post.setTitle(snapshot.getString("title"));
        post.setDescription(snapshot.getString("description"));
        DocumentSnapshot.ServerTimestampBehavior behavior = ESTIMATE;
        post.setTime(snapshot.getDate("time", behavior));

        ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
        if(images != null && images.size() > 0)
            post.setImageUrl(images);
        else
            post.setImageUrl(new ArrayList<String>());

        try {
            ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
            if (files != null && files.size() != 0) {
                ArrayList<String> fileName = new ArrayList<>();
                ArrayList<String> fileUrl = new ArrayList<>();

                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).get("name");
                    Log.i("app_test : ", name);
                    if(name.length() > 13)
                        name = name.substring(0,name.length()-13);
                    fileName.add(name);
                    fileUrl.add(files.get(i).get("url"));
                }
                post.setFileName(fileName);
                post.setFileUrl(fileUrl);
            }
            else {
                post.setFileName(new ArrayList<String>());
                post.setFileUrl(new ArrayList<String>());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            post.setFileName(new ArrayList<String>());
            post.setFileUrl(new ArrayList<String>());
        }

        try {
            ArrayList<String> dept = (ArrayList<String>) snapshot.get("dept");
            if (dept != null && dept.size() != 0)
                post.setDept(dept);
            else
                post.setDept(new ArrayList<String>());
        }
        catch (Exception e){
            e.printStackTrace();
            post.setDept(new ArrayList<String>());
        }

        try {
            ArrayList<String> years = new ArrayList<>();
            Map<String, Boolean> year = (HashMap<String, Boolean>) snapshot.get("year");
            TreeMap<String, Boolean> sorted_year = new TreeMap<>(year);
            for (Map.Entry<String, Boolean> entry : sorted_year.entrySet()) {
                if (entry.getValue().booleanValue()) {

                    //Change it yearly once using force_update
                    switch (entry.getKey()){
                        case Constants.fourth:
                            years.add("IV");
                            break;
                        case Constants.third:
                            years.add("III");
                            break;
                        case Constants.second:
                            years.add("II");
                            break;
                        case Constants.first:
                            years.add("I");
                            break;
                    }
                }
            }
            if(years.size() > 1)
                Collections.reverse(years);
            post.setYear(years);
        }
        catch (Exception e){
            e.printStackTrace();
            post.setYear(new ArrayList<String>());
        }

        try {
            String email = snapshot.getString("author");
            post.setAuthor_image_url(email);

            String name = SharedPref.getString(context, "faculty_name", email);
            if (name != null && !name.equals(""))
                post.setAuthor(name);
            else if(email!=null)
                post.setAuthor(email.split("@")[0]);
            else
                post.setAuthor("");

            String position = SharedPref.getString(context, "faculty_position", email);
            if (position != null && !position.equals(""))
                post.setPosition(position);
            else
                post.setPosition("Faculty");
        }
        catch (Exception e){
            e.printStackTrace();
            post.setAuthor_image_url("");
            post.setAuthor("");
            post.setPosition("Faculty");
        }
        return post;
    }

    public static Club getClubFromSnapshot(Context context, DocumentSnapshot snapshot){
        Club club = new Club();
        club.setId(snapshot.getString("id"));
        club.setName(snapshot.getString("name"));
        club.setDp_url(snapshot.getString("dp_url"));
        club.setCover_url(snapshot.getString("cover_url"));
        club.setContact(snapshot.getString("contact"));
        club.setDescription(snapshot.getString("description"));
        try {
            club.setFollowers((ArrayList<String>) snapshot.get("followers"));
        }
        catch (Exception e){
            e.printStackTrace();
            club.setFollowers(null);
        }
        try {
            club.setHead((ArrayList<String>) snapshot.get("head"));
        }
        catch (Exception e){
            e.printStackTrace();
            club.setHead(null);
        }
        return club;
    }

    public static ClubPost getClubPostFromSnapshot(Context context, DocumentSnapshot documentSnapshot){
        ClubPost post = new ClubPost();
        post.setId(documentSnapshot.getString("id"));
        post.setCid(documentSnapshot.getString("cid"));
        post.setAuthor(documentSnapshot.getString("author"));
        post.setTitle(documentSnapshot.getString("title"));
        post.setDescription(documentSnapshot.getString("description"));
        post.setTime(documentSnapshot.getTimestamp("time").toDate());

        ArrayList<String> images = (ArrayList<String>) documentSnapshot.get("img_urls");
        if(images != null && images.size() > 0)
            post.setImg_urls(images);
        else
            post.setImg_urls(new ArrayList<String>());

        try {
            ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) documentSnapshot.get("file_urls");
            if (files != null && files.size() != 0) {
                ArrayList<String> fileName = new ArrayList<>();
                ArrayList<String> fileUrl = new ArrayList<>();

                for (int i = 0; i < files.size(); i++) {
                    String name = files.get(i).get("name");
                    if(name.length() > 13)
                        name = name.substring(0,name.length()-13);
                    fileName.add(name);
                    fileUrl.add(files.get(i).get("url"));
                }
                post.setFileName(fileName);
                post.setFileUrl(fileUrl);
            }
            else {
                post.setFileName(new ArrayList<String>());
                post.setFileUrl(new ArrayList<String>());
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            post.setFileName(new ArrayList<String>());
            post.setFileUrl(new ArrayList<String>());
        }

        try {
            ArrayList<String> like = (ArrayList<String>) documentSnapshot.get("like");
            post.setLike(like);

            if (like != null && like.size() != 0)
                post.setLike(like);
            else
                post.setLike(new ArrayList<String>());
        }
        catch (Exception ex){
            ex.printStackTrace();
            post.setLike(new ArrayList<String>());
        }

        try {
            ArrayList<Comments> comments = (ArrayList<Comments>) documentSnapshot.get("comment");
            if (comments != null && comments.size() > 0)
                post.setComment(comments);
            else
                post.setComment(new ArrayList<Comments>());
        }
        catch (Exception ex){
            ex.printStackTrace();
            post.setComment(new ArrayList<Comments>());
        }

        return post;
    }

    /************************************************************************/

    public static void handleBottomSheet(View v, final Post post, final int type, final Context context) {
        RelativeLayout ll_save,ll_share;
        final TextView tv_save;

        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        View sheetView= LayoutInflater.from(context).inflate(R.layout.bottom_menu, null);
        bottomSheetDialog.setContentView(sheetView);

        ll_save=sheetView.findViewById(R.id.saveLL);
        ll_share=sheetView.findViewById(R.id.shareLL);
        tv_save=sheetView.findViewById(R.id.tv_save);

        final DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(context);
        if(dataBaseHelper.checkPost(post.getId()))
            tv_save.setText("Remove from Favourites");
        else
            tv_save.setText("Add to Favourites");

        bottomSheetDialog.show();

        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataBaseHelper.checkPost(post.getId())){
                    dataBaseHelper.deletePost(post.getId());
                    tv_save.setText("Add to Favourites");
                }
                else{
                    tv_save.setText("Remove from Favourites");
                    dataBaseHelper.addPost(post,Integer.toString(type));
                }
                bottomSheetDialog.hide();
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + post.getAuthor().trim() + ". Check it out: https://ssn-app-web.web.app/share.html?type=" + type + "&vca=" + post.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /************************************************************************/

    public static Boolean getIs_blocked() {
        return is_blocked;
    }

    public static void setIs_blocked(Boolean is_blocked) {
        CommonUtils.is_blocked = is_blocked;
    }
}