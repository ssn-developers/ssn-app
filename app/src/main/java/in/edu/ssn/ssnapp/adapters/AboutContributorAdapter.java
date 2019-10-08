package in.edu.ssn.ssnapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.ContributorProfileActivity;
import in.edu.ssn.ssnapp.NoNetworkActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.StudentHomeActivity;
import in.edu.ssn.ssnapp.WebViewActivity;
import in.edu.ssn.ssnapp.models.TeamDetails;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class AboutContributorAdapter extends RecyclerView.Adapter<AboutContributorAdapter.ContributionViewHolder> implements View.OnClickListener{

    private ArrayList<TeamDetails> teamDetails;
    private Context context;
    boolean darkMode=false;

    public AboutContributorAdapter(Context context, ArrayList<TeamDetails> teamDetails) {
        this.context = context;
        this.teamDetails = teamDetails;
        darkMode = SharedPref.getBoolean(context,"darkMode");
    }

    @NonNull
    @Override
    public AboutContributorAdapter.ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem;
        if(darkMode){
            listItem = layoutInflater.inflate(R.layout.contributor_item_dark, parent, false);
        }else {
            listItem = layoutInflater.inflate(R.layout.contributor_item, parent, false);
        }
        return new AboutContributorAdapter.ContributionViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull final AboutContributorAdapter.ContributionViewHolder holder, int position) {
        final TeamDetails drawer = (TeamDetails) teamDetails.get(position);

        holder.tv_name.setText(drawer.getName());
        holder.tv_position.setText(drawer.getPosition());
        holder.iv_dp.setImageResource(drawer.getDp());

        holder.iv_img1.setBackgroundResource(drawer.getType().get(0));
        holder.iv_img2.setBackgroundResource(drawer.getType().get(1));
        holder.iv_img3.setBackgroundResource(drawer.getType().get(2));

        holder.iv_img1.setTag(drawer.getUrl().get(0));
        holder.iv_img2.setTag(drawer.getUrl().get(1));
        holder.iv_img3.setTag(drawer.getUrl().get(2));

        holder.iv_img1.setOnClickListener(this);
        holder.iv_img2.setOnClickListener(this);
        holder.iv_img3.setOnClickListener(this);

        holder.containerRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContributorProfileActivity.class);
                intent.putExtra("Contributor",drawer);
                ActivityOptionsCompat options = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity) context,
                            holder.iv_dp,
                            holder.iv_dp.getTransitionName()
                            );
                    ActivityCompat.startActivity(context, intent, options.toBundle());
                }else{
                    context.startActivity(intent);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return teamDetails.size();
    }

    public class ContributionViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_position;
        public ImageView iv_dp, iv_img1, iv_img2, iv_img3;
        public RelativeLayout containerRL;

        public ContributionViewHolder(View convertView) {
            super(convertView);

            tv_name = convertView.findViewById(R.id.tv_name);
            tv_position = convertView.findViewById(R.id.tv_position);
            iv_dp = convertView.findViewById(R.id.iv_dp);

            iv_img1 = convertView.findViewById(R.id.iv_img1);
            iv_img2 = convertView.findViewById(R.id.iv_img2);
            iv_img3 = convertView.findViewById(R.id.iv_img3);

            containerRL = convertView.findViewById(R.id.containerRL);
        }
    }

    @Override
    public void onClick(View v) {
        /*if(!CommonUtils.alerter(context)) {
            switch (v.getId()) {
                case R.id.iv_img1:
                    if (v.getTag() != null) {
                        CharSequence seq = "dribbble";
                        if (!(v.getTag().toString().contains(seq)))
                            context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", v.getTag().toString(), null)));
                        else {
                            SharedPref.putString(context, "url", v.getTag().toString());
                            context.startActivity(new Intent(context, WebViewActivity.class));
                            Bungee.slideLeft(context);
                        }
                    }
                    break;
                case R.id.iv_img2:
                case R.id.iv_img3:
                    if (v.getTag() != null) {
                        SharedPref.putString(context, "url", v.getTag().toString());
                        context.startActivity(new Intent(context, WebViewActivity.class));
                        Bungee.slideLeft(context);
                    }
                    break;
            }
        }
        else{
            Intent intent = new Intent(context, NoNetworkActivity.class);
            intent.putExtra("key","appinfo");
            context.startActivity(intent);
            Bungee.fade(context);
        }*/
    }
}
