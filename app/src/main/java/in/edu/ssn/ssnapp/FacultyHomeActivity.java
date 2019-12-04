package in.edu.ssn.ssnapp;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import com.suke.widget.SwitchButton;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.DrawerAdapter;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.models.Drawer;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class FacultyHomeActivity extends BaseActivity {

    ImageView notifUI;
    CircleImageView userImageIV, profileIV;
    DrawerLayout drawerLayout;
    ViewPager viewPager;
    TextView nameTV, emailTV, accessTV;
    SwitchButton darkModeSwitch;

    ListView lv_items;
    DrawerAdapter adapter;

    static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(darkModeEnabled){
            setContentView(R.layout.activity_faculty_home_dark);
            clearLightStatusBar(this);
        }
        else
            setContentView(R.layout.activity_faculty_home);

        initUI();

        /******************************************************************/
        //Darkmode handle

        if(darkModeEnabled){
            darkModeSwitch.setChecked(true);
        }else {
            darkModeSwitch.setChecked(false);
        }

        darkModeSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if(isChecked){
                    darkModeEnabled=true;
                    SharedPref.putBoolean(getApplicationContext(),"dark_mode",darkModeEnabled);
                    finish();
                    startActivity(getIntent());
                    Bungee.fade(FacultyHomeActivity.this);
                }else {
                    darkModeEnabled=false;
                    SharedPref.putBoolean(getApplicationContext(),"dark_mode",darkModeEnabled);
                    finish();
                    startActivity(getIntent());
                    Bungee.fade(FacultyHomeActivity.this);
                }
            }
        });

        /******************************************************************/
        //What's new

        if(BuildConfig.VERSION_CODE > SharedPref.getInt(getApplicationContext(),"dont_delete","current_version_code")){
            SharedPref.putInt(getApplicationContext(),"dont_delete","current_version_code", BuildConfig.VERSION_CODE);
            CommonUtils.showWhatsNewDialog(this,darkModeEnabled);
        }

        /******************************************************************/

        userImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Drawer rs = (Drawer) parent.getItemAtPosition(position);
                switch (rs.getTitle()) {
                    case "News Feed":
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case "Calendar":
                        if(!CommonUtils.alerter(getApplicationContext())) {
                            Intent i = new Intent(getApplicationContext(), PdfViewerActivity.class);
                            i.putExtra(Constants.PDF_URL, Constants.calendar);
                            startActivity(i);
                            Bungee.fade(FacultyHomeActivity.this);
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                            intent.putExtra("key","home");
                            startActivity(intent);
                            Bungee.fade(FacultyHomeActivity.this);
                        }
                        break;
                    case "Invite Friends":
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Hi all! Manage your internals, results & exam schedule with ease and Find your bus routes on the go! Click here to stay updated on department, club or placement feeds and events: https://play.google.com/store/apps/details?id=" + FacultyHomeActivity.this.getPackageName();
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
                            Bungee.fade(FacultyHomeActivity.this);
                        }
                        break;
                    case "Helpline":
                        startActivity(new Intent(getApplicationContext(), HelplineActivity.class));
                        Bungee.slideLeft(FacultyHomeActivity.this);
                        break;
                    case "Make a Suggestion":
                        startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
                        Bungee.slideLeft(FacultyHomeActivity.this);
                        break;
                    case "App Info":
                        startActivity(new Intent(getApplicationContext(), AppInfoActivity.class));
                        Bungee.slideLeft(FacultyHomeActivity.this);
                        break;
                    case "Privacy Policy":
                        if(!CommonUtils.alerter(getApplicationContext())) {
                            CommonUtils.openCustomBrowser(getApplicationContext(),Constants.termsfeed);
                        }
                        else{
                            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                            intent.putExtra("key","home");
                            startActivity(intent);
                            Bungee.fade(FacultyHomeActivity.this);
                        }
                        break;
                    case "Logout":
                        FirebaseAuth.getInstance().signOut();
                        DataBaseHelper dbHelper=DataBaseHelper.getInstance(FacultyHomeActivity.this);
                        dbHelper.dropAllTables();
                        SharedPref.removeAll(getApplicationContext());
                        SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in", 1);
                        finish();

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        Bungee.slideLeft(FacultyHomeActivity.this);
                        break;
                }
            }
        });
    }

    /*********************************************************/

    void initUI() {
        notifUI = findViewById(R.id.notifUI);
        userImageIV = findViewById(R.id.userImageIV);
        profileIV = findViewById(R.id.profileIV);

        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);

        nameTV = findViewById(R.id.nameTV);
        emailTV = findViewById(R.id.emailTV);
        accessTV = findViewById(R.id.accessTV);

        darkModeSwitch = findViewById(R.id.darkModeSwitch);

        lv_items = findViewById(R.id.lv_items);
        adapter = new DrawerAdapter(this, new ArrayList<Drawer>());

        nameTV.setText(SharedPref.getString(getApplicationContext(), "name"));
        emailTV.setText(SharedPref.getString(getApplicationContext(), "email"));

        String access = SharedPref.getString(getApplicationContext(), "access");
        if (access.equals("SA"))
            accessTV.setText("ADMIN");
        else if (access.equals("PC"))
            accessTV.setText("PLACEMENT");
        else if (access.equals("EC"))
            accessTV.setText("EXAM CELL");
        else
            accessTV.setVisibility(View.GONE);

        try{
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(userImageIV);
            Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(profileIV);
        }
        catch (Exception e){
            e.printStackTrace();
            Glide.with(this).load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(userImageIV);
            Glide.with(this).load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(profileIV);
        }

        setUpDrawer();
        setupViewPager();
    }

    void setUpDrawer() {
        adapter.add(new Drawer("News Feed", R.drawable.ic_feeds));
        adapter.add(new Drawer("Calendar", R.drawable.ic_calendar));
        adapter.add(new Drawer("Helpline", R.drawable.ic_phone));
        adapter.add(new Drawer("Make a Suggestion", R.drawable.ic_feedback));
        adapter.add(new Drawer("Invite Friends", R.drawable.ic_invite));
        adapter.add(new Drawer("Rate Our App", R.drawable.ic_star));
        adapter.add(new Drawer("Privacy Policy", R.drawable.ic_feedback));
        adapter.add(new Drawer("App Info", R.drawable.ic_info));
        adapter.add(new Drawer("Logout", R.drawable.ic_logout));
        lv_items.setAdapter(adapter);
    }

    void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BusAlertsFragment(),"Bus alert");
        viewPager.setAdapter(adapter);
    }

    /*********************************************************/

    @Override
    protected void onResume() {
        super.onResume();

        if(CommonUtils.alerter(getApplicationContext())){
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key","home");
            startActivity(intent);
            Bungee.fade(FacultyHomeActivity.this);
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
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
}
