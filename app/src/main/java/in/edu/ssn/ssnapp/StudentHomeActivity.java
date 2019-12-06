package in.edu.ssn.ssnapp;

import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.suke.widget.SwitchButton;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.DrawerAdapter;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.fragments.ClubFragment;
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.PlacementFragment;
import in.edu.ssn.ssnapp.fragments.StudentFeedFragment;
import in.edu.ssn.ssnapp.fragments.EventFragment;
import in.edu.ssn.ssnapp.models.Drawer;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class StudentHomeActivity extends BaseActivity implements View.OnClickListener{
    CircleImageView userImageIV;
    ViewPager viewPager;
    private static int count = 0;
    ImageView chatIV, favIV,settingsIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (darkModeEnabled) {
            setContentView(R.layout.activity_student_home_dark);
            clearLightStatusBar(this);
        }
        else
            setContentView(R.layout.activity_student_home);

        initUI();

        /******************************************************************/
        //What's new

        if(BuildConfig.VERSION_CODE > SharedPref.getInt(getApplicationContext(),"dont_delete","current_version_code")){
            SharedPref.putInt(getApplicationContext(),"dont_delete","current_version_code", BuildConfig.VERSION_CODE);
            CommonUtils.showWhatsNewDialog(this,darkModeEnabled);
        }
    }

    /*********************************************************/

    void initUI() {
        userImageIV = findViewById(R.id.userImageIV);
        viewPager = findViewById(R.id.viewPager);
        chatIV = findViewById(R.id.chatIV);         //chatIV.setOnClickListener(this);
        favIV = findViewById(R.id.favIV);           //favIV.setOnClickListener(this);
        settingsIV = findViewById(R.id.settingsIV); //settingsIV.setOnClickListener(this);

        try {
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(userImageIV);
        } catch (Exception e) {
            e.printStackTrace();
            Glide.with(this).load(SharedPref.getString(getApplicationContext(), "dp_url")).placeholder(R.drawable.ic_user_white).into(userImageIV);
        }
        setupViewPager();
    }

    void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(SharedPref.getInt(getApplicationContext(),"clearance") == 0)
            adapter.addFragment(new StudentFeedFragment(), "News feed");
        adapter.addFragment(new ClubFragment(), "Club");
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
            adapter.addFragment(new PlacementFragment(), "Placement");
        if(SharedPref.getInt(getApplicationContext(),"clearance") != 2)
            adapter.addFragment(new BusAlertsFragment(), "Bus alert");
        if(SharedPref.getInt(getApplicationContext(),"clearance") == 0)
            adapter.addFragment(new ExamCellFragment(), "Exam cell");
        adapter.addFragment(new EventFragment(), "Event");
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }

    /*********************************************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chatIV:
                startActivity(new Intent(getApplicationContext(), GroupChatActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case R.id.favIV:
                startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            case R.id.settingsIV:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
        }
    }

    /*********************************************************/

    @Override
    protected void onResume() {
        super.onResume();

        if (CommonUtils.alerter(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key", "home");
            startActivity(intent);
            Bungee.fade(StudentHomeActivity.this);
        }
    }

    /*********************************************************/

    @Override
    public void onBackPressed() {
        if (count > 0) {
            count = 0;
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(startMain);
            finish();
        } else {
            count++;
            Toast toast = Toast.makeText(getApplicationContext(), "Press back once again to exit!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
