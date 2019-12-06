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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import in.edu.ssn.ssnapp.adapters.BusRouteAdapter;
import in.edu.ssn.ssnapp.adapters.SubjectsAdapter;
import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.models.DepartmentSubjects;
import in.edu.ssn.ssnapp.models.Subject;
import spencerstudios.com.bungeelib.Bungee;

public class GPACalculatorActivity extends BaseActivity {

    TextView gradeResult;
    ImageView backImage;
    RelativeLayout gpaOutput;
    ArrayList<Subject> subjects;
    SubjectsAdapter subjectsAdapter;
    CardView calculateGPA;
    RecyclerView subjectsList;
    ArrayList<DepartmentSubjects> departmentSubjects;
    int semester;

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

        semester = getIntent().getIntExtra("sem",0);
        new getDepartmentSubjects().execute();
    }

    private void initUI() {
        gradeResult = findViewById(R.id.gpaTV);
        backImage = findViewById(R.id.backIV);
        gpaOutput = findViewById(R.id.gpaRL);
        calculateGPA = findViewById(R.id.calculateCV);
        subjectsList = findViewById(R.id.subjectsRV);
        if(departmentSubjects != null)
            subjects = departmentSubjects.get(0).getSem().get(semester).getSubjects();

        subjectsAdapter = new SubjectsAdapter(getApplicationContext(),subjects);
    }

    private void calculateAndDisplay() {
        float gpa=0;
        float totalCreditsGained=0;
        float totalCredits=0;
        for(int i=0;i<subjects.size();i++) {
            totalCreditsGained+=(float)subjects.get(i).getGrade() * subjects.get(i).getCredits();
            totalCredits+=(float)subjects.get(i).getCredits();
        }

        gpa = totalCreditsGained/totalCredits;
        gpaOutput.setVisibility(View.VISIBLE);
        if(gpa%1 > 0.00)
            gradeResult.setText(String.format("%.2f", gpa));
        else
            gradeResult.setText(Integer.toString((int)gpa));
    }

    public class getDepartmentSubjects extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("subjects.json")));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                final Set<String> linkedHashSet = new LinkedHashSet<>();
                JSONArray arr = new JSONArray(sb.toString());
                departmentSubjects = new ArrayList<>();

                for(int i=0; i<arr.length(); i++){
                    JSONObject obj = (JSONObject) arr.get(i);
                    DepartmentSubjects departmentSubject = new Gson().fromJson(obj.toString(), DepartmentSubjects.class);
                    departmentSubjects.add(departmentSubject);
                }
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
    }

    /********************************************************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(GPACalculatorActivity.this);
    }
}
