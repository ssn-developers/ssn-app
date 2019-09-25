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
import in.edu.ssn.ssnapp.models.Club;
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

import static com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;

public class SplashActivity extends AppCompatActivity {

    Intent intent, notif_intent;
    private final static String TAG = "test_set";
    private GifDrawable gifFromResource;

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

        CommonUtils.isDebug();

        initUI();
        checkIsBlocked();
        forceUpdate();
        setUpCrashReport();

        try {
            gifFromResource = (GifDrawable) new GifDrawable(getResources(), R.drawable.splash_screen);
            GifImageView gifImageView = (GifImageView) findViewById(R.id.splashIV);
            gifImageView.setImageDrawable(gifFromResource);

            gifFromResource.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifFromResource.stop();
                    if (!isUpdateAvailable) {
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

    void setUpCrashReport()
    {
        // only enable bug tracking in release version
        if (!BuildConfig.DEBUG) {
            //https://stackoverflow.com/a/49836972/10664312
            Fabric.with(this, new Crashlytics());
        }
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
                isUpdateAvailable = false;
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

    /**********************************************************************/

    public class updateFaculty extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if(CommonUtils.alerter(getApplicationContext())) {
                Glide.with(SplashActivity.this).asFile().load("https://ssnportal.cf/scripts/data_faculty.csv").into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "SSN-App");
                        if (!dir.exists())
                            dir.mkdir();

                        File file = new File(dir, "data_faculty.csv");
                        try {
                            FileInputStream inStream = new FileInputStream(resource);
                            FileOutputStream outStream = new FileOutputStream(file);
                            FileChannel inChannel = inStream.getChannel();
                            FileChannel outChannel = outStream.getChannel();
                            inChannel.transferTo(0, inChannel.size(), outChannel);
                            inStream.close();
                            outStream.close();

                            if (file.exists()) {
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!isUpdateAvailable)
                            handleIntent();
                    }
                });
            }
            else {
                Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                intent.putExtra("key","splash");
                startActivity(intent);
                finish();
                Bungee.fade(SplashActivity.this);
            }
            return null;
        }
    }

    public void FetchPostById(final String postId, final String collectionName, final String post_id, final int type){
        String collection = collectionName;
        if(collectionName.equals(Constants.collection_post_club))
            collection = Constants.collection_club;

        if(!CommonUtils.alerter(getApplicationContext())) {
            FirebaseFirestore.getInstance().collection(collection).document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (collectionName.equals(Constants.collection_club) || collectionName.equals(Constants.collection_post_club)) {
                        Club club = CommonUtils.getClubFromSnapshot(getApplicationContext(), snapshot);

                        if (collectionName.equals(Constants.collection_post_club)) {
                            try {
                                notif_intent = new Intent(SplashActivity.this, ClubPostDetailsActivity.class);
                                notif_intent.putExtra("data", post_id);
                                notif_intent.putExtra("club", club);
                                worst_case = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            notif_intent = new Intent(SplashActivity.this, ClubPageActivity.class);
                            notif_intent.putExtra("data", club);
                            worst_case = false;
                        }
                    } else {
                        Post post = CommonUtils.getPostFromSnapshot(getApplicationContext(), snapshot);
                        if (type == 2) {
                            post.setAuthor_image_url("placement@ssn.edu.in");
                            post.setAuthor("SSN Career Development Centre");
                            post.setPosition("Placement team");
                        } else if (type == 5) {
                            post.setAuthor_image_url("examcell@ssn.edu.in");
                            post.setAuthor("SSNCE COE");
                            post.setPosition("Exam cell team");
                        }

                        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(getApplicationContext());
                        //dataBaseHelper.addNotification(new Notification("1",postId,"",post));

                        notif_intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                        notif_intent.putExtra("post", post);
                        notif_intent.putExtra("type", type);
                        worst_case = false;
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "failed to fetch the post");
                }
            });
        }
        else{
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key","splash");
            startActivity(intent);
            finish();
            Bungee.fade(SplashActivity.this);
        }
    }

    /**********************************************************************/

    void handleIntent() {
        int type=1;
        String pdfUrl = "";
        String vca="",vac="";

        String collectionName="";

        Bundle bundle = intent.getExtras();
        Uri uri = null;

        if((Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction()) || bundle != null) && SharedPref.getInt(this,"dont_delete","is_logged_in")==2){
            if(Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction())) {
                uri = intent.getData();
                try {
                    type = Integer.parseInt(uri.getQueryParameter("type"));
                    collectionName = CommonUtils.getCollectionName(type);
                } catch (Exception e) {
                    e.printStackTrace();
                    type = 1;
                    collectionName = Constants.collection_post;
                }

                try {
                    vca = uri.getQueryParameter("vca");
                    vac = uri.getQueryParameter("vac");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if(bundle != null){
                try {
                    if (bundle.containsKey("PostType")) {
                        type = Integer.parseInt(intent.getStringExtra("PostType"));
                        collectionName = CommonUtils.getCollectionName(type);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    type = 1;
                    collectionName = Constants.collection_post;
                }

                if (bundle.containsKey("vca"))
                    vca = intent.getStringExtra("vca");
                if (bundle.containsKey("vac"))
                    vac = intent.getStringExtra("vac");
                if (bundle.containsKey("PostUrl"))
                    pdfUrl = intent.getStringExtra("PostUrl");
            }
            else
                return;

            if(pdfUrl!=null && !pdfUrl.equals("")){
                if(!CommonUtils.alerter(getApplicationContext())) {
                    notif_intent = new Intent(getApplicationContext(), PdfViewerActivity.class);
                    notif_intent.putExtra(Constants.PDF_URL, pdfUrl);
                    worst_case = false;
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key","splash");
                    startActivity(intent);
                    finish();
                    Bungee.fade(SplashActivity.this);
                }
            }
            else if (collectionName.equals(Constants.collection_post_club)) {
                // http://ssnportal.cf/share.html?vca =     K1gFiFwA3A2Y2O30PJUA & type=4  & acv=5d &  vac=43
                // http://ssnportal.cf/share.html?club_id = K1gFiFwA3A2Y2O30PJUA & type=4  & time=5d & post_id=43

                //vca ==> id [club_id]
                //vac ==> id [post_id]
                FetchPostById(vca, collectionName, vac, type);
            }
            else if(!collectionName.equals("")){
                FetchPostById(vca, collectionName, vac, type);
            }
        }
    }

    public void passIntent() {
        if(!CommonUtils.getIs_blocked()) {
            worst_case = false;
            if (notif_intent != null) {
                startActivity(notif_intent);
                finish();
                Bungee.slideLeft(SplashActivity.this);
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
            }
            else {
                Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                intent.putExtra("key", "splash");
                startActivity(intent);
                finish();
                Bungee.fade(SplashActivity.this);
            }
        }
    }

    /**********************************************************************/

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