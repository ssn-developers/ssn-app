package in.edu.ssn.ssnapp;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import in.edu.ssn.ssnapp.adapters.ViewPagerAdapter;
import in.edu.ssn.ssnapp.fragments.AdminDetailsFragment;
import in.edu.ssn.ssnapp.fragments.FacultyFeedFragment;

public class ViewAdminActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usercolref = db.collection("user");
    RecyclerView feedsRV;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_admin);

        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager) ;

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AdminDetailsFragment(), "Feed");
        viewPager.setAdapter(adapter);

    }
}
