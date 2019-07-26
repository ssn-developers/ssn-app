package in.edu.ssn.ssnapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.AdminDetails;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class AdminDetailsFragment extends Fragment {

    public AdminDetailsFragment() { }

    RecyclerView feedsRV;
    FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed , container, false);
        CommonUtils.initFonts(getContext(), view);
        initUI(view);

        setupFireStore();

        return view;
    }

    /*********************************************************/

    void setupFireStore(){
        String id = SharedPref.getString(getContext(),"id");
        String dept = SharedPref.getString(getContext(),"dept");
        //TODO: Needs to manually create composite query before release for each author. [VERY IMPORTANT]

        Query query = FirebaseFirestore.getInstance().collection("user").whereEqualTo("access","AD").whereEqualTo("dept",dept);
        FirestoreRecyclerOptions<AdminDetails> options = new FirestoreRecyclerOptions.Builder<AdminDetails>().setQuery(query, new SnapshotParser<AdminDetails>() {
            @NonNull
            @Override
            public AdminDetails parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                final AdminDetails post = new AdminDetails();
                post.setName(snapshot.getString("name"));
                post.setEmail(snapshot.getString("email"));
                return post;
            }
        })
        .build();

        adapter = new FirestoreRecyclerAdapter<AdminDetails, FeedViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int i, @NonNull AdminDetails adminDetails) {
                feedViewHolder.name_text.setTypeface(null, Typeface.BOLD_ITALIC);
                feedViewHolder.name_text.setText(adminDetails.getName());
                feedViewHolder.email_text.setText(adminDetails.getEmail());
            }

            @Override
            public FeedViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.admin_details_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        feedsRV.setAdapter(adapter);
    }

    void initUI(View view){
        feedsRV = view.findViewById(R.id.feedsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);
        feedsRV.setHasFixedSize(true);
    }

    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView name_text,email_text;

        public FeedViewHolder(View itemView) {
            super(itemView);

            name_text = itemView.findViewById(R.id.name_text);
            email_text = itemView.findViewById(R.id.email_text);

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