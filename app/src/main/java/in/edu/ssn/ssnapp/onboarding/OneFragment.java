package in.edu.ssn.ssnapp.onboarding;

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

public class OneFragment extends Fragment {
    public OneFragment() { }

    static ImageView towerIV, landIV, treesIV;
    static ImageView tempIV;
    static boolean firstRun=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        initUI(view);
        startAnimation();
        return view;
    }

    private void initUI(View view){
        towerIV = view.findViewById(R.id.towerIV);
        landIV = view.findViewById(R.id.landIV);
        treesIV = view.findViewById(R.id.treesIV);
        tempIV = view.findViewById(R.id.tempIV);

        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/clock_tower.png")).into(towerIV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/trees.png")).into(treesIV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/land.png")).into(landIV);
    }

    public static void startAnimation(){
        firstRun=true;
        landIV.animate().translationY(tempIV.getBaseline()).scaleX(1).scaleY(1).setDuration(600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                towerIV.animate()
                        .translationY(tempIV.getBaseline())
                        .scaleX(1)
                        .scaleY(1)
                        .alpha(1)
                        .setDuration(800)
                        .setInterpolator(new AccelerateDecelerateInterpolator());

                treesIV.animate()
                        .translationY(tempIV.getBaseline())
                        .scaleY(1)
                        .scaleX(1)
                        .alpha(1)
                        .setDuration(400)
                        .setInterpolator(new DecelerateInterpolator());
            }
        },400);

    }

    public static void clearAnimation(){
        landIV.animate().translationY(landIV.getWidth()/2).scaleX(0).scaleY(0).setDuration(1);
        towerIV.animate().translationY(1000).alpha(0).scaleX((float) 0.5).scaleY((float) 0.5).setDuration(1);
        treesIV.animate().translationY(tempIV.getWidth()/2).alpha(0).scaleY(0).scaleX(0).setDuration(1);
        OnboardingActivity.firstRun1=false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser) {
            if(firstRun)
                clearAnimation();
        }
    }
}
