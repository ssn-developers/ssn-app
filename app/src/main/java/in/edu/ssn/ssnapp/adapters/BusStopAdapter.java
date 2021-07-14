package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusRoute;
import in.edu.ssn.ssnapp.utils.SharedPref;

//this Adapter is Used by 'BusRouteAdapter' which is used in "BusRoutesActivity" to Show the bus routes.
public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.TimeLineViewHolder> {
    Typeface regular, bold;
    boolean darkMode = false;
    private List<String> stops;
    private List<String> time;
    private BusRoute model;
    private Context context;

    public BusStopAdapter(Context context, BusRoute model) {
        this.context = context;
        this.stops = model.getStop();
        this.time = model.getTime();
        this.model = model;
        regular = ResourcesCompat.getFont(context, R.font.open_sans);
        bold = ResourcesCompat.getFont(context, R.font.open_sans_bold);
        //get darkmode preference
        darkMode = SharedPref.getBoolean(context, "dark_mode");
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listitem;
        //check if the darkmode enabled and open respective Item layout.
        if (darkMode) {
            listitem = layoutInflater.inflate(R.layout.bus_stop_item_dark, parent, false);
        } else {
            listitem = layoutInflater.inflate(R.layout.bus_stop_item, parent, false);
        }
        return new TimeLineViewHolder(listitem, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        final String stop_data = stops.get(position);
        final String time_data = time.get(position);
        //stop name
        holder.titleTV.setText(stop_data);
        //stop timing
        holder.timeTV.setText(time_data);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        public TimelineView mTimelineView;
        public TextView titleTV, timeTV;
        public CardView busStopsCV;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);

            mTimelineView = itemView.findViewById(R.id.timeline);
            mTimelineView.initLine(viewType);
            titleTV = itemView.findViewById(R.id.text_timeline_title);
            timeTV = itemView.findViewById(R.id.text_timeline_date);
            busStopsCV = itemView.findViewById(R.id.busStopsCV);
        }
    }
}  
