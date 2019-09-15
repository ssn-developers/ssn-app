package in.edu.ssn.ssnapp.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubFragment extends Fragment {

    public ClubFragment() {
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference club_colref = db.collection("club");

    private RecyclerView subs_RV, unsubs_RV;
    private FirestoreRecyclerAdapter subs_adap;
    private FirestoreRecyclerAdapter unsubs_adap;
    int clearance = 0;
    int followers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(), view);

        initUI(view);

        setupFireStore();


        return view;

    }

    void setupFireStore() {
        String dept = SharedPref.getString(getContext(), "dept");
        String year = "year." + SharedPref.getInt(getContext(), "year");

        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();

        Query query = FirebaseFirestore.getInstance().collection("club");
        FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
            @NonNull
            @Override
            public Club parseSnapshot(@NonNull DocumentSnapshot snapshot) {

                final Club post = new Club();
                post.setId(snapshot.getString("id"));
                post.setClub_name(snapshot.getString("name"));
                post.setClub_image_url(snapshot.getString("dp_url"));
                post.setClub_cover_image_url(snapshot.getString("cover_url"));
                post.setContact(Long.parseLong(snapshot.get("contact").toString()));
                post.setDescription(snapshot.getString("description"));
                post.setFollowers(Integer.parseInt(snapshot.get("followers").toString()));
                post.setHeads((ArrayList<String>) snapshot.get("head"));

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
                            String name = files.get(i).get("name");
                            if (name.length() > 13)
                                name = name.substring(0, name.length() - 13);
                            fileName.add(name);
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
                }
                return post;
            }
        })
                .build();

        subs_adap = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {

                // checking for club subscription....
                if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {

                    //clearance for club head...
                    if (model.getHeads().contains(SharedPref.getString(getContext(), "email"))) {
                        clearance = 1;
                    } else {
                        clearance = 0;
                    }
                    if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {
                        holder.club_follow.setImageResource(R.drawable.follow_blue);
                    } else {
                        holder.club_follow.setImageResource(R.drawable.follow);
                    }
                    followers = model.getFollowers();

                    Glide.with(getContext()).load(model.getClub_image_url()).placeholder(R.drawable.ic_user_white).into(holder.club_dp);
                    if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {
                        holder.club_follow.setImageResource(R.drawable.follow_blue);
                    } else {
                        holder.club_follow.setImageResource(R.drawable.follow);
                    }
                    holder.club_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {
                                holder.club_follow.setImageResource(R.drawable.follow);
                                SharedPref.putBoolean(getContext(), "club_subscribed", model.getId(), false);
                                followers--;
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", followers);
                                club_colref.document(model.getId()).set(follower_det, SetOptions.merge());
                            } else {
                                holder.club_follow.setImageResource(R.drawable.follow_blue);
                                SharedPref.putBoolean(getContext(), "club_subscribed", model.getId(), true);
                                followers++;
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", followers);
                                club_colref.document(model.getId()).set(follower_det, SetOptions.merge());
                            }
                        }
                    });
                    holder.club_RL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ClubPageActivity.class);
                            intent.putExtra("name", model.getClub_name());
                            intent.putExtra("dp_url", model.getClub_image_url());
                            intent.putExtra("cover_url", model.getClub_cover_image_url());
                            intent.putExtra("description", model.getDescription());
                            intent.putExtra("followers", followers);
                            intent.putExtra("contact", model.getContact());
                            intent.putExtra("Head_clearance", clearance);

                            getContext().startActivity(intent);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.club_item, group, false);
                return new FeedViewHolder(view);
            }
        };
        unsubs_adap = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {

                // checking for club subscription....
                if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())==null || !SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {

                    //clearance for club head...
                    if (model.getHeads().contains(SharedPref.getString(getContext(), "email"))) {
                        clearance = 1;
                    } else {
                        clearance = 0;
                    }
                    if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {
                        holder.club_follow.setImageResource(R.drawable.follow_blue);
                    } else {
                        holder.club_follow.setImageResource(R.drawable.follow);
                    }
                    followers = model.getFollowers();

                    Glide.with(getContext()).load(model.getClub_image_url()).placeholder(R.drawable.ic_user_white).into(holder.club_dp);
                    if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {
                        holder.club_follow.setImageResource(R.drawable.follow_blue);
                    } else {
                        holder.club_follow.setImageResource(R.drawable.follow);
                    }
                    holder.club_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SharedPref.getBoolean(getContext(), "club_subscribed", model.getId())) {
                                holder.club_follow.setImageResource(R.drawable.follow);
                                SharedPref.putBoolean(getContext(), "club_subscribed", model.getId(), false);
                                followers--;
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", followers);
                                club_colref.document(model.getId()).set(follower_det, SetOptions.merge());
                            } else {
                                holder.club_follow.setImageResource(R.drawable.follow_blue);
                                SharedPref.putBoolean(getContext(), "club_subscribed", model.getId(), true);
                                followers++;
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", followers);
                                club_colref.document(model.getId()).set(follower_det, SetOptions.merge());
                            }
                        }
                    });
                    holder.club_RL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ClubPageActivity.class);
                            intent.putExtra("name", model.getClub_name());
                            intent.putExtra("dp_url", model.getClub_image_url());
                            intent.putExtra("cover_url", model.getClub_cover_image_url());
                            intent.putExtra("description", model.getDescription());
                            intent.putExtra("followers", followers);
                            intent.putExtra("contact", model.getContact());
                            intent.putExtra("Head_clearance", clearance);

                            getContext().startActivity(intent);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.club_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        subs_RV.setAdapter(subs_adap);
        unsubs_RV.setAdapter(unsubs_adap);
    }

    void initUI(View view) {
        subs_RV = view.findViewById(R.id.subs_RV);
        unsubs_RV = view.findViewById(R.id.unsubs_RV);


        subs_RV.setLayoutManager(new LinearLayoutManager(getContext()));
        unsubs_RV.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout club_RL;
        TextView club_name, club_desc;
        ImageView club_dp, club_follow;


        public FeedViewHolder(View itemView) {
            super(itemView);
            club_name = itemView.findViewById(R.id.club_tv);
            club_desc = itemView.findViewById(R.id.desc_tv);
            club_dp = itemView.findViewById(R.id.dp_iv);
            club_follow = itemView.findViewById(R.id.follow_iv);
            club_RL = itemView.findViewById(R.id.club_RL);
        }
    }

    /*********************************************************/

    @Override
    public void onStart() {
        super.onStart();
        subs_adap.startListening();
        unsubs_adap.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subs_adap.stopListening();
        unsubs_adap.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        subs_adap.startListening();
        unsubs_adap.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**********************************************************/

}