package in.edu.ssn.ssnapp;

import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;


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
import in.edu.ssn.ssnapp.fragments.WorkshopFragment;
import in.edu.ssn.ssnapp.models.Drawer;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class StudentHomeActivity extends BaseActivity {
    private static final String TAG ="StudentHomeActivity" ;
    ImageView notifUI;
    CircleImageView userImageIV, iv_profile;
    DrawerLayout drawerLayout;
    ViewPager viewPager;
    TextView tv_name, tv_email;

    ListView lv_items;
    DrawerAdapter adapter;

    static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        initUI();

        userImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        notifUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
                Bungee.slideLeft(StudentHomeActivity.this);
            }
        });

        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Drawer rs=(Drawer)parent.getItemAtPosition(position);
                switch (rs.getTitle()){
                    case "News Feed":
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case "Favourites":
                        startActivity(new Intent(getApplicationContext(), SavedPostActivity.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                        break;
                    case "Helpline":
                        startActivity(new Intent(getApplicationContext(), HelpLineActivity.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                        break;
                    case "Library Renewals":
                        if(CommonUtils.checkWifiOnAndConnected(getApplicationContext(),"ssn")) {
                            SharedPref.putString(getApplicationContext(),"url","http://opac.ssn.net:8081/");
                            startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                            Bungee.slideLeft(StudentHomeActivity.this);
                        }
                        else {
                            Toast toast = Toast.makeText(StudentHomeActivity.this, "Please connect to SSN wifi ", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                        break;
                    case "AlmaConnect":
                        if(!CommonUtils.alerter(getApplicationContext())) {
                            SharedPref.putString(getApplicationContext(),"url","https://ssn.almaconnect.com");
                            startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                            Bungee.slideLeft(StudentHomeActivity.this);
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                            intent.putExtra("key","home");
                            startActivity(intent);
                            Bungee.fade(StudentHomeActivity.this);
                        }
                        break;
                    case "Notification Settings":
                        startActivity(new Intent(getApplicationContext(), NotificationSettings.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                        break;
                    case "Invite Friends":
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Hello! Manage your internals, results & exam schedule with ease and Find your bus routes on the go! Click here to stay updated on department feeds: https://play.google.com/store/apps/details?id="+StudentHomeActivity.this.getPackageName();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        break;
                    case "Rate Our App":
                        if(!CommonUtils.alerter(getApplicationContext())) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                            intent.putExtra("key","home");
                            startActivity(intent);
                            Bungee.fade(StudentHomeActivity.this);
                        }
                        break;
                    case "Make a Suggestion":
                        startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                        break;
                    case "App Info":
                        startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                        Bungee.slideLeft(StudentHomeActivity.this);
                        break;
                    case "Privacy Policy":
                        if(!CommonUtils.alerter(getApplicationContext())) {
                            SharedPref.putString(getApplicationContext(), "url", "https://www.termsfeed.com/privacy-policy/59fe74661969551554a7a886f0767308");
                            startActivity(new Intent(getApplicationContext(), WebViewActivity.class));
                            Bungee.slideLeft(StudentHomeActivity.this);
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                            intent.putExtra("key","home");
                            startActivity(intent);
                            Bungee.fade(StudentHomeActivity.this);
                        }
                        break;
                    case "Logout":
                        FirebaseAuth.getInstance().signOut();
                        UnSubscribeToAlerts(getApplicationContext());
                        DataBaseHelper dbHelper=DataBaseHelper.getInstance(StudentHomeActivity.this);
                        dbHelper.dropAllTables();
                        SharedPref.removeAll(getApplicationContext());
                        SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in", 1);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Bungee.slideLeft(StudentHomeActivity.this);

                        break;
                }
            }
        });
    }

    /*********************************************************/

    void initUI(){
        notifUI = findViewById(R.id.notifUI);
        userImageIV = findViewById(R.id.userImageIV);
        iv_profile = findViewById(R.id.iv_profile);

        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);

        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);

        lv_items = findViewById(R.id.lv_items);
        adapter = new DrawerAdapter(this, new ArrayList<Drawer>());

        tv_name.setText(SharedPref.getString(getApplicationContext(),"name"));
        tv_email.setText(SharedPref.getString(getApplicationContext(),"email"));

        try{
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(userImageIV);
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(iv_profile);
        }
        catch (Exception e){
            e.printStackTrace();
            Glide.with(this).load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(userImageIV);
            Glide.with(this).load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(iv_profile);
        }

        setUpDrawer();
        setupViewPager();
    }

    void setUpDrawer(){
        adapter.add(new Drawer("News Feed", R.drawable.ic_feeds));
        adapter.add(new Drawer("Favourites", R.drawable.ic_fav));
        adapter.add(new Drawer("AlmaConnect", R.drawable.ic_alumni));
        adapter.add(new Drawer("Library Renewals", R.drawable.ic_book));
        adapter.add(new Drawer("Notification Settings", R.drawable.ic_notify_grey));
        adapter.add(new Drawer("Helpline", R.drawable.ic_team));
        adapter.add(new Drawer("Make a Suggestion", R.drawable.ic_feedback));
        adapter.add(new Drawer("Invite Friends", R.drawable.ic_invite));
        adapter.add(new Drawer("Rate Our App", R.drawable.ic_star));
        adapter.add(new Drawer("Privacy Policy", R.drawable.ic_feedback));
        adapter.add(new Drawer("App Info", R.drawable.ic_info));
        adapter.add(new Drawer("Logout", R.drawable.ic_logout));
        lv_items.setAdapter(adapter);
    }

    void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudentFeedFragment(), "News feed");
        adapter.addFragment(new ClubFragment(), "Club");
        if(SharedPref.getInt(getApplicationContext(),"year") == Integer.parseInt(Constants.fourth))
            adapter.addFragment(new PlacementFragment(), "Placement");
        adapter.addFragment(new BusAlertsFragment(), "Bus alert");
        adapter.addFragment(new ExamCellFragment(), "Exam cell");
        adapter.addFragment(new WorkshopFragment(), "Workshop");
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }

    public void UnSubscribeToAlerts(Context context){
        FCMHelper.UnSubscribeToTopic(context, Constants.BUS_ALERTS);
        FCMHelper.UnSubscribeToTopic(context, Constants.CLUB_ALERTS);
        FCMHelper.UnSubscribeToTopic(context, Constants.EXAM_CELL_ALERTS);
        FCMHelper.UnSubscribeToTopic(context, Constants.WORKSHOP_ALERTS);
        FCMHelper.UnSubscribeToTopic(context,SharedPref.getString(context,"dept") + SharedPref.getInt(context,"year"));
    }

    /*********************************************************/

    @Override
    protected void onResume() {
        super.onResume();

        if(CommonUtils.alerter(getApplicationContext())){
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key","home");
            startActivity(intent);
            Bungee.fade(StudentHomeActivity.this);
        }
    }

    /*********************************************************/

    @Override
    public void onBackPressed() {
        if (count > 0) {
            count=0;
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(startMain);
            finish();
        }
        else {
            count++;
            Toast toast = Toast.makeText(getApplicationContext(), "Press back once again to exit!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}