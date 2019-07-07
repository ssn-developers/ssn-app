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
    ImageView iv_bell;
    TextView tv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        iv_bell = findViewById(R.id.iv_bell);
        tv_desc = findViewById(R.id.tv_desc);

        lv_items = findViewById(R.id.lv_items);
        adapter = new NotifyAdapter(this, new ArrayList<Post>());

        iv_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iv_bell.getTag().equals("disable")){
                    iv_bell.setImageResource(R.drawable.ic_enable_notify);
                    tv_desc.setText("Would you like to unsubscribe from alerts? click on the bell icon!");
                    iv_bell.setTag("enable");
                }
                else{
                    iv_bell.setImageResource(R.drawable.ic_disable_notify);
                    tv_desc.setText("Would you like to get notified on new feeds? click on the bell icon!");
                    iv_bell.setTag("disable");
                }
            }
        });

        setUpDrawer();
    }

    void setUpDrawer(){
        adapter.add(new Post("Reminder - Class committee meeting", "www.url.com", new Date()));
        adapter.add(new Post("Invente Day Volunteer Registration", "www.url.com", new Date()));
        adapter.add(new Post("Lecture on big data processing", "www.url.com", new Date()));
        adapter.add(new Post("Invente Day Volunteer Registration", "www.url.com", new Date()));
        adapter.add(new Post("Reminder - Class committee meeting", "www.url.com", new Date()));

        lv_items.setAdapter(adapter);
    }
}
