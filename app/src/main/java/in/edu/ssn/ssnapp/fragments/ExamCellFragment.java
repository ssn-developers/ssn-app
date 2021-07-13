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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import in.edu.ssn.ssnapp.ChooseSemesterActivity;
import in.edu.ssn.ssnapp.GradeCalculatorActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ExamCellFragment extends Fragment {

    private static final String TAG = "testtt";
    boolean darkMode = false;
    RecyclerView feedsRV;
    LinearLayoutManager layoutManager;
    LinearLayout linearlayout1;
    RelativeLayout layout_progress;
    ShimmerFrameLayout shimmer_view;
    FirestoreRecyclerAdapter adapter;
    CardView gpaCV, passmarkCV;
    TextView newPostTV, linkTitleTV1, linkTitleTV2;
    public ExamCellFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: "+SharedPref.getString(getContext(), "dept"));
        Log.i(TAG, "onCreateView: "+SharedPref.getInt(getContext(), "year"));
        CommonUtils.addScreen(getContext(), getActivity(), "ExamCellFragment");

        //get the darkmode variable
        darkMode = SharedPref.getBoolean(getContext(), "dark_mode");
        View view;

        //check if darkmode is enabled and open the appropriate layout.
        if (darkMode)
            view = inflater.inflate(R.layout.fragment_exam_feed_dark, container, false);
        else
            view = inflater.inflate(R.layout.fragment_exam_feed, container, false);

        //instantiate fonts
        CommonUtils.initFonts(getContext(), view);
        initUI(view);

        //Load up firestore collection.
        setupFireStore();

        //Referesh page for new posts
        newPostTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedsRV.smoothScrollToPosition(0);
                newPostTV.setVisibility(View.GONE);
            }
        });

        //GPA Calculator
        gpaCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChooseSemesterActivity.class);
                startActivity(intent);
                Bungee.slideLeft(getContext());

            }
        });

        //Grade Calculator
        passmarkCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), GradeCalculatorActivity.class);
                startActivity(intent);
                Bungee.slideLeft(getContext());
            }
        });

        return view;
    }
    /*********************************************************/

    /************************************************************************/
    //load Exam_cell_posts for the students based on their dept and year.
    void setupFireStore() {
        //get dept & year from shared pref.
        String dept = SharedPref.getString(getContext(), "dept");
        String year = "year." + SharedPref.getInt(getContext(), "year");
        Log.i(TAG, "setupFireStore: "+year +"\nsetupFireStore: "+Constants.collection_exam_cell+"\nsetupFireStore: "+dept);

        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_exam_cell).whereArrayContains("dept", dept).whereEqualTo(year, true).orderBy("time", Query.Direction.DESCENDING);

        //Get Exam_cell_posts from the Firestore.
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, new SnapshotParser<Post>() {
            @NonNull
            @Override
            public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);

                final Post post = CommonUtils.getPostFromSnapshot(getContext(), snapshot);
                post.setAuthor_image_url("examcell@ssn.edu.in");
                post.setAuthor("SSNCE COE");
                post.setPosition("Exam cell Coordinator");

                return post;
            }
        })
                .build();

        //Assign Exam_cell_posts details to UI elements
        adapter = new FirestoreRecyclerAdapter<Post, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Post model) {
                holder.titleTV.setText(model.getTitle());
                holder.timeTV.setText(CommonUtils.getTime(model.getTime()));

                //creating a short version of description less then 100 letters for display.
                //if description > 100 letters, shrink it.
                if (model.getDescription().length() > 100) {
                    SpannableString ss = new SpannableString(model.getDescription().substring(0, 100) + "... see more");
                    ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
                    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
                    holder.descriptionTV.setText(ss);
                }
                //if description < 100 letters then directly assign it.
                else
                    holder.descriptionTV.setText(model.getDescription().trim());

                //if the post has one or more images.
                if (model.getImageUrl() != null && model.getImageUrl().size() != 0) {
                    holder.viewPager.setVisibility(View.VISIBLE);

                    final ImageAdapter imageAdapter = new ImageAdapter(getContext(), model.getImageUrl(), 5, model);
                    holder.viewPager.setAdapter(imageAdapter);

                    //if the number of images is 1
                    if (model.getImageUrl().size() == 1) {
                        holder.current_imageTV.setVisibility(View.GONE);
                    }
                    //if there are more than 1 images.
                    else {
                        holder.current_imageTV.setVisibility(View.VISIBLE);
                        holder.current_imageTV.setText(1 + " / " + model.getImageUrl().size());
                        holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int pos) {
                                holder.current_imageTV.setText((pos + 1) + " / " + model.getImageUrl().size());
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }
                }
                //if the post contains no images.
                else {
                    holder.viewPager.setVisibility(View.GONE);
                    holder.current_imageTV.setVisibility(View.GONE);
                }

                //proceed to post Details Screen on clicking the post.
                holder.feed_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                        intent.putExtra("post", model);
                        intent.putExtra("type", 5);
                        startActivity(intent);
                        Bungee.slideLeft(getContext());
                    }
                });

                //Long click for favourites and share options.
                holder.feed_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CommonUtils.handleBottomSheet(v, model, 5, getContext());
                        return true;
                    }
                });

                layout_progress.setVisibility(View.GONE);
                shimmer_view.setVisibility(View.GONE);
            }

            //Get the Exam_cell_posts_Item layout.
            @Override
            public FeedViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view;
                //Get the appropriate Exam_cell_posts_Item layout based on the darkmode preference.
                if (darkMode) {
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.faculty_post_item_dark, group, false);
                } else {
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.faculty_post_item, group, false);
                }

                return new FeedViewHolder(view);
            }

            //When a new post is sensed.
            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DocumentSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                if (type == ChangeEventType.CHANGED) {
                    // New post added (Show new post available text)
                    newPostTV.setVisibility(View.VISIBLE);
                }
            }
        };

        feedsRV.setAdapter(adapter);
    }
    /************************************************************************/

    /************************************************************************/
    // Initiate variables and UI elements.
    void initUI(View view) {
        feedsRV = view.findViewById(R.id.feedsRV);
        layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);

        newPostTV = view.findViewById(R.id.newPostTV);
        feedsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    //Log.d("Feed","First item completely visible");
                    newPostTV.setVisibility(View.GONE);
                }
            }
        });

        shimmer_view = view.findViewById(R.id.shimmer_view);
        layout_progress = view.findViewById(R.id.layout_progress);
        linearlayout1 = view.findViewById(R.id.linearlayout1);

        int year = SharedPref.getInt(getContext(), "year");
        if (year > 2017)
            linearlayout1.setVisibility(View.VISIBLE);
        else
            linearlayout1.setVisibility(View.GONE);

        gpaCV = view.findViewById(R.id.gpaCV);
        passmarkCV = view.findViewById(R.id.passmarkCV);
        linkTitleTV1 = view.findViewById(R.id.linkTitleTV1);
        linkTitleTV2 = view.findViewById(R.id.linkTitleTV2);

        linkTitleTV1.setSelected(true);
        linkTitleTV2.setSelected(true);
    }
    /*********************************************************/

    /*********************************************************/
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    /*********************************************************/

    public class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView authorTV, positionTV, titleTV, timeTV, current_imageTV;
        public SocialTextView descriptionTV;
        public ImageView userImageIV;
        public RelativeLayout feed_view;
        public ViewPager viewPager;

        public FeedViewHolder(View itemView) {
            super(itemView);

            authorTV = itemView.findViewById(R.id.authorTV);
            positionTV = itemView.findViewById(R.id.positionTV);
            titleTV = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            current_imageTV = itemView.findViewById(R.id.currentImageTV);
            userImageIV = itemView.findViewById(R.id.userImageIV);
            feed_view = itemView.findViewById(R.id.feed_view);
            viewPager = itemView.findViewById(R.id.viewPager);
        }
    }
}