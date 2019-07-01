package in.edu.ssn.ssnapp;

import android.animation.Animator;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.common.util.NumberUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.adapters.BusStopAdapter;
import in.edu.ssn.ssnapp.models.BusRoute;
import io.opencensus.internal.StringUtils;

public class BusRoutesActivity extends BaseActivity {

    ImageView backIV;
    RecyclerView busRoutesRV;
    EditText et_num;
    FirestoreRecyclerAdapter adapter;
    LottieAnimationView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_routes);

        initUI();
        Query query = FirebaseFirestore.getInstance().collection("bus_route").orderBy("id");
        setupFireStore(query);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String val = s.toString().trim().toLowerCase();

                Query query = FirebaseFirestore.getInstance().collection("bus_route").whereEqualTo("name",val);
                setupFireStore(query);
                adapter.startListening();
            }
        });

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupFireStore();
            }
        },1500);*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.startListening();
            }
        },1500);*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    void initUI(){
        backIV = findViewById(R.id.backIV);
        et_num = findViewById(R.id.et_num);
        busRoutesRV = findViewById(R.id.busRoutesRV);
        loadingView = findViewById(R.id.animation_view);
        loadingView.setSpeed(2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        busRoutesRV.setLayoutManager(layoutManager);
        busRoutesRV.setHasFixedSize(true);
        //changeFont(bold,(ViewGroup)this.findViewById(android.R.id.content));
    }

    void setupFireStore(Query query){
        FirestoreRecyclerOptions<BusRoute> options = new FirestoreRecyclerOptions.Builder<BusRoute>().setQuery(query, new SnapshotParser<BusRoute>() {
            @NonNull
            @Override
            public BusRoute parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                BusRoute busRoute = new BusRoute();
                busRoute.setRouteName(snapshot.getString("name"));
                busRoute.setAvail(snapshot.getBoolean("avail"));
                busRoute.setDname(snapshot.getString("dname"));
                busRoute.setDphone(snapshot.getString("dphone"));

                List<String> stops = (List<String>) snapshot.get("stop");
                List<String> time = (List<String>) snapshot.get("time");
                busRoute.setStop(stops);
                busRoute.setTime(time);

                return busRoute;
            }
        }).build();

        adapter = new FirestoreRecyclerAdapter<BusRoute, BusRouteViewHolder>(options) {
            @Override
            public void onBindViewHolder(BusRouteViewHolder holder, int position, final BusRoute model) {
                holder.routeNameTV.setText("Route " + model.getRouteName());
                holder.busStopsRV.setAdapter(new BusStopAdapter(getApplicationContext(),model.getStop(),model.getTime()));
                holder.busRouteCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),BusRouteDetailsActivity.class);
                        intent.putExtra("route",model);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public BusRouteViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.bus_route_item, group, false);
                return new BusRouteViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                // Called each time there is a new query snapshot. You may want to use this method
                // to hide a loading spinner or check for the "no documents" state and update your UI.
                loadingView.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).alpha(0).setDuration(250).start();
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                // Called when there is an error getting a query snapshot. You may want to update
                // your UI to display an error message to the user.
                loadingView.animate().setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).alpha(0).setDuration(250).start();
            }
        };
        busRoutesRV.setAdapter(adapter);
    }

    public class BusRouteViewHolder extends RecyclerView.ViewHolder {
        public TextView routeNameTV;
        public RecyclerView busStopsRV;
        public CardView busRouteCV;

        public BusRouteViewHolder(View itemView) {
            super(itemView);

            routeNameTV = itemView.findViewById(R.id.routeNameTV);
            busStopsRV = itemView.findViewById(R.id.busStopsRV);
            busRouteCV = itemView.findViewById(R.id.busRouteCV);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
            busStopsRV.setHasFixedSize(true);
            busStopsRV.setLayoutManager(layoutManager);
        }
    }
}