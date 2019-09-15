package in.edu.ssn.ssnapp.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class UnSubscribeAdapter extends RecyclerView.Adapter<UnSubscribeAdapter.FeedViewHolder>{

    private List<Club> clubs;
    private Context context;

    public UnSubscribeAdapter(Context context, List<Club> clubs) {
        this.context = context;
        this.clubs = clubs;
    }

    @NonNull
    @Override
    public UnSubscribeAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.club_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UnSubscribeAdapter.FeedViewHolder holder, final int position) {
        final Club model = (Club) clubs.get(position);
        holder.tv_name.setText(model.getName());
        holder.tv_description.setText(model.getDescription());

        try {
            Glide.with(context).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.iv_dp);
        }
        catch (Exception e){
            holder.iv_dp.setImageResource(R.color.shimmering_back);
        }

        holder.lottie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore.getInstance().collection("club").document(model.getId()).update("followers", FieldValue.arrayUnion(SharedPref.getString(context,"email")));
                FCMHelper.SubscribeToTopic(context,"club_" + model.getId());
                clubs.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.club_RL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClubPageActivity.class);
                intent.putExtra("data", model);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubs.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout club_RL;
        TextView tv_name, tv_description;
        ImageView iv_dp;
        LottieAnimationView lottie;

        public FeedViewHolder(View convertView) {
            super(convertView);

            tv_name = convertView.findViewById(R.id.tv_name);
            tv_description = convertView.findViewById(R.id.tv_description);
            iv_dp = convertView.findViewById(R.id.iv_dp);
            lottie = convertView.findViewById(R.id.lottie);
            club_RL = convertView.findViewById(R.id.club_RL);

            lottie.playAnimation();
            lottie.loop(true);
            lottie.setSpeed(0.7f);

            lottie.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if(valueAnimator.isRunning()){
                        if(lottie.getProgress() > 0.45) {
                            lottie.setProgress(0.0f);
                            lottie.pauseAnimation();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    lottie.playAnimation();
                                }
                            },2000);
                        }
                    }
                }
            });
        }
    }
}
