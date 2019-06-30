package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

public class GistActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String TAG = "test_set";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gist);

        db = FirebaseFirestore.getInstance();

        /*

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
        });

        */
    }
}

/* Add bus route
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

/* Add faculty
        Map<String, Object> city = new HashMap<>();

        String id="F0006";
        city.put("name", "Dr. T.T.Mirnalinee");
        city.put("dp_url", "https://firebasestorage.googleapis.com/v0/b/ssn-app-web.appspot.com/o/faculty%2Fprofile%2FF0006.jpg?alt=media&token=ecafde15-96f7-4810-8ea2-43a035f72df6");
        city.put("email", " mirnalineett@ssn.edu.in");

        city.put("position", "Professor");

        city.put("id", id);
        city.put("dept", "cse");
        db.collection("faculty").document(id).set(city);
*/

/*  Add bus post, provided file should be present in assets directory
        ArrayList<Object> arr = new ArrayList<>();
        arr.add("1");
        Map<String, Object> city = new HashMap<>();

        city.put("desc", "For Faculty");
        city.put("title", "05-07-2019");
        city.put("time", FieldValue.serverTimestamp());
        city.put("avail", arr);
        db.collection("post_bus").add(city).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                final String id =task.getResult().getId();
                String filename = "Bus-Routes-from-24-06-19-onwards.pdf";

                final StorageReference mountainsRef = FirebaseStorage.getInstance().getReference().child("post_bus/"+ id +"/" + filename);
                try {
                    InputStream stream = getAssets().open(filename);
                    UploadTask uploadTask = mountainsRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            Log.d(TAG,"done123");
                            return mountainsRef.getDownloadUrl();
                        }}).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String downloadUrl= task.getResult().toString();
                                Log.d(TAG, downloadUrl);
                                Map<String, Object> city = new HashMap<>();
                                city.put("url", downloadUrl);
                                db.collection("post_bus").document(id).set(city, SetOptions.merge());
                            }
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG,e.getMessage());
                }

            }
        });
*/