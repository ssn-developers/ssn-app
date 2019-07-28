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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.crashlytics.android.Crashlytics;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class FacultyFeedFragment extends Fragment {

    public FacultyFeedFragment() { }

    RecyclerView feedsRV;
    RelativeLayout layout_progress;
    ShimmerFrameLayout shimmer_view;
    FirestoreRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        CommonUtils.initFonts(getContext(), view);
        initUI(view);

        setupFireStore();

        return view;
    }

    /*********************************************************/

    void setupFireStore(){
        String dept = SharedPref.getString(getContext(),"dept");

        //TODO: Needs to manually create composite query before release for each dept. [VERY IMPORTANT]

        Query query = FirebaseFirestore.getInstance().collection("post").whereArrayContains("dept", dept).orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, new SnapshotParser<Post>() {
            @NonNull
            @Override
            public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);
                layout_progress.setVisibility(View.GONE);

                final Post post = new Post();
                post.setId(snapshot.getString("id"));
                post.setTitle(snapshot.getString("title"));
                post.setDescription(snapshot.getString("description"));
                post.setTime(snapshot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) snapshot.get("img_urls");
                if(images != null && images.size() != 0)
                    post.setImageUrl(images);
                else
                    post.setImageUrl(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) snapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            fileName.add((String) files.get(i).get("name"));
                            fileUrl.add((String) files.get(i).get("url"));
                        }
                        post.setFileName(fileName);
                        post.setFileUrl(fileUrl);
                        Log.d("test_set", "size:" + fileName.size());
                    }
                    else {
                        post.setFileName(null);
                        post.setFileUrl(null);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
                    post.setFileName(null);
                    post.setFileUrl(null);
                }

                try {
                    ArrayList<String> dept = (ArrayList<String>) snapshot.get("dept");
                    if (dept != null && dept.size() != 0)
                        post.setDept(dept);
                    else
                        post.setDept(null);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
                    post.setDept(null);
                }

                try {
                    ArrayList<String> years = new ArrayList<>();
                    Map<Object, Boolean> year = (HashMap<Object, Boolean>) snapshot.get("year");
                    for (Map.Entry<Object, Boolean> entry : year.entrySet()) {
                        if (entry.getValue().booleanValue())
                            years.add((String) entry.getKey());
                    }
                    Collections.sort(years);
                    post.setYear(years);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
                }

                String id = snapshot.getString("author");

                post.setAuthor_image_url(SharedPref.getString(getContext(),"faculty",id + "_dp_url"));

                String name = SharedPref.getString(getContext(),"faculty",id + "_name");
                if(name!=null && !name.equals(""))
                    post.setAuthor(SharedPref.getString(getContext(),"faculty",id + "_name"));
                else
                    post.setAuthor("SSN Institutions");

                String position = SharedPref.getString(getContext(),"faculty",id + "_position");
                if(position!=null && !position.equals(""))
                    post.setPosition(SharedPref.getString(getContext(),"faculty",id + "_position"));
                else
                    post.setPosition("Admin");

                return post;
            }
        })
        .build();

        adapter = new FirestoreRecyclerAdapter<Post, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Post model) {
                holder.tv_author.setText(model.getAuthor());

                try {
                    if (model.getAuthor_image_url() != null)
                        Picasso.get().load(model.getAuthor_image_url()).placeholder(R.drawable.ic_user_white).into(holder.userImageIV);
                    else
                        holder.userImageIV.setImageResource(R.drawable.ic_user_white);
                }
                catch (Exception e){
                    e.printStackTrace();
                    holder.userImageIV.setImageResource(R.drawable.ic_user_white);
                    Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
                }

                holder.tv_position.setText(model.getPosition());
                holder.tv_title.setText(model.getTitle());

                Date time = model.getTime();
                Date now = new Date();
                Long t = now.getTime() - time.getTime();
                String timer;

                if(t < 60000)
                    timer = Long.toString(t / 1000) + "s ago";
                else if(t < 3600000)
                    timer = Long.toString(t / 60000) + "m ago";
                else if(t < 86400000)
                    timer = Long.toString(t / 3600000) + "h ago";
                else if(t < 604800000)
                    timer = Long.toString(t/86400000) + "d ago";
                else if(t < 2592000000L)
                    timer = Long.toString(t/604800000) + "w ago";
                else if(t < 31536000000L)
                    timer = Long.toString(t/2592000000L) + "M ago";
                else
                    timer = Long.toString(t/31536000000L) + "y ago";

                holder.tv_time.setText(timer);

                if(model.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    holder.tv_description.setText(ss);
                }
                else
                    holder.tv_description.setText(model.getDescription().trim());

                if(model.getImageUrl() != null && model.getImageUrl().size() != 0) {
                    holder.viewPager.setVisibility(View.VISIBLE);

                    final ImageAdapter imageAdapter = new ImageAdapter(getContext(), model.getImageUrl(),true, model, timer);
                    holder.viewPager.setAdapter(imageAdapter);

                    if(model.getImageUrl().size()==1){
                        holder.tv_current_image.setVisibility(View.GONE);
                    }
                    else {
                        holder.tv_current_image.setVisibility(View.VISIBLE);
                        holder.tv_current_image.setText(String.valueOf(1)+" / "+String.valueOf(model.getImageUrl().size()));
                        holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int pos) {
                                holder.tv_current_image.setText(String.valueOf(pos+1)+" / "+String.valueOf(model.getImageUrl().size()));
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }
                }
                else {
                    holder.viewPager.setVisibility(View.GONE);
                    holder.tv_current_image.setVisibility(View.GONE);
                }

                holder.feed_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                        intent.putExtra("post", model);
                        intent.putExtra("time", holder.tv_time.getText().toString());
                        startActivity(intent);
                    }
                });
                holder.feed_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        handleBottomSheet(v,model);
                        return true;
                    }
                });

                shimmer_view.setVisibility(View.GONE);
            }

            @Override
            public FeedViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext()).inflate(R.layout.student_post_item, group, false);
                return new FeedViewHolder(view);
            }
        };

        feedsRV.setAdapter(adapter);
    }

    void initUI(View view){
        feedsRV = view.findViewById(R.id.feedsRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);
        feedsRV.setHasFixedSize(true);
        shimmer_view = view.findViewById(R.id.shimmer_view);
        layout_progress = view.findViewById(R.id.layout_progress);
    }

    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_author, tv_position, tv_title, tv_time, tv_current_image;
        public SocialTextView tv_description;
        public ImageView userImageIV;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View itemView) {
            super(itemView);

            tv_author = itemView.findViewById(R.id.tv_author);
            tv_position = itemView.findViewById(R.id.tv_position);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_current_image = itemView.findViewById(R.id.currentImageTV);
            userImageIV = itemView.findViewById(R.id.userImageIV);
            feed_view = itemView.findViewById(R.id.feed_view);
            viewPager = itemView.findViewById(R.id.viewPager);
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

    void handleBottomSheet(View v,final Post post) {
        RelativeLayout ll_save,ll_share;
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
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + post.getAuthor().trim() + ". Check it out: http://ssnportal.cf/" + post.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }
}