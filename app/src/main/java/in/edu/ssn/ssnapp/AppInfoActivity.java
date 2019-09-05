package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.adapters.AboutAlumniAdapter;
import in.edu.ssn.ssnapp.adapters.AboutContributorAdapter;
import in.edu.ssn.ssnapp.models.AlumniDetails;
import in.edu.ssn.ssnapp.models.TeamDetails;
import spencerstudios.com.bungeelib.Bungee;

public class AppInfoActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv_items1, rv_items2, rv_items3;
    ArrayList<TeamDetails> teams1, teams2;
    ArrayList<AlumniDetails> alumni;
    ImageView iv_web, iv_fb, iv_twit, iv_linkedin, iv_insta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.smoothScrollTo(0,0);

        rv_items1 = findViewById(R.id.rv_items1);
        rv_items2 = findViewById(R.id.rv_items2);
        rv_items3 = findViewById(R.id.rv_items3);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);

        rv_items1.setLayoutManager(layoutManager1);
        rv_items1.setNestedScrollingEnabled(false);
        rv_items2.setLayoutManager(layoutManager2);
        rv_items2.setNestedScrollingEnabled(false);
        rv_items3.setLayoutManager(layoutManager3);
        rv_items3.setNestedScrollingEnabled(false);

        teams1 = new ArrayList<>();
        teams2 = new ArrayList<>();
        alumni = new ArrayList<>();

        iv_web = findViewById(R.id.iv_web);             iv_web.setOnClickListener(this);
        iv_fb = findViewById(R.id.iv_fb);               iv_fb.setOnClickListener(this);
        iv_twit = findViewById(R.id.iv_twit);           iv_twit.setOnClickListener(this);
        iv_linkedin = findViewById(R.id.iv_linkedin);   iv_linkedin.setOnClickListener(this);
        iv_insta = findViewById(R.id.iv_insta);         iv_insta.setOnClickListener(this);

        setUpPreFinalContributors();

        setUpFinalContributors();

        setUpAlumni();
    }

    void setUpPreFinalContributors(){
        ArrayList<Integer> type  = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();

        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("ezhilprasanth17040@cse.ssn.edu.in");
        url.add("https://github.com/ezhilnero99");
        url.add("https://www.linkedin.com/in/ezhilnero-m");
        teams1.add(new TeamDetails("Ezhil Prasanth M","App Development",R.drawable.ezhil_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_insta_circle);

        url= new ArrayList<>();
        url.add("ajaykumar17006@cse.ssn.edu.in");
        url.add("https://github.com/ajaykumar047");
        url.add("https://instagram.com/ajaykumar_047");
        teams1.add(new TeamDetails("Ajay Kumar U","App Development",R.drawable.ajay_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_insta_circle);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("sudhakar.jeeva7@gmail.com");
        url.add("https://www.instagram.com/sudhakar_shady");
        url.add("https://www.linkedin.com/in/sudhakar-j");
        teams1.add(new TeamDetails("Sudhakar J","Web UI Designs",R.drawable.sudhakar_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_fb_circle);
        type.add(R.drawable.ic_insta_circle);

        url= new ArrayList<>();
        url.add("yadhukrishnannair99@gmail.com");
        url.add("https://www.facebook.com/yadhukrishnan.nair.5");
        url.add("https://www.instagram.com/yk_2310");
        teams1.add(new TeamDetails("Yadhukrishnan P","Web UI Designs",R.drawable.yadhuv_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("pavikaramanchu@gmail.com");
        url.add("https://github.com/pavithrakarumanchi");
        url.add("https://www.linkedin.com/in/pavithra-karumanchi-0420b9166");
        teams1.add(new TeamDetails("Pavithra N","Web Development",R.drawable.pavithra_profile, type, url));

        url= new ArrayList<>();
        url.add("nandhiniraja208@gmail.com");
        url.add("https://github.com/nandy20");
        url.add("https://www.linkedin.com/in/nandhini-raja-8b71b4143");
        teams1.add(new TeamDetails("Nandhini R","Web Development",R.drawable.nandhini_profile, type, url));

        rv_items1.setAdapter(new AboutContributorAdapter(this, teams1));
    }

    void setUpFinalContributors(){
        ArrayList<Integer> type  = new ArrayList<>();
        ArrayList<String> url = new ArrayList<>();

        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_git);
        type.add(R.drawable.ic_linkedin_circle);

        url.add("ddlogesh@gmail.com");
        url.add("https://github.com/ddlogesh");
        url.add("https://www.linkedin.com/in/logesh-dinakaran");
        teams2.add(new TeamDetails("Logesh D","App Development",R.drawable.logesh_profile, type, url));

        url= new ArrayList<>();
        url.add("harshavardhan.zodiac@gmail.com");
        url.add("https://github.com/harshavardhan98");
        url.add("https://www.linkedin.com/in/harshavardhan-p");
        teams2.add(new TeamDetails("Harshavardhan P","App Development",R.drawable.harsha_profile, type, url));

        url= new ArrayList<>();
        url.add("catcalm7698@gmail.com");
        url.add("https://github.com/shrikanth7698");
        url.add("https://www.linkedin.com/in/shrikanthravi");
        teams2.add(new TeamDetails("Shrikanth Ravi","App Development",R.drawable.shrikanth_profile, type, url));

        type= new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_behance);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("jennifer.malathi@gmail.com");
        url.add("https://www.behance.net/jenniferj1");
        url.add("https://www.linkedin.com/in/jennifer-j-a77260142");
        teams2.add(new TeamDetails("Jennifer J","App Illustrations",R.drawable.jenifer_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_dribble);
        type.add(R.drawable.ic_insta_circle);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("https://dribbble.com/Shibikannan");
        url.add("https://www.instagram.com/shibi_ssn");
        url.add("https://www.linkedin.com/in/shibikannan-t-m-a79493155");
        teams2.add(new TeamDetails("Shibikannan T M","Motion Graphic Designs",R.drawable.shibi_profile, type, url));

        type  = new ArrayList<>();
        type.add(R.drawable.ic_google);
        type.add(R.drawable.ic_fb_circle);
        type.add(R.drawable.ic_linkedin_circle);

        url= new ArrayList<>();
        url.add("tarun.krithik@gmail.com");
        url.add("https://www.facebook.com/tarung.kangeyan");
        url.add("https://www.linkedin.com/in/tarun-ganesh-a35594181");
        teams2.add(new TeamDetails("Tarun Ganesh K","Web Development",R.drawable.tarun_profile, type, url));

        rv_items2.setAdapter(new AboutContributorAdapter(this, teams2));
    }

    void setUpAlumni(){
        alumni.add(new AlumniDetails("Karnik Ram","karnikram@gmail.com"));
        alumni.add(new AlumniDetails("Adithya J","adithya321@hotmail.com"));
        alumni.add(new AlumniDetails("Varun Ranganathan","dvarunranganathan@gmail.com"));
        alumni.add(new AlumniDetails("Muthu Annamalai CT","muthuct@outlook.com"));
        rv_items3.setAdapter(new AboutAlumniAdapter(this, alumni));
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
            case R.id.iv_insta:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/ssninstitutions")));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(AppInfoActivity.this);
    }
}
