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

    ImageView backIV;
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
        backIV = findViewById(R.id.backIV);
        rv_helpline = findViewById(R.id.helplineRV);

        helplineDetailsList = new ArrayList<>();
        populateHelplineList();
        helplineAdapter = new HelplineAdapter(this,helplineDetailsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        rv_helpline.setLayoutManager(layoutManager);
        rv_helpline.setNestedScrollingEnabled(false);
        rv_helpline.setAdapter(helplineAdapter);


        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void populateHelplineList() {
        helplineDetailsList.clear();
        helplineDetailsList.add(new HelplineDetails("Dr. S. Chitra","chitra@ssn.edu.in","Professor & HOD - CSE","chitra@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. T. Nagarajan","nagarajant@ssn.edu.in","Professor & HOD - IT","nagarajant@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. S. Radha","radhas@ssn.edu.in","Professor & HOD - ECE","radhas@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. V. Kamaraj","kamarajv@ssn.edu.in","Professor & HOD - EEE","kamarajv@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. V.E. Annamalai","annamalaive@ssn.edu.in","Professor & HOD - MECH","annamalaive@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. A. Kavitha","kavithaa@ssn.edu.in","Professor & HOD - BME","kavithaa@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. Ramanagopal","ramanagopal@ssn.edu.in","Professor & HOD - CIVIL","ramanagopals@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. R. Parthiban","parthibanr@ssn.edu.in","Professor & HOD - CHEM","parthibanr@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. S. Thiruvenkataswami","thiruvenkataswamis@ssn.edu.in","Professor & HOD - ENG","thiruvenkataswamis@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. A. Rajalakshmi","rajalakshmia@ssn.edu.in","Professor & HOD - PHY","rajalakshmia@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. V.S. Gayathri","gayathrivs@ssn.edu.in ","Professor & HOD - CHE","gayathrivs@ssn.edu.in "));
        helplineDetailsList.add(new HelplineDetails("Dr. G. Sethuraman","sethuramang@ssn.edu.in","Librarian","sethuramang@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. S. Nanda","nandas@ssn.edu.in","Student Counsellor","nandas@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. S. Narasimman","narasimmans@ssn.edu.in","COE","narasimmans@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. Sunita Nair","sunitanair@ssn.edu.in","Professor & Head - Student affairs","sunitanair@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Mr. Amit Tyagi","amittyagi@ssn.edu.in","Associate Director - Marketing & Head - CDC","amittyagi@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Mr. Arun Prakash","arunprakashsm@ssn.edu.in","Asst. Manager - Marketing & Alumni Relations","arunprakashsm@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. Balaji Ponnalagar","balajip@ssn.edu.in","Physical Director","balajip@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Mr. Gopalakrishnan","gopalakrishnanr@ssn.edu.in","Admin Manager","gopalakrishnanr@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Mr. Gopalan","gopalann@ssn.edu.in","Administrative officer","gopalann@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Mr. NP Rajesh","rajeshnp@ssn.edu.in","Head - Warden","rajeshnp@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Mr. Kannan","kannank@ssn.edu.in","Warden","kannank@ssn.edu.in"));
        helplineDetailsList.add(new HelplineDetails("Dr. Sachin","sachings@ssn.edu.in","Campus Doctor","sachings@ssn.edu.in"));
    }
}
