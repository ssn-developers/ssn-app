package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import in.edu.ssn.ssnapp.adapters.HelplineAdapter;
import in.edu.ssn.ssnapp.models.HelplineDetails;

public class HelplineActivity extends BaseActivity {

    ImageView iv_back;
    RecyclerView rv_helpline;

    HelplineAdapter helplineAdapter;
    ArrayList<HelplineDetails> helplineDetailsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(darkModeEnabled){
            setContentView(R.layout.activity_helpline_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        }else{
            setContentView(R.layout.activity_helpline);
        }

        initUI();
    }

    void initUI(){
        iv_back = findViewById(R.id.iv_back);
        rv_helpline = findViewById(R.id.helplineRV);

        helplineDetailsList = new ArrayList<>();
        populateHelplineList();
        helplineAdapter = new HelplineAdapter(this,helplineDetailsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rv_helpline.setLayoutManager(layoutManager);
        rv_helpline.setAdapter(helplineAdapter);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void populateHelplineList() {
        helplineDetailsList.add(new HelplineDetails("Lame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Mame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Name","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Oame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Pame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Qame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Rame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Same","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Tame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Uame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Vame","shrikanthravi.me@gmail.com","Position","Extn"));
        helplineDetailsList.add(new HelplineDetails("Wame","shrikanthravi.me@gmail.com","Position","Extn"));
    }
}
