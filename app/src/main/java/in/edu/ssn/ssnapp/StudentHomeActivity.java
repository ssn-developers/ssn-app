package in.edu.ssn.ssnapp;

import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.DrawerAdapter;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.StudentFeedFragment;
import in.edu.ssn.ssnapp.models.Drawer;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class StudentHomeActivity extends BaseActivity {
    private static final String TAG ="StudentHomeActivity" ;
    ImageView menuIV, notifUI;
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

        menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        notifUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
            }
        });

        lv_items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Drawer rs=(Drawer)parent.getItemAtPosition(position);
                switch (rs.getTitle()){
                    case "Feeds":
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case "Favourites":
                        //startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
                        break;
                    case "Library Renewals":
                        if(CommonUtils.checkWifiOnAndConnected(getApplicationContext(),"ssn"))
                            startActivity(new Intent(getApplicationContext(), LibraryActivity.class));
                        else
                            Toast.makeText(StudentHomeActivity.this, "Please connect to SSN wifi ", Toast.LENGTH_SHORT).show();
                        break;
                    case "Alumni Connect":
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ssn.almaconnect.com")));
                        break;
                    case "Notification Settings":
                        startActivity(new Intent(getApplicationContext(), NotificationSettings.class));
                        break;
                    case "Invite Friends":
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "Hello! Manage your internals, results & exam schedule with ease and Find your bus routes on the go! Click here to stay updated on department feeds: https://play.google.com/store/apps/details?id=se.par.amsen.experimentshopdesign";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        break;
                    case "Rate Our App":
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=se.par.amsen.experimentshopdesign")));
                        break;
                    case "Make a Suggestion":
                        //startActivity(new Intent(getApplicationContext(),FeedbackActivity.class));
                        break;
                    case "About Team":
                        startActivity(new Intent(getApplicationContext(), AboutTeamActivity.class));
                        break;
                    case "Logout":
                        FirebaseAuth.getInstance().signOut();
                        SharedPref.removeAll(getApplicationContext());
                        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", false);
                        SharedPref.putBoolean(getApplicationContext(),"is_logged_out", true);

                        Intent intent = new Intent(getApplicationContext(), LogoutActivity.class);
                        intent.putExtra("is_log_in",false);
                        startActivity(intent);

                        break;
                }
            }
        });
    }

    /*********************************************************/

    void initUI(){
        menuIV = findViewById(R.id.menuIV);
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
            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(userImageIV);
            Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).placeholder(R.drawable.ic_user_white).into(iv_profile);
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }


        setUpDrawer();
        setupViewPager();
    }

    void setUpDrawer(){
        adapter.add(new Drawer("Feeds", R.drawable.ic_feeds_blue));
        adapter.add(new Drawer("Favourites", R.drawable.ic_fav));
        adapter.add(new Drawer("Library Renewals", R.drawable.ic_book));
        adapter.add(new Drawer("Alumni Connect", R.drawable.ic_alumni));
        adapter.add(new Drawer("Notification Settings", R.drawable.ic_notify));
        adapter.add(new Drawer("Invite Friends", R.drawable.ic_invite));
        adapter.add(new Drawer("Rate Our App", R.drawable.ic_star));
        adapter.add(new Drawer("Make a Suggestion", R.drawable.ic_feedback));
        adapter.add(new Drawer("About Team", R.drawable.ic_team));
        adapter.add(new Drawer("Logout", R.drawable.ic_logout));
        lv_items.setAdapter(adapter);
    }

    void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudentFeedFragment(), "Feed");
        adapter.addFragment(new BusAlertsFragment(), "Bus alert");
        adapter.addFragment(new ExamCellFragment(), "Exam cell");
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
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
            Toast.makeText(getApplicationContext(), "Press back once again to exit!", Toast.LENGTH_SHORT).show();
        }
    }
}
