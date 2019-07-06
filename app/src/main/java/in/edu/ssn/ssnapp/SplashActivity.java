package in.edu.ssn.ssnapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;
    Intent intent;
    private final static String TAG="SplashActivity";

    ImageView splashIV;
    private int currentApiVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();

        db = FirebaseFirestore.getInstance();
        intent=getIntent();
        //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
        splashIV = findViewById(R.id.splashIV);
        Glide.with(getApplicationContext()).asGif().load("file:///android_asset/splashscreen/splash_loading.gif").listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {

                return false;
            }
        }).into(splashIV);
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

                        passIntent();
                    }
                    else
                        passIntent();
                }
            });

            return null;
        }
    }

    public void passIntent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SharedPref.getBoolean(getApplicationContext(),"is_logged_in")){
                    handleIntent(intent);
                }
                else
                    startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
            }
        },3000);

    }



    void handleIntent(Intent intent){
        // handling the payload from the push notification
        String postType=" ",postId="",pdfUrl="";

        Bundle bundle=intent.getExtras();

        if(bundle!=null){

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
                FetchPostById(this,postId);
            }
            else if(postType.equals("2")){
                intent=new Intent(this,PdfViewerActivity.class);
                intent.putExtra(Constants.PDF_URL,pdfUrl);
                startActivity(intent);
            }

        }
        else {
            Log.d(TAG,"no extras to handle");
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }



    public static void FetchPostById(final Context context, String postId){

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



}
