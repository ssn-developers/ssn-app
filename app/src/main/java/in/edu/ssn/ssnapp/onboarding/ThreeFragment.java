package in.edu.ssn.ssnapp.onboarding;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import in.edu.ssn.ssnapp.R;

public class ThreeFragment extends Fragment {


    static ImageView hand1IV, hand2IV;
    static ImageView tempIV;
    static boolean firstRun = false;
    public ThreeFragment() {
        // Required empty public constructor
    }

    public static void startAnimation() {
        firstRun = true;
        hand1IV.animate()
                .translationY(tempIV.getBaseline())
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setDuration(600)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        hand2IV.animate()
                .translationY(tempIV.getBaseline())
                .scaleX(1)
                .scaleY(1)
                .alpha(1)
                .setDuration(800)
                .setStartDelay(150)
                .setInterpolator(new AccelerateDecelerateInterpolator());

    }

    public static void clearAnimation() {
        hand1IV.animate().translationY(100).alpha(0).scaleX(0).scaleY(0).setDuration(1);
        hand2IV.animate().translationY(250).alpha(0).scaleX(0).scaleY(0).setDuration(1);
        OnboardingActivity.firstRun3 = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_three, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        hand1IV = view.findViewById(R.id.hand1IV);
        hand2IV = view.findViewById(R.id.hand2IV);
        tempIV = view.findViewById(R.id.tempIV);

        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/trees2.png")).into(hand1IV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/bench.png")).into(hand2IV);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            /*if(firstRun){
                startAnimation();
            }*/
        } else {
            if (firstRun)
                clearAnimation();
        }
    }

}
