package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import in.edu.ssn.ssnapp.adapters.NotifyAdapter;

import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.database.Notification;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class NotificationActivity extends AppCompatActivity{

    ListView lv_items;
    NotifyAdapter adapter;
    DataBaseHelper dbHelper;
    RelativeLayout layout_progress;
    ImageView iv_back;
    ArrayList<String> postType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lv_items = findViewById(R.id.lv_items);
        iv_back = findViewById(R.id.iv_back);
        layout_progress = findViewById(R.id.layout_progress);
        postType=new ArrayList<>();
        adapter = new NotifyAdapter(this, new ArrayList<Post>());
        dbHelper=DataBaseHelper.getInstance(this);

        setUpDrawer();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void setUpDrawer(){
        lv_items.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        ArrayList<Pair<Post,String>> postList=dbHelper.getNotificationList();
        Collections.reverse(postList);

        for(int i=0;i<postList.size();i++)
        {
            adapter.add(postList.get(i).first);
            postType.add(postList.get(i).second);
        }

        adapter.setPostType(postType);
        adapter.notifyDataSetChanged();

        if(postList.size() > 0){
            layout_progress.setVisibility(View.GONE);
            lv_items.setVisibility(View.VISIBLE);
        }
        else{
            layout_progress.setVisibility(View.VISIBLE);
            lv_items.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(NotificationActivity.this);
    }
}