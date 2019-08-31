package in.edu.ssn.ssnapp;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.adapters.BusStopAdapter;
import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import io.opencensus.internal.StringUtils;
import pl.droidsonroids.gif.GifImageView;
import spencerstudios.com.bungeelib.Bungee;

public class BusRoutesActivity extends BaseActivity implements TextWatcher {

    ImageView backIV;
    RecyclerView busRoutesRV;
    EditText et_num;
    ImageView clearIV;
    AppBarLayout searchRL;
    LinearLayout searchRL1;
    RelativeLayout layout_empty;
    ChipGroup chipCloud;
    FirestoreRecyclerAdapter adapter;
    List<String> suggestions;
    ShimmerFrameLayout shimmer_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_routes);

        initUI();
        getSuggestions();
        Query query = FirebaseFirestore.getInstance().collection("bus_route").orderBy("id");
        setupFireStore(query);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        clearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_num.setText("");
            }
        });
    }

    /*********************************************************/

    void initUI(){
        backIV = findViewById(R.id.backIV);
        et_num = findViewById(R.id.et_num);     et_num.addTextChangedListener(this);
        busRoutesRV = findViewById(R.id.busRoutesRV);
        chipCloud = findViewById(R.id.chipCloud);
        clearIV = findViewById(R.id.clearIV);
        searchRL = findViewById(R.id.searchRL);
        layout_empty = findViewById(R.id.layout_empty);
        searchRL1 = findViewById(R.id.searchRL1);
        searchRL.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        searchRL1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        busRoutesRV.setLayoutManager(layoutManager);
        busRoutesRV.setHasFixedSize(true);
        shimmer_view = findViewById(R.id.shimmer_view);
        shimmer_view.setVisibility(View.VISIBLE);

        suggestions = new ArrayList<>();
    }

    private Chip getChip(final ChipGroup entryChipGroup, String text) {
        final Chip chip = new Chip(this);
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.suggestion_item));
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(text);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryChipGroup.removeView(chip);
            }
        });
        return chip;
    }

    void getSuggestions(){
        final Set<String> linkedHashSet = new LinkedHashSet<>();
        FirebaseFirestore.getInstance().collection("bus_route").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot ds:queryDocumentSnapshots){
                    ArrayList<String> stop = (ArrayList<String>) ds.get("stop");
                    for(String s:stop){
                        linkedHashSet.add(s);
                    }
                }
                suggestions.clear();
                for(String s: linkedHashSet){
                    suggestions.add(s);
                }
            }
        });
    }

    void setupFireStore(Query query){
        FirestoreRecyclerOptions<BusRoute> options = new FirestoreRecyclerOptions.Builder<BusRoute>().setQuery(query, new SnapshotParser<BusRoute>() {
            @NonNull
            @Override
            public BusRoute parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                BusRoute busRoute = new BusRoute();
                busRoute.setRouteName(snapshot.getString("name"));

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
                holder.busStopsRV.setAdapter(new BusStopAdapter(getApplicationContext(),model));
                /*holder.busRouteCV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(),BusRouteDetailsActivity.class);
                        intent.putExtra("route",model);
                        startActivity(intent);
                    }
                });*/
                shimmer_view.setVisibility(View.GONE);
            }

            @Override
            public BusRouteViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.bus_route_item, group, false);
                return new BusRouteViewHolder(view);
            }
        };

        busRoutesRV.setAdapter(adapter);
    }

    /*********************************************************/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        busRoutesRV.setVisibility(View.VISIBLE);
        layout_empty.setVisibility(View.GONE);

        String val = s.toString().trim().toLowerCase();
        Query query;
        if (val.equals("")) {
            clearIV.setVisibility(View.GONE);
            chipCloud.removeAllViews();

            shimmer_view.setVisibility(View.VISIBLE);
            query = FirebaseFirestore.getInstance().collection("bus_route").orderBy("id");
            setupFireStore(query);
            adapter.startListening();
        }
        else if (Character.isDigit(val.charAt(0))) {
            clearIV.setVisibility(View.VISIBLE);
            chipCloud.removeAllViews();

            shimmer_view.setVisibility(View.VISIBLE);
            if (val.equalsIgnoreCase("9a"))
                query = FirebaseFirestore.getInstance().collection("bus_route").whereEqualTo("name", "9A");
            else if (val.equalsIgnoreCase("30a"))
                query = FirebaseFirestore.getInstance().collection("bus_route").whereEqualTo("name", "30A");
            else
                query = FirebaseFirestore.getInstance().collection("bus_route").whereEqualTo("name", val);

            setupFireStore(query);
            adapter.startListening();
        }
        else {
            clearIV.setVisibility(View.VISIBLE);
            if (val.length() > 2) {
                chipCloud.removeAllViews();
                chipCloud.setVisibility(View.VISIBLE);

                for (int i = 0; i < suggestions.size(); i++) {
                    if (suggestions.get(i).toLowerCase().contains(val.toLowerCase())) {
                        final Chip chip = getChip(chipCloud, suggestions.get(i));
                        chipCloud.addView(chip);
                        final int finalI = i;

                        chip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonUtils.hideKeyboard(BusRoutesActivity.this);
                                chipCloud.removeAllViews();

                                et_num.removeTextChangedListener(BusRoutesActivity.this);
                                et_num.setText(suggestions.get(finalI));
                                et_num.addTextChangedListener(BusRoutesActivity.this);

                                shimmer_view.setVisibility(View.VISIBLE);
                                Query query = FirebaseFirestore.getInstance().collection("bus_route").whereArrayContains("stop", suggestions.get(finalI));
                                setupFireStore(query);
                                adapter.startListening();
                            }
                        });
                    }
                }
                if(chipCloud.getChildCount() == 0){
                    CommonUtils.hideKeyboard(BusRoutesActivity.this);
                    busRoutesRV.setVisibility(View.GONE);
                    layout_empty.setVisibility(View.VISIBLE);
                }
                else {
                    busRoutesRV.setVisibility(View.VISIBLE);
                    layout_empty.setVisibility(View.GONE);
                }
            }
            else {
                chipCloud.removeAllViews();
            }
        }
    }

    /*********************************************************/

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /*********************************************************/

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(BusRoutesActivity.this);
    }
}