package in.edu.ssn.ssnapp.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.List;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.ClubPostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class SubscribeFeedsAdapter extends RecyclerView.Adapter<SubscribeFeedsAdapter.FeedViewHolder>{

    private ArrayList<ClubPost> posts;
    private ArrayList<Club> clubs;
    private Context context;
    boolean darkMode;
    private TextDrawable.IBuilder builder;

    public SubscribeFeedsAdapter(Context context, ArrayList<Club> clubs, ArrayList<ClubPost> posts) {
        this.context = context;
        this.clubs = clubs;
        this.posts = posts;
        darkMode = SharedPref.getBoolean(context,"darkMode");
        builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(darkMode)
            view = LayoutInflater.from(context).inflate(R.layout.club_post_item_dark, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.club_post_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position) {
        final ClubPost model = (ClubPost) posts.get(position);
        final Club club = (Club) clubs.get(position);

        holder.tv_author.setText(CommonUtils.getNameFromEmail(model.getAuthor()));
        holder.tv_title.setText(model.getTitle());

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(model.getAuthor());
        TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
        holder.userImageIV.setImageDrawable(ic1);

        try{
            if (model.getLike().contains(SharedPref.getString(context, "email"))) {
                holder.iv_like.setImageResource(R.drawable.blue_heart);
            } else {
                holder.iv_like.setImageResource(R.drawable.heart);
            }
        }catch (Exception e){
            holder.iv_like.setImageResource(R.drawable.heart);
        }

        holder.tv_time.setText(CommonUtils.getTime(model.getTime()));

        if (model.getDescription().length() > 100) {
            SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
            ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
            holder.tv_description.setText(ss);
        } else
            holder.tv_description.setText(model.getDescription().trim());

        if (model.getImg_urls() != null && model.getImg_urls().size() != 0) {
            holder.viewPager.setVisibility(View.VISIBLE);

            final ImageAdapter imageAdapter = new ImageAdapter(context, model.getImg_urls(),4, club, model.getId());
            holder.viewPager.setAdapter(imageAdapter);

            if (model.getImg_urls().size() == 1) {
                holder.tv_current_image.setVisibility(View.GONE);
            } else {
                holder.tv_current_image.setVisibility(View.VISIBLE);
                holder.tv_current_image.setText(String.valueOf(1) + " / " + String.valueOf(model.getImg_urls().size()));
                holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        holder.tv_current_image.setText(String.valueOf(pos + 1) + " / " + String.valueOf(model.getImg_urls().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        } else {
            holder.viewPager.setVisibility(View.GONE);
            holder.tv_current_image.setVisibility(View.GONE);
        }

        try {
            holder.tv_like.setText(Integer.toString(model.getLike().size()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tv_like.setText("0");
        }

        try {
            holder.tv_comment.setText(Integer.toString(model.getComment().size()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.tv_comment.setText("0");
        }

        holder.feed_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClubPostDetailsActivity.class);
                intent.putExtra("data", model.getId());
                intent.putExtra("club", club);
                context.startActivity(intent);
                Bungee.slideLeft(context);
            }
        });

        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (!model.getLike().contains(SharedPref.getString(context, "email"))) {
                        holder.iv_like.setImageResource(R.drawable.blue_heart);
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(context, "email")));
                    } else {
                        holder.iv_like.setImageResource(R.drawable.heart);
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(context, "email")));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + club.getName() + ". Check it out: https://ssnportal.cf/share.html?type=4&vca=" + club.getId() + "&vac=" + model.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author, tv_title, tv_time, tv_current_image, tv_like, tv_comment;
        public SocialTextView tv_description;
        public ImageView userImageIV, iv_like, iv_comment, iv_share;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View convertView) {
            super(convertView);

            userImageIV = itemView.findViewById(R.id.userImageIV);
            tv_author = itemView.findViewById(R.id.tv_author);

            tv_time = itemView.findViewById(R.id.tv_time);
            viewPager = itemView.findViewById(R.id.viewPager);
            tv_current_image = itemView.findViewById(R.id.currentImageTV);

            feed_view = itemView.findViewById(R.id.feed_view);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            iv_like = itemView.findViewById(R.id.iv_like);
            tv_like = itemView.findViewById(R.id.tv_like);
            iv_comment = itemView.findViewById(R.id.iv_comment);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            iv_share = itemView.findViewById(R.id.iv_share);
        }
    }
}
