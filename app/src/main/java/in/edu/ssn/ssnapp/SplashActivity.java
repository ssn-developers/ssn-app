package in.edu.ssn.ssnapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sahurjt.objectcsv.CsvDelimiter;
import com.sahurjt.objectcsv.CsvHolder;
import com.sahurjt.objectcsv.ObjectCsv;


import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.database.Notification;
import in.edu.ssn.ssnapp.models.Faculty;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import io.fabric.sdk.android.Fabric;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import spencerstudios.com.bungeelib.Bungee;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Intent intent, notif_intent;
    private final static String TAG = "test_set";
    private GifDrawable gifFromResource;
    private GifImageView gifImageView;

    private static Boolean flag = false;
    private static Boolean worst_case = true;
    private static Boolean isUpdateAvailable = false;
    private int currentApiVersion;
    private String latestVersion;
    private Map<String,Object> nodes = new HashMap<>();

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();
        //checkIsBlocked();
        //forceUpdate();

        try {
            gifFromResource = (GifDrawable) new GifDrawable(getResources(), R.drawable.splash_screen);
            gifImageView = (GifImageView) findViewById(R.id.splashIV);
            gifImageView.setImageDrawable(gifFromResource);

            gifFromResource.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifFromResource.stop();
                    if (flag && !isUpdateAvailable) {
                         passIntent();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        Long current = calendar.getTimeInMillis();
        Long prev = SharedPref.getLong(getApplicationContext(),"dont_delete","db_update");
        if(current - prev > 604800000) {
            new updateFaculty().execute();
            SharedPref.putLong(getApplicationContext(), "dont_delete", "db_update", current);
        }
        else if(!isUpdateAvailable){
            handleIntent();
        }
    }

    void initUI() {
        currentApiVersion = Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("block_screen");
        intent = getIntent();
        notif_intent = null;
    }

    /**********************************************************************/
    // check version on play store and force update

    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
            String currentVersion = packageInfo.versionName;
            new ForceUpdateAsync(currentVersion,SplashActivity.this).execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkIsBlocked(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nodes = (Map<String, Object>) dataSnapshot.getValue();
                CommonUtils.setIs_blocked((Boolean) nodes.get("is_block"));
                if(CommonUtils.getIs_blocked()){
                    startActivity(new Intent(getApplicationContext(), BlockScreenActivity.class));
                    Bungee.fade(SplashActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, JSONObject> {
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context) {
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SplashActivity.this.getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    isUpdateAvailable = true;
                    showForceUpdateDialog();
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    public void showForceUpdateDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog2, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView tv_ok = dialogView.findViewById(R.id.tv_ok);
        TextView tv_title= dialogView.findViewById(R.id.tv_title);
        TextView tv_message = dialogView.findViewById(R.id.tv_message);

        tv_title.setText("New Update available!");
        tv_message.setText("Please update your app to the latest version: " + latestVersion);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                alertDialog.dismiss();
            }
        });
    }

    public class updateFaculty extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Glide.with(SplashActivity.this).asFile().load("https://ssn-app-web.web.app/scripts/data_faculty.csv").into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"SSN-App");
                    if(!dir.exists())
                        dir.mkdir();

                    File file = new File(dir,"data_faculty.csv");
                    try {
                        FileInputStream inStream = new FileInputStream(resource);
                        FileOutputStream outStream = new FileOutputStream(file);
                        FileChannel inChannel = inStream.getChannel();
                        FileChannel outChannel = outStream.getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                        inStream.close();
                        outStream.close();

                        if(file.exists()) {
                            try {
                                CsvHolder<Faculty> holder = ObjectCsv.getInstance().from(file.getPath()).with(CsvDelimiter.COMMA).getCsvHolderforClass(Faculty.class);
                                List<Faculty> models = holder.getCsvRecords();

                                for (Faculty m : models) {
                                    String email = m.getEmail();
                                    SharedPref.putString(getApplicationContext(), "faculty_access", email, m.getAccess());
                                    SharedPref.putString(getApplicationContext(), "faculty_dept", email, m.getDept());
                                    SharedPref.putString(getApplicationContext(), "faculty_name", email, m.getName());
                                    SharedPref.putString(getApplicationContext(), "faculty_position", email, m.getPosition());
                                }

                                file.delete();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(!isUpdateAvailable)
                        handleIntent();
                }
            });
            return null;
        }
    }

    /**********************************************************************/

    void handleIntent() {
        String postType = "";
        String postId = "";
        String pdfUrl = "";
        Bundle bundle = intent.getExtras();

        if(Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction()) && SharedPref.getInt(this,"dont_delete","is_logged_in")==2){
            Uri uri=intent.getData();
            postId=uri.getQueryParameter("id");
            flag = false;
            worst_case = false;
            FetchPostById(postId);
            return;
        }
        if (bundle != null && SharedPref.getInt(this,"dont_delete","is_logged_in")==2) {
            if (bundle.containsKey("PostType")) {
                postType = intent.getStringExtra("PostType");
            }
            if (bundle.containsKey("PostId")) {
                postId = intent.getStringExtra("PostId");
            }
            if (bundle.containsKey("PostUrl")) {
                pdfUrl = intent.getStringExtra("PostUrl");
            }

            if (postType.equals("1")) {
                flag = false;
                worst_case = false;
                FetchPostById(postId);
            }
            else if (postType.equals("2")) {
                flag = false;
                worst_case = false;
                notif_intent = new Intent(getApplicationContext(), PdfViewerActivity.class);
                notif_intent.putExtra(Constants.PDF_URL, pdfUrl);
                DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(this);
                dataBaseHelper.addNotification(new Notification("2",postId,pdfUrl,new Post("Bus Post","",new Date(),"2",pdfUrl)));
            }
        }
        if (gifFromResource.isAnimationCompleted())
            passIntent();
        else
            flag = true;
    }

    public void passIntent() {

        if(!CommonUtils.getIs_blocked()) {
            worst_case = false;
            if (notif_intent != null) {
                startActivity(notif_intent);
                finish();
            }
            else if (!CommonUtils.alerter(getApplicationContext())) {
                if (SharedPref.getInt(getApplicationContext(), "dont_delete", "is_logged_in") == 2) {
                    if (SharedPref.getInt(getApplicationContext(), "clearance") == 1) {
                        startActivity(new Intent(getApplicationContext(), FacultyHomeActivity.class));
                        finish();
                        Bungee.fade(this);
                    } else {
                        startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                        finish();
                        Bungee.fade(this);
                    }
                } else if (SharedPref.getInt(getApplicationContext(), "dont_delete", "is_logged_in") == 1) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("is_log_in", true);
                    startActivity(intent);
                    finish();
                    Bungee.slideLeft(this);
                } else {
                    startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                    finish();
                    Bungee.slideLeft(this);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                intent.putExtra("key", "splash");
                startActivity(intent);
                finish();
                Bungee.fade(SplashActivity.this);
            }
        }
    }

    void FetchPostById(final String postId){
        FirebaseFirestore.getInstance().collection("post").document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Post post = new Post();
                post.setId(postId);
                post.setTitle(snapshot.getString("title"));
                post.setDescription(snapshot.getString("description"));
                post.setTime(snapshot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if(images != null && images.size() > 0)
                    post.setImageUrl(images);
                else
                    post.setImageUrl(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            fileName.add(files.get(i).get("name"));
                            fileUrl.add(files.get(i).get("url"));
                        }
                        post.setFileName(fileName);
                        post.setFileUrl(fileUrl);
                    }
                    else {
                        post.setFileName(null);
                        post.setFileUrl(null);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    post.setFileName(null);
                    post.setFileUrl(null);
                }

                try {
                    ArrayList<String> dept = (ArrayList<String>) snapshot.get("dept");
                    if (dept != null && dept.size() != 0)
                        post.setDept(dept);
                    else
                        post.setDept(null);
                }
                catch (Exception e){
                    e.printStackTrace();
                    post.setDept(null);
                }

                try {
                    ArrayList<String> years = new ArrayList<>();
                    Map<Object, Boolean> year = (HashMap<Object, Boolean>) snapshot.get("year");
                    for (Map.Entry<Object, Boolean> entry : year.entrySet()) {
                        if (entry.getValue().booleanValue())
                            years.add((String) entry.getKey());
                    }
                    Collections.sort(years);
                    post.setYear(years);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                String email = snapshot.getString("author");

                post.setAuthor_image_url(email);

                String name = SharedPref.getString(getApplicationContext(),"faculty_name",email);
                if(name!=null && !name.equals(""))
                    post.setAuthor(name);
                else
                    post.setAuthor(email.split("@")[0]);

                String position = SharedPref.getString(getApplicationContext(),"faculty_position",email);
                if(position!=null && !position.equals(""))
                    post.setPosition(position);
                else
                    post.setPosition("Faculty");

                DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(getApplicationContext());
                dataBaseHelper.addNotification(new in.edu.ssn.ssnapp.database.Notification("1",postId,"",post));

                Intent intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                intent.putExtra("post",post);
                intent.putExtra("time", FCMHelper.getTime(post.getTime()));
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failed to fetch the post");
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Worst-case scenario (it will intent to any other activity) after 5s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(worst_case & !isUpdateAvailable)
                    passIntent();
            }
        }, 5000);
    }
}