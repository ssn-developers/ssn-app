package in.edu.ssn.ssnapp;

import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.fragments.EventsFragment;
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.FeedFragment;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class HomeActivity extends BaseActivity {

    ImageView menuIV, notificationIV, userImageIV;
    DrawerLayout drawerLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        Picasso.get().load(SharedPref.getString(getApplicationContext(),"dp_url")).placeholder(R.drawable.ic_user_white).into(userImageIV);

        menuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

    }

    void initUI(){
        menuIV = findViewById(R.id.menuIV);
        notificationIV = findViewById(R.id.notificationIV);
        userImageIV = findViewById(R.id.userImageIV);
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);
        //changeFont(regular,(ViewGroup)this.findViewById(android.R.id.content));
        setupViewPager();
    }

    void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FeedFragment(), "Feed");
        adapter.addFragment(new BusAlertsFragment(), "Bus alert");
        adapter.addFragment(new ExamCellFragment(), "Exam cell");
        adapter.addFragment(new EventsFragment(), "Workshop");
        adapter.addFragment(new EventsFragment(), "Event");
        //adapter.addFragment(new GlobalChatFragment(), "Global chat");
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }
}
