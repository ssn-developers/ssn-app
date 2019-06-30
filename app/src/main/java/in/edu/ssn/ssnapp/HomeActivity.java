package in.edu.ssn.ssnapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.fragments.BusAlertsFragment;
import in.edu.ssn.ssnapp.fragments.EventsFragment;
import in.edu.ssn.ssnapp.fragments.ExamCellFragment;
import in.edu.ssn.ssnapp.fragments.FeedFragment;
import in.edu.ssn.ssnapp.fragments.GlobalChatFragment;

public class HomeActivity extends BaseActivity {

    ImageView menuIV, notificationIV, userImageIV;
    DrawerLayout drawerLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/user%2Fprofile%2FU0001.jpg?alt=media&token=8d27e31e-3622-4ec9-bbeb-51897858070f").placeholder(R.drawable.ic_user_white).into(userImageIV);

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
