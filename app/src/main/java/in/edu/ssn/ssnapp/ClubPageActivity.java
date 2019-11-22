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
import android.os.Handler;
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
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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

public class ClubPageActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private boolean isSubscriptionChange = false;

    private Toolbar toolbar;
    private RelativeLayout layout_page_detail, layout_progress;
    private LottieAnimationView lottie;

    private FirestoreRecyclerAdapter adapter;
    private ShimmerFrameLayout shimmer_view;
    private RecyclerView feedsRV;
    private TextView tool_tv_count, following_textTV, followersTV, followers_textTV;
    private Club club;
    private TextView newPostTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(darkModeEnabled){
            setContentView(R.layout.activity_club_page_dark);
        }else{
            setContentView(R.layout.activity_club_page);
        }

        initUI();

        setupFireStore();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                feedsRV.smoothScrollToPosition(0);
            }
        },3000);

        newPostTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedsRV.smoothScrollToPosition(0);
                newPostTV.setVisibility(View.GONE);
            }
        });


        /****************************************************/
    }

    private void initUI() {
        //Toolbar

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView tool_backIV = findViewById(R.id.tool_backIV);
        tool_backIV.setOnClickListener(this);
        ImageView tool_shareIV = findViewById(R.id.tool_shareIV);
        tool_shareIV.setOnClickListener(this);
        CircleImageView tool_dpIV = findViewById(R.id.tool_dpIV);
        TextView tool_titleTV = findViewById(R.id.tool_titleTV);
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
        ImageView cover_picIV = findViewById(R.id.cover_picIV);       cover_picIV.setOnClickListener(this);
        CircleImageView dpIV_pic = findViewById(R.id.dpIV_pic);

        following_textTV = findViewById(R.id.following_textTV);
        lottie = findViewById(R.id.lottie);    lottie.setOnClickListener(this);

        TextView titleTV = findViewById(R.id.titleTV);
        TextView descriptionTV = findViewById(R.id.descriptionTV);
        TextView contactTV = findViewById(R.id.contactTV);
        contactTV.setOnClickListener(this);

        followersTV = findViewById(R.id.followersTV);
        followers_textTV = findViewById(R.id.followers_textTV);

        layout_progress = findViewById(R.id.layout_progress);

        shimmer_view = findViewById(R.id.shimmer_view);
        feedsRV = findViewById(R.id.feedsRV);
        newPostTV = findViewById(R.id.newPostTV);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(ClubPageActivity.this);
        feedsRV.setLayoutManager(layoutManager);
        feedsRV.setNestedScrollingEnabled(false);
        feedsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    //Log.d("Feed","First item completely visible");
                    newPostTV.setVisibility(View.GONE);
                }
            }
        });

        /****************************************************/
        //Update Data

        club = getIntent().getParcelableExtra("data");

        if (club.getFollowers().contains(SharedPref.getString(getApplicationContext(), "email"))) {
            following_textTV.setText("Following");
            if(darkModeEnabled){
                following_textTV.setTextColor(Color.WHITE);
            }else{
                following_textTV.setTextColor(Color.BLACK);
            }

        } else {
            following_textTV.setText("Follow");
            if(darkModeEnabled){
                following_textTV.setTextColor(getResources().getColor(R.color.colorAccentDark));
            }
            else {
                following_textTV.setTextColor(getResources().getColor(R.color.colorAccent));
            }
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

        titleTV.setText(club.getName());
        tool_titleTV.setText(club.getName());

        descriptionTV.setText(club.getDescription());
        contactTV.setText(club.getContact());

        if (club.getFollowers().size() > 0) {
            followersTV.setText(club.getFollowers().size() + "");
            followers_textTV.setText("Followers");
        } else {
            followersTV.setText("0");
            followers_textTV.setText("Follower");
        }

        Glide.with(this).load(club.getDp_url()).placeholder(R.color.shimmering_front).into(dpIV_pic);
        Glide.with(this).load(club.getDp_url()).placeholder(R.color.shimmering_front).into(tool_dpIV);
        Glide.with(this).load(club.getCover_url()).placeholder(R.color.shimmering_back).into(cover_picIV);
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
                holder.authorTV.setText(CommonUtils.getNameFromEmail(model.getAuthor()));
                holder.titleTV.setText(model.getTitle());

                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(model.getAuthor());
                TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
                holder.userImageIV.setImageDrawable(ic1);

                try{
                    if (model.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                        holder.likeIV.setImageResource(R.drawable.blue_heart);
                    } else {
                        holder.likeIV.setImageResource(R.drawable.heart);
                    }
                }catch (Exception e){
                    holder.likeIV.setImageResource(R.drawable.heart);
                }

                holder.timeTV.setText(CommonUtils.getTime(model.getTime()));

                if (model.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    holder.descriptionTV.setText(ss);
                } else
                    holder.descriptionTV.setText(model.getDescription().trim());

                if (model.getImg_urls() != null && model.getImg_urls().size() != 0) {
                    holder.viewPager.setVisibility(View.VISIBLE);

                    final ImageAdapter imageAdapter = new ImageAdapter(ClubPageActivity.this, model.getImg_urls(),4, club, model.getId());
                    holder.viewPager.setAdapter(imageAdapter);

                    if (model.getImg_urls().size() == 1) {
                        holder.current_imageTV.setVisibility(View.GONE);
                    } else {
                        holder.current_imageTV.setVisibility(View.VISIBLE);
                        holder.current_imageTV.setText(String.valueOf(1) + " / " + String.valueOf(model.getImg_urls().size()));
                        holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int pos) {
                                holder.current_imageTV.setText(String.valueOf(pos + 1) + " / " + String.valueOf(model.getImg_urls().size()));
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }
                } else {
                    holder.viewPager.setVisibility(View.GONE);
                    holder.current_imageTV.setVisibility(View.GONE);
                }

                try {
                    holder.likeTV.setText(Integer.toString(model.getLike().size()));
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.likeTV.setText("0");
                }

                try {
                    holder.commentTV.setText(Integer.toString(model.getComment().size()));
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.commentTV.setText("0");
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

                holder.likeIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            if (!model.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                                holder.likeIV.setImageResource(R.drawable.blue_heart);
                                FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(), "email")));
                            } else {
                                holder.likeIV.setImageResource(R.drawable.heart);
                                FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(), "email")));
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

                holder.shareIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String timer = holder.timeTV.getText().toString();
                        String shareBody = "Hi all! New posts from " + club.getName() + ". Check it out: https://ssnportal.cf/share.html?type=4&vca=" + club.getId() + "&vac=" + model.getId();
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
                View view;
                if(darkModeEnabled){
                    view = LayoutInflater.from(ClubPageActivity.this).inflate(R.layout.club_post_item_dark, group, false);
                }else{
                    view = LayoutInflater.from(ClubPageActivity.this).inflate(R.layout.club_post_item, group, false);
                }

                return new FeedViewHolder(view);
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                if(type==ChangeEventType.CHANGED){
                    // New post added (Show new post available text)
                    newPostTV.setVisibility(View.VISIBLE);
                }
            }
        };

        feedsRV.setAdapter(adapter);
    }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTV, titleTV, timeTV, current_imageTV, likeTV, commentTV;
        public SocialTextView descriptionTV;
        public ImageView userImageIV, likeIV, commentIV, shareIV;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View itemView) {
            super(itemView);

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

    /****************************************************/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cover_picIV:
                Intent intent = new Intent(getApplicationContext(), OpenImageActivity.class);
                intent.putExtra("url", club.getCover_url());
                startActivity(intent);
                Bungee.slideLeft(ClubPageActivity.this);
                break;
            case R.id.backIV:
            case R.id.tool_backIV:
                onBackPressed();
                break;
            case R.id.shareIV:
            case R.id.tool_shareIV:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi all! Check out the " + club.getName() + " page: https://ssnportal.cf/share.html?type=3&vca=" + club.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.contactTV:
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
                    following_textTV.setText("Following");
                    if(darkModeEnabled){
                        following_textTV.setTextColor(Color.WHITE);
                    }else{
                        following_textTV.setTextColor(Color.BLACK);
                    }
                }
                else {
                    FirebaseFirestore.getInstance().collection(Constants.collection_club).document(club.getId()).update("followers", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(),"email")));
                    FCMHelper.UnSubscribeToTopic(getApplicationContext(),"club_" + club.getId());
                    club.getFollowers().remove(SharedPref.getString(getApplicationContext(),"email"));
                    following_textTV.setText("Follow");
                    if(darkModeEnabled){
                        following_textTV.setTextColor(getResources().getColor(R.color.colorAccentDark));
                    }
                    else {
                        following_textTV.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }

                if (club.getFollowers().size() > 0) {
                    followersTV.setText(club.getFollowers().size() + "");
                    followers_textTV.setText("Followers");
                } else {
                    followersTV.setText("0");
                    followers_textTV.setText("Follower");
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