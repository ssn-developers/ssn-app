package in.edu.ssn.ssnapp.onboarding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.HomeActivity;
import in.edu.ssn.ssnapp.LoginActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageView backgroundIV,backgroundIV1,backgroundIV2;
    CardView signInCV;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 111;

    public static boolean firstRun1=false;
    public static boolean firstRun2=false;
    public static boolean firstRun3=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initUI();

        initGoogleSignIn();

        signInCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Get started clicked",Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int positionOffsetPixels) {
                if(v<0.70 && i==0 && !firstRun1){
                    OneFragment.startAnimation();
                    firstRun1=true;
                }
                if(v>0.30 && i==0 && !firstRun2){
                    TwoFragment.startAnimation();
                    firstRun2=true;
                }
                if(v<0.70 && i==1 && !firstRun2){
                    TwoFragment.startAnimation();
                    firstRun2=true;
                }
                if(v>0.40 && i==1 && !firstRun3){
                    ThreeFragment.startAnimation();
                    firstRun3=true;
                }

                switch (i){
                    case 0:{
                        backgroundIV.setRotation(v * 180.0f);
                        backgroundIV.setAlpha(1-v);
                        backgroundIV.setScaleX(1-v);
                        backgroundIV.setScaleY(1-v);

                        backgroundIV1.setRotation(v * 360f);
                        backgroundIV1.setAlpha(v);
                        backgroundIV1.setScaleX(v);
                        backgroundIV1.setScaleY(v);

                        break;
                    }
                    case 1:{
                        backgroundIV1.setRotation(v * 180.0f);
                        backgroundIV1.setAlpha(1-v);
                        backgroundIV1.setScaleX(1-v);
                        backgroundIV1.setScaleY(1-v);

                        backgroundIV2.setRotation(v * 360f);
                        backgroundIV2.setAlpha(v);
                        backgroundIV2.setScaleX(v);
                        backgroundIV2.setScaleY(v);

                        break;
                    }
                    case 2:{
                        backgroundIV2.setRotation(v * 180.0f);
                        backgroundIV2.setAlpha(1-v);
                        backgroundIV2.setScaleX(1-v);
                        backgroundIV2.setScaleY(1-v);

                        break;
                    }
                }


            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:{
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor1));
                        dotsIndicator.animate().scaleY(1).scaleX(1).setDuration(500);
                        signInCV.animate().scaleX(0).scaleY(0).setDuration(500);
                        signInCV.setEnabled(false);
                        break;
                    }
                    case 1:{
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor2));
                        dotsIndicator.animate().scaleY(1).scaleX(1).setDuration(500);
                        signInCV.animate().scaleX(0).scaleY(0).setDuration(500);
                        signInCV.setEnabled(false);
                        break;
                    }
                    case 2:{
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor3));
                        dotsIndicator.animate().scaleY(0).scaleX(0).setDuration(500);
                        signInCV.animate().scaleX(1).scaleY(1).setDuration(500);
                        signInCV.setEnabled(true);
                        break;
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    void initUI(){
        viewPager = findViewById(R.id.viewPager);
        signInCV = findViewById(R.id.signInCV);
        backgroundIV = findViewById(R.id.backgroundIV);
        backgroundIV1 = findViewById(R.id.backgroundIV1);
        backgroundIV2 = findViewById(R.id.backgroundIV2);
        dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor1));
        Picasso.get().load("file:///android_asset/onboarding/bg1.png").into(backgroundIV);
        Picasso.get().load("file:///android_asset/onboarding/bg2.png").into(backgroundIV1);
        Picasso.get().load("file:///android_asset/onboarding/bg3.png").into(backgroundIV2);
        startAnimation();
        setupViewPager(viewPager);
    }

    void initGoogleSignIn(){
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(OnboardingActivity.this, gso);
    }

    DotsIndicator dotsIndicator;
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "OneFragment");
        adapter.addFragment(new TwoFragment(), "TwoFragment");
        adapter.addFragment(new ThreeFragment(),"ThreeFragment");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        dotsIndicator.setViewPager(viewPager);
        dotsIndicator.setDotsClickable(false);
    }

    public void startAnimation(){
        backgroundIV.animate().alpha(1).scaleY(1).scaleX(1).setDuration(600).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount acct = task.getResult(ApiException.class);
                Pattern pat = Pattern.compile("@[a-z]{2,3}(.ssn.edu.in)$");
                Matcher m = pat.matcher(acct.getEmail());

                if (m.find()) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("test_set", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                checkForSignin(user);
                            }
                            else
                                Log.d("test_set", "signInWithCredential:failure");
                        }
                    });
                }
                else
                    Toast.makeText(this, "Please use SSN mail ID", Toast.LENGTH_SHORT).show();
            }
            catch (ApiException e) {
                Log.d("test_set", e.getMessage());
            }
        }
    }

    public void checkForSignin(final FirebaseUser user){
        String id = user.getUid();

        FirebaseFirestore.getInstance().collection("user").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty())
                        signUp(user);
                    else {
                        List<DocumentSnapshot> document = task.getResult().getDocuments();
                        signIn(user, document.get(0));
                    }
                }
            }
        });
    }

    public void signIn(FirebaseUser user, DocumentSnapshot document){
        String dept = (String) document.get("dept");
        String dp_url = (String) document.get("dp_url");
        String email = user.getEmail();
        String id = (String) document.get("id");
        String name = (String) document.get("name");
        Long year = (Long) document.get("year");

        SharedPref.putInt(getApplicationContext(),"clearance",0);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"dp_url", dp_url);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putInt(getApplicationContext(),"year", Integer.parseInt(year.toString()));
        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", true);

        Log.d("test_set", "signin");
        FCMHelper.SubscribeToTopic(this,dept);
        FCMHelper.UpdateFCM(this,SharedPref.getString(this,"FCMToken"));
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    public void signUp(FirebaseUser user){
        String email = user.getEmail();
        String id = user.getUid();

        String[] split = email.split("@");
        int y = split[1].indexOf(".");
        String dept = split[1].substring(0, y);

        String dp_url = user.getPhotoUrl().toString();
        String name = user.getDisplayName();
        int year = Integer.parseInt(split[0].substring(split[0].length() - 5, split[0].length() - 3)) + 2000;

        Map<String, Object> users = new HashMap<>();
        users.put("access", "");
        users.put("clearance", 0);
        users.put("dept", dept);
        users.put("dp_url", dp_url);
        users.put("email", email);
        users.put("id", id);
        users.put("name", name);
        users.put("year", year);
        users.put("FCMToken",SharedPref.getString(this,"FCMToken"));
        FirebaseFirestore.getInstance().collection("user").document(id).set(users);
        FCMHelper.SubscribeToTopic(this,dept);

        SharedPref.putInt(getApplicationContext(),"clearance", 0);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"dp_url", dp_url);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putInt(getApplicationContext(),"year", year);
        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", true);

        Log.d("test_set", "signup");
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }


}
