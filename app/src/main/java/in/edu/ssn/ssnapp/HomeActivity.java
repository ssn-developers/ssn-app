package in.edu.ssn.ssnapp;

import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.DrawerAdapter;
import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.fragments.EventsFragment;
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.FeedFragment;
import in.edu.ssn.ssnapp.models.Drawer;
import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class HomeActivity extends BaseActivity {
    ImageView menuIV, notificationIV;
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
        setContentView(R.layout.activity_home);

        initUI();

        menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
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
                    case "Notifications":
                        startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
                        break;
                    case "About Team":
                        startActivity(new Intent(getApplicationContext(),AboutTeamActivity.class));
                        break;
                    case "Logout":
                        startActivity(new Intent(getApplicationContext(), OnboardingActivity.class));
                        break;
                }
            }
        });

        notificationIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NotificationActivity.class));
            }
        });
    }

    void initUI(){
        menuIV = findViewById(R.id.menuIV);
        notificationIV = findViewById(R.id.notificationIV);
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

        Picasso.get().load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(userImageIV);
        Picasso.get().load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(iv_profile);

        setUpDrawer();
        setupViewPager();
    }

    void setUpDrawer(){
        adapter.add(new Drawer("Feeds", R.drawable.ic_feeds_blue));
        adapter.add(new Drawer("Favourites", R.drawable.ic_fav));
        adapter.add(new Drawer("Library Renewals", R.drawable.ic_book));
        adapter.add(new Drawer("Alumni Connect", R.drawable.ic_alumni));
        adapter.add(new Drawer("Notifications", R.drawable.ic_notify));

        adapter.add(new Drawer("Invite Friends", R.drawable.ic_invite));
        adapter.add(new Drawer("Rate Our App", R.drawable.ic_star));
        adapter.add(new Drawer("Make a Suggestion", R.drawable.ic_feedback));
        adapter.add(new Drawer("About Team", R.drawable.ic_team));
        adapter.add(new Drawer("Logout", R.drawable.ic_logout));
        lv_items.setAdapter(adapter);
    }

    void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment(), "Feed");
        adapter.addFragment(new BusAlertsFragment(), "Bus alert");
        adapter.addFragment(new ExamCellFragment(), "Exam cell");
        //adapter.addFragment(new EventsFragment(), "Workshop");
        //adapter.addFragment(new EventsFragment(), "Event");
        //adapter.addFragment(new GlobalChatFragment(), "Global chat");
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void onBackPressed() {
        if (count > 0) {
            count=0;
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            finish();
        } else {
            count++;
            Toast.makeText(getApplicationContext(), "Press back once again to exit!", Toast.LENGTH_SHORT).show();
        }
    }
}
