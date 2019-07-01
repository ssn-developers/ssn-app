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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.edu.ssn.ssnapp.adapters.BusStopAdapter;
import in.edu.ssn.ssnapp.models.BusRoute;

public class BusRoutesActivity extends BaseActivity {

    ImageView backIV;
    RecyclerView busRoutesRV;
    FirestoreRecyclerAdapter adapter;
    LottieAnimationView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_routes);

        initUI();
        setupFireStore();

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
        busRoutesRV = findViewById(R.id.busRoutesRV);
        loadingView = findViewById(R.id.animation_view);
        loadingView.setSpeed(2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        busRoutesRV.setLayoutManager(layoutManager);
        busRoutesRV.setHasFixedSize(true);
        //changeFont(bold,(ViewGroup)this.findViewById(android.R.id.content));
    }

    void setupFireStore(){

        Query query = FirebaseFirestore.getInstance()
                .collection("bus_route")
                .orderBy("id");

        FirestoreRecyclerOptions<BusRoute> options = new FirestoreRecyclerOptions.Builder<BusRoute>()
                .setQuery(query, new SnapshotParser<BusRoute>() {
                    @NonNull
                    @Override
                    public BusRoute parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        BusRoute busRoute = new BusRoute();
                        busRoute.setRouteName(snapshot.getString("name"));
                        busRoute.setAvail(snapshot.getBoolean("avail"));
                        busRoute.setDname(snapshot.getString("dname"));
                        busRoute.setDphone(snapshot.getString("dphone"));
                        List<BusRoute.stop> stops = new ArrayList<>();
                        List<Map<String,String>> temp = (List<Map<String, String>>) snapshot.get("stop");
                        for(int i=0;i<temp.size();i++){
                            BusRoute.stop stop = new BusRoute.stop();
                            stop.setPlace(temp.get(i).get("place"));
                            stop.setTime(temp.get(i).get("time"));
                            stops.add(stop);
                        }
                        busRoute.setStop(stops);
                        //Log.d("PARSE CHECK", String.valueOf(busRoute.getStop().get(0)));

                        return busRoute;
                    }
                })
                .build();

        adapter = new FirestoreRecyclerAdapter<BusRoute, BusRouteViewHolder>(options) {
            @Override
            public void onBindViewHolder(BusRouteViewHolder holder, int position, final BusRoute model) {
                holder.routeNameTV.setText("Route "+model.getRouteName());
                holder.busStopsRV.setAdapter(new BusStopAdapter(getApplicationContext(),model.getStop()));
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

                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.bus_route_item, group, false);

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
            //routeNameTV.setTypeface(bold);
            busStopsRV = itemView.findViewById(R.id.busStopsRV);
            busRouteCV = itemView.findViewById(R.id.busRouteCV);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
            busStopsRV.setHasFixedSize(true);
            busStopsRV.setLayoutManager(layoutManager);
        }
    }

}