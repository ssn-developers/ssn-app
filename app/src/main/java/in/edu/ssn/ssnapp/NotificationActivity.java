package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import in.edu.ssn.ssnapp.adapters.DrawerAdapter;
import in.edu.ssn.ssnapp.adapters.NotifyAdapter;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.models.Post;

public class NotificationActivity extends AppCompatActivity {

    ListView lv_items;
    NotifyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        lv_items = findViewById(R.id.lv_items);
        adapter = new NotifyAdapter(this, new ArrayList<Post>());

        setUpDrawer();
    }

    void setUpDrawer(){
        adapter.add(new Post("New posts from Dr. Chitra Babu", "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/dummy_test%2Fdummyid1.jfif?alt=media&token=b117fd84-9b3d-4173-8954-8f97e6a7ff76", new Date()));
        adapter.add(new Post("New posts from Mr. Balasubramanian", "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/dummy_test%2Fdummyid2.jpg?alt=media&token=4839863a-c188-41f8-8288-979f7c044889", new Date()));
        adapter.add(new Post("New posts from Dr. Chitra Babu", "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/dummy_test%2Fdummyid1.jfif?alt=media&token=b117fd84-9b3d-4173-8954-8f97e6a7ff76", new Date()));
        adapter.add(new Post("New posts from Mr. Balasubramanian", "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/dummy_test%2Fdummyid2.jpg?alt=media&token=4839863a-c188-41f8-8288-979f7c044889", new Date()));
        adapter.add(new Post("New posts from Dr. Chitra Babu", "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/dummy_test%2Fdummyid1.jfif?alt=media&token=b117fd84-9b3d-4173-8954-8f97e6a7ff76", new Date()));

        lv_items.setAdapter(adapter);
    }
}