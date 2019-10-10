package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.suke.widget.SwitchButton;

import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class NotificationSettings extends BaseActivity {

    com.suke.widget.SwitchButton switch_all, switch_dept, switch_bus, switch_exam, switch_place, switch_event;
    ImageView iv_back;
    LinearLayout layout_news_feed, layout_placement, layout_exam_cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(darkModeEnabled){
            setContentView(R.layout.activity_notification_settings_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        }
        else
            setContentView(R.layout.activity_notification_settings);

        initUI();

        switch_all.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                switch_dept.setChecked(isChecked);
                switch_bus.setChecked(isChecked);
                switch_exam.setChecked(isChecked);
                if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
                    switch_place.setChecked(isChecked);
                switch_event.setChecked(isChecked);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initUI(){
        switch_all = (com.suke.widget.SwitchButton) findViewById(R.id.switch_all);

        switch_dept = (com.suke.widget.SwitchButton) findViewById(R.id.switch_dept);
        switch_bus = (com.suke.widget.SwitchButton) findViewById(R.id.switch_bus);
        switch_exam = (com.suke.widget.SwitchButton) findViewById(R.id.switch_exam);
        switch_place = (com.suke.widget.SwitchButton) findViewById(R.id.switch_place);
        switch_event = (com.suke.widget.SwitchButton) findViewById(R.id.switch_event);

        layout_news_feed = findViewById(R.id.layout_news_feed);
        layout_placement = findViewById(R.id.layout_placement);
        layout_exam_cell = findViewById(R.id.layout_exam_cell);
        iv_back = findViewById(R.id.iv_back);

        switch_all.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_all"));

        if(SharedPref.getInt(getApplicationContext(),"clearance") == 0) {
            switch_dept.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_dept"));
            layout_news_feed.setVisibility(View.VISIBLE);
        }
        else
            layout_news_feed.setVisibility(View.GONE);

        switch_bus.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_bus"));

        if(SharedPref.getInt(getApplicationContext(),"clearance") == 0) {
            switch_exam.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_exam"));
            layout_exam_cell.setVisibility(View.VISIBLE);
        }
        else
            layout_exam_cell.setVisibility(View.GONE);

        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth)) {
            switch_place.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_place"));
            layout_placement.setVisibility(View.VISIBLE);
        }
        else
            layout_placement.setVisibility(View.GONE);

        switch_event.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_event"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPref.putBoolean(getApplicationContext(),"switch_all",switch_all.isChecked());

        SharedPref.putBoolean(getApplicationContext(), "switch_dept", switch_dept.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_bus", switch_bus.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_exam", switch_exam.isChecked());
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
            SharedPref.putBoolean(getApplicationContext(), "switch_place", switch_place.isChecked());
        SharedPref.putBoolean(getApplicationContext(), "switch_event", switch_event.isChecked());

        if(SharedPref.getInt(getApplicationContext(),"clearance") == 0) {
            if (switch_dept.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year"));
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year"));
        }

        if(switch_bus.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.BUS_ALERTS);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.BUS_ALERTS);

        if(SharedPref.getInt(getApplicationContext(),"clearance") == 0) {
            if (switch_exam.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "exam");
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "exam");
        }

        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth)) {
            if (switch_place.isChecked())
                FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "place");
            else
                FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept") + SharedPref.getInt(getApplicationContext(), "year") + "place");
        }

        if(switch_event.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.Event);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.Event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(NotificationSettings.this);
    }
}