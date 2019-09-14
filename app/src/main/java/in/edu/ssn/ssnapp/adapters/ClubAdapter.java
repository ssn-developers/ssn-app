package in.edu.ssn.ssnapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Date;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ClubAdapter extends ArrayAdapter<Club> {

    public ClubAdapter(@NonNull Context context, ArrayList<Club> resource) {
        super(context,0,resource);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Club model=getItem(position);

        final TextView tv_name;
        TextView tv_description;
        final ImageView userImageIV,follow_iv;
        RelativeLayout feed_view;

        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.club_item, parent,false);

        tv_name = convertView.findViewById(R.id.club_tv);
        tv_description = convertView.findViewById(R.id.desc_tv);
        userImageIV = convertView.findViewById(R.id.dp_iv);
        follow_iv = convertView.findViewById(R.id.follow_iv);
        feed_view = convertView.findViewById(R.id.club_RL);

        tv_name.setText(model.getClub_name());
        tv_description.setText(model.getDescription());
        Glide.with(getContext()).load(model.getClub_image_url()).placeholder(R.drawable.ic_user_white).into(userImageIV);
        follow_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPref.getBoolean(getContext(),"club_subscribed",model.getId())) {
                        follow_iv.setImageResource(R.drawable.follow);
                    SharedPref.putBoolean(getContext(),"club_subscribed",model.getId(),false);
                }
                else{
                    follow_iv.setImageResource(R.drawable.follow_blue);
                    SharedPref.putBoolean(getContext(),"club_subscribed",model.getId(),true);
                }
            }
        });
        feed_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ClubPageActivity.class);
                intent.putExtra("name",model.getClub_name());
                intent.putExtra("dp_url",model.getClub_image_url());
                intent.putExtra("cover_url",model.getClub_cover_image_url());
                intent.putExtra("description",model.getDescription());
                intent.putExtra("followers",model.getFollowers());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
