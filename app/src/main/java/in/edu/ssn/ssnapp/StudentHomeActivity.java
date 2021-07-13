package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.fragments.ClubFragment;
import in.edu.ssn.ssnapp.fragments.EventFragment;
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.PlacementFragment;
import in.edu.ssn.ssnapp.fragments.StudentFeedFragment;
import in.edu.ssn.ssnapp.message_utils.NewMessageEvent;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;
// Extends Base activity for darkmode variable and status bar.
public class StudentHomeActivity extends BaseActivity implements View.OnClickListener {
    private static int count = 0;
    CircleImageView userImageIV;
    ViewPager viewPager;
    ImageView chatIV, favIV, settingsIV;
    TextView newMessageCountTV;

    int newMessageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if darkmode is enabled and open the appropriate layout.
        if (darkModeEnabled) {
            setContentView(R.layout.activity_student_home_dark);
            clearLightStatusBar(this);
        } else
            setContentView(R.layout.activity_student_home);

        initUI();

        /******************************************************************/
        //What's new

        if (BuildConfig.VERSION_CODE > SharedPref.getInt(getApplicationContext(), "dont_delete", "current_version_code")) {
            SharedPref.putInt(getApplicationContext(), "dont_delete", "current_version_code", BuildConfig.VERSION_CODE);
            CommonUtils.showWhatsNewDialog(this, darkModeEnabled);
        }

        /******************************************************************/
        //First time subscription to global chat

        boolean chat_sub = SharedPref.getBoolean(getApplicationContext(), "chat_first_sub");
        if (!chat_sub && !Constants.fresher_email.contains(SharedPref.getString(getApplicationContext(), "email"))) {
            SharedPref.putBoolean(getApplicationContext(), "chat_first_sub", true);
            FCMHelper.SubscribeToTopic(getApplicationContext(), Constants.GLOBAL_CHAT);
            SharedPref.putBoolean(getApplicationContext(), "switch_global_chat", true);
        }
    }

    /**********************************************************************/
    // Initiate variables and UI elements.
    void initUI() {
        userImageIV = findViewById(R.id.userImageIV);
        viewPager = findViewById(R.id.viewPager);
        newMessageCountTV = findViewById(R.id.newMessageCountTV);
        chatIV = findViewById(R.id.chatIV);
        chatIV.setOnClickListener(this);
        favIV = findViewById(R.id.favIV);
        favIV.setOnClickListener(this);
        settingsIV = findViewById(R.id.settingsIV);
        settingsIV.setOnClickListener(this);

        try {
            //try loading DP using firebase Auth instance.
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(userImageIV);
        } catch (Exception e) {
            e.printStackTrace();
            //If for some reasons there was a error use dpUrl stored in sharedpref.
            Glide.with(this).load(SharedPref.getString(getApplicationContext(), "dp_url")).placeholder(R.drawable.ic_user_white).into(userImageIV);
        }
        setupViewPager();
    }
    /**********************************************************************/

    /**********************************************************************/
    //setting-up fragments viewpager.
    void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Newsfeed for Ug students
        if (SharedPref.getInt(getApplicationContext(), "clearance") == 0)
            adapter.addFragment(new StudentFeedFragment(), "News feed");

        //club for ug/pg/alumni
        adapter.addFragment(new ClubFragment(), "Club");

        //placements for Ug final year
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
            adapter.addFragment(new PlacementFragment(), "Placement");

        //bus alerts for Ug & PG except Almuni
        if (SharedPref.getInt(getApplicationContext(), "clearance") != 2)
            adapter.addFragment(new BusAlertsFragment(), "Bus alert");

        //Examcell for Ug
        if (SharedPref.getInt(getApplicationContext(), "clearance") == 0)
            adapter.addFragment(new ExamCellFragment(), "Exam cell");

        //Events for ug/pg/alumni
        adapter.addFragment(new EventFragment(), "Event");

        //set the adapter
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }
    /**********************************************************************/

    /*********************************************************/
    //When clicking any buttons on the page this function is called.
    @Override
    public void onClick(View v) {
        // On click function is differentiated for different buttons using the buttons ID.
        switch (v.getId()) {
            //global chat
            case R.id.chatIV:
                //if global_chat_is blocked make error msg.
                if (CommonUtils.getGlobal_chat_is_blocked()) {
                    Toast toast = Toast.makeText(getApplicationContext(), Constants.global_chat_error_maintenance, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    chatIV.setVisibility(View.GONE);
                    newMessageCountTV.setVisibility(View.GONE);
                }
                //if not blocked
                else {
                    //if there is a active network connection.
                    if (!CommonUtils.alerter(getApplicationContext())) {
                        startActivity(new Intent(getApplicationContext(), GroupChatActivity.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                    }
                    //if there is not a active network connection.
                    else {
                        Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                        intent.putExtra("key", "home");
                        startActivity(intent);
                        Bungee.fade(StudentHomeActivity.this);
                    }

                }
                break;
            // favorites
            case R.id.favIV:
                startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
            //Settings
            case R.id.settingsIV:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
                break;
        }
    }

    /*********************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPref.putBoolean(getApplicationContext(), "isStudHomeActive", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPref.putBoolean(getApplicationContext(), "isStudHomeActive", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonUtils.getGlobal_chat_is_blocked()) {
            newMessageCountTV.setVisibility(View.VISIBLE);
            chatIV.setVisibility(View.VISIBLE);
            updateNewMessageUI();
        } else {
            newMessageCountTV.setVisibility(View.GONE);
            chatIV.setVisibility(View.GONE);
        }

        SharedPref.putBoolean(getApplicationContext(), "isStudHomeActive", true);
        if (CommonUtils.alerter(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key", "home");
            startActivity(intent);
            Bungee.fade(StudentHomeActivity.this);
        } else if (darkModeEnabled != SharedPref.getBoolean(getApplicationContext(), "dark_mode")) {
            startActivity(getIntent());
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

    public void updateNewMessageUI() {
        if (newMessageCountTV != null) {
            newMessageCount = SharedPref.getInt(getApplicationContext(), "new_message_count");
            if (newMessageCount > 0) {
                newMessageCountTV.setText(String.valueOf(newMessageCount));
                newMessageCountTV.setVisibility(View.VISIBLE);
            } else {
                newMessageCountTV.setVisibility(View.GONE);
            }
        }
    }

    //Updating the new message count received from firebase messaging service
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMessageEvent event) {
        updateNewMessageUI();
    }


}
