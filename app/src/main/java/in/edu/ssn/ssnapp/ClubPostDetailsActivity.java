package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ExpandableListView;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.adapters.ClubPostImageAdapter;
import in.edu.ssn.ssnapp.adapters.CustomExpandableListAdapter;
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

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    ListenerRegistration listenerRegistration;
    ArrayList<Comments> commentsArrayList=new ArrayList<>();

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
    }


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

        expandableListView =  findViewById(R.id.EV_comment);
        expandableListAdapter = new CustomExpandableListAdapter(this, new ArrayList<Comments>());

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(), expandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + " List Collapsed.",Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //Toast.makeText(getApplicationContext(),expandableListTitle.get(groupPosition) + " -> " + expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        DocumentReference documentReference= FirebaseFirestore.getInstance().collection("post_club").document( "L3xGyQRaz8yRsjqP5Jto");

        listenerRegistration= documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                ArrayList<HashMap<Object,Object>> comment_data=(ArrayList<HashMap<Object,Object>>) documentSnapshot.get("comment");

                commentsArrayList.clear();
                for(HashMap<Object,Object> i:comment_data){
                    Comments tempComment=new Comments((String)i.get("author"),(String)i.get("message"),((Timestamp)i.get("time")).toDate(),(ArrayList<HashMap<String, Object>>) i.get("reply"));
                    commentsArrayList.add(tempComment);
                }


                expandableListAdapter=null;
                //Collections.sort(commentsArrayList);
                expandableListAdapter=new CustomExpandableListAdapter(ClubPostDetailsActivity.this,commentsArrayList);
                expandableListView.setAdapter(expandableListAdapter);
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

}
