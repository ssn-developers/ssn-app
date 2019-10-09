package in.edu.ssn.ssnapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collections;
import java.util.Date;

import in.edu.ssn.ssnapp.BusRoutesActivity;
import in.edu.ssn.ssnapp.FacultyHomeActivity;
import in.edu.ssn.ssnapp.NoNetworkActivity;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusPost;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class BusAlertsFragment extends Fragment {

    public BusAlertsFragment() {}

    CardView busRoutesCV;
    RecyclerView alertRV;
    ShimmerFrameLayout shimmer_view;
    FirestoreRecyclerAdapter adapter;
    boolean darkMode=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        darkMode = SharedPref.getBoolean(getActivity().getApplicationContext(),"darkMode");
        View view;
        if(darkMode){
            view = inflater.inflate(R.layout.fragment_bus_alerts_dark, container, false);
        }else{
            view = inflater.inflate(R.layout.fragment_bus_alerts, container, false);
        }
        CommonUtils.initFonts(getContext(),view);
        initUI(view);

        setupFireStore();

        busRoutesCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), BusRoutesActivity.class));
                Bungee.slideLeft(getContext());
            }
        });

        return view;
    }

    /*********************************************************/

    void setupFireStore(){
        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_post_bus).orderBy("time", Query.Direction.DESCENDING).limit(5);

        FirestoreRecyclerOptions<BusPost> options = new FirestoreRecyclerOptions.Builder<BusPost>().setQuery(query, new SnapshotParser<BusPost>() {
            @NonNull
            @Override
            public BusPost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                final BusPost busPost = new BusPost();
                busPost.setTitle(snapshot.getString("title"));
                busPost.setTime(snapshot.getTimestamp("time").toDate());
                busPost.setUrl(snapshot.getString("url"));
                busPost.setDesc(snapshot.getString("desc"));
                return busPost;
            }
        })
        .build();

        adapter = new FirestoreRecyclerAdapter<BusPost, BusAlertHolder>(options) {
            @Override
            public void onBindViewHolder(final BusAlertHolder holder, int position, final BusPost model) {
                holder.tv_date.setText("Bus routes from " + model.getTitle());
                holder.tv_desc.setText(model.getDesc());
                holder.tv_time.setText(CommonUtils.getTime(model.getTime()));

                holder.rl_bus_alert_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!CommonUtils.alerter(getContext())) {
                            Intent i = new Intent(getContext(), PdfViewerActivity.class);
                            i.putExtra(Constants.PDF_URL, model.getUrl());
                            startActivity(i);
                            Bungee.fade(getContext());
                        }
                        else{
                            Intent intent = new Intent(getContext(), NoNetworkActivity.class);
                            intent.putExtra("key","home");
                            startActivity(intent);
                            Bungee.fade(getContext());
                        }
                    }
                });
                shimmer_view.setVisibility(View.GONE);
            }

            @Override
            public BusAlertHolder onCreateViewHolder(ViewGroup group, int i) {
                View view;
                if(darkMode){
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.bus_alert_dark, group, false);
                }else{
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.bus_alert, group, false);
                }

                return new BusAlertHolder(view);
            }
        };

        alertRV.setAdapter(adapter);

        alertRV.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                alertRV.scrollToPosition(0);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    void initUI(View view){
        busRoutesCV = view.findViewById(R.id.busRoutesCV);
        alertRV = view.findViewById(R.id.alertRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        alertRV.setLayoutManager(layoutManager);

        shimmer_view = view.findViewById(R.id.shimmer_view);
        shimmer_view.setVisibility(View.VISIBLE);
    }

    /*********************************************************/

    public class BusAlertHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_time, tv_desc;
        LinearLayout rl_bus_alert_item;

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
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}