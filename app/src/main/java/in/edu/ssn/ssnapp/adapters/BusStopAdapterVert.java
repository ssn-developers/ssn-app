package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusRoute;

public class BusStopAdapterVert extends RecyclerView.Adapter<BusStopAdapterVert.TimeLineViewHolder>{
    private List<String> stops;
    private List<String> time;
    private Context context;
    Typeface regular,bold;

    public BusStopAdapterVert(Context contex, List<String> stops, List<String> time) {
        this.context = contex;
        this.stops = stops;
        this.time = time;
        regular = ResourcesCompat.getFont(context, R.font.open_sans);
        bold = ResourcesCompat.getFont(context, R.font.open_sans_bold);
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.bus_stop_item_vert, parent, false);
        return new TimeLineViewHolder(listItem,viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        final String stop_data = stops.get(position);
        final String time_data = time.get(position);
        holder.titleTV.setText(stop_data);
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
        public  TimelineView mTimelineView;
        public TextView titleTV,timeTV;

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);

            mTimelineView = itemView.findViewById(R.id.timeline);
            mTimelineView.initLine(viewType);
            titleTV = itemView.findViewById(R.id.text_timeline_title);
            timeTV = itemView.findViewById(R.id.text_timeline_date);
        }
    }
}  
