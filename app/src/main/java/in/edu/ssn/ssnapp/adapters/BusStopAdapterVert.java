package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.BusRoute;

public class BusStopAdapterVert extends RecyclerView.Adapter<BusStopAdapterVert.TimeLineViewHolder>{
    private List<BusRoute.stop> stops;
    private Context context;
    Typeface regular,bold;
    // RecyclerView recyclerView;
    public BusStopAdapterVert(Context contex, List<BusRoute.stop> stops) {
        this.context = contex;
        this.stops = stops;
        regular = Typeface.createFromAsset(context.getAssets(), "fonts/product_san_regular.ttf");
        bold = Typeface.createFromAsset(context.getAssets(), "fonts/product_sans_bold.ttf");
    }
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.bus_stop_item_vert, parent, false);
        return new TimeLineViewHolder(listItem,viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        final BusRoute.stop data = stops.get(position);
        holder.timeTV.setText(data.getTime());
        holder.titleTV.setText(data.getPlace());
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
            //titleTV.setTypeface(bold);
            //timeTV.setTypeface(regular);
        }
    }
}  
