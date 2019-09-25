package in.edu.ssn.ssnapp.fragments;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.SubscribeFeedsAdapter;
import in.edu.ssn.ssnapp.adapters.UnSubscribeAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubFragment extends Fragment {

    public ClubFragment() { }

    private RecyclerView subs_RV, unsubs_RV, feed_RV;
    private TextView tv_suggestion;
    private RelativeLayout layout_subscribed, layout_empty_feed;
    private FirestoreRecyclerAdapter subs_adap;

    private boolean darkMode;
    private List<Club> clubs, subscribed_clubs;
    private List<ClubPost> post;
    Map<String, Object> mClub;
    private List<Map<String, Object>> subscribe_post;

    private UnSubscribeAdapter adapter;
    private SubscribeFeedsAdapter subscribe_adapter;
    private ShimmerFrameLayout shimmer_view;
    ListenerRegistration listenerRegistration;

    private TextView tv_text1,tv_text2,tv_text11,tv_text22;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(), view);
        darkMode = SharedPref.getBoolean(getActivity().getApplicationContext(),"darkMode");

        initUI(view);
        setupFireStore();

        return view;
    }

    private void setupFireStore() {
        setUpSubcriptions();
        setUpUnSubcriptions();
    }

    private void setUpSubcriptions() {
        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_club).whereArrayContains("followers",SharedPref.getString(getContext(),"email"));
        final FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
            @NonNull
            @Override
            public Club parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                return CommonUtils.getClubFromSnapshot(getContext(),snapshot);
            }
        }).build();

        subs_adap = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {
                holder.tv_name.setText(model.getName());
                holder.tv_description.setText(model.getDescription());
                FCMHelper.SubscribeToTopic(getContext(),"club_" + model.getId());

                try {
                    Glide.with(getContext()).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.iv_dp);
                }
                catch (Exception e){
                    holder.iv_dp.setImageResource(R.color.shimmering_back);
                }

                holder.lottie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseFirestore.getInstance().collection(Constants.collection_club).document(model.getId()).update("followers", FieldValue.arrayRemove(SharedPref.getString(getContext(),"email")));
                        model.getFollowers().remove(SharedPref.getString(getContext(),"email"));
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
                        Bungee.slideLeft(getContext());
                    }
                });

                subs_RV.setVisibility(View.VISIBLE);
                layout_subscribed.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view;
                if(darkMode)
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.club_item_dark, group, false);
                else
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.club_item, group, false);
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

    private void setUpUnSubcriptions(){
        FirebaseFirestore.getInstance().collection(Constants.collection_club).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    clubs = task.getResult().toObjects(Club.class);

                    subscribed_clubs.clear();
                    for(int i = 0; i< clubs.size(); i++) {
                        Club c = clubs.get(i);
                        if(c.getFollowers().contains(SharedPref.getString(getContext(),"email"))) {
                            shimmer_view.setVisibility(View.VISIBLE);
                            subscribed_clubs.add(c);
                            clubs.remove(i);
                            i--;
                        }
                    }
                    adapter = new UnSubscribeAdapter(getContext(), clubs);
                    unsubs_RV.setAdapter(adapter);

                    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                        @Override
                        public void onChanged() {
                            super.onChanged();

                            if (clubs.size() > 0)
                                tv_suggestion.setVisibility(View.VISIBLE);
                            else
                                tv_suggestion.setVisibility(View.GONE);
                        }
                    });

                    adapter.notifyDataSetChanged();

                    if(subscribed_clubs.size()>0) {
                        setUpFeeds();
                    }
                    else
                        layout_empty_feed.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setUpFeeds(){

        listenerRegistration=FirebaseFirestore.getInstance().collection(Constants.collection_post_club).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try{

                    if(queryDocumentSnapshots!=null){
                        post = queryDocumentSnapshots.toObjects(ClubPost.class);

                        subscribe_post.clear();

                        for(int i = 0; i< post.size(); i++) {
                            ClubPost c = post.get(i);
                            for(int j=0; j<subscribed_clubs.size(); j++){
                                if(c.getCid().equals(subscribed_clubs.get(j).getId())){
                                    mClub = new HashMap<>();
                                    mClub.put("club",subscribed_clubs.get(j));
                                    mClub.put("post",c);
                                    subscribe_post.add(mClub);
                                    break;
                                }
                            }
                        }

                        Collections.sort(subscribe_post, new Comparator<Map<String, Object>> () {
                            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                                return (((ClubPost) m2.get("post")).getTime()).compareTo(((ClubPost) m1.get("post")).getTime());
                            }
                        });

                        ArrayList s_club = new ArrayList();
                        ArrayList s_post = new ArrayList();

                        for(Map<String, Object> map:subscribe_post){
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if(entry.getKey().equals("club"))
                                    s_club.add((Club) entry.getValue());
                                else
                                    s_post.add((ClubPost) entry.getValue());
                            }
                        }

                        subscribe_adapter = new SubscribeFeedsAdapter(getContext(), s_club, s_post);
                        feed_RV.setAdapter(subscribe_adapter);

                        //TODO:Realtime change need to be done

                        shimmer_view.setVisibility(View.GONE);
                        if(subscribe_post.size() == 0)
                            layout_empty_feed.setVisibility(View.VISIBLE);
                        else
                            layout_empty_feed.setVisibility(View.GONE);

                        subscribe_adapter.notifyDataSetChanged();
                    }
                    else{
                        shimmer_view.setVisibility(View.GONE);
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }
        });

    }

    private void initUI(View view) {
        subs_RV = view.findViewById(R.id.subs_RV);
        unsubs_RV = view.findViewById(R.id.unsubs_RV);
        feed_RV = view.findViewById(R.id.feed_RV);
        tv_suggestion = view.findViewById(R.id.tv_suggestion);
        layout_subscribed = view.findViewById(R.id.layout_subscribed);
        layout_empty_feed = view.findViewById(R.id.layout_empty_feed);
        shimmer_view = view.findViewById(R.id.shimmer_view);

        tv_text1 = view.findViewById(R.id.tv_text1);
        tv_text2 = view.findViewById(R.id.tv_text2);
        tv_text11 = view.findViewById(R.id.tv_text11);
        tv_text22 = view.findViewById(R.id.tv_text22);

        if(darkMode){
            tv_text1.setTextColor(Color.WHITE);
            tv_text2.setTextColor(Color.WHITE);
            tv_text11.setTextColor(Color.WHITE);
            tv_text22.setTextColor(Color.WHITE);
            tv_suggestion.setTextColor(getResources().getColor(R.color.colorAccentDark));
        }
        else{
            tv_text1.setTextColor(getResources().getColor(R.color.colorAccentText));
            tv_text2.setTextColor(getResources().getColor(R.color.colorAccentText));
            tv_text11.setTextColor(getResources().getColor(R.color.colorAccentText));
            tv_text22.setTextColor(getResources().getColor(R.color.colorAccentText));
            tv_suggestion.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        subs_RV.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        unsubs_RV.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        feed_RV.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));

        subscribed_clubs = new ArrayList<Club>();
        subscribe_post = new ArrayList<>();
        post = new ArrayList<ClubPost>();
    }

    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout club_RL;
        TextView tv_name, tv_description;
        ImageView iv_dp;
        LottieAnimationView lottie;

        public FeedViewHolder(View itemView) {
            super(itemView);
            
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_description = itemView.findViewById(R.id.tv_description);
            iv_dp = itemView.findViewById(R.id.iv_dp);
            lottie = itemView.findViewById(R.id.lottie);
            club_RL = itemView.findViewById(R.id.club_RL);

            lottie.playAnimation();
            lottie.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    if(valueAnimator.isRunning()){
                        if(lottie.getProgress() > 0.6)
                            lottie.pauseAnimation();
                    }
                }
            });
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


        if(adapter!=null)
            subs_adap.stopListening();

        if(listenerRegistration!=null)
            listenerRegistration.remove();



    }

    @Override
    public void onResume() {
        super.onResume();

        if(SharedPref.getBoolean(getContext(),"subs_changes_made")) {
            SharedPref.remove(getContext(),"subs_changes_made");

            try {
                final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(this);
                fragmentTransaction.attach(this);
                fragmentTransaction.commit();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}