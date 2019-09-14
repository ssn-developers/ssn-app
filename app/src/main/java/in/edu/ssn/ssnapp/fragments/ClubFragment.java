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
import android.widget.Toast;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ClubImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubFragment extends Fragment {

    public ClubFragment() { }

    private RecyclerView feedsRV;
    private FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_club, container, false);
        CommonUtils.initFonts(getContext(),view);
        initUI(view);

        setupFireStore();

        return view;
    }

    /*********************************************************/

    void setupFireStore(){
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();


        // manually create composite query for each year & dept
        // [cse | it | ece | eee | mech | bme | chemical | civil | admin]

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
                post.setDescription(snapshot.getString("description"));
                //post.setFollowers(Integer.valueOf(snapshot.get("followers").toString()));

                return post;
            }
        })
        .build();

        adapter = new FirestoreRecyclerAdapter<Club, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Club model) {
                holder.tv_club.setText(model.getClub_name());
                Glide.with(getContext()).load(model.getClub_image_url()).placeholder(R.drawable.ic_user_white).into(holder.dp_iv);
                if(SharedPref.getBoolean(getContext(),"club_subscribed",model.getId()))
                {
                    holder.follow_iv.setImageResource(R.drawable.follow_blue);
                }else{
                    holder.follow_iv.setImageResource(R.drawable.follow);
                }
                holder.Club_rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(),ClubPageActivity.class);
                        intent.putExtra("dp_url",model.getClub_image_url());
                        intent.putExtra("cover_url",model.getClub_cover_image_url());
                        intent.putExtra("name",model.getClub_name());
                        intent.putExtra("followers",model.getFollowers());
                        intent.putExtra("description",model.getDescription());
                        startActivity(intent);
                    }
                });
                holder.follow_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(SharedPref.getBoolean(getContext(),"club_subscribed",model.getId()))
                        {
                            holder.follow_iv.setImageResource(R.drawable.follow);
                            SharedPref.putBoolean(getContext(),"club_subscribed",model.getId(),false);
                        }
                        else{
                            holder.follow_iv.setImageResource(R.drawable.follow_blue);
                            SharedPref.putBoolean(getContext(),"club_subscribed",model.getId(),true);
                        }
                    }
                });

            }

            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.club_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        feedsRV.setAdapter(adapter);
    }

    void initUI(View view){
        feedsRV = view.findViewById(R.id.feedsRV_club);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);

    }

    /*********************************************************/

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_club;
        public ImageView dp_iv,follow_iv;
        public RelativeLayout Club_rl;

        public FeedViewHolder(View itemView) {
            super(itemView);

            tv_club = itemView.findViewById(R.id.club_tv);
            dp_iv = itemView.findViewById(R.id.dp_iv);
            follow_iv = itemView.findViewById(R.id.follow_iv);
            Club_rl = itemView.findViewById(R.id.club_RL);
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

    /**********************************************************/

    private void handleBottomSheet(View v,final Club post) {
       /* RelativeLayout ll_save,ll_share;
        final TextView tv_save;

        final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(getContext());
        View sheetView=getActivity().getLayoutInflater().inflate(R.layout.bottom_menu,null);
        bottomSheetDialog.setContentView(sheetView);

        ll_save=sheetView.findViewById(R.id.saveLL);
        ll_share=sheetView.findViewById(R.id.shareLL);
        tv_save=sheetView.findViewById(R.id.tv_save);

        final DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(getContext());
        if(dataBaseHelper.checkPost(post.getId()))
            tv_save.setText("Remove from Favourites");
        else
            tv_save.setText("Add to Favourites");

        bottomSheetDialog.show();

        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataBaseHelper.checkPost(post.getId())){
                    dataBaseHelper.deletePost(post.getId());
                    tv_save.setText("Add to Favourites");
                }
                else{
                    tv_save.setText("Remove from Favourites");
                    dataBaseHelper.addPost(post);
                }
                bottomSheetDialog.hide();
            }
        });

        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + post.getAuthor().trim() + ". Check it out: http://ssnportal.cf/share.html?id=" + post.getId();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });*/
    }
}