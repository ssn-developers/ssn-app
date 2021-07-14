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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;

import in.edu.ssn.ssnapp.ClubPostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

//this Adapter is Used in ClubFragment to Show the posts from all the subscribed clubs.
public class SubscribeFeedsAdapter extends RecyclerView.Adapter<SubscribeFeedsAdapter.FeedViewHolder> {

    boolean darkMode;
    private ArrayList<ClubPost> posts;
    private ArrayList<Club> clubs;
    private Context context;
    private TextDrawable.IBuilder builder;

    public SubscribeFeedsAdapter(Context context, ArrayList<Club> clubs, ArrayList<ClubPost> posts) {
        this.context = context;
        this.clubs = clubs;
        this.posts = posts;

        //get darkmode preference
        darkMode = SharedPref.getBoolean(context, "dark_mode");

        //Icon creator from the first letter of a word. To create DP's using a Letter.
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

        //check if the darkmode enabled and open respective Item layout.
        if (darkMode)
            view = LayoutInflater.from(context).inflate(R.layout.club_post_item_dark, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.club_post_item, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeedViewHolder holder, final int position) {
        final ClubPost model = posts.get(position);
        final Club club = clubs.get(position);

        holder.authorTV.setText(CommonUtils.getNameFromEmail(model.getAuthor()));
        holder.titleTV.setText(model.getTitle());

        //Icon creator from the first letter of a word. To create DP's using a Letter.
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(model.getAuthor());
        TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
        holder.userImageIV.setImageDrawable(ic1);

        //Check if the post is already liked by the user or not.
        try {
            //check if the user's email is present in the 'likes' list in firestore.
            if (model.getLike().contains(SharedPref.getString(context, "email"))) {
                holder.likeIV.setImageResource(R.drawable.blue_heart);
            } else {
                holder.likeIV.setImageResource(R.drawable.heart);
            }
        } catch (Exception e) {
            holder.likeIV.setImageResource(R.drawable.heart);
        }

        //Set the posted time ( eg: 1min ago, 1year ago)
        holder.timeTV.setText(CommonUtils.getTime(model.getTime()));

        //creating a short version of description less then 100 letters for display.
        //if description > 100 letters, shrink it.
        if (model.getDescription().length() > 100) {
            SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
            ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
            holder.descriptionTV.setText(ss);
        }
        //if description < 100 letters then directly assign it.
        else
            holder.descriptionTV.setText(model.getDescription().trim());

        //if the post has one or more images.
        if (model.getImg_urls() != null && model.getImg_urls().size() != 0) {
            holder.viewPager.setVisibility(View.VISIBLE);

            final ImageAdapter imageAdapter = new ImageAdapter(context, model.getImg_urls(), 4, club, model.getId());
            holder.viewPager.setAdapter(imageAdapter);

            //if the number of images is 1
            if (model.getImg_urls().size() == 1) {
                holder.current_imageTV.setVisibility(View.GONE);
            }
            //if there are more than 1 images.
            else {
                holder.current_imageTV.setVisibility(View.VISIBLE);
                holder.current_imageTV.setText(1 + " / " + model.getImg_urls().size());
                holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        holder.current_imageTV.setText((pos + 1) + " / " + model.getImg_urls().size());
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
        //if the post contains no images.
        else {
            holder.viewPager.setVisibility(View.GONE);
            holder.current_imageTV.setVisibility(View.GONE);
        }

        //No. Of. Likes obtained by the post
        try {
            holder.likeTV.setText(Integer.toString(model.getLike().size()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.likeTV.setText("0");
        }

        //No. Of. Comments obtained by the post
        try {
            holder.commentTV.setText(Integer.toString(model.getComment().size()));
        } catch (Exception e) {
            e.printStackTrace();
            holder.commentTV.setText("0");
        }

        //Clicking on the post proceed to the club post Details Screen.
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

        //Liking or Unliking a Post
        holder.likeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //if user not present in the "likes" list, he is now Liking the post.
                    if (!model.getLike().contains(SharedPref.getString(context, "email"))) {
                        holder.likeIV.setImageResource(R.drawable.blue_heart);
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(context, "email")));
                    }
                    //if user already present in the "likes" list, he is now Un-Liking the post.
                    else {
                        holder.likeIV.setImageResource(R.drawable.heart);
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(context, "email")));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Share Button prompts sharing options to share the link for that particular post.
        holder.shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi all! New posts from " + club.getName() + ". Check it out: https://ssnportal.netlify.app/share.html?type=4&vca=" + club.getId() + "&vac=" + model.getId();
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
        public TextView authorTV, titleTV, timeTV, current_imageTV, likeTV, commentTV;
        public SocialTextView descriptionTV;
        public ImageView userImageIV, likeIV, commentIV, shareIV;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View convertView) {
            super(convertView);

            userImageIV = itemView.findViewById(R.id.userImageIV);
            authorTV = itemView.findViewById(R.id.authorTV);

            timeTV = itemView.findViewById(R.id.timeTV);
            viewPager = itemView.findViewById(R.id.viewPager);
            current_imageTV = itemView.findViewById(R.id.currentImageTV);

            feed_view = itemView.findViewById(R.id.feed_view);
            titleTV = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            likeIV = itemView.findViewById(R.id.likeIV);
            likeTV = itemView.findViewById(R.id.likeTV);
            commentIV = itemView.findViewById(R.id.commentIV);
            commentTV = itemView.findViewById(R.id.commentTV);
            shareIV = itemView.findViewById(R.id.shareIV);
        }
    }
}
