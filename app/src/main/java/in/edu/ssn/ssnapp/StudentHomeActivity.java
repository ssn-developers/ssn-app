package in.edu.ssn.ssnapp;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.PlacementFragment;
import in.edu.ssn.ssnapp.fragments.StudentFeedFragment;
import in.edu.ssn.ssnapp.fragments.EventFragment;
import in.edu.ssn.ssnapp.message_utils.NewMessageEvent;
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
    TextView newMessageCountTV;

    int newMessageCount=0;
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

        /******************************************************************/
        //First time subscription to global chat

        boolean chat_sub = SharedPref.getBoolean(getApplicationContext(),"chat_first_sub");
        if(!chat_sub && !Constants.fresher_email.contains(SharedPref.getString(getApplicationContext(),"email"))){
            SharedPref.putBoolean(getApplicationContext(),"chat_first_sub",true);
            System.out.println("global_chat first time sub triggered");
            FCMHelper.SubscribeToTopic(getApplicationContext(),Constants.GLOBAL_CHAT);
            SharedPref.putBoolean(getApplicationContext(), "switch_global_chat",true);
        }
    }

    /*********************************************************/

    void initUI() {
        userImageIV = findViewById(R.id.userImageIV);
        viewPager = findViewById(R.id.viewPager);
        newMessageCountTV = findViewById(R.id.newMessageCountTV);
        chatIV = findViewById(R.id.chatIV);         chatIV.setOnClickListener(this);
        favIV = findViewById(R.id.favIV);           favIV.setOnClickListener(this);
        settingsIV = findViewById(R.id.settingsIV); settingsIV.setOnClickListener(this);

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
                if(CommonUtils.getGlobal_chat_is_blocked()){
                    Toast.makeText(getApplicationContext(),"Global chat is under maintenance. Please try again after some time",Toast.LENGTH_SHORT).show();
                    chatIV.setVisibility(View.GONE);
                    newMessageCountTV.setVisibility(View.GONE);
                }else {
                    if(!CommonUtils.alerter(getApplicationContext())){
                        startActivity(new Intent(getApplicationContext(), GroupChatActivity.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                        intent.putExtra("key", "home");
                        startActivity(intent);
                        Bungee.fade(StudentHomeActivity.this);
                    }

                }
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
        SharedPref.putBoolean(getApplicationContext(),"isStudHomeActive",false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPref.putBoolean(getApplicationContext(),"isStudHomeActive",false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!CommonUtils.getGlobal_chat_is_blocked()){
            newMessageCountTV.setVisibility(View.VISIBLE);
            chatIV.setVisibility(View.VISIBLE);
            updateNewMessageUI();
        }else{
            newMessageCountTV.setVisibility(View.GONE);
            chatIV.setVisibility(View.GONE);
        }

        SharedPref.putBoolean(getApplicationContext(),"isStudHomeActive",true);
        if (CommonUtils.alerter(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key", "home");
            startActivity(intent);
            Bungee.fade(StudentHomeActivity.this);
        }
        else if(darkModeEnabled != SharedPref.getBoolean(getApplicationContext(),"dark_mode")) {
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
        }
        else {
            count++;
            Toast toast = Toast.makeText(getApplicationContext(), "Press back once again to exit!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void updateNewMessageUI(){
        if(newMessageCountTV!=null) {
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
