package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.suke.widget.SwitchButton;

import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class NotificationActivity extends AppCompatActivity {

    com.suke.widget.SwitchButton switch_all, switch_dept, switch_bus, switch_club, switch_exam, switch_workshop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initUI();

        switch_all.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Boolean status = switch_all.isChecked();

                try {
                    switch_dept.setChecked(status);
                    switch_bus.setChecked(status);
                    switch_club.setChecked(status);
                    switch_exam.setChecked(status);
                    switch_workshop.setChecked(status);
                    switch_all.setChecked(status);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        switch_dept.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Boolean status = switch_dept.isChecked();

                try {
                    switch_dept.setChecked(status);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
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
}