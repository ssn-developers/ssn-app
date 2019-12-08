package in.edu.ssn.ssnapp.onboarding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.LoginActivity;
import in.edu.ssn.ssnapp.StudentHomeActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.FacultyHomeActivity;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageView backgroundIV, backgroundIV1, backgroundIV2;

    CardView signInCV;

    public static boolean firstRun1 = false;
    public static boolean firstRun2 = false;
    public static boolean firstRun3 = false;
    DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initUI();

        signInCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in",1);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                Bungee.slideLeft(OnboardingActivity.this);
                finish();
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int positionOffsetPixels) {
                if (v < 0.70 && i == 0 && !firstRun1) {
                    OneFragment.startAnimation();
                    firstRun1 = true;
                }
                if (v > 0.30 && i == 0 && !firstRun2) {
                    TwoFragment.startAnimation();
                    firstRun2 = true;
                }
                if (v < 0.70 && i == 1 && !firstRun2) {
                    TwoFragment.startAnimation();
                    firstRun2 = true;
                }
                if (v > 0.40 && i == 1 && !firstRun3) {
                    ThreeFragment.startAnimation();
                    firstRun3 = true;
                }

                switch (i) {
                    case 0: {
                        backgroundIV.setRotation(v * 180.0f);
                        backgroundIV.setAlpha(1 - v);
                        backgroundIV.setScaleX(1 - v);
                        backgroundIV.setScaleY(1 - v);

                        backgroundIV1.setRotation(v * 360f);
                        backgroundIV1.setAlpha(v);
                        backgroundIV1.setScaleX(v);
                        backgroundIV1.setScaleY(v);

                        break;
                    }
                    case 1: {
                        backgroundIV1.setRotation(v * 180.0f);
                        backgroundIV1.setAlpha(1 - v);
                        backgroundIV1.setScaleX(1 - v);
                        backgroundIV1.setScaleY(1 - v);

                        backgroundIV2.setRotation(v * 360f);
                        backgroundIV2.setAlpha(v);
                        backgroundIV2.setScaleX(v);
                        backgroundIV2.setScaleY(v);

                        break;
                    }
                    case 2: {
                        backgroundIV2.setRotation(v * 180.0f);
                        backgroundIV2.setAlpha(1 - v);
                        backgroundIV2.setScaleX(1 - v);
                        backgroundIV2.setScaleY(1 - v);

                        break;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: {
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor1));
                        dotsIndicator.animate().scaleY(1).scaleX(1).setDuration(500);
                        signInCV.animate().scaleX(0).scaleY(0).setDuration(500);
                        signInCV.setEnabled(false);
                        break;
                    }
                    case 1: {
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor2));
                        dotsIndicator.animate().scaleY(1).scaleX(1).setDuration(500);
                        signInCV.animate().scaleX(0).scaleY(0).setDuration(500);
                        signInCV.setEnabled(false);
                        break;
                    }
                    case 2: {
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

    /************************************************************************/
    //Boarding Animations

    void initUI() {
        viewPager = findViewById(R.id.viewPager);
        signInCV = findViewById(R.id.signInCV);

        backgroundIV = findViewById(R.id.backgroundIV);
        backgroundIV1 = findViewById(R.id.backgroundIV1);
        backgroundIV2 = findViewById(R.id.backgroundIV2);
        dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor1));

        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/bg1.png")).into(backgroundIV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/bg2.png")).into(backgroundIV1);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/bg3.png")).into(backgroundIV2);

        startAnimation();
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "OneFragment");
        adapter.addFragment(new TwoFragment(), "TwoFragment");
        adapter.addFragment(new ThreeFragment(), "ThreeFragment");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        dotsIndicator.setViewPager(viewPager);
        dotsIndicator.setDotsClickable(false);
    }

    public void startAnimation() {
        backgroundIV.animate().alpha(1).scaleY(1).scaleX(1).setDuration(600).start();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(startMain);
        finish();
    }
}