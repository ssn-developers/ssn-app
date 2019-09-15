package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.adapters.ClubImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ClubPostDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    public TextView tv_author, tv_club, tv_title, tv_time, tv_current_image, like_count, comment_count,layout_title;
    public SocialTextView tv_description;
    public EditText comment_etv;
    public ImageView userImageIV, like,layout_dp_iv;
    public RelativeLayout feed_view;
    public ViewPager viewPager;
    RecyclerView comment_RV;

    String pid,name,dp_url;

    private ShimmerFrameLayout shimmer_view;
    private FirestoreRecyclerAdapter adapter;

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private RelativeLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference club_post_colref = db.collection("post_club");
    CollectionReference club_colref = db.collection("club");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_post_details);

        initUI();
        bindActivity();

        mAppBarLayout.addOnOffsetChangedListener((AppBarLayout.OnOffsetChangedListener) this);

        startAlphaAnimation(mTitle, 0, View.VISIBLE);

        setupFireStore();

    }
    void setupFireStore() {
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();


        // manually create composite query for each year & dept
        // [cse | it | ece | eee | mech | bme | chemical | civil | admin]

        Query query = FirebaseFirestore.getInstance().collection("post_club").whereEqualTo("id",pid);
        FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
            @NonNull
            @Override
            public Club parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);

                final Club post = new Club();
                /*post.setId(snapshot.getString("cid"));
                post.setPid(snapshot.getString("id"));
                post.setAuthor(snapshot.getString("author"));
                post.setTitle(snapshot.getString("title"));
                post.setLike(Integer.parseInt(snapshot.get("like").toString()));
                post.setDescription(snapshot.getString("description"));
                post.setComments((ArrayList<String>) snapshot.get("comment"));
                post.setTime(snapshot.getTimestamp("time").toDate());


                ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if (images != null && images.size() > 0)
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
                    } else {
                        post.setFileName(null);
                        post.setFileUrl(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: " + Arrays.toString(e.getStackTrace()) + " \n Error: " + e.getMessage());
                    post.setFileName(null);
                    post.setFileUrl(null);
                }*/
                return post;
            }
        }).build();

        adapter = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {

                /*String author = "";
                String email = model.getAuthor();
                email = email.substring(0, email.indexOf("@"));
                for (int j = 0; j < email.length(); j++) {
                    if (Character.isDigit(email.charAt(j))) {
                        author = email.substring(0, j);
                        break;
                    }
                }
                if (author.isEmpty()) {
                    author = email;
                }


                tv_author.setText(author);
                layout_title.setText(name);

                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(model.getAuthor());
                TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
                userImageIV.setImageDrawable(ic1);
                Glide.with(getApplicationContext()).load(dp_url).placeholder(R.drawable.ic_user_white).into(layout_dp_iv);
                tv_title.setText(model.getTitle());

                if (SharedPref.getBoolean(getApplicationContext(), "post_liked", model.getPid())) {
                    like.setImageResource(R.drawable.blue_heart);

                } else {
                    like.setImageResource(R.drawable.heart);
                }

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

                tv_time.setText(timer);

                if (model.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    tv_description.setText(ss);
                } else
                    tv_description.setText(model.getDescription().trim());

                if (model.getImageUrl() != null && model.getImageUrl().size() != 0) {
                    viewPager.setVisibility(View.VISIBLE);

                    final ClubImageAdapter imageAdapter = new ClubImageAdapter(ClubPostDetailsActivity.this, model.getImageUrl(), true, model, timer);
                    viewPager.setAdapter(imageAdapter);

                    if (model.getImageUrl().size() == 1) {
                        tv_current_image.setVisibility(View.GONE);
                    } else {
                        tv_current_image.setVisibility(View.VISIBLE);
                        tv_current_image.setText(String.valueOf(1) + " / " + String.valueOf(model.getImageUrl().size()));
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int pos) {
                                tv_current_image.setText(String.valueOf(pos + 1) + " / " + String.valueOf(model.getImageUrl().size()));
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }
                } else {
                    viewPager.setVisibility(View.GONE);
                    tv_current_image.setVisibility(View.GONE);
                }

                shimmer_view.setVisibility(View.GONE);
                */
            }


            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(ClubPostDetailsActivity.this).inflate(R.layout.club_post_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        comment_RV.setAdapter(adapter);
    }

    void initUI() {
        pid = getIntent().getExtras().getString("pid");
        name = getIntent().getExtras().getString("name");
        dp_url = getIntent().getExtras().getString("dp_url");

        userImageIV =findViewById(R.id.userImageIV_com);
        tv_author = findViewById(R.id.tv_author_com);
        tv_time = findViewById(R.id.tv_time_com);
        viewPager = findViewById(R.id.viewPager_com);
        tv_title = findViewById(R.id.tv_title_com);
        tv_description = findViewById(R.id.tv_description_com);
        like = findViewById(R.id.like_IV_com);
        like_count = findViewById(R.id.like_count_tv_com);
        shimmer_view = findViewById(R.id.shimmer_view_com);
        comment_etv = findViewById(R.id.comment_etv);
        layout_dp_iv = findViewById(R.id.layout_dp_iv_3);
        layout_title = findViewById(R.id.layout_title_tv_com);
        tv_current_image = findViewById(R.id.currentImageTV_com);

        comment_RV = findViewById(R.id.comment_RV);
        comment_RV.setLayoutManager(new LinearLayoutManager(ClubPostDetailsActivity.this));

    }

    /*********************************************************/

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author, tv_club, tv_title, tv_time, tv_current_image, like_count, comment_count;
        public SocialTextView tv_description;
        public ImageView userImageIV, like, comment;
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
            like_count = itemView.findViewById(R.id.like_count_tv);
            comment_count = itemView.findViewById(R.id.comment_count_tv);
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void bindActivity() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar_com);
        mTitle = (TextView) findViewById(R.id.layout_title_tv_com);
        mTitleContainer = (RelativeLayout) findViewById(R.id.feed_view_com);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbarlayout_3);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}
