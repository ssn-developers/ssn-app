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
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.BusRouteAdapter;
import in.edu.ssn.ssnapp.adapters.DummyAdapter;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubFragment extends Fragment {

    public ClubFragment() { }

    private RecyclerView subs_RV, unsubs_RV;
    private TextView tv_suggestion;
    private RelativeLayout layout_subscribed;
    private FirestoreRecyclerAdapter subs_adap;

    List<Club> clubs;
    DummyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(), view);

        initUI(view);

        setupFireStore();

        return view;
    }

    private void setupFireStore() {
        setUpSubcriptions();

        FirebaseFirestore.getInstance().collection("club").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    clubs = task.getResult().toObjects(Club.class);
                    for(int i = 0; i< clubs.size(); i++) {
                        Club c = clubs.get(i);
                        if(c.getFollowers().contains(SharedPref.getString(getContext(),"email"))) {
                            clubs.remove(i);
                            i--;
                        }
                    }

                    adapter = new DummyAdapter(getContext(), clubs);
                    unsubs_RV.setAdapter(adapter);
                    if(clubs.size() > 0)
                        tv_suggestion.setVisibility(View.VISIBLE);

                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();

                            if (clubs.size() == 0)
                                tv_suggestion.setVisibility(View.GONE);
                            else
                                tv_suggestion.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void setUpSubcriptions() {
        Query query = FirebaseFirestore.getInstance().collection("club").whereArrayContains("followers",SharedPref.getString(getContext(),"email"));
        final FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
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
                try {
                    club.setFollowers((ArrayList<String>) snapshot.get("followers"));
                }
                catch (Exception e){
                    e.printStackTrace();
                    club.setFollowers(null);
                }
                try {
                    club.setHead((ArrayList<String>) snapshot.get("head"));
                }
                catch (Exception e){
                    e.printStackTrace();
                    club.setHead(null);
                }

                return club;
            }
        }).build();

        subs_adap = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {
                holder.tv_name.setText(model.getName());
                holder.tv_description.setText(model.getDescription());

                try {
                    Glide.with(getContext()).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.iv_dp);
                }
                catch (Exception e){
                    holder.iv_dp.setImageResource(R.color.shimmering_back);
                }

                holder.iv_follow.setImageResource(R.drawable.follow_blue);

                holder.iv_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.iv_follow.setImageResource(R.drawable.follow);

                        Map<String, Object> follower_det = new HashMap<>();
                        model.getFollowers().remove(SharedPref.getString(getContext(),"email"));
                        follower_det.put("followers", model.getFollowers());
                        FirebaseFirestore.getInstance().collection("club").document(model.getId()).set(follower_det, SetOptions.merge());
                        clubs.add(model);
                        adapter.notifyDataSetChanged();
                    }
                });

                holder.club_RL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), ClubPageActivity.class);
                        intent.putExtra("data", model);
                        getContext().startActivity(intent);
                    }
                });

                subs_RV.setVisibility(View.VISIBLE);
                layout_subscribed.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.club_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        subs_RV.setAdapter(subs_adap);

        subs_RV.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {

            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                layout_subscribed.setVisibility(View.GONE);
                subs_RV.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if(subs_adap.getItemCount() == 0){
                    layout_subscribed.setVisibility(View.VISIBLE);
                    subs_RV.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initUI(View view) {
        subs_RV = view.findViewById(R.id.subs_RV);
        unsubs_RV = view.findViewById(R.id.unsubs_RV);
        tv_suggestion = view.findViewById(R.id.tv_suggestion);
        layout_subscribed = view.findViewById(R.id.layout_subscribed);

        subs_RV.setLayoutManager(new LinearLayoutManager(getContext()));
        subs_RV.setNestedScrollingEnabled(false);
        unsubs_RV.setLayoutManager(new LinearLayoutManager(getContext()));
        unsubs_RV.setNestedScrollingEnabled(false);

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subs_adap.stopListening();
    }

    /*@Override
    public void onResume() {
        super.onResume();
        subs_adap.startListening();
    }*/

    @Override
    public void onStop() {
        super.onStop();
    }
}