package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = FirebaseFirestore.getInstance();
        startActivity(new Intent(getApplicationContext(), BusRoutesActivity.class));
        //new updateFaculty().execute();
    }

    public class updateFaculty extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.collection("user").whereEqualTo("clearance",1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dp_url = (String) document.get("dp_url");
                            String access = (String) document.get("access");
                            String dept = (String) document.get("dept");
                            String email = (String) document.get("email");
                            String id = (String) document.get("id");
                            String name = (String) document.get("name");
                            String position = (String) document.get("position");

                            SharedPref.putString(getApplicationContext(),"faculty", id + "_access", access);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_dept", dept);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_dp_url", dp_url);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_email", email);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_name", name);
                            SharedPref.putString(getApplicationContext(),"faculty", id + "_position", position);
                        }

                        passIntent();
                    }
                    else
                        passIntent();
                }
            });

            return null;
        }
    }

    public void passIntent(){
        if(SharedPref.getBoolean(getApplicationContext(),"is_logged_in"))
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        else
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}
