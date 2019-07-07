package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.adapters.AboutTeamAdapter;
import in.edu.ssn.ssnapp.models.TeamDetails;

public class AboutTeamActivity extends AppCompatActivity {

    ListView lv_items;
    AboutTeamAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_team);

        lv_items = findViewById(R.id.lv_items);
        adapter = new AboutTeamAdapter(this, new ArrayList<TeamDetails>());

        setUpDrawer();
    }

    void setUpDrawer(){
        ArrayList<Integer> type  = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();

        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);


        url.add("www.hello.com");
        url.add("www.hello.com");
        url.add("www.hello.com");

        adapter.add(new TeamDetails("Logesh D","App Developer",R.drawable.logesh_profile, type, url));
        adapter.add(new TeamDetails("Harshavardhan P","App Developer",R.drawable.harsha_profile, type, url));
        adapter.add(new TeamDetails("Shrikanth Ravi","App Developer & UI/UX Designer",R.drawable.shrikanth_profile, type, url));
        adapter.add(new TeamDetails("Jennifer J","UI/UX Designer",R.drawable.jenifer_profile, type, url));
        adapter.add(new TeamDetails("Mohammed Rizwan S","Web Developer & Designer",R.drawable.rizwan_profile, type, url));
        adapter.add(new TeamDetails("Antony Mevin Fernando","Web Developer & Designer",R.drawable.mevin_profile, type, url));
        adapter.add(new TeamDetails("Shibikannan T M","Motion Graphics Designer",R.drawable.shibi_profile, type, url));

        lv_items.setAdapter(adapter);
    }
}
