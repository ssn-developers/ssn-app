package in.edu.ssn.ssnapp.onboarding;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import in.edu.ssn.ssnapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {


    public TwoFragment() {
        // Required empty public constructor
    }

    static ImageView towerIV, landIV, treesIV;
    static ImageView tempIV;
    static boolean firstRun=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        initUI(view);
        //startAnimation();
        return view;
    }

    private void initUI(View view){
        towerIV = view.findViewById(R.id.towerIV);
        landIV = view.findViewById(R.id.landIV);
        treesIV = view.findViewById(R.id.treesIV);
        tempIV = view.findViewById(R.id.tempIV);

        Picasso.get().load("file:///android_asset/onboarding/bus.png").into(towerIV);
        Picasso.get().load("file:///android_asset/onboarding/trees1.png").into(treesIV);
        Picasso.get().load("file:///android_asset/onboarding/land1.png").into(landIV);
    }

    public static void startAnimation(){
        firstRun=true;
        landIV.animate().translationY(tempIV.getBaseline()).scaleY(1).scaleX(1).setDuration(600);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                towerIV.animate()
                        .translationY(tempIV.getBaseline())
                        .scaleX(1)
                        .scaleY(1)
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
        towerIV.animate().translationY(250).scaleX(0).scaleY(0).setDuration(1);
        treesIV.animate().translationY(tempIV.getWidth()/2).alpha(0).scaleY(0).scaleX(0).setDuration(1);
        OnboardingActivity.firstRun2=false;
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
