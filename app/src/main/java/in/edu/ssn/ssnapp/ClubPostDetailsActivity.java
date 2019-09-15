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

import in.edu.ssn.ssnapp.adapters.ClubPostImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class ClubPostDetailsActivity extends AppCompatActivity {

    public TextView tv_author, tv_club, tv_title, tv_time, tv_current_image, like_count, comment_count, layout_title;
    public SocialTextView tv_description;
    public EditText comment_etv;
    public ImageView userImageIV, like, layout_dp_iv;
    public RelativeLayout feed_view;
    public ViewPager viewPager;
    RecyclerView comment_RV;

    ClubPost clubpost;
    Club club;

    private ShimmerFrameLayout shimmer_view;
    private FirestoreRecyclerAdapter adapter;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference club_post_colref = db.collection("post_club");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_post_details);

        initUI();

        //setupFireStore();

    }

    /*void setupFireStore() {
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();


        // manually create composite query for each year & dept
        // [cse | it | ece | eee | mech | bme | chemical | civil | admin]

        Query query = FirebaseFirestore.getInstance().collection("post_club").whereEqualTo("id", clubpost.getId());
        FirestoreRecyclerOptions<ClubPost> options = new FirestoreRecyclerOptions.Builder<ClubPost>().setQuery(query, new SnapshotParser<ClubPost>() {
            @NonNull
            @Override
            public ClubPost parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);
                final ClubPost post = new ClubPost();
                post.setId(snapshot.getString("id"));
                post.setCid(snapshot.getString("cid"));
                post.setAuthor(snapshot.getString("author"));
                post.setTitle(snapshot.getString("title"));
                post.setDescription(snapshot.getString("description"));
                post.setTime(snapshot.getTimestamp("time").toDate());
                try {
                    post.setLike((ArrayList<String>) snapshot.get("like"));
                    Log.i("app_test", "  like collected ");


                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("app_test", "  like not collected "+ e.toString());
                    post.setLike(null);
                }
                try {
                    post.setComment((ArrayList<String>) snapshot.get("comment"));
                    Log.i("app_test", "  comment collected ");


                }catch (Exception e){
                    e.printStackTrace();
                    post.setComment(null);
                }


                ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if (images != null && images.size() > 0)
                    post.setImage_Urls(images);
                else
                    post.setImage_Urls(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            fileName.add(files.get(i).get("name"));
                            fileUrl.add(files.get(i).get("url"));
                        }
                        post.setFile_name(fileName);
                        post.setFile_urls(fileUrl);
                    } else {
                        post.setFile_name(null);
                        post.setFile_urls(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: " + Arrays.toString(e.getStackTrace()) + " \n Error: " + e.getMessage());
                    post.setFile_name(null);
                    post.setFile_urls(null);
                }
                return post;
            }
        }).build();

        adapter = new FirestoreRecyclerAdapter<ClubPost, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final ClubPost post) {

                String author = "";
                String email = clubpost.getAuthor();
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
                layout_title.setText(club.getName());

                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(clubpost.getAuthor());
                TextDrawable ic1 = builder.build(String.valueOf(clubpost.getAuthor().charAt(0)), color);
                userImageIV.setImageDrawable(ic1);
                Glide.with(getApplicationContext()).load(club.getDp_url()).placeholder(R.drawable.ic_user_white).into(layout_dp_iv);
                tv_title.setText(clubpost.getTitle());

                if (SharedPref.getBoolean(getApplicationContext(), "post_liked", clubpost.getId())) {
                    like.setImageResource(R.drawable.blue_heart);

                } else {
                    like.setImageResource(R.drawable.heart);
                }

                Date time = clubpost.getTime();
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

                if (clubpost.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(clubpost.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    tv_description.setText(ss);
                } else
                    tv_description.setText(clubpost.getDescription().trim());

                if (clubpost.getImage_Urls() != null && clubpost.getImage_Urls().size() != 0) {
                    viewPager.setVisibility(View.VISIBLE);

                    final ClubPostImageAdapter imageAdapter = new ClubPostImageAdapter(ClubPostDetailsActivity.this, clubpost.getImage_Urls(), true, clubpost, timer);
                    viewPager.setAdapter(imageAdapter);

                    if (clubpost.getImage_Urls().size() == 1) {
                        tv_current_image.setVisibility(View.GONE);
                    } else {
                        tv_current_image.setVisibility(View.VISIBLE);
                        tv_current_image.setText(String.valueOf(1) + " / " + String.valueOf(clubpost.getImage_Urls().size()));
                        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int pos) {
                                tv_current_image.setText(String.valueOf(pos + 1) + " / " + String.valueOf(clubpost.getImage_Urls().size()));
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
                if (clubpost.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                    like.setImageResource(R.drawable.blue_heart);

                } else {
                    like.setImageResource(R.drawable.heart);
                }

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!clubpost.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                            like.setImageResource(R.drawable.blue_heart);
                            clubpost.getLike().add(SharedPref.getString(getApplicationContext(), "email"));

                        } else {
                            like.setImageResource(R.drawable.heart);
                            {
                                clubpost.getLike().remove(SharedPref.getString(getApplicationContext(), "email"));
                            }
                        }
                        Map<String, Object> likes_details = new HashMap<>();
                        likes_details.put("like", clubpost.getLike());
                        club_post_colref.document(clubpost.getId()).set(likes_details, SetOptions.merge());

                    }
                });
            }


            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(ClubPostDetailsActivity.this).inflate(R.layout.comment_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        comment_RV.setAdapter(adapter);
    }*/

    void initUI() {
        clubpost = getIntent().getParcelableExtra("ClubPost");
        club = getIntent().getParcelableExtra("Club");


        userImageIV = findViewById(R.id.userImageIV_com);
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
        RelativeLayout comment_RL;
        ImageView user_dp_iv;
        TextView user_name_tv, comment_tv, reply_tv, view_reply_tv;
        EditText reply_etv;
        RecyclerView reply_RV;


        public FeedViewHolder(View itemView) {
            super(itemView);
            comment_RL = itemView.findViewById(R.id.com_view_RL);
            user_dp_iv = itemView.findViewById(R.id.user_iv_com);
            user_name_tv = itemView.findViewById(R.id.user_name_tv_com);
            comment_tv = itemView.findViewById(R.id.comment_tv);
            reply_tv = itemView.findViewById(R.id.reply_tv);
            view_reply_tv = itemView.findViewById(R.id.view_reply_tv);
            reply_etv = itemView.findViewById(R.id.reply_etv);
            reply_etv.setVisibility(View.GONE);
            reply_RV = itemView.findViewById(R.id.view_reply_RV);
            reply_RV.setVisibility(View.GONE);
        }
    }

    /*********************************************************/

    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //adapter.stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
