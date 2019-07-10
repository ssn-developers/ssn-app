package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import in.edu.ssn.ssnapp.adapters.BusStopAdapterVert;
import in.edu.ssn.ssnapp.models.BusRoute;

public class BusRouteDetailsActivity extends BaseActivity {

     BusRoute busRoute;
     ImageView backIV;
     TextView routeNameTV, driverNameTV, driverNumberTV;
     RecyclerView busStopsRV;
     CardView callCard,shareCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route_details);

        initUI();

        busRoute = getIntent().getParcelableExtra("route");
        routeNameTV.setText("Route " + busRoute.getRouteName());
        driverNameTV.setText(busRoute.getDname());
        driverNameTV.setTypeface(regular);
        driverNumberTV.setText(busRoute.getDphone());
        driverNumberTV.setTypeface(regular);
        busStopsRV.setAdapter(new BusStopAdapterVert(getApplicationContext(), busRoute.getStop(), busRoute.getTime()));

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        callCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + busRoute.getDphone()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        shareCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Bus route: " + busRoute.getRouteName() + "\nDriver: " + busRoute.getDname() + "\nContact: " + busRoute.getDphone();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    void initUI(){
        backIV = findViewById(R.id.backIV);
        routeNameTV = findViewById(R.id.routeNameTV);
        driverNameTV = findViewById(R.id.driverNameTV);
        driverNumberTV = findViewById(R.id.driverNumberTV);
        callCard = findViewById(R.id.callCard);
        shareCard = findViewById(R.id.shareCard);

        busStopsRV = findViewById(R.id.busStopsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        busStopsRV.setLayoutManager(layoutManager);
        busStopsRV.setNestedScrollingEnabled(false);
    }


}
