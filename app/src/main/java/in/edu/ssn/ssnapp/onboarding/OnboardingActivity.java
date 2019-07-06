package in.edu.ssn.ssnapp.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    ImageView backgroundIV,backgroundIV1,backgroundIV2;
    public static boolean firstRun1=false;
    public static boolean firstRun2=false;
    public static boolean firstRun3=false;
    DotsIndicator dotsIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        initUI();

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
                        break;
                    }
                    case 1:{
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor2));
                        break;
                    }
                    case 2:{
                        dotsIndicator.setSelectedPointColor(getResources().getColor(R.color.pageColor3));
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
}