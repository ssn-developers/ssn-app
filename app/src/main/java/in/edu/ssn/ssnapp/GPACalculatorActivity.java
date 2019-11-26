package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.edu.ssn.ssnapp.adapters.SubjectsAdapter;
import in.edu.ssn.ssnapp.models.Subject;

public class GPACalculatorActivity extends BaseActivity {

    TextView gradeResult;
    ImageView backImage;
    RelativeLayout gpaOutput;
    ArrayList<Subject> subjects;
    SubjectsAdapter subjectsAdapter;
    CardView calculateGPA;
    RecyclerView subjectsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpa_calculator_dark);

        darkModeEnabled=false;
        if(darkModeEnabled){
            setContentView(R.layout.activity_gpa_calculator_dark);
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        }
        else {
            setContentView(R.layout.activity_gpa_calculator);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }

        initUI();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        subjectsList.setLayoutManager(layoutManager);
        subjectsList.setAdapter(subjectsAdapter);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        calculateGPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndDisplay();
            }
        });

    }

    private void initUI()
    {
        gradeResult = findViewById(R.id.gpa_value);
        backImage = findViewById(R.id.gpa_back);
        gpaOutput = findViewById(R.id.gpa_output_layout);
        subjects = new ArrayList<>();
        subjects.add(new Subject("MA4566","Maths1",4));
        subjects.add(new Subject("MA4566","Theory of computation",3));
        subjects.add(new Subject("MA4566","Maths3",2));
        subjects.add(new Subject("MA4566","Maths1",4));
        subjects.add(new Subject("MA4566","Theory of computation",3));
        subjects.add(new Subject("MA4566","Maths3",2));
        subjects.add(new Subject("MA4566","Maths1",4));
        subjects.add(new Subject("MA4566","Theory of computation",3));
        subjects.add(new Subject("MA4566","Maths3",2));
        subjects.add(new Subject("MA4566","Maths1",4));
        subjects.add(new Subject("MA4566","Theory of computation",3));
        subjects.add(new Subject("MA4566","Maths3",2));
        subjectsAdapter = new SubjectsAdapter(getApplicationContext(),subjects);
        gpaOutput.setVisibility(View.INVISIBLE);
        calculateGPA = findViewById(R.id.calculate_gpa_card);
        subjectsList = findViewById(R.id.gpa_recycler_view);



    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void calculateAndDisplay()
    {
        float gpa=0;
        float totalCreditsGained=0;
        float totalCredits=0;
        for(int i=0;i<subjects.size();i++)
        {
            totalCreditsGained+=(float)subjects.get(i).getGrade() * subjects.get(i).getCredits();
            totalCredits+=(float)subjects.get(i).getCredits();
        }
        gpa = totalCreditsGained/totalCredits;
        gpaOutput.setVisibility(View.VISIBLE);
        if(gpa%1>0.00) {
            gradeResult.setText(String.format("%.2f", gpa));
        }else
        {
            gradeResult.setText(Integer.toString((int)gpa));
        }

    }
}
