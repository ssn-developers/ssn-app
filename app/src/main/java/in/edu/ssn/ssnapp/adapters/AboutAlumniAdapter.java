package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.AlumniDetails;

public class AboutAlumniAdapter extends RecyclerView.Adapter<AboutAlumniAdapter.ContributionViewHolder>{

    private ArrayList<AlumniDetails> alumniDetails;
    private Context context;
    private TextDrawable.IBuilder builder;

    public AboutAlumniAdapter(Context context, ArrayList<AlumniDetails> alumniDetails) {
        this.context = context;
        this.alumniDetails = alumniDetails;
        builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
    }

    @NonNull
    @Override
    public AboutAlumniAdapter.ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.alumni_item, parent, false);
        return new AboutAlumniAdapter.ContributionViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull AboutAlumniAdapter.ContributionViewHolder holder, int position) {
        AlumniDetails drawer = (AlumniDetails) alumniDetails.get(position);

        holder.tv_name.setText(drawer.getName());
        holder.tv_email.setText(drawer.getEmail());

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(drawer.getEmail());
        TextDrawable ic1 = builder.build(String.valueOf(drawer.getName().charAt(0)), color);
        holder.iv_dp.setImageDrawable(ic1);

        if(getItemCount() == position+1)
            holder.view.setVisibility(View.GONE);
        else
            holder.view.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return alumniDetails.size();
    }

    public class ContributionViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_email;
        public ImageView iv_dp;
        public View view;

        public ContributionViewHolder(View convertView) {
            super(convertView);

            tv_name = convertView.findViewById(R.id.tv_name);
            tv_email = convertView.findViewById(R.id.tv_email);
            iv_dp = convertView.findViewById(R.id.iv_dp);
            view = convertView.findViewById(R.id.view);
        }
    }
}
