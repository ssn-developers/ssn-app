package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import in.edu.ssn.ssnapp.adapters.ClubImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ClubPageActivity extends AppCompatActivity {

    private RecyclerView feedsRV;
    public ImageView dp_pic,cover_pic,follow_iv;
    public TextView Club_name_tv,Club_desc_tv,followers_tv;
    private RelativeLayout layout_progress;
    private ShimmerFrameLayout shimmer_view;
    private FirestoreRecyclerAdapter adapter;
    RelativeLayout top_rl;
    CollapsingToolbarLayout collapsingToolbar;
    String dp_url;
    String cover_url;
    String name;
    int followers;
    int flag1=0;
    int flag2=0;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_page);
        initUI();

        setupFireStore();

    }
    void setupFireStore(){
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();


        // manually create composite query for each year & dept
        // [cse | it | ece | eee | mech | bme | chemical | civil | admin]

        Query query = FirebaseFirestore.getInstance().collection("post_club").orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
            @NonNull
            @Override
            public Club parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);

                final Club post = new Club();
                post.setId(snapshot.getString("cid"));
                post.setPid(snapshot.getString("id"));
                post.setAuthor(snapshot.getString("author"));
                post.setTitle(snapshot.getString("title"));
                post.setDescription(snapshot.getString("description"));
                post.setTime(snapshot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if(images != null && images.size() > 0)
                    post.setImageUrl(images);
                else
                    post.setImageUrl(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            fileName.add(files.get(i).get("name"));
                            fileUrl.add(files.get(i).get("url"));
                        }
                        post.setFileName(fileName);
                        post.setFileUrl(fileUrl);
                    }
                    else {
                        post.setFileName(null);
                        post.setFileUrl(null);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: "+ Arrays.toString(e.getStackTrace()) +" \n Error: "+e.getMessage());
                    post.setFileName(null);
                    post.setFileUrl(null);
                }


                return post;
            }
        })
                .build();

        adapter = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {
                holder.tv_author.setText(model.getAuthor());


                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(model.getAuthor());
                TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
                holder.userImageIV.setImageDrawable(ic1);
                holder.tv_title.setText(model.getTitle());

                Date time = model.getTime();
                Date now = new Date();
                Long t = now.getTime() - time.getTime();
                String timer;

                if (t < 60000)
                    timer = Long.toString(t / 1000) + "s ago";
                else if (t < 3600000)
                    timer = Long.toString(t / 60000) + "m ago";
                else if (t < 86400000)
                    timer = Long.toString(t / 3600000) + "h ago";
                else if (t < 604800000)
                    timer = Long.toString(t / 86400000) + "d ago";
                else if (t < 2592000000L)
                    timer = Long.toString(t / 604800000) + "w ago";
                else if (t < 31536000000L)
                    timer = Long.toString(t / 2592000000L) + "M ago";
                else
                    timer = Long.toString(t / 31536000000L) + "y ago";

                holder.tv_time.setText(timer);

                if (model.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    holder.tv_description.setText(ss);
                } else
                    holder.tv_description.setText(model.getDescription().trim());

                if (model.getImageUrl() != null && model.getImageUrl().size() != 0) {
                    holder.viewPager.setVisibility(View.VISIBLE);

                    final ClubImageAdapter imageAdapter = new ClubImageAdapter(ClubPageActivity.this, model.getImageUrl(), true, model, timer);
                    holder.viewPager.setAdapter(imageAdapter);

                    if (model.getImageUrl().size() == 1) {
                        holder.tv_current_image.setVisibility(View.GONE);
                    } else {
                        holder.tv_current_image.setVisibility(View.VISIBLE);
                        holder.tv_current_image.setText(String.valueOf(1) + " / " + String.valueOf(model.getImageUrl().size()));
                        holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int pos) {
                                holder.tv_current_image.setText(String.valueOf(pos + 1) + " / " + String.valueOf(model.getImageUrl().size()));
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
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ClubPageActivity.this, "hiiiiiii", Toast.LENGTH_SHORT).show();
                    }
                });
                if (SharedPref.getBoolean(ClubPageActivity.this, "club_subscribed", model.getId())) {
                    follow_iv.setImageResource(R.drawable.follow_blue);
                } else {
                    follow_iv.setImageResource(R.drawable.follow);
                }

                follow_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SharedPref.getBoolean(ClubPageActivity.this, "club_subscribed", model.getId())) {
                            follow_iv.setImageResource(R.drawable.follow);
                            SharedPref.putBoolean(ClubPageActivity.this, "club_subscribed", model.getId(), false);
                        } else {
                            follow_iv.setImageResource(R.drawable.follow_blue);
                            SharedPref.putBoolean(ClubPageActivity.this, "club_subscribed", model.getId(), true);
                        }
                    }
                });


                Glide.with(ClubPageActivity.this).load(dp_url).placeholder(R.drawable.ic_user_white).into(dp_pic);
                Glide.with(ClubPageActivity.this).load(cover_url).placeholder(R.drawable.ic_user_white).into(cover_pic);
                Club_name_tv.setText(name);
                Club_desc_tv.setText(description);
                followers_tv.setText(String.valueOf(followers)+" followers");

                collapsingToolbar.setTitle(name);
                layout_progress.setVisibility(View.GONE);
                shimmer_view.setVisibility(View.GONE);

            }


            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(ClubPageActivity.this).inflate(R.layout.club_post_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        feedsRV.setAdapter(adapter);
    }

    void initUI(){
        dp_url= getIntent().getExtras().getString("dp_url");
        cover_url=getIntent().getExtras().getString("cover_url");
        name=getIntent().getExtras().getString("name");
        description=getIntent().getExtras().getString("description");
        followers= getIntent().getExtras().getInt("followers");

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        feedsRV = findViewById(R.id.feedsRV_cl);
        dp_pic = findViewById(R.id.dp_pic_iv);
        cover_pic = findViewById(R.id.cover_pic_iv);
        follow_iv = findViewById(R.id.follow_iv);
        Club_name_tv = findViewById(R.id.club_name_tv);
        Club_desc_tv = findViewById(R.id.club_description_tv);
        followers_tv = findViewById(R.id.followers_tv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ClubPageActivity.this);
        feedsRV.setLayoutManager(layoutManager);

        shimmer_view = findViewById(R.id.shimmer_view_cl);
        layout_progress = findViewById(R.id.layout_progress_cl);
    }

    /*********************************************************/

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author, tv_club, tv_title, tv_time, tv_current_image;
        public SocialTextView tv_description;
        public ImageView userImageIV,like,comment;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View itemView) {
            super(itemView);

            tv_author = itemView.findViewById(R.id.tv_author_club);
            tv_title = itemView.findViewById(R.id.tv_title_club);
            tv_description = itemView.findViewById(R.id.tv_description_club);
            tv_time = itemView.findViewById(R.id.tv_time_club);
            tv_current_image = itemView.findViewById(R.id.currentImageTV_club);
            userImageIV = itemView.findViewById(R.id.userImageIV_club);
            feed_view = itemView.findViewById(R.id.feed_view_club);
            viewPager = itemView.findViewById(R.id.viewPager_club);
            like = itemView.findViewById(R.id.like_IV_club);
            comment = itemView.findViewById(R.id.comment_IV_club);
        }
    }

    /*********************************************************/

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
