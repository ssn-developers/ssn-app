package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class NoNetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_network);

        final String key = getIntent().getStringExtra("key");

        final LottieAnimationView lottie = findViewById(R.id.lottie);
        CardView retryCV = findViewById(R.id.retryCV);

        retryCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtils.alerter(getApplicationContext())) {
                    if (key.equals("splash")) {
                        if (SharedPref.getBoolean(getApplicationContext(), "is_logged_in")) {
                            if (SharedPref.getInt(getApplicationContext(), "clearance") == 1) {
                                startActivity(new Intent(getApplicationContext(), FacultyHomeActivity.class));
                                finish();
                                Bungee.fade(NoNetworkActivity.this);
                            } else {
                                startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
                                finish();
                                Bungee.fade(NoNetworkActivity.this);
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
                            Bungee.slideLeft(NoNetworkActivity.this);
                        }
                    }
                    else
                        onBackPressed();
                }
                else
                    lottie.playAnimation();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!CommonUtils.alerter(getApplicationContext()))
            super.onBackPressed();
        else{
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(startMain);
            finish();
        }
    }
}
