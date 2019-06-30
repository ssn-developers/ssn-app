package in.edu.ssn.ssnapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import in.edu.ssn.ssnapp.adapters.BusStopAdapterVert;
import in.edu.ssn.ssnapp.models.BusRoute;

public class BusRouteDetailsActivity extends BaseActivity {

     BusRoute busRoute;
     ImageView backIV;
     TextView routeNameTV, driverNameTV, driverNumberTV;
     RecyclerView busStopsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route_details);

        initUI();

        busRoute = getIntent().getParcelableExtra("route");
        routeNameTV.setText("Route "+busRoute.getRouteName());
        driverNameTV.setText(busRoute.getDname());
        driverNameTV.setTypeface(regular);
        driverNumberTV.setText(busRoute.getDphone());
        driverNumberTV.setTypeface(regular);
        busStopsRV.setAdapter(new BusStopAdapterVert(getApplication(),busRoute.getStop()));

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    void initUI(){
        //changeFont(bold,(ViewGroup)this.findViewById(android.R.id.content));
        backIV = findViewById(R.id.backIV);
        routeNameTV = findViewById(R.id.routeNameTV);
        driverNameTV = findViewById(R.id.driverNameTV);
        driverNumberTV = findViewById(R.id.driverNumberTV);
        busStopsRV = findViewById(R.id.busStopsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        busStopsRV.setLayoutManager(layoutManager);
        busStopsRV.setNestedScrollingEnabled(false);
    }


}
