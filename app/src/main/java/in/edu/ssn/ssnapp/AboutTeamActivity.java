package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.adapters.AboutTeamAdapter;
import in.edu.ssn.ssnapp.models.TeamDetails;

public class AboutTeamActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lv_items;
    AboutTeamAdapter adapter;
    ImageView iv_web, iv_fb, iv_twit, iv_linkedin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_team);

        lv_items = findViewById(R.id.lv_items);
        adapter = new AboutTeamAdapter(this, new ArrayList<TeamDetails>());

        iv_web = findViewById(R.id.iv_web);             iv_web.setOnClickListener(this);
        iv_fb = findViewById(R.id.iv_fb);               iv_fb.setOnClickListener(this);
        iv_twit = findViewById(R.id.iv_twit);           iv_twit.setOnClickListener(this);
        iv_linkedin = findViewById(R.id.iv_linkedin);   iv_linkedin.setOnClickListener(this);

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
        adapter.add(new TeamDetails("Shibikannan T M","Motion Graphics Designer",R.drawable.shibi_profile, type, url));
        adapter.add(new TeamDetails("Mohammed Rizwan S","Web Developer & Designer",R.drawable.rizwan_profile, type, url));
        adapter.add(new TeamDetails("Antony Mevin Fernando","Web Developer & Designer",R.drawable.mevin_profile, type, url));
        adapter.add(new TeamDetails("Kaushik P","Web Developer",R.drawable.ic_profile_background, type, url));
        adapter.add(new TeamDetails("Sudhakar J","Web Designer",R.drawable.ic_profile_background, type, url));
        adapter.add(new TeamDetails("Yadhu Nair","Web Designer",R.drawable.ic_profile_background, type, url));
        adapter.add(new TeamDetails("Ezhil Prasanth M","App Developer",R.drawable.ezhil_profile, type, url));
        adapter.add(new TeamDetails("Ajay Kumar U","App Developer",R.drawable.ic_profile_background, type, url));
        adapter.add(new TeamDetails("Pavithra N","Web Developer",R.drawable.pavithra_profile, type, url));
        adapter.add(new TeamDetails("Nandhini R","Web Developer",R.drawable.nandhini_profile, type, url));

        lv_items.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_web:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ssn.edu.in/")));
                break;
            case R.id.iv_fb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/SSNInstitution/")));
                break;
            case R.id.iv_twit:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ssninstitutions")));
                break;
            case R.id.iv_linkedin:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/school/ssn-college-of-engineering/")));
                break;
        }
    }
}
