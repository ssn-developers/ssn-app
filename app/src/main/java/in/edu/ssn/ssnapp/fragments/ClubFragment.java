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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.UnSubscribeAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubFragment extends Fragment {

    public ClubFragment() { }

    private RecyclerView subs_RV, unsubs_RV;
    private TextView tv_suggestion;
    private RelativeLayout layout_subscribed;
    private FirestoreRecyclerAdapter subs_adap;

    List<Club> clubs;
    UnSubscribeAdapter adapter;

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
                        FCMHelper.UnSubscribeToTopic(getContext(),"club_" + model.getId());
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

    private void setUpUnSubcriptions(){
        FirebaseFirestore.getInstance().collection(Constants.collection_club).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                }
            }
        });
    }

    private void initUI(View view) {
        subs_RV = view.findViewById(R.id.subs_RV);
        unsubs_RV = view.findViewById(R.id.unsubs_RV);
        tv_suggestion = view.findViewById(R.id.tv_suggestion);
        layout_subscribed = view.findViewById(R.id.layout_subscribed);

        subs_RV.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
        unsubs_RV.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false));
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
        subs_adap.stopListening();
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