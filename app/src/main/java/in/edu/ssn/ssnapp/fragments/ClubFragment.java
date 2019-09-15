package in.edu.ssn.ssnapp.fragments;


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

    public ClubFragment() { }

    private RecyclerView subs_RV, unsubs_RV;
    private FirestoreRecyclerAdapter subs_adap;
    private FirestoreRecyclerAdapter unsubs_adap;
    Boolean isHead;
    Long followers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(), view);

        initUI(view);
        setupFireStore();

        return view;
    }

    void setupFireStore() {
        Query query = FirebaseFirestore.getInstance().collection("club");
        FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
            @NonNull
            @Override
            public Club parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                final Club club = new Club();
                club.setId(snapshot.getString("id"));
                club.setName(snapshot.getString("name"));
                club.setDp_url(snapshot.getString("dp_url"));
                club.setCover_url(snapshot.getString("cover_url"));
                club.setContact(snapshot.getString("contact"));
                club.setDescription(snapshot.getString("description"));
                club.setFollowers(snapshot.getLong("followers"));
                try {
                    club.setHead((ArrayList<String>) snapshot.get("head"));
                }
                catch (Exception e){
                    e.printStackTrace();
                    club.setHead(null);
                }

                /*ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if (images != null && images.size() > 0)
                    club.setImageUrl(images);
                else
                    club.setImageUrl(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            String tv_name = files.get(i).get("tv_name");
                            if (tv_name.length() > 13)
                                tv_name = tv_name.substring(0, tv_name.length() - 13);
                            fileName.add(tv_name);
                            fileUrl.add(files.get(i).get("url"));
                        }
                        club.setFileName(fileName);
                        club.setFileUrl(fileUrl);
                    } else {
                        club.setFileName(null);
                        club.setFileUrl(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: " + Arrays.toString(e.getStackTrace()) + " \n Error: " + e.getMessage());
                    club.setFileName(null);
                    club.setFileUrl(null);
                }*/

                return club;
            }
        }).build();

        subs_adap = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {

                // checking for club subscription....
                if (SharedPref.getBoolean(getContext(), "club", model.getId())) {
                    holder.tv_name.setText(model.getName());
                    holder.tv_description.setText(model.getDescription());
                    followers = model.getFollowers();

                    try {
                        Glide.with(getContext()).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.iv_dp);
                    }
                    catch (Exception e){
                        holder.iv_dp.setImageResource(R.color.shimmering_back);
                    }

                    if (SharedPref.getBoolean(getContext(), "club", model.getId())) {
                        holder.iv_follow.setImageResource(R.drawable.follow_blue);
                    } else {
                        holder.iv_follow.setImageResource(R.drawable.follow);
                    }

                    holder.iv_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SharedPref.getBoolean(getContext(), "club", model.getId())) {
                                holder.iv_follow.setImageResource(R.drawable.follow);
                                SharedPref.putBoolean(getContext(), "club", model.getId(), false);
                                
                                model.setFollowers(model.getFollowers() - 1);
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", model.getFollowers());
                                
                                FirebaseFirestore.getInstance().collection("club").document(model.getId()).set(follower_det, SetOptions.merge());
                            } 
                            else {
                                holder.iv_follow.setImageResource(R.drawable.follow_blue);
                                SharedPref.putBoolean(getContext(), "club", model.getId(), true);

                                model.setFollowers(model.getFollowers() + 1);
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", model.getFollowers());
                                FirebaseFirestore.getInstance().collection("club").document(model.getId()).set(follower_det, SetOptions.merge());
                            }
                        }
                    });

                    //isHead for club head...
                    if (model.getHead().contains(SharedPref.getString(getContext(), "email")))
                        isHead = true;
                    else
                        isHead = false;
                    
                    holder.club_RL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ClubPageActivity.class);
                            intent.putExtra("data", model);
                            intent.putExtra("isHead", isHead);
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

                // checking for club unsubscription....
                if (!SharedPref.getBoolean(getContext(),"club", model.getId())) {
                    holder.tv_name.setText(model.getName());
                    holder.tv_description.setText(model.getDescription());
                    followers = model.getFollowers();

                    try {
                        Glide.with(getContext()).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.iv_dp);
                    }
                    catch (Exception e){
                        holder.iv_dp.setImageResource(R.color.shimmering_back);
                    }

                    if (SharedPref.getBoolean(getContext(), "club", model.getId())) {
                        holder.iv_follow.setImageResource(R.drawable.follow_blue);
                    } else {
                        holder.iv_follow.setImageResource(R.drawable.follow);
                    }

                    holder.iv_follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SharedPref.getBoolean(getContext(), "club", model.getId())) {
                                holder.iv_follow.setImageResource(R.drawable.follow);
                                SharedPref.putBoolean(getContext(), "club", model.getId(), false);

                                model.setFollowers(model.getFollowers()-1);
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", model.getFollowers());

                                FirebaseFirestore.getInstance().collection("club").document(model.getId()).set(follower_det, SetOptions.merge());
                            }
                            else {
                                holder.iv_follow.setImageResource(R.drawable.follow_blue);
                                SharedPref.putBoolean(getContext(), "club", model.getId(), true);

                                model.setFollowers(model.getFollowers()+1);
                                Map<String, Object> follower_det = new HashMap<>();
                                follower_det.put("followers", model.getFollowers());
                                FirebaseFirestore.getInstance().collection("club").document(model.getId()).set(follower_det, SetOptions.merge());
                            }
                        }
                    });

                    //isHead for club head...
                    if (model.getHead().contains(SharedPref.getString(getContext(), "email")))
                        isHead = true;
                    else
                        isHead = false;

                    holder.club_RL.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), ClubPageActivity.class);
                            intent.putExtra("data", model);
                            intent.putExtra("isHead", isHead);
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
        TextView tv_name, tv_description;
        ImageView iv_dp, iv_follow;


        public FeedViewHolder(View itemView) {
            super(itemView);
            
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_description = itemView.findViewById(R.id.tv_description);
            iv_dp = itemView.findViewById(R.id.iv_dp);
            iv_follow = itemView.findViewById(R.id.iv_follow);
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
}