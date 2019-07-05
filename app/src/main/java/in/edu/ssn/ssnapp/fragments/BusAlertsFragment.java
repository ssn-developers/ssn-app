package in.edu.ssn.ssnapp.fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

import in.edu.ssn.ssnapp.BusRoutesActivity;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusPost;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FontChanger;

/**
 * A simple {@link Fragment} subclass.
 */
public class BusAlertsFragment extends Fragment {

    public BusAlertsFragment() {}

    CardView busRoutesCV;
    CardView trackBusCV;
    RecyclerView alertRV;
    FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bus_alerts, container, false);
        initFonts(view);
        initUI(view);

        setupFireStore();

        busRoutesCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), BusRoutesActivity.class));
            }
        });

        trackBusCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    void setupFireStore(){
        Query query = FirebaseFirestore.getInstance().collection("post_bus").orderBy("time", Query.Direction.DESCENDING).limit(5);

        FirestoreRecyclerOptions<BusPost> options = new FirestoreRecyclerOptions.Builder<BusPost>()
                .setQuery(query, new SnapshotParser<BusPost>() {
                    @NonNull
                    @Override
                    public BusPost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        final BusPost busPost = new BusPost();
                        busPost.setTitle(snapshot.getString("title"));
                        busPost.setTime(snapshot.getTimestamp("time").toDate());
                        busPost.setUrl(snapshot.getString("url"));
                        return busPost;
                    }
                })
                .build();

        adapter = new FirestoreRecyclerAdapter<BusPost, BusAlertHolder>(options) {
            @Override
            public void onBindViewHolder(final BusAlertHolder holder, int position, final BusPost model) {
                holder.tv_date.setText("Bus routes from " + model.getTitle());

                Date time = model.getTime();
                Date now = new Date();
                Long t = now.getTime() - time.getTime();

                if(t < 60000)
                    holder.tv_time.setText(Long.toString(t / 1000) + "s ago");
                else if(t < 3600000)
                    holder.tv_time.setText(Long.toString(t / 60000) + "m ago");
                else if(t < 86400000)
                    holder.tv_time.setText(Long.toString(t / 3600000) + "h ago");
                else if(t < 604800000)
                    holder.tv_time.setText(Long.toString(t/86400000) + "d ago");
                else if(t < 2592000000L)
                    holder.tv_time.setText(Long.toString(t/604800000) + "w ago");
                else if(t < 31536000000L)
                    holder.tv_time.setText(Long.toString(t/2592000000L) + "M ago");
                else
                    holder.tv_time.setText(Long.toString(t/31536000000L) + "y ago");


                holder.rl_bus_alert_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i=new Intent(getContext(), PdfViewerActivity.class);
                        i.putExtra(Constants.PDF_URL,model.getUrl());
                        startActivity(i);
                    }
                });
            }

            @Override
            public BusAlertHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.bus_alert, group, false);
                return new BusAlertHolder(view);
            }
        };

        alertRV.setAdapter(adapter);
    }

    public class BusAlertHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_time;
        RelativeLayout rl_bus_alert_item;

        public BusAlertHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            rl_bus_alert_item=itemView.findViewById(R.id.bus_alert_item);
        }
    }

    void initUI(View view){
        busRoutesCV = view.findViewById(R.id.busRoutesCV);
        trackBusCV = view.findViewById(R.id.trackBusCV);
        alertRV = view.findViewById(R.id.alertRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        alertRV.setLayoutManager(layoutManager);
        alertRV.setHasFixedSize(true);
    }

    //Fonts
    public Typeface regular, bold, italic, bold_italic;
    private void initFonts(View view){
        regular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold.ttf");
        italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_italic.ttf");
        bold_italic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/product_sans_bold_italic.ttf");
        FontChanger fontChanger = new FontChanger(bold);
        //fontChanger.replaceFonts((ViewGroup) view);
    }
}