package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import in.edu.ssn.ssnapp.models.HelplineDetails;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class HelplineAdapter extends RecyclerView.Adapter<HelplineAdapter.ContributionViewHolder>{

    private ArrayList<HelplineDetails> helplineDetails;
    private Context context;
    private TextDrawable.IBuilder builder;
    boolean darkMode=false;

    public HelplineAdapter(Context context, ArrayList<HelplineDetails> helplineDetails) {
        this.context = context;
        this.helplineDetails = helplineDetails;
        builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
        darkMode = SharedPref.getBoolean(context,"dark_mode");
    }

    @NonNull
    @Override
    public HelplineAdapter.ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if(darkMode){
            listItem = layoutInflater.inflate(R.layout.helpline_item_dark, parent, false);
        }else {
            listItem = layoutInflater.inflate(R.layout.helpline_item, parent, false);
        }
        return new HelplineAdapter.ContributionViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull HelplineAdapter.ContributionViewHolder holder, int position) {
        final HelplineDetails drawer = (HelplineDetails) helplineDetails.get(position);

        holder.tv_name.setText(drawer.getName());
        holder.tv_position.setText(drawer.getPosition());
        holder.tv_extn.setText("Extn: "+drawer.getExtn());

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(drawer.getName());
        TextDrawable ic1 = builder.build(String.valueOf(drawer.getName().charAt(0)), color);
        holder.iv_dp.setImageDrawable(ic1);

        holder.iv_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", drawer.getEmail(), null)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return helplineDetails.size();
    }

    public class ContributionViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_position, tv_extn;
        public ImageView iv_dp,iv_mail;

        public ContributionViewHolder(View convertView) {
            super(convertView);

            tv_name = convertView.findViewById(R.id.tv_name);
            tv_position = convertView.findViewById(R.id.tv_position);
            tv_extn = convertView.findViewById(R.id.tv_extn);
            iv_dp = convertView.findViewById(R.id.iv_dp);
            iv_mail = convertView.findViewById(R.id.mailIV);
        }
    }
}
