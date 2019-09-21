package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ClubPageActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private boolean isSubscriptionChange = false;

    private Toolbar toolbar;
    private RelativeLayout layout_page_detail, layout_progress;
    private LottieAnimationView lottie;

    private FirestoreRecyclerAdapter adapter;
    private ShimmerFrameLayout shimmer_view;
    private RecyclerView feedsRV;
    private TextView tool_tv_count, tv_following_text, tv_followers, tv_followers_text;
    private Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_page);

        initUI();

        setupFireStore();


        /****************************************************/
    }

    private void initUI() {
        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView tool_iv_back = findViewById(R.id.tool_iv_back);
        tool_iv_back.setOnClickListener(this);
        ImageView tool_iv_share = findViewById(R.id.tool_iv_share);
        tool_iv_share.setOnClickListener(this);
        CircleImageView tool_iv_dp = findViewById(R.id.tool_iv_dp);
        TextView tool_tv_title = findViewById(R.id.tool_tv_title);
        tool_tv_count = findViewById(R.id.tool_tv_count);

        setSupportActionBar(toolbar);
        startAlphaAnimation(toolbar, 0, View.INVISIBLE);

        //Collapsible layout

        AppBarLayout layout_app_bar = findViewById(R.id.layout_app_bar);
        layout_app_bar.addOnOffsetChangedListener(this);
        layout_page_detail = findViewById(R.id.layout_page_detail);

        ImageView backIV = findViewById(R.id.backIV);
        backIV.setOnClickListener(this);
        ImageView shareIV = findViewById(R.id.shareIV);
        shareIV.setOnClickListener(this);
        ImageView iv_cover_pic = findViewById(R.id.iv_cover_pic);       iv_cover_pic.setOnClickListener(this);
        CircleImageView iv_dp_pic = findViewById(R.id.iv_dp_pic);

        tv_following_text = findViewById(R.id.tv_following_text);
        lottie = findViewById(R.id.lottie);    lottie.setOnClickListener(this);

        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_description = findViewById(R.id.tv_description);
        TextView tv_contact = findViewById(R.id.tv_contact);
        tv_contact.setOnClickListener(this);

        tv_followers = findViewById(R.id.tv_followers);
        tv_followers_text = findViewById(R.id.tv_followers_text);

        layout_progress = findViewById(R.id.layout_progress);

        shimmer_view = findViewById(R.id.shimmer_view);
        feedsRV = findViewById(R.id.feedsRV);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ClubPageActivity.this);
        feedsRV.setLayoutManager(layoutManager);
        feedsRV.setNestedScrollingEnabled(false);

        /****************************************************/
        //Update Data

        club = getIntent().getParcelableExtra("data");

        if (club.getFollowers().contains(SharedPref.getString(getApplicationContext(), "email"))) {
            tv_following_text.setText("Following");
            tv_following_text.setTextColor(Color.BLACK);
        } else {
            tv_following_text.setText("Follow");
            tv_following_text.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        lottie.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (valueAnimator.isRunning()) {
                    if (!club.getFollowers().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                        if (lottie.getProgress() > 0.50) {
                            lottie.setProgress(0.0f);
                            lottie.pauseAnimation();
                        }
                    } else if (lottie.getProgress() > 0.6)
                        lottie.pauseAnimation();
                }
            }
        });

        tv_title.setText(club.getName());
        tool_tv_title.setText(club.getName());

        tv_description.setText(club.getDescription());
        tv_contact.setText(club.getContact());

        if (club.getFollowers().size() > 0) {
            tv_followers.setText(club.getFollowers().size() + "");
            tv_followers_text.setText("Followers");
        } else {
            tv_followers.setText("0");
            tv_followers_text.setText("Follower");
        }

        Glide.with(this).load(club.getDp_url()).placeholder(R.color.shimmering_front).into(iv_dp_pic);
        Glide.with(this).load(club.getDp_url()).placeholder(R.color.shimmering_front).into(tool_iv_dp);
        Glide.with(this).load(club.getCover_url()).placeholder(R.color.shimmering_back).into(iv_cover_pic);
    }

    private void setupFireStore() {
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();

        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_post_club).whereEqualTo("cid", club.getId()).orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ClubPost> options = new FirestoreRecyclerOptions.Builder<ClubPost>().setQuery(query, new SnapshotParser<ClubPost>() {
            @NonNull
            @Override
            public ClubPost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);
                return CommonUtils.getClubPostFromSnapshot(getApplicationContext(),snapshot);
            }
        }).build();

        adapter = new FirestoreRecyclerAdapter<ClubPost, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final ClubPost model) {
                holder.tv_author.setText(CommonUtils.getNameFromEmail(model.getAuthor()));
                holder.tv_title.setText(model.getTitle());

                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(model.getAuthor());
                TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
                holder.userImageIV.setImageDrawable(ic1);

                try{
                    if (model.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
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

                    final ImageAdapter imageAdapter = new ImageAdapter(ClubPageActivity.this, model.getImg_urls(),4, club, model.getId());
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
                        Intent intent = new Intent(getApplicationContext(), ClubPostDetailsActivity.class);
                        intent.putExtra("data", model.getId());
                        intent.putExtra("club", club);
                        startActivity(intent);
                        Bungee.slideLeft(ClubPageActivity.this);
                    }
                });

                holder.iv_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            if (!model.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                                holder.iv_like.setImageResource(R.drawable.blue_heart);
                                FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(), "email")));
                            } else {
                                holder.iv_like.setImageResource(R.drawable.heart);
                                FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(), "email")));
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
                        String timer = holder.tv_time.getText().toString();
                        String shareBody = "Hello! New posts from " + club.getName() + ". Check it out: https://ssn-app-web.web.app/share.html?type=4&vca=" + club.getId() + "&vac=" + model.getId();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });

                if(getItemCount() > 0)
                    tool_tv_count.setText(getItemCount() + " posts");
                else
                    tool_tv_count.setText("No posts");
                shimmer_view.setVisibility(View.GONE);
                layout_progress.setVisibility(View.GONE);
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

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author, tv_title, tv_time, tv_current_image, tv_like, tv_comment;
        public SocialTextView tv_description;
        public ImageView userImageIV, iv_like, iv_comment, iv_share;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View itemView) {
            super(itemView);

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

    /****************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_cover_pic:
                Intent intent = new Intent(getApplicationContext(), OpenImageActivity.class);
                intent.putExtra("url", club.getCover_url());
                startActivity(intent);
                Bungee.slideLeft(ClubPageActivity.this);
                break;
            case R.id.backIV:
            case R.id.tool_iv_back:
                onBackPressed();
                break;
            case R.id.shareIV:
            case R.id.tool_iv_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! Check out the " + club.getName() + " page: https://ssn-app-web.web.app/share.html?type=3&vca=" + club.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.tv_contact:
                if (!CommonUtils.hasPermissions(ClubPageActivity.this, Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(ClubPageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                else {
                    try {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + club.getContact())));
                    }
                    catch (SecurityException e){
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.lottie:
                isSubscriptionChange = !isSubscriptionChange;
                lottie.playAnimation();

                if (!club.getFollowers().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                    FirebaseFirestore.getInstance().collection(Constants.collection_club).document(club.getId()).update("followers", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(),"email")));
                    FCMHelper.SubscribeToTopic(getApplicationContext(),"club_" + club.getId());
                    club.getFollowers().add(SharedPref.getString(getApplicationContext(),"email"));
                    tv_following_text.setText("Following");
                    tv_following_text.setTextColor(Color.BLACK);
                }
                else {
                    FirebaseFirestore.getInstance().collection(Constants.collection_club).document(club.getId()).update("followers", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(),"email")));
                    FCMHelper.UnSubscribeToTopic(getApplicationContext(),"club_" + club.getId());
                    club.getFollowers().remove(SharedPref.getString(getApplicationContext(),"email"));
                    tv_following_text.setText("Follow");
                    tv_following_text.setTextColor(getResources().getColor(R.color.colorAccent));
                }

                if (club.getFollowers().size() > 0) {
                    tv_followers.setText(club.getFollowers().size() + "");
                    tv_followers_text.setText("Followers");
                } else {
                    tv_followers.setText("0");
                    tv_followers_text.setText("Follower");
                }
                break;
        }
    }

    /****************************************************/
    //Transition effects

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= 0.8f) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar, 200, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        }
        else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbar, 200, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= 0.8f) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(layout_page_detail, 200, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        }
        else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(layout_page_detail, 200, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
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

    @Override
    public void onBackPressed() {
        SharedPref.putBoolean(getApplicationContext(),"subs_changes_made",isSubscriptionChange);
        super.onBackPressed();
        Bungee.slideRight(ClubPageActivity.this);
    }
}