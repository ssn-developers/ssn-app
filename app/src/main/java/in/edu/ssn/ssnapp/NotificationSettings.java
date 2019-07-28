package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.suke.widget.SwitchButton;

import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class NotificationSettings extends AppCompatActivity implements SwitchButton.OnCheckedChangeListener {

    com.suke.widget.SwitchButton switch_all, switch_dept, switch_bus, switch_club, switch_exam, switch_workshop;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        initUI();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initUI(){
        switch_all = (com.suke.widget.SwitchButton) findViewById(R.id.switch_all);              switch_all.setOnCheckedChangeListener(this);
        switch_dept = (com.suke.widget.SwitchButton) findViewById(R.id.switch_dept);            switch_dept.setOnCheckedChangeListener(this);
        switch_bus = (com.suke.widget.SwitchButton) findViewById(R.id.switch_bus);              switch_bus.setOnCheckedChangeListener(this);
        switch_club = (com.suke.widget.SwitchButton) findViewById(R.id.switch_club);            switch_club.setOnCheckedChangeListener(this);
        switch_exam = (com.suke.widget.SwitchButton) findViewById(R.id.switch_exam);            switch_exam.setOnCheckedChangeListener(this);
        switch_workshop = (com.suke.widget.SwitchButton) findViewById(R.id.switch_workshop);    switch_workshop.setOnCheckedChangeListener(this);

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
            FCMHelper.SubscribeToTopic(this, SharedPref.getString(getApplicationContext(),"dept"));
        else {
            FCMHelper.UnSubscribeToTopic(this, SharedPref.getString(getApplicationContext(), "dept"));
        }

        //TODO: Perform the above for other categories
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.switch_dept:
            case R.id.switch_bus:
            case R.id.switch_club:
            case R.id.switch_exam:
            case R.id.switch_workshop:
                if (switch_all.isChecked() && !isChecked) {
                    switch_all.setChecked(false);
                    Log.d("test_set", Boolean.toString(isChecked) + " others");
                }
                break;
            case R.id.switch_all:
                if(isChecked) {
                    Log.d("test_set", Boolean.toString(isChecked) + " all");
                    switch_dept.setChecked(isChecked);
                    switch_bus.setChecked(isChecked);
                    switch_club.setChecked(isChecked);
                    switch_exam.setChecked(isChecked);
                    switch_workshop.setChecked(isChecked);
                }
                break;
        }
    }
}