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

        url.add("ddlogesh@gmail.com");
        url.add("https://github.com/ddlogesh");
        url.add("https://www.linkedin.com/in/logesh-dinakaran");
        adapter.add(new TeamDetails("Logesh D","App Developer",R.drawable.logesh_profile, type, url));

        url= new ArrayList<>();
        url.add("harshavardhan.zodiac@gmail.com");
        url.add("https://github.com/harshavardhan98");
        url.add("https://www.linkedin.com/in/harshavardhan-p");
        adapter.add(new TeamDetails("Harshavardhan P","App Developer",R.drawable.harsha_profile, type, url));

        url= new ArrayList<>();
        url.add("catcalm7698@gmail.com");
        url.add("https://github.com/shrikanth7698");
        url.add("https://www.linkedin.com/in/shrikanthravi");
        adapter.add(new TeamDetails("Shrikanth Ravi","App Developer & UI/UX Designer",R.drawable.shrikanth_profile, type, url));

        type= new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_behance);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("jennifer.malathi@gmail.com");
        url.add("https://www.behance.net/jenniferj1");
        url.add("https://www.linkedin.com/in/jennifer-j-a77260142");
        adapter.add(new TeamDetails("Jennifer J","UI/UX Designer",R.drawable.jenifer_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_dribble);
        type.add(R.drawable.ic_insta_circle);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("https://dribbble.com/Shibikannan");
        url.add("https://www.instagram.com/shibi_ssn");
        url.add("https://www.linkedin.com/in/shibikannan-t-m-a79493155");
        adapter.add(new TeamDetails("Shibikannan T M","Motion Graphics Designer",R.drawable.shibi_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("smohammedrizwan98@gmail.com");
        url.add("https://github.com/smriz");
        url.add("https://www.linkedin.com/in/mohammed-r-249034b2");
        adapter.add(new TeamDetails("Mohammed Rizwan S","Web Developer & Designer",R.drawable.rizwan_profile, type, url));

        url= new ArrayList<>();
        url.add("mfmevins@gmail.com");
        url.add(null);
        url.add("https://www.linkedin.com/in/antonymevinfernando");
        adapter.add(new TeamDetails("Antony Mevin Fernando","Web Developer & Designer",R.drawable.mevin_profile, type, url));

        url= new ArrayList<>();
        url.add("kaushik.personal.98@gmail.com");
        url.add("https://github.com/kshake");
        url.add("https://www.linkedin.com/in/kaushik-p-2921aa178");
        adapter.add(new TeamDetails("Kaushik P","Web Developer",R.drawable.ic_profile_background, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_insta_circle);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("sudhakar.jeeva7@gmail.com");
        url.add("https://www.instagram.com/sudhakar_shady");
        url.add("https://www.linkedin.com/in/sudhakar-j");
        adapter.add(new TeamDetails("Sudhakar J","Web Designer",R.drawable.sudhakar_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_fb_circle);
        type.add(R.drawable.ic_insta_circle);

        url= new ArrayList<>();
        url.add("yadhukrishnannair99@gmail.com");
        url.add(null);
        url.add("https://www.instagram.com/yk_2310");
        adapter.add(new TeamDetails("Yadhu Nair","Web Designer",R.drawable.yadhuv_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("ezhilprasanth17040@cse.ssn.edu.in");
        url.add("https://github.com/ezhilnero99");
        url.add("https://www.linkedin.com/in/ezhilnero-m");
        adapter.add(new TeamDetails("Ezhil Prasanth M","App Developer",R.drawable.ezhil_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_insta_circle);

        url= new ArrayList<>();
        url.add("ajaykumar17006@cse.ssn.edu.in");
        url.add("https://github.com/ajaykumar047");
        url.add("https://instagram.com/ajaykumar_047");
        adapter.add(new TeamDetails("Ajay Kumar U","App Developer",R.drawable.ajay_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("pavikaramanchu@gmail.com");
        url.add("https://github.com/pavithrakarumanchi");
        url.add("https://www.linkedin.com/in/pavithra-karumanchi-0420b9166");
        adapter.add(new TeamDetails("Pavithra N","Web Developer",R.drawable.pavithra_profile, type, url));

        url= new ArrayList<>();
        url.add("nandhiniraja208@gmail.com");
        url.add("https://github.com/nandy20");
        url.add("https://www.linkedin.com/in/nandhini-raja-8b71b4143");
        adapter.add(new TeamDetails("Nandhini R","Web Developer",R.drawable.nandhini_profile, type, url));

        lv_items.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_web:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ssn.edu.in")));
                break;
            case R.id.iv_fb:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/SSNInstitution")));
                break;
            case R.id.iv_twit:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ssninstitutions")));
                break;
            case R.id.iv_linkedin:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/school/ssn-college-of-engineering")));
                break;
        }
    }
}
