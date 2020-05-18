package in.edu.ssn.ssnapp;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.adapters.HelplineAdapter;
import in.edu.ssn.ssnapp.models.FuncHeadDetails;
import spencerstudios.com.bungeelib.Bungee;

public class FunctionalHeadsActivity extends BaseActivity {

    ImageView backIV;
    RecyclerView helplineRV;

    HelplineAdapter helplineAdapter;
    ArrayList<FuncHeadDetails> funcHeadDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkModeEnabled) {
            setContentView(R.layout.activity_functional_heads_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        } else {
            setContentView(R.layout.activity_functional_heads);
        }

        initUI();
    }

    void initUI() {
        backIV = findViewById(R.id.backIV);
        helplineRV = findViewById(R.id.helplineRV);

        funcHeadDetailsList = new ArrayList<>();
        populateHelplineList();
        helplineAdapter = new HelplineAdapter(this, funcHeadDetailsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        helplineRV.setLayoutManager(layoutManager);
        helplineRV.setNestedScrollingEnabled(false);
        helplineRV.setAdapter(helplineAdapter);


        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void populateHelplineList() {
        funcHeadDetailsList.clear();
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. S. Chitra", "chitra@ssn.edu.in", "Professor & HOD - CSE", "chitra@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. T. Nagarajan", "nagarajant@ssn.edu.in", "Professor & HOD - IT", "nagarajant@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. S. Radha", "radhas@ssn.edu.in", "Professor & HOD - ECE", "radhas@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. V. Kamaraj", "kamarajv@ssn.edu.in", "Professor & HOD - EEE", "kamarajv@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. V.E. Annamalai", "annamalaive@ssn.edu.in", "Professor & HOD - MECH", "annamalaive@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. A. Kavitha", "kavithaa@ssn.edu.in", "Professor & HOD - BME", "kavithaa@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. Ramanagopal", "ramanagopal@ssn.edu.in", "Professor & HOD - CIVIL", "ramanagopals@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. R. Parthiban", "parthibanr@ssn.edu.in", "Professor & HOD - CHEM", "parthibanr@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. S. Thiruvenkataswami", "thiruvenkataswamis@ssn.edu.in", "Professor & HOD - ENG", "thiruvenkataswamis@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. A. Rajalakshmi", "rajalakshmia@ssn.edu.in", "Professor & HOD - PHY", "rajalakshmia@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. V.S. Gayathri", "gayathrivs@ssn.edu.in ", "Professor & HOD - CHE", "gayathrivs@ssn.edu.in "));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. G. Sethuraman", "sethuramang@ssn.edu.in", "Librarian", "sethuramang@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. S. Nanda", "nandas@ssn.edu.in", "Student Counsellor", "nandas@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. S. Narasimman", "narasimmans@ssn.edu.in", "COE", "narasimmans@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. Sunita Nair", "sunitanair@ssn.edu.in", "Professor & Head - Student affairs", "sunitanair@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. S.T. Jothi Basu", "placement@ssn.edu.in", "Manager - Placement", "placement@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. Balaji Ponnalagar", "balajip@ssn.edu.in", "Physical Director", "balajip@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Mr. Gopalakrishnan", "gopalakrishnanr@ssn.edu.in", "Admin Manager", "gopalakrishnanr@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Mr. Gopalan", "gopalann@ssn.edu.in", "Administrative officer", "gopalann@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Mr. N.P. Rajesh", "rajeshnp@ssn.edu.in", "Head - Warden", "rajeshnp@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Mr. Kannan", "kannank@ssn.edu.in", "Warden", "kannank@ssn.edu.in"));
        funcHeadDetailsList.add(new FuncHeadDetails("Dr. Sachin", "sachings@ssn.edu.in", "Campus Doctor", "sachings@ssn.edu.in"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(FunctionalHeadsActivity.this);
    }
}
