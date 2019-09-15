package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.FeedViewHolder>{

    private List<Club> clubs;
    private Context context;

    public DummyAdapter(Context context, List<Club> clubs) {
        this.context = context;
        this.clubs = clubs;
    }

    @NonNull
    @Override
    public DummyAdapter.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.club_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DummyAdapter.FeedViewHolder holder, final int position) {
        final Club model = (Club) clubs.get(position);
        holder.tv_name.setText(model.getName());
        holder.tv_description.setText(model.getDescription());

        try {
            Glide.with(context).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.iv_dp);
        }
        catch (Exception e){
            holder.iv_dp.setImageResource(R.color.shimmering_back);
        }

        holder.iv_follow.setImageResource(R.drawable.follow);

        holder.iv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.iv_follow.setImageResource(R.drawable.follow_blue);

                Map<String, Object> follower_det = new HashMap<>();
                model.getFollowers().add(SharedPref.getString(context,"email"));
                follower_det.put("followers", model.getFollowers());
                FirebaseFirestore.getInstance().collection("club").document(model.getId()).set(follower_det, SetOptions.merge());
                //FirebaseFirestore.getInstance().collection("contact").document(model).update("number", FieldValue.arrayRemove(Integer.parseInt(edt_phone.getText().toString())))

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
        ImageView iv_dp, iv_follow;

        public FeedViewHolder(View convertView) {
            super(convertView);

            tv_name = convertView.findViewById(R.id.tv_name);
            tv_description = convertView.findViewById(R.id.tv_description);
            iv_dp = convertView.findViewById(R.id.iv_dp);
            iv_follow = convertView.findViewById(R.id.iv_follow);
            club_RL = convertView.findViewById(R.id.club_RL);
        }
    }
}
