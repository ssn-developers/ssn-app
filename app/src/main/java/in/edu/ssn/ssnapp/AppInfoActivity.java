package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.adapters.AboutAlumniAdapter;
import in.edu.ssn.ssnapp.adapters.AboutContributorAdapter;
import in.edu.ssn.ssnapp.models.AlumniDetails;
import in.edu.ssn.ssnapp.models.TeamDetails;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import spencerstudios.com.bungeelib.Bungee;

public class AppInfoActivity extends BaseActivity implements View.OnClickListener {

    RecyclerView items1RV, items2RV, items3RV;
    ArrayList<TeamDetails> teams1, teams2;
    ArrayList<AlumniDetails> alumni;
    ImageView webIV, fbIV, twitIV, linkedinIV, instaIV;
    TextView textfbUrlTV, textinstaUrlTV, text6TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkModeEnabled) {
            setContentView(R.layout.activity_app_info_dark);
            clearLightStatusBar(this);
        } else
            setContentView(R.layout.activity_app_info);

        ScrollView scroll_view = findViewById(R.id.scroll_view);
        scroll_view.smoothScrollTo(0, 0);

        TextView versionTV = findViewById(R.id.versionTV);
        versionTV.setText("v" + BuildConfig.VERSION_NAME);

        items1RV = findViewById(R.id.items1RV);
        items2RV = findViewById(R.id.items2RV);
        items3RV = findViewById(R.id.items3RV);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);

        items1RV.setLayoutManager(layoutManager1);
        items1RV.setNestedScrollingEnabled(false);
        items2RV.setLayoutManager(layoutManager2);
        items2RV.setNestedScrollingEnabled(false);
        items3RV.setLayoutManager(layoutManager3);
        items3RV.setNestedScrollingEnabled(false);

        teams1 = new ArrayList<>();
        teams2 = new ArrayList<>();
        alumni = new ArrayList<>();

        webIV = findViewById(R.id.webIV);
        webIV.setOnClickListener(this);
        fbIV = findViewById(R.id.fbIV);
        fbIV.setOnClickListener(this);
        twitIV = findViewById(R.id.twitIV);
        twitIV.setOnClickListener(this);
        linkedinIV = findViewById(R.id.linkedinIV);
        linkedinIV.setOnClickListener(this);
        instaIV = findViewById(R.id.instaIV);
        instaIV.setOnClickListener(this);
        textfbUrlTV = findViewById(R.id.textfbUrlTV);
        textfbUrlTV.setOnClickListener(this);
        textinstaUrlTV = findViewById(R.id.textinstaUrlTV);
        textinstaUrlTV.setOnClickListener(this);

        text6TV = findViewById(R.id.text6TV);
        text6TV.setSelected(true);

        setUpPreFinalContributors();

        setUpFinalContributors();

        setUpAlumni();
    }

    void setUpPreFinalContributors() {
        ArrayList<String> url = new ArrayList<>();
        // Todo: Add pre final year students data here and set visibilty in xml
        items1RV.setAdapter(new AboutContributorAdapter(this, teams1));
    }

    void setUpFinalContributors() {
        ArrayList<String> url = new ArrayList<>();

        url.add("ezhilprasanth17040@cse.ssn.edu.in");
        url.add("https://github.com/ezhilnero99");
        url.add("https://www.linkedin.com/in/ezhilnero-m");
        teams2.add(new TeamDetails("Ezhil Prasanth M", "App Development", R.drawable.ezhil_profile, url));

        url = new ArrayList<>();
        url.add("pavikaramanchu@gmail.com");
        url.add("https://github.com/pavithrakarumanchi");
        url.add("https://www.linkedin.com/in/pavithra-karumanchi-0420b9166");
        teams2.add(new TeamDetails("Pavithra N", "Web Development", R.drawable.pavithra_profile, url));

        url = new ArrayList<>();
        url.add("nandhiniraja208@gmail.com");
        url.add("https://github.com/nandy20");
        url.add("https://www.linkedin.com/in/nandhini-raja-8b71b4143");
        teams2.add(new TeamDetails("Nandhini R", "Web Development", R.drawable.nandhini_profile, url));

        url = new ArrayList<>();
        url.add("lksujins@gmail.com");
        url.add("https://github.com/sujink1999");
        url.add("https://www.linkedin.com/in/sujin-kingselin");
        teams2.add(new TeamDetails("Sujin K", "App Development", R.drawable.sujin_profile, url));

        url = new ArrayList<>();
        url.add("amrithasudharsan@gmail.com");
        url.add("https://www.instagram.com/amrithahaha");
        url.add("https://www.linkedin.com/in/amrithasudharsan");
        teams2.add(new TeamDetails("Amritha Sudharsan", "App UI Designs", R.drawable.amirtha_profile, url));

        items2RV.setAdapter(new AboutContributorAdapter(this, teams2));
    }

    void setUpAlumni() {

        alumni.add(new AlumniDetails("Logesh Dinakaran", "ddlogesh@gmail.com"));
        alumni.add(new AlumniDetails("Harshavardhan P", "harshavardhan.zodiac@gmail.com"));
        alumni.add(new AlumniDetails("Shrikanth Ravi", "shrikanthravi.me@gmail.com"));
        alumni.add(new AlumniDetails("Tarun Ganesh K", "tarun.krithik@gmail.com"));

        alumni.add(new AlumniDetails("Karnik Ram", "karnikram@gmail.com"));
        alumni.add(new AlumniDetails("Adithya J", "adithya321@hotmail.com"));
        alumni.add(new AlumniDetails("Varun Ranganathan", "dvarunranganathan@gmail.com"));
        alumni.add(new AlumniDetails("Muthu Annamalai CT", "muthuct@outlook.com"));
        items3RV.setAdapter(new AboutAlumniAdapter(this, alumni));
    }

    @Override
    public void onClick(View v) {
        if (!CommonUtils.alerter(getApplicationContext())) {
            switch (v.getId()) {
                case R.id.webIV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "http://www.ssn.edu.in");
                    break;
                case R.id.fbIV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://www.facebook.com/SSNInstitution");
                    break;
                case R.id.twitIV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://twitter.com/ssninstitutions");
                    break;
                case R.id.linkedinIV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://www.linkedin.com/school/ssn-college-of-engineering");
                    break;
                case R.id.instaIV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://www.instagram.com/ssninstitutions");
                    break;
                case R.id.textfbUrlTV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://www.facebook.com/ssnceapp");
                    break;
                case R.id.textinstaUrlTV:
                    CommonUtils.openCustomBrowser(getApplicationContext(), "https://www.instagram.com/ssnce_app");
                    break;
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key", "appinfo");
            startActivity(intent);
            Bungee.fade(AppInfoActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(AppInfoActivity.this);
    }
}