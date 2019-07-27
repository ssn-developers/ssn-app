package in.edu.ssn.ssnapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.Date;

import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.database.Notification;
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
    private int currentApiVersion;
    private String latestVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();
        setUpCrashReport();

        //forceUpdate();

        try {
            gifFromResource = (GifDrawable) new GifDrawable(getResources(), R.drawable.splash_screen);
            gifImageView = (GifImageView) findViewById(R.id.splashIV);
            gifImageView.setImageDrawable(gifFromResource);

            gifFromResource.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifFromResource.stop();
                    if (flag)
                        passIntent();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
            Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
        }

        new updateFaculty().execute();
    }

    void setUpCrashReport()
    {
        // only enable bug tracking in release version
        if (!BuildConfig.DEBUG) {
            //https://stackoverflow.com/a/49836972/10664312
            Fabric.with(this, new Crashlytics());
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
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion,SplashActivity.this).execute();
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
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + SplashActivity.this.getPackageName()+ "&hl=en")
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
                    // Toast.makeText(context,"update is available.",Toast.LENGTH_LONG).show();
                    if (!(context instanceof SplashActivity)) {
                        if (!((Activity) context).isFinishing()) {
                            showForceUpdateDialog();
                        }
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    public void showForceUpdateDialog(){
        final Dialog dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.custom_alert_dialog2);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tv_ok = dialog.findViewById(R.id.tv_ok);
        TextView tv_title= dialog.findViewById(R.id.tv_title);
        TextView tv_message = dialog.findViewById(R.id.tv_message);

        tv_title.setText("New Update available!");
        tv_message.setText("Please update your app to the latest version: " + latestVersion);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public class updateFaculty extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db.collection("user").whereEqualTo("clearance", 1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dp_url = (String) document.get("dp_url");
                            String access = (String) document.get("access");
                            String dept = (String) document.get("dept");
                            String email = (String) document.get("email");
                            String id = (String) document.get("id");
                            String name = (String) document.get("name");
                            String position = (String) document.get("position");

                            SharedPref.putString(getApplicationContext(), "faculty", id + "_access", access);
                            SharedPref.putString(getApplicationContext(), "faculty", id + "_dept", dept);
                            SharedPref.putString(getApplicationContext(), "faculty", id + "_dp_url", dp_url);
                            SharedPref.putString(getApplicationContext(), "faculty", id + "_email", email);
                            SharedPref.putString(getApplicationContext(), "faculty", id + "_name", name);
                            SharedPref.putString(getApplicationContext(), "faculty", id + "_position", position);
                        }
                    }
                    handleIntent();
                }
            });

            return null;
        }
    }

    void handleIntent() {
        String postType = "";
        String postId = "";
        String pdfUrl = "";
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (bundle.containsKey("PostType")) {
                postType = intent.getStringExtra("PostType");
            }
            if (bundle.containsKey("PostId")) {
                postId = intent.getStringExtra("PostId");
            }
            if (bundle.containsKey("PostUrl")) {
                pdfUrl = intent.getStringExtra("PostUrl");
            }
            Log.d(TAG, "post details: " + postId + " " + postType);

            if (postType.equals("1")) {
                final Post post = new Post();
                /*post.setTitle();
                post.setDescription();
                post.setTime();
                post.setImageUrl();

                String id = getString("author");
                post.setAuthor(SharedPref.getString(getApplicationContext(),"faculty",id + "_name"));
                post.setAuthor_image_url(SharedPref.getString(getApplicationContext(),"faculty",id + "_dp_url"));
                post.setPosition(SharedPref.getString(getApplicationContext(),"faculty",id + "_position"));*/

                //notif_intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                //notif_intent.putExtra("post", post);
                //notif_intent.putExtra("time", FCMHelper.getTime(post.getTime()));
                FCMHelper.FetchPostById(getApplicationContext(),postId,2);
                notif_intent=null;
            } else if (postType.equals("2")) {
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
        worst_case = false;

        if (notif_intent != null) {
            startActivity(notif_intent);
            finish();
        }
        else if(!CommonUtils.alerter(getApplicationContext())){
            if (SharedPref.getBoolean(getApplicationContext(), "is_logged_in")) {
                if (SharedPref.getInt(getApplicationContext(), "clearance") == 1) {
                    startActivity(new Intent(getApplicationContext(), FacultyHomeActivity.class));
                    finish();
                    Bungee.fade(this);
                } else {
                    startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                    finish();
                    Bungee.fade(this);
                }
            }
            else {
                if (!SharedPref.getBoolean(getApplicationContext(), "is_logged_out")) {
                    startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), LogoutActivity.class);
                    intent.putExtra("is_log_in", true);
                    startActivity(intent);
                    finish();
                }
                Bungee.slideLeft(this);
            }
        }
        else{
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key","splash");
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("NewApi")
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
                if (worst_case)
                    passIntent();
            }
        }, 5000);
    }
}