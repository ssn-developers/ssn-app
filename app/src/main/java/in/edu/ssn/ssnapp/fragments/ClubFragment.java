package in.edu.ssn.ssnapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    Map<String, Object> mClub;
    ListenerRegistration feedsListener, clubListener;
    private RecyclerView subs_RV, unsubs_RV, feed_RV;
    private TextView suggestionTV;
    private RelativeLayout layout_subscribed, layout_empty_feed;
    private FirestoreRecyclerAdapter subs_adap;
    private boolean darkMode;
    private List<Club> clubs, subscribed_clubs;
    private List<ClubPost> post;
    private List<Map<String, Object>> subscribe_post;

    private UnSubscribeAdapter adapter;
    private SubscribeFeedsAdapter subscribe_adapter;
    private ShimmerFrameLayout shimmer_view;
    private TextView text1TV, text2TV, text11TV, text22TV;

    public ClubFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonUtils.addScreen(getContext(), getActivity(), "ClubFragment");
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(), view);
        darkMode = SharedPref.getBoolean(getContext(), "dark_mode");

        initUI(view);
        setupFireStore();

        return view;
    }

    /*********************************************************/

    private void initUI(View view) {
        subs_RV = view.findViewById(R.id.subs_RV);
        unsubs_RV = view.findViewById(R.id.unsubs_RV);
        feed_RV = view.findViewById(R.id.feed_RV);
        suggestionTV = view.findViewById(R.id.suggestionTV);
        layout_subscribed = view.findViewById(R.id.layout_subscribed);
        layout_empty_feed = view.findViewById(R.id.layout_empty_feed);
        shimmer_view = view.findViewById(R.id.shimmer_view);

        text1TV = view.findViewById(R.id.text1TV);
        text2TV = view.findViewById(R.id.text2TV);
        text11TV = view.findViewById(R.id.text11TV);
        text22TV = view.findViewById(R.id.text22TV);

        if (darkMode) {
            text1TV.setTextColor(Color.WHITE);
            text2TV.setTextColor(Color.WHITE);
            text11TV.setTextColor(Color.WHITE);
            text22TV.setTextColor(Color.WHITE);
            suggestionTV.setTextColor(getResources().getColor(R.color.colorAccentDark));
        } else {
            text1TV.setTextColor(getResources().getColor(R.color.colorAccentText));
            text2TV.setTextColor(getResources().getColor(R.color.colorAccentText));
            text11TV.setTextColor(getResources().getColor(R.color.colorAccentText));
            text22TV.setTextColor(getResources().getColor(R.color.colorAccentText));
            suggestionTV.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        subs_RV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        subs_RV.setNestedScrollingEnabled(false);
        unsubs_RV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        unsubs_RV.setNestedScrollingEnabled(false);
        feed_RV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        feed_RV.setNestedScrollingEnabled(false);

        subscribed_clubs = new ArrayList<Club>();
        subscribe_post = new ArrayList<>();
        post = new ArrayList<ClubPost>();
        clubs = new ArrayList<>();
    }

    /*********************************************************/

    private void setupFireStore() {
        setUpSubcriptions();
        setUpUnSubcriptions();
    }

    private void setUpSubcriptions() {
        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_club).whereArrayContains("followers", SharedPref.getString(getContext(), "email"));
        final FirestoreRecyclerOptions<Club> options = new FirestoreRecyclerOptions.Builder<Club>().setQuery(query, new SnapshotParser<Club>() {
            @NonNull
            @Override
            public Club parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                return CommonUtils.getClubFromSnapshot(getContext(), snapshot);
            }
        }).build();

        subs_adap = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {
                holder.nameTV.setText(model.getName());
                holder.descriptionTV.setText(model.getDescription());
                FCMHelper.SubscribeToTopic(getContext(), "club_" + model.getId());

                try {
                    Glide.with(getContext()).load(model.getDp_url()).placeholder(R.color.shimmering_back).into(holder.dpIV);
                } catch (Exception e) {
                    holder.dpIV.setImageResource(R.color.shimmering_back);
                }

                holder.lottie.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = SharedPref.getString(getContext(), "email");
                        FirebaseFirestore.getInstance().collection(Constants.collection_club).document(model.getId()).update("followers", FieldValue.arrayRemove(email));
                        model.getFollowers().remove(email);
                        subscribed_clubs.add(model);
                        clubs.add(model);
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
                if (darkMode)
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
                if (subs_adap.getItemCount() == 0) {
                    layout_subscribed.setVisibility(View.VISIBLE);
                    subs_RV.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setUpUnSubcriptions() {
        clubListener = FirebaseFirestore.getInstance().collection(Constants.collection_club).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    String email = "";
                    try {
                        email = SharedPref.getString(getContext(), "email");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    clubs = queryDocumentSnapshots.toObjects(Club.class);

                    subscribed_clubs.clear();
                    for (int i = 0; i < clubs.size(); i++) {
                        Club c = clubs.get(i);
                        if (email != null && c.getFollowers().contains(email)) {
                            shimmer_view.setVisibility(View.VISIBLE);
                            FCMHelper.SubscribeToTopic(getContext(), "club_" + c.getId());
                            subscribed_clubs.add(c);
                            clubs.remove(i);
                            i--;
                        } else
                            FCMHelper.UnSubscribeToTopic(getContext(), "club_" + c.getId());

                    }
                    adapter = new UnSubscribeAdapter(getContext(), clubs);
                    unsubs_RV.setAdapter(adapter);

                    if (clubs.size() > 0)
                        suggestionTV.setVisibility(View.VISIBLE);
                    else
                        suggestionTV.setVisibility(View.GONE);

                    setUpFeeds();
                }
            }
        });
    }

    private void setUpFeeds() {
        feedsListener = FirebaseFirestore.getInstance().collection(Constants.collection_post_club).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    if (queryDocumentSnapshots != null) {
                        post = queryDocumentSnapshots.toObjects(ClubPost.class);

                        subscribe_post.clear();

                        for (int i = 0; i < post.size(); i++) {
                            ClubPost c = post.get(i);
                            for (int j = 0; j < subscribed_clubs.size(); j++) {
                                if (c.getCid().equals(subscribed_clubs.get(j).getId())) {
                                    mClub = new HashMap<>();
                                    mClub.put("club", subscribed_clubs.get(j));
                                    mClub.put("post", c);
                                    subscribe_post.add(mClub);
                                    break;
                                }
                            }
                        }

                        Collections.sort(subscribe_post, new Comparator<Map<String, Object>>() {
                            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                                return (((ClubPost) m2.get("post")).getTime()).compareTo(((ClubPost) m1.get("post")).getTime());
                            }
                        });

                        ArrayList<Club> s_club = new ArrayList<>();
                        ArrayList<ClubPost> s_post = new ArrayList<>();

                        for (Map<String, Object> map : subscribe_post) {
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (entry.getKey().equals("club"))
                                    s_club.add((Club) entry.getValue());
                                else
                                    s_post.add((ClubPost) entry.getValue());
                            }
                        }

                        subscribe_adapter = new SubscribeFeedsAdapter(getContext(), s_club, s_post);
                        feed_RV.setAdapter(subscribe_adapter);

                        shimmer_view.setVisibility(View.GONE);
                        if (subscribe_post.size() == 0)
                            layout_empty_feed.setVisibility(View.VISIBLE);
                        else
                            layout_empty_feed.setVisibility(View.GONE);
                    } else
                        shimmer_view.setVisibility(View.GONE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
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

        if (adapter != null)
            subs_adap.stopListening();

        if (feedsListener != null)
            feedsListener.remove();

        if (clubListener != null)
            clubListener.remove();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SharedPref.getBoolean(getContext(), "subs_changes_made")) {
            SharedPref.remove(getContext(), "subs_changes_made");

            try {
                final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(this);
                fragmentTransaction.attach(this);
                fragmentTransaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout club_RL;
        TextView nameTV, descriptionTV;
        ImageView dpIV;
        LottieAnimationView lottie;

        public FeedViewHolder(View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            dpIV = itemView.findViewById(R.id.dpIV);
            lottie = itemView.findViewById(R.id.lottie);
            club_RL = itemView.findViewById(R.id.club_RL);

            lottie.setProgress(0.61f);
        }
    }
}