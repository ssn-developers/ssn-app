package in.edu.ssn.ssnapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.models.AdminDetails;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ViewAdminActivity extends AppCompatActivity {

    RecyclerView adminRV;
    FirestoreRecyclerAdapter adapter;
    ShimmerFrameLayout shimmer_view;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_admin);

        initUI();
        setupFireStore();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void initUI(){
        adminRV = findViewById(R.id.adminRV);
        iv_back = findViewById(R.id.iv_back);
        shimmer_view = findViewById(R.id.shimmer_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        adminRV.setLayoutManager(layoutManager);
        adminRV.setHasFixedSize(true);
    }

    void setupFireStore(){
        String dept = SharedPref.getString(getApplicationContext(),"dept");

        Query query = FirebaseFirestore.getInstance().collection("faculty").whereEqualTo("access","AD").whereEqualTo("dept",dept);
        FirestoreRecyclerOptions<AdminDetails> options = new FirestoreRecyclerOptions.Builder<AdminDetails>().setQuery(query, new SnapshotParser<AdminDetails>() {
            @NonNull
            @Override
            public AdminDetails parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);
                final AdminDetails post = new AdminDetails();
                post.setName(snapshot.getString("name"));
                String email = snapshot.getString("email");
                post.setEmail(email);
                post.setUrl(SharedPref.getString(getApplicationContext(),"faculty",email + "_dp_url"));
                return post;
            }
        }).build();

        adapter = new FirestoreRecyclerAdapter<AdminDetails, FeedViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder holder, int i, @NonNull AdminDetails adminDetails) {
                holder.tv_name.setText(adminDetails.getName());
                holder.tv_email.setText(adminDetails.getEmail());
                try {
                    if (adminDetails.getUrl() != null)
                        Picasso.get().load(adminDetails.getUrl()).placeholder(R.drawable.ic_user_white).into(holder.userImageIV);
                    else
                        holder.userImageIV.setImageResource(R.drawable.ic_user_white);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
                    holder.userImageIV.setImageResource(R.drawable.ic_user_white);
                }
                shimmer_view.setVisibility(View.GONE);
            }

            @Override
            public FeedViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.admin_details_item, group, false);
                return new FeedViewHolder(view);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }
        };

        adminRV.setAdapter(adapter);
    }

    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_email;
        public CircleImageView userImageIV;
        public View view;

        public FeedViewHolder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_email = itemView.findViewById(R.id.tv_email);
            userImageIV = itemView.findViewById(R.id.userImageIV);
            view = itemView.findViewById(R.id.view);
        }
    }

    /*********************************************************/

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
