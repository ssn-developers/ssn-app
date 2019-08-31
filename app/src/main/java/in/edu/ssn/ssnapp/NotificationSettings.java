package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.suke.widget.SwitchButton;

import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class NotificationSettings extends AppCompatActivity {

    com.suke.widget.SwitchButton switch_all, switch_dept, switch_bus, switch_club, switch_exam, switch_workshop;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        initUI();

        switch_all.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                switch_dept.setChecked(isChecked);
                switch_bus.setChecked(isChecked);
                switch_club.setChecked(isChecked);
                switch_exam.setChecked(isChecked);
                switch_workshop.setChecked(isChecked);
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
        switch_club = (com.suke.widget.SwitchButton) findViewById(R.id.switch_club);
        switch_exam = (com.suke.widget.SwitchButton) findViewById(R.id.switch_exam);
        switch_workshop = (com.suke.widget.SwitchButton) findViewById(R.id.switch_workshop);

        iv_back = findViewById(R.id.iv_back);

        switch_all.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_all"));
        switch_dept.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_dept"));
        switch_bus.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_bus"));
        switch_club.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_club"));
        switch_exam.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_exam"));
        switch_workshop.setChecked(SharedPref.getBoolean(getApplicationContext(), "switch_workshop"));
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPref.putBoolean(getApplicationContext(),"switch_all",switch_all.isChecked());
        SharedPref.putBoolean(getApplicationContext(),"switch_dept",switch_dept.isChecked());
        SharedPref.putBoolean(getApplicationContext(),"switch_bus",switch_bus.isChecked());
        SharedPref.putBoolean(getApplicationContext(),"switch_club",switch_club.isChecked());
        SharedPref.putBoolean(getApplicationContext(),"switch_exam",switch_exam.isChecked());
        SharedPref.putBoolean(getApplicationContext(),"switch_workshop",switch_workshop.isChecked());

        if(switch_dept.isChecked())
            FCMHelper.SubscribeToTopic(this,SharedPref.getString(getApplicationContext(),"dept") + SharedPref.getInt(getApplicationContext(),"year"));
        else
            FCMHelper.UnSubscribeToTopic(this,SharedPref.getString(getApplicationContext(),"dept") + SharedPref.getInt(getApplicationContext(),"year"));

        if(switch_bus.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.BUS_ALERTS);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.BUS_ALERTS);

        if(switch_club.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.CLUB_ALERTS);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.CLUB_ALERTS);

        if(switch_exam.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.EXAM_CELL_ALERTS);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.EXAM_CELL_ALERTS);

        if(switch_workshop.isChecked())
            FCMHelper.SubscribeToTopic(this, Constants.WORKSHOP_ALERTS);
        else
            FCMHelper.UnSubscribeToTopic(this, Constants.WORKSHOP_ALERTS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(NotificationSettings.this);
    }
}