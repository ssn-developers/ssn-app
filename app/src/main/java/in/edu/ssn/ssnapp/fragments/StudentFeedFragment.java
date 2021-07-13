package in.edu.ssn.ssnapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import in.edu.ssn.ssnapp.ClubPageActivity;
import in.edu.ssn.ssnapp.NoNetworkActivity;
import in.edu.ssn.ssnapp.PdfViewerActivity;
import in.edu.ssn.ssnapp.PostDetailsActivity;
import in.edu.ssn.ssnapp.R;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class StudentFeedFragment extends Fragment {

    private static final String TAG = "StudentFeedFragmentTest";
    boolean darkMode = false;
    private RecyclerView feedsRV;
    private LinearLayoutManager layoutManager;
    private RelativeLayout layout_progress;
    private ShimmerFrameLayout shimmer_view;
    private FirestoreRecyclerAdapter adapter;
    private TextView newPostTV, linkTitleTV2;
    private CardView syllabusCV, libraryCV, lakshyaCV, lmsCV;
    private String dept;

    public StudentFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CommonUtils.addScreen(getContext(), getActivity(), "StudentFeedFragment");
        //get the darkmode variable
        darkMode = SharedPref.getBoolean(getContext(), "dark_mode");
        View view;

        //check if the darkmode enabled and open respective layout.
        if (darkMode) {
            view = inflater.inflate(R.layout.fragment_student_feed_dark, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_student_feed, container, false);
        }

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

        dept = SharedPref.getString(getContext(), "dept");

        /************************************************************************/
        //CARDVIEWS - SYLLABUS, LIBRARY, LMS, LAKSHYA

        //Library renewal
        libraryCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if connected to SSN WIFI.
                if (CommonUtils.checkWifiOnAndConnected(getContext(), "ssn")) {
                    //redirect to ssn library intranet.
                    CommonUtils.openCustomBrowser(getContext(), "http://opac.ssn.net:8081/");
                }
                //if not connected to SSN WIFI.
                else {
                    Toast toast = Toast.makeText(getContext(), "Please connect to SSN wifi ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        //SSN Lakshya
        lakshyaCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we have an active internet connection
                if (!CommonUtils.alerter(getContext())) {
                    //Redirect to Lakshya club page.
                    FirebaseFirestore.getInstance().collection(Constants.collection_club).whereEqualTo("name", Constants.lakshya).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {
                                DocumentSnapshot ds = queryDocumentSnapshots.getDocuments().get(0);
                                Club model = CommonUtils.getClubFromSnapshot(getContext(), ds);

                                Intent intent = new Intent(getContext(), ClubPageActivity.class);
                                intent.putExtra("data", model);
                                getContext().startActivity(intent);
                                Bungee.slideLeft(getContext());
                            }
                        }
                    });
                }
                //if we don't have an active internet connection
                else {
                    Intent intent = new Intent(getContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(getContext());
                }
            }
        });

        //SSN LMS SYSTEM
        lmsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we have an active internet connection
                if (!CommonUtils.alerter(getContext())) {
                    //redirect to ssn lms page.
                    CommonUtils.openCustomBrowser(getContext(), Constants.lms);
                }
                //if we don't have an active internet connection
                else {
                    Intent intent = new Intent(getContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "home");
                    startActivity(intent);
                    Bungee.fade(getContext());
                }
            }
        });

        //Syllabus
        syllabusCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get year
                int year = SharedPref.getInt(getContext(), "year");
                //2017 Anna university(AU) regulation
                if (year == 2016 || year == 2017) {
                    switch (dept) {
                        case "cse":
                            openSyllabus(Constants.cseAU);
                            break;
                        case "it":
                            openSyllabus(Constants.itAU);
                            break;
                        case "ece":
                            openSyllabus(Constants.eceAU);
                            break;
                        case "eee":
                            openSyllabus(Constants.eeeAU);
                            break;
                        case "che":
                            openSyllabus(Constants.cheAU);
                            break;
                        case "bme":
                            openSyllabus(Constants.bmeAU);
                            break;
                        case "civ":
                            openSyllabus(Constants.civAU);
                            break;
                        case "mec":
                            openSyllabus(Constants.mecAU);
                            break;
                    }
                }
                //autonomous(AN) sullabus
                else {
                    switch (dept) {
                        case "cse":
                            openSyllabus(Constants.cseAN);
                            break;
                        case "it":
                            openSyllabus(Constants.itAN);
                            break;
                        case "ece":
                            openSyllabus(Constants.eceAN);
                            break;
                        case "eee":
                            openSyllabus(Constants.eeeAN);
                            break;
                        case "che":
                            openSyllabus(Constants.cheAN);
                            break;
                        case "bme":
                            openSyllabus(Constants.bmeAN);
                            break;
                        case "civ":
                            openSyllabus(Constants.civAN);
                            break;
                        case "mec":
                            openSyllabus(Constants.mecAN);
                            break;
                    }
                }
            }
        });
        /************************************************************************/


        return view;
    }
    /*********************************************************/

    /************************************************************************/
    //load Feed posts for the students based on their dept and year.
    private void setupFireStore() {
        //get dept & year from shared pref.
        String dept = SharedPref.getString(getContext(), "dept");
        String year = "year." + SharedPref.getInt(getContext(), "year");

        //Icon creator from the first letter of a word. To create DP's using a Letter.
        final TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .toUpperCase()
                .endConfig()
                .round();

        //Get posts from the Firestore.
        Query query = FirebaseFirestore.getInstance().collection(Constants.collection_post).whereArrayContains("dept", dept).whereEqualTo(year, true).orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, new SnapshotParser<Post>() {
            @NonNull
            @Override
            public Post parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                shimmer_view.setVisibility(View.VISIBLE);
                return CommonUtils.getPostFromSnapshot(getContext(), snapshot);
            }
        }).build();

        //Assign post details to UI elements
        adapter = new FirestoreRecyclerAdapter<Post, FeedViewHolder>(options) {
            @Override
            public void onBindViewHolder(final FeedViewHolder holder, final int position, final Post model) {
                holder.authorTV.setText(model.getAuthor());

                //Icon creator from the first letter of a word. To create DP's using a Letter.
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getColor(model.getAuthor_image_url());
                TextDrawable ic1 = builder.build(String.valueOf(model.getAuthor().charAt(0)), color);
                holder.userImageIV.setImageDrawable(ic1);

                holder.positionTV.setText(model.getPosition());
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

                    final ImageAdapter imageAdapter = new ImageAdapter(getContext(), model.getImageUrl(), 1, model);
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
                        intent.putExtra("type", 1);
                        startActivity(intent);
                        Bungee.slideLeft(getContext());
                    }
                });

                //Long click for favourites and share options.
                holder.feed_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        CommonUtils.handleBottomSheet(v, model, Constants.post, getContext());
                        return true;
                    }
                });

                layout_progress.setVisibility(View.GONE);
                shimmer_view.setVisibility(View.GONE);
            }

            //Get the post_Item layout.
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
                View view;
                //Get the appropriate post_item layout based on the darkmode preference.
                if (SharedPref.getBoolean(getContext(), "dark_mode")) {
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.student_post_item_dark, group, false);
                } else {
                    view = LayoutInflater.from(group.getContext()).inflate(R.layout.student_post_item, group, false);
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
    private void initUI(View view) {
        feedsRV = view.findViewById(R.id.feedsRV);
        newPostTV = view.findViewById(R.id.newPostTV);
        layoutManager = new LinearLayoutManager(getContext());
        feedsRV.setLayoutManager(layoutManager);
        feedsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    newPostTV.setVisibility(View.GONE);
                }
            }
        });
        shimmer_view = view.findViewById(R.id.shimmer_view);
        layout_progress = view.findViewById(R.id.layout_progress);
        syllabusCV = view.findViewById(R.id.syllabusCV);
        libraryCV = view.findViewById(R.id.libraryCV);
        lmsCV = view.findViewById(R.id.lmsCV);
        lakshyaCV = view.findViewById(R.id.lakshyaCV);

        linkTitleTV2 = view.findViewById(R.id.linkTitleTV2);
        linkTitleTV2.setSelected(true);
    }
    /*********************************************************/

    /*********************************************************/
    // open syllabus PDF from the url.
    public void openSyllabus(String url) {
        //Active network connection
        if (!CommonUtils.alerter(getContext())) {
            Intent i = new Intent(getContext(), PdfViewerActivity.class);
            i.putExtra(Constants.PDF_URL, url);
            startActivity(i);
            Bungee.fade(getContext());
        }
        //No active network connection
        else {
            Intent intent = new Intent(getContext(), NoNetworkActivity.class);
            intent.putExtra("key", "home");
            startActivity(intent);
            Bungee.fade(getContext());
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