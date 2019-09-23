package in.edu.ssn.ssnapp.fragments;

import android.content.Intent;
import android.graphics.Color;
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

import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
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

import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.BusPost;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class FacultySentBusPostFragment extends Fragment {

    public FacultySentBusPostFragment() { }

    RecyclerView feedsRV;
    RelativeLayout layout_progress;
    ShimmerFrameLayout shimmer_view;
    FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sent_bus_feed, container, false);
        CommonUtils.initFonts(getContext(), view);
        initUI(view);

        setupFireStore();

        return view;
    }

    /*********************************************************/

    void setupFireStore(){
        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_post_bus).orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<BusPost> options = new FirestoreRecyclerOptions.Builder<BusPost>().setQuery(query, new SnapshotParser<BusPost>() {
                    @NonNull
                    @Override
                    public BusPost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        shimmer_view.setVisibility(View.VISIBLE);
                        layout_progress.setVisibility(View.GONE);

                        final BusPost busPost = new BusPost();
                        busPost.setTitle(snapshot.getString("title"));
                        busPost.setTime(snapshot.getTimestamp("time").toDate());
                        busPost.setUrl(snapshot.getString("url"));
                        busPost.setDesc(snapshot.getString("desc"));
                        return busPost;
                    }
                })
                .build();

        adapter = new FirestoreRecyclerAdapter<BusPost, FacultySentBusPostFragment.BusAlertHolder>(options) {
            @Override
            public void onBindViewHolder(final FacultySentBusPostFragment.BusAlertHolder holder, int position, final BusPost model) {
                holder.tv_date.setText("Bus routes from " + model.getTitle());
                holder.tv_desc.setText(model.getDesc());
                holder.tv_time.setText(CommonUtils.getTime(model.getTime()));


                holder.rl_bus_alert_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getContext(), PdfViewerActivity.class);
                        i.putExtra(Constants.PDF_URL,model.getUrl());
                        startActivity(i);
                        Bungee.fade(getContext());
                    }
                });
                shimmer_view.setVisibility(View.GONE);
            }

            @Override
            public FacultySentBusPostFragment.BusAlertHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.bus_alert, group, false);
                return new BusAlertHolder(view);
            }
        };

        feedsRV.setAdapter(adapter);
    }

    void initUI(View view){
        feedsRV = view.findViewById(R.id.feedsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);

        shimmer_view = view.findViewById(R.id.shimmer_view);
        layout_progress = view.findViewById(R.id.layout_progress);
    }

    /*********************************************************/

    public class BusAlertHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_time, tv_desc;
        RelativeLayout rl_bus_alert_item;

        public BusAlertHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            rl_bus_alert_item=itemView.findViewById(R.id.bus_alert_item);
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