package in.edu.ssn.ssnapp.onboarding;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


import in.edu.ssn.ssnapp.R;

public class ThreeFragment extends Fragment {


    public ThreeFragment() {
        // Required empty public constructor
    }
    static ImageView hand1IV, hand2IV, hand3IV, landIV;
    static ImageView tempIV;
    static boolean firstRun=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        initUI(view);

        //startAnimation();
        return view;
    }

    private void initUI(View view){
        hand1IV = view.findViewById(R.id.hand1IV);
        hand2IV = view.findViewById(R.id.hand2IV);
        hand3IV = view.findViewById(R.id.hand3IV);
        landIV = view.findViewById(R.id.landIV);
        tempIV = view.findViewById(R.id.tempIV);

        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/hand1.png")).into(hand1IV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/hand2.png")).into(hand2IV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/hand3.png")).into(hand3IV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/land2.png")).into(landIV);
    }

    public static void startAnimation(){
        firstRun=true;
        landIV.animate().translationY(tempIV.getBaseline()).scaleY(1).scaleX(1).setDuration(600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hand1IV.animate()
                        .translationY(tempIV.getBaseline())
                        .scaleX(1)
                        .scaleY(1)
                        .setDuration(800)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                hand2IV.animate()
                        .translationY(tempIV.getBaseline())
                        .scaleX(1)
                        .scaleY(1)
                        .setDuration(800)
                        .setStartDelay(150)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                hand3IV.animate()
                        .translationY(tempIV.getBaseline())
                        .scaleX(1)
                        .scaleY(1)
                        .setDuration(800)
                        .setStartDelay(250)
                        .setInterpolator(new AccelerateDecelerateInterpolator());

            }
        },200);

    }

    public static void clearAnimation(){
        landIV.animate().translationY(landIV.getWidth()/2).scaleX(0).scaleY(0).setDuration(1);
        hand1IV.animate().translationY(1000).scaleX(0).scaleY(0).setDuration(1);
        hand2IV.animate().translationY(1000).scaleX(0).scaleY(0).setDuration(1);
        hand3IV.animate().translationY(1000).scaleX(0).scaleY(0).setDuration(1);
        OnboardingActivity.firstRun3=false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            /*if(firstRun){
                startAnimation();
            }*/
        }else {
            if(firstRun)
                clearAnimation();
        }
    }

}
