package in.edu.ssn.ssnapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //TODO check for condition & delay-timer during release
        if(!SharedPref.getBoolean(getApplicationContext(),"is_logged_in")){
            db = FirebaseFirestore.getInstance();
            new updateFaculty().execute();
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            },500);
            //2000
        }
    }

    public class updateFaculty extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.collection("faculty").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        JSONArray fac_array = new JSONArray();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dp_url = (String) document.get("dp_url");
                            String access = (String) document.get("access");
                            String dept = (String) document.get("dept");
                            String email = (String) document.get("email");
                            String id = (String) document.get("id");
                            String name = (String) document.get("name");
                            String position = (String) document.get("position");

                            CommonUtils.saveData(access,dept,dp_url,email,id,name,position,document.getId(),fac_array);
                        }
                        CommonUtils.exportFile(getApplicationContext(), fac_array);
                        //Log.d("test_set",CommonUtils.getData(getApplicationContext()));
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    }
                }
            });

            return null;
        }
    }
}
