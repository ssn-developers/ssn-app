package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import in.edu.ssn.ssnapp.adapters.NotifyAdapter;

import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.database.Notification;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class NotificationActivity extends AppCompatActivity{

    ListView lv_items;
    NotifyAdapter adapter;
    DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lv_items = findViewById(R.id.lv_items);
        adapter = new NotifyAdapter(this, new ArrayList<Post>());
        dbHelper=DataBaseHelper.getInstance(this);

        setUpDrawer();
    }

    void setUpDrawer(){
        lv_items.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        ArrayList<Post> postList=dbHelper.getNotificationList();
        Collections.reverse(postList);
        adapter.addAll(postList);
        adapter.notifyDataSetChanged();
    }


}