package in.edu.ssn.ssnapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String TAG = "test_set";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*db = FirebaseFirestore.getInstance();

        db.collection("bus_route").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //String dname = (String) document.get("dname");
                        GeoPoint cur_gp = (GeoPoint) document.get("loc");
                        //Double.toString(cur_gp.getLatitude())

                        ArrayList<Map<Object,Object>> ap = (ArrayList<Map<Object,Object>>) document.get("stop");
                        for(Map<Object,Object> mp : ap){
                            //String time = (String)mp.get("time");
                            //String place = (String)mp.get("place");
                            //GeoPoint pl_gp = (GeoPoint) mp.get("loc");
                        }
                    }
                }
            }
        });

        db.collection("post_bus").document("alert").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot ds = task.getResult();
                    //String avail = (String) ds.get("avail");
                    //String title = (String) ds.get("title");
                    //String url = (String) ds.get("url");
                    //Timestamp time = (Timestamp) ds.get("time");
                    //Date d = time.toDate();
                }
            }
        });*/
    }
}

/*
        ArrayList<Object> arr = new ArrayList<>();
        Map<String, Object> city = new HashMap<>();
        city.put("avail", true);
        city.put("dname", "Muthu");
        city.put("dphone", "9934512345");
        city.put("loc", new GeoPoint(1.0,4.0));
        String val[]={"Ford BS","Maraimalai Nagar BS","Katangalathur","Potheri","Guduvanchery","EB","Urapakkam Teakadai","Urapakkam School","Vandalur Zoo","Kolapakkam","Tagore Engg College","Kandigai","Mambakkam","Pudupakkam","Kelambakkam","College"};
        String tim[]={"06:40","06:42","06:44","06:47","06:50","06:52","06:53","06:55","07:00","07:05","07:10","07:15","07:20","07:25","07:30","07:40"};
        for(int i=0; i < val.length; i++) {
            Map<String, Object> mp = new HashMap<>();
            mp.put("loc", new GeoPoint(1.0, 4.0));
            mp.put("place", val[i]);
            mp.put("time", tim[i]);
            arr.add(mp);
        }
        city.put("stop", arr);
        db.collection("bus_route").document("43").set(city);
*/