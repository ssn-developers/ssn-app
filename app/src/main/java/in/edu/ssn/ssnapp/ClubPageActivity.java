package in.edu.ssn.ssnapp;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

// Extends Base activity for darkmode variable and status bar.
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
    private TextView toolCountTV, following_textTV, followersTV, followers_textTV;
    private Club club;
    private TextView newPostTV;

    /****************************************************/
    //Transition effects
    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
    /****************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if darkmode is enabled and open the appropriate layout.
        if (darkModeEnabled) {
            setContentView(R.layout.activity_club_page_dark);
        } else {
            setContentView(R.layout.activity_club_page);
        }

        initUI();

        //Load up firestore collection.
        setupFireStore();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                feedsRV.smoothScrollToPosition(0);
            }
        }, 3000);

        //Referesh page for new posts
        newPostTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedsRV.smoothScrollToPosition(0);
                newPostTV.setVisibility(View.GONE);
            }
        });
    }

    /**********************************************************************/
    // Initiate variables and UI elements.
    private void initUI() {
        //Toolbar

        toolbar = findViewById(R.id.toolbar);
        ImageView tool_backIV = findViewById(R.id.tool_backIV);
        tool_backIV.setOnClickListener(this);
        ImageView tool_shareIV = findViewById(R.id.tool_shareIV);
        tool_shareIV.setOnClickListener(this);
        CircleImageView tool_dpIV = findViewById(R.id.tool_dpIV);
        TextView tool_titleTV = findViewById(R.id.tool_titleTV);
        toolCountTV = findViewById(R.id.toolCountTV);

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
        ImageView cover_picIV = findViewById(R.id.cover_picIV);
        cover_picIV.setOnClickListener(this);
        CircleImageView dpIV = findViewById(R.id.dpIV);

        following_textTV = findViewById(R.id.following_textTV);
        lottie = findViewById(R.id.lottie);
        lottie.setOnClickListener(this);

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

        //Get the Details of the Club passed from the Previous Screen via the Intent Extra.
        club = getIntent().getParcelableExtra("data");

        //Check whether you have subscribed to the club or not
        //If subscribed change text as "Following"
        if (club.getFollowers().contains(SharedPref.getString(getApplicationContext(), "email"))) {
            following_textTV.setText("Following");
            //Set Text color mode based on darkmode preferences.
            if (darkModeEnabled) {
                following_textTV.setTextColor(Color.WHITE);
            } else {
                following_textTV.setTextColor(Color.BLACK);
            }

        }
        //If Not Subscribed Change text to "Follow"
        else {
            following_textTV.setText("Follow");
            //Set Text color mode based on darkmode preferences.
            if (darkModeEnabled) {
                following_textTV.setTextColor(getResources().getColor(R.color.colorAccentDark));
            } else {
                following_textTV.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }

        //Bell Icon Animation
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

        //Setting the DP and cover images
        Glide.with(this).load(club.getDp_url()).placeholder(R.color.shimmering_front).into(dpIV);
        Glide.with(this).load(club.getDp_url()).placeholder(R.color.shimmering_front).into(tool_dpIV);
        Glide.with(this).load(club.getCover_url()).placeholder(R.color.shimmering_back).into(cover_picIV);
    }
    /**********************************************************************/

    /************************************************************************/
    //load club posts for the students.
    private void setupFireStore() {

        //Icon creator from the first letter of a word. To create DP using a Letter.
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();

        //Get posts from the Firestore.
        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_post_club).whereEqualTo("cid", club.getId()).orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ClubPost> options = new FirestoreRecyclerOptions.Builder<ClubPost>().setQuery(query, new SnapshotParser<ClubPost>() {
            @NonNull
            @Override
            public ClubPost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);
                return CommonUtils.getClubPostFromSnapshot(getApplicationContext(), snapshot);
            }
        }).build();

        //Assign post details to UI elements
        adapter = new FirestoreRecyclerAdapter<ClubPost, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final ClubPost model) {
                holder.authorTV.setText(CommonUtils.getNameFromEmail(model.getAuthor()));
                holder.titleTV.setText(model.getTitle());

                //Icon creator from the first letter of a word. To create DP using a Letter.
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(model.getAuthor());
                TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
                holder.userImageIV.setImageDrawable(ic1);

                //Check if the post is already liked by the user or not.
                try {
                    //check if the user's email is present in the 'likes' list in firestore.
                    if (model.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
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
                    //Making The ImageViewPager Visible.
                    holder.viewPager.setVisibility(View.VISIBLE);

                    final ImageAdapter imageAdapter = new ImageAdapter(ClubPageActivity.this, model.getImg_urls(), 4, club, model.getId());
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
                        Intent intent = new Intent(getApplicationContext(), ClubPostDetailsActivity.class);
                        intent.putExtra("data", model.getId());
                        intent.putExtra("club", club);
                        startActivity(intent);
                        Bungee.slideLeft(ClubPageActivity.this);
                    }
                });

                //Liking or Unliking a Post
                holder.likeIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //if user not present in the "likes" list he is Liking the post.
                            if (!model.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                                holder.likeIV.setImageResource(R.drawable.blue_heart);
                                FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(), "email")));
                            }
                            //if user already present in the "likes" list he is Un-Liking the post.
                            else {
                                holder.likeIV.setImageResource(R.drawable.heart);
                                FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(model.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(), "email")));
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
                        String timer = holder.timeTV.getText().toString();
                        String shareBody = "Hi all! New posts from " + club.getName() + ". Check it out: https://ssnportal.netlify.app/share.html?type=4&vca=" + club.getId() + "&vac=" + model.getId();
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    }
                });

                if (getItemCount() > 0)
                    toolCountTV.setText(getItemCount() + " posts");
                else
                    toolCountTV.setText("No posts");
                shimmer_view.setVisibility(View.GONE);
                layout_progress.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view;
                if (darkModeEnabled) {
                    view = LayoutInflater.from(ClubPageActivity.this).inflate(R.layout.club_post_item_dark, group, false);
                } else {
                    view = LayoutInflater.from(ClubPageActivity.this).inflate(R.layout.club_post_item, group, false);
                }

                return new FeedViewHolder(view);
            }

            //if a new post is sensed.
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                if (type == ChangeEventType.CHANGED) {
                    // New post added (Show new post available text)
                    newPostTV.setVisibility(View.VISIBLE);
                }
            }
        };

        feedsRV.setAdapter(adapter);
    }

    /****************************************************/

    /************************************************************************/
    //When clicking any buttons on the page this function is called.
    @Override
    public void onClick(View view) {
        // On click function is differentiated for different buttons using the buttons ID.
        switch (view.getId()) {
            case R.id.cover_picIV:
                //Downloading the cover pic of the Club.
                Intent intent = new Intent(getApplicationContext(), OpenImageActivity.class);
                intent.putExtra("url", club.getCover_url());
                startActivity(intent);
                Bungee.slideLeft(ClubPageActivity.this);
                break;
            //Back Buttons
            case R.id.backIV:
            case R.id.tool_backIV:
                onBackPressed();
                break;
            //Share Buttons
            case R.id.shareIV:
            case R.id.tool_shareIV:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi all! Check out the " + club.getName() + " page: https://ssnportal.netlify.app/share.html?type=3&vca=" + club.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            //Contact the club admin upon clicking the phone number.
            case R.id.contactTV:
                //Check for app permission to make a call from the phone.
                if (!CommonUtils.hasPermissions(ClubPageActivity.this, Manifest.permission.CALL_PHONE)) {
                    ActivityCompat.requestPermissions(ClubPageActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + club.getContact())));
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
                break;
            //Clicking on the bell icon subscribes or un-subscribes the club.
            case R.id.lottie:
                isSubscriptionChange = !isSubscriptionChange;
                lottie.playAnimation();

                //if user not present in the "followers" list, he is Subscribing the post.
                if (!club.getFollowers().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                    FirebaseFirestore.getInstance().collection(Constants.collection_club).document(club.getId()).update("followers", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(), "email")));
                    FCMHelper.SubscribeToTopic(getApplicationContext(), "club_" + club.getId());
                    //add to user to followers.
                    club.getFollowers().add(SharedPref.getString(getApplicationContext(), "email"));
                    following_textTV.setText("Following");
                    if (darkModeEnabled) {
                        following_textTV.setTextColor(Color.WHITE);
                    } else {
                        following_textTV.setTextColor(Color.BLACK);
                    }
                }
                //if user present in the "followers" list, he is Un-Subscribing the post.
                else {
                    FirebaseFirestore.getInstance().collection(Constants.collection_club).document(club.getId()).update("followers", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(), "email")));
                    FCMHelper.UnSubscribeToTopic(getApplicationContext(), "club_" + club.getId());
                    //remove user from followers.
                    club.getFollowers().remove(SharedPref.getString(getApplicationContext(), "email"));
                    following_textTV.setText("Follow");
                    if (darkModeEnabled) {
                        following_textTV.setTextColor(getResources().getColor(R.color.colorAccentDark));
                    } else {
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

    //For animation
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    //For animation
    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= 0.8f) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(toolbar, 200, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(toolbar, 200, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    //for animation
    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= 0.8f) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(layout_page_detail, 200, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {
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
        SharedPref.putBoolean(getApplicationContext(), "subs_changes_made", isSubscriptionChange);
        super.onBackPressed();
        Bungee.slideRight(ClubPageActivity.this);
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
}