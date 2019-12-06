package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import in.edu.ssn.ssnapp.adapters.BusRouteAdapter;
import in.edu.ssn.ssnapp.adapters.SubjectsAdapter;
import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.models.DepartmentSubjects;
import in.edu.ssn.ssnapp.models.Subject;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class GPACalculatorActivity extends BaseActivity {

    TextView gradeResult;
    ImageView backImage;
    RelativeLayout gpaOutput;
    ArrayList<Subject> subjects;
    SubjectsAdapter subjectsAdapter;
    CardView calculateGPA;
    RecyclerView subjectsList;
    DepartmentSubjects.SemesterSubjects semesterSubjects;
    int semester, dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(darkModeEnabled){
            setContentView(R.layout.activity_gpa_calculator_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        }
        else
            setContentView(R.layout.activity_gpa_calculator);

        new getDepartmentSubjects().execute();
    }

    private void initUI() {
        gradeResult = findViewById(R.id.gpaTV);
        backImage = findViewById(R.id.backIV);
        gpaOutput = findViewById(R.id.gpaRL);
        calculateGPA = findViewById(R.id.calculateCV);

        subjectsList = findViewById(R.id.subjectsRV);
        subjectsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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

    private void calculateAndDisplay() {
        float gpa=0;
        float totalCreditsGained = 0;
        float totalCredits = 0;
        for(int i=0; i<subjects.size(); i++) {
            totalCreditsGained += subjects.get(i).getGrade() * subjects.get(i).getCredits();
            totalCredits += subjects.get(i).getCredits();
        }

        gpa = totalCreditsGained/totalCredits;
        gpaOutput.setVisibility(View.VISIBLE);
        gradeResult.setText(String.format("%.2f", gpa));
    }

    public class getDepartmentSubjects extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            semester = getIntent().getIntExtra("sem",0);
            String department = SharedPref.getString(getApplicationContext(),"dept");
            switch (department){
                case "cse":
                    dept=0;
                    break;
                case "it":
                    dept=1;
                    break;
                case "ece":
                    dept=2;
                    break;
                case "eee":
                    dept=3;
                    break;
                case "bme":
                    dept=4;
                    break;
                case "che":
                    dept=5;
                    break;
                case "civ":
                    dept=6;
                    break;
                case "mec":
                    dept=7;
                    break;
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("subjects.json")));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                JSONArray arr = new JSONArray(sb.toString());
                JSONObject obj1 = (JSONObject) arr.get(dept);
                JSONArray arr1 = obj1.getJSONArray("sem");
                JSONObject obj2 = (JSONObject) arr1.get(semester);

                semesterSubjects = new Gson().fromJson(obj2.toString(), DepartmentSubjects.SemesterSubjects.class);
                if(semesterSubjects != null)
                    subjects = semesterSubjects.getSubjects();

                subjectsAdapter = new SubjectsAdapter(getApplicationContext(),subjects);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            initUI();
        }
    }

    /********************************************************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(GPACalculatorActivity.this);
    }
}
