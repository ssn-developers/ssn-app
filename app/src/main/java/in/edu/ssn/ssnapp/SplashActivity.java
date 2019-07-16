package in.edu.ssn.ssnapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import spencerstudios.com.bungeelib.Bungee;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Intent intent, notif_intent;
    private final static String TAG="test_set";
    private GifDrawable gifFromResource;
    private GifImageView gifImageView;

    private static Boolean flag = false;
    private static Boolean worst_case = true;
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();

        try {
            gifFromResource = (GifDrawable) new GifDrawable(getResources(), R.drawable.splash_screen);
            gifImageView = (GifImageView) findViewById(R.id.splashIV);
            gifImageView.setImageDrawable(gifFromResource);

            gifFromResource.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    gifFromResource.stop();
                    if(flag)
                        passIntent();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        new updateFaculty().execute();
    }

    void initUI(){
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
        intent=getIntent();
        notif_intent = null;
    }

    public class updateFaculty extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db.collection("user").whereEqualTo("clearance",1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                            SharedPref.putString(getApplicationContext(),"faculty", id + "_access", access);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_dept", dept);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_dp_url", dp_url);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_email", email);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_name", name);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_position", position);
                        }
                    }
                    handleIntent();
                }
            });

            return null;
        }
    }

    void handleIntent(){
        String postType="";
        String postId="";
        String pdfUrl="";
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            if(bundle.containsKey("PostType")){
                postType=intent.getStringExtra("PostType");
            }
            if(bundle.containsKey("PostId")){
                postId=intent.getStringExtra("PostId");
            }
            if(bundle.containsKey("PdfUrl")){
                pdfUrl=intent.getStringExtra("PdfUrl");
            }
            Log.d(TAG,"post details: "+postId+" "+postType);

            if(postType.equals("1")){
                final Post post = new Post();
                /*post.setTitle();
                post.setDescription();
                post.setTime();
                post.setImageUrl();

                String id = getString("author");
                post.setAuthor(SharedPref.getString(getApplicationContext(),"faculty",id + "_name"));
                post.setAuthor_image_url(SharedPref.getString(getApplicationContext(),"faculty",id + "_dp_url"));
                post.setPosition(SharedPref.getString(getApplicationContext(),"faculty",id + "_position"));*/

                notif_intent = new Intent(getApplicationContext(), PostDetailsActivity.class);
                notif_intent.putExtra("post", post);
                notif_intent.putExtra("time", FCMHelper.getTime(post.getTime()));
            }
            else if(postType.equals("2")){
                notif_intent = new Intent(this, PdfViewerActivity.class);
                notif_intent.putExtra(Constants.PDF_URL, pdfUrl);
            }
        }
        if(gifFromResource.isAnimationCompleted())
            passIntent();
        else
            flag=true;
    }

    public void passIntent(){
        worst_case = false;
        if(notif_intent != null)
            startActivity(notif_intent);
        else if(SharedPref.getBoolean(getApplicationContext(),"is_logged_in")) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            Bungee.fade(this);
        }
        else {
            startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
            Bungee.slideLeft(this);
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
                if(worst_case)
                    passIntent();
            }
        },5000);
    }

    /*public static void FetchPostById(final Context context, String postId){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("post").document(postId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot postSnapShot) {
                Log.d(TAG,"post fetched successfully");

                final Post post = new Post();
                Log.d(TAG,postSnapShot.getString("title"));
                post.setTitle(postSnapShot.getString("title"));
                post.setDescription(postSnapShot.getString("description"));
                post.setTime(postSnapShot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) postSnapShot.get("img_urls");
                if(images != null){
                    post.setImageUrl(images);
                }

                String id = postSnapShot.getString("author");

                post.setAuthor(SharedPref.getString(context,"faculty",id + "_name"));
                post.setAuthor_image_url(SharedPref.getString(context,"faculty",id + "_dp_url"));
                post.setPosition(SharedPref.getString(context,"faculty",id + "_position"));

                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("post",post);
                intent.putExtra("time",FCMHelper.getTime(post.getTime()));
                context.startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"failed to fetch the post");

            }
        });
    }*/

    /*final Set<String> linkedHashSet = new LinkedHashSet<>();
        FirebaseFirestore.getInstance().collection("bus_route").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot ds:queryDocumentSnapshots){
                    ArrayList<String> stop = (ArrayList<String>) ds.get("stop");
                    for(String s:stop){
                        linkedHashSet.add(s);
                    }
                }

                for(String s: linkedHashSet){
                    Log.d("test_set",s);
                }
            }
        });*/
}