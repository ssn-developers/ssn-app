package in.edu.ssn.ssnapp.onboarding;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import in.edu.ssn.ssnapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwoFragment extends Fragment {


    static ImageView towerIV, treesIV;
    static ImageView tempIV;
    static boolean firstRun = false;
    public TwoFragment() {
        // Required empty public constructor
    }

    public static void startAnimation() {
        firstRun = true;
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

    public static void clearAnimation() {
        towerIV.animate().translationY(0).scaleX(0).scaleY(0).setDuration(1);
        treesIV.animate().translationY(0).alpha(0).scaleY(0).scaleX(0).setDuration(1);
        OnboardingActivity.firstRun2 = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        towerIV = view.findViewById(R.id.towerIV);
        treesIV = view.findViewById(R.id.treesIV);
        tempIV = view.findViewById(R.id.tempIV);

        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/bus_ob.png")).into(towerIV);
        Glide.with(this).load(Uri.parse("file:///android_asset/onboarding/trees_ob.png")).into(treesIV);
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
