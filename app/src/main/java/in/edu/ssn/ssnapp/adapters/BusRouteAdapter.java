package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.utils.SharedPref;

//this Adapter is Used in BusRoutesActivity to Show the bus routes.
public class BusRouteAdapter extends RecyclerView.Adapter<BusRouteAdapter.BusRouteViewHolder> {

    boolean darkMode = false;
    private ArrayList<BusRoute> busRoutes;
    private Context context;

    public BusRouteAdapter(Context context, ArrayList<BusRoute> busRoutes) {
        this.context = context;
        this.busRoutes = busRoutes;
        //get darkmode preference
        darkMode = SharedPref.getBoolean(context, "dark_mode");
    }

    @NonNull
    @Override
    public BusRouteAdapter.BusRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        //check if the darkmode enabled and open respective Item layout.
        if (darkMode) {
            view = LayoutInflater.from(context).inflate(R.layout.bus_route_item_dark, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.bus_route_item, parent, false);
        }
        return new BusRouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusRouteAdapter.BusRouteViewHolder holder, int position) {
        //route number
        BusRoute busRoute = busRoutes.get(position);
        holder.routeNameTV.setText("Route " + busRoute.getName());

        //route details such as stops and timings
        //This is a nested Adapter.
        holder.busStopsRV.setAdapter(new BusStopAdapter(context, busRoute));
    }

    @Override
    public int getItemCount() {
        return busRoutes.size();
    }

    public class BusRouteViewHolder extends RecyclerView.ViewHolder {
        public TextView routeNameTV;
        public RecyclerView busStopsRV;
        public CardView busRouteCV;

        public BusRouteViewHolder(View convertView) {
            super(convertView);

            routeNameTV = convertView.findViewById(R.id.routeNameTV);
            busStopsRV = convertView.findViewById(R.id.busStopsRV);
            busRouteCV = convertView.findViewById(R.id.busRouteCV);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            busStopsRV.setHasFixedSize(true);
            busStopsRV.setLayoutManager(layoutManager);
        }
    }
}
