package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.edu.ssn.ssnapp.adapters.CustomExpandableListAdapter;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubPostDetailsActivity extends BaseActivity {
    ImageView backIV, userImageIV, likeIV, commentIV, shareIV,sendIV,cancel_replyIV;
    ViewPager viewPager;
    TextView authorTV, nameTV, timeTV, titleTV, current_imageTV,attachmentsTV, likeTV, commentTV,selected_replyTV;
    SocialTextView descriptionTV;
    ChipGroup attachmentsChipGroup;
    RelativeLayout textGroupRL;
    EditText et_Comment;
    CardView replyCV;

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    ListenerRegistration listenerRegistration;
    ArrayList<Comments> commentsArrayList=new ArrayList<>();

    String id;
    ClubPost post;
    Club club;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(darkModeEnabled){
            setContentView(R.layout.activity_club_post_details_dark);
            clearLightStatusBar(this);
        }else{
            setContentView(R.layout.activity_club_post_details);
        }


        initUI();
    }

    /*****************************************************************/

    void initUI() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        id = getIntent().getStringExtra("data");
        club = getIntent().getParcelableExtra("club");
        post = new ClubPost();

        backIV = findViewById(R.id.backIV);
        userImageIV = findViewById(R.id.userImageIV);
        authorTV = findViewById(R.id.authorTV);
        nameTV= findViewById(R.id.nameTV);
        timeTV = findViewById(R.id.timeTV);
        titleTV = findViewById(R.id.titleTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        current_imageTV = findViewById(R.id.currentImageTV);
        attachmentsTV = findViewById(R.id.attachmentTV);
        selected_replyTV=findViewById(R.id.reply_selectedTV);
        viewPager = findViewById(R.id.viewPager);
        attachmentsChipGroup = findViewById(R.id.attachmentsGroup);
        textGroupRL = findViewById(R.id.textGroupRL);
        textGroupRL.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        likeIV = findViewById(R.id.likeIV);
        likeTV = findViewById(R.id.likeTV);
        commentIV = findViewById(R.id.commentIV);
        commentTV = findViewById(R.id.commentTV);
        shareIV = findViewById(R.id.shareIV);
        sendIV=findViewById(R.id.sendIV);
        et_Comment=findViewById(R.id.edt_comment);
        cancel_replyIV=findViewById(R.id.cancelIV);
        replyCV=findViewById(R.id.replyCV);

        CommonUtils.hideKeyboard(ClubPostDetailsActivity.this);

        cancel_replyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_replyTV.setText("");
                replyCV.setVisibility(View.GONE);
                expandableListAdapter.setReplyingForComment(false);
            }
        });

        sendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_Comment.getText().toString().trim().length()>1) {
                    Boolean replyingForComment = expandableListAdapter.getReplyingForComment();

                    if (replyingForComment == null)
                        replyingForComment = false;

                    if (!replyingForComment) {
                        Comments temp = new Comments(SharedPref.getString(ClubPostDetailsActivity.this, "email"), et_Comment.getEditableText().toString(), Calendar.getInstance().getTime(), new ArrayList<HashMap<String, Object>>());
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(id).update("comment", FieldValue.arrayUnion(temp));
                    } else {

                        HashMap<String, Object> temp = new HashMap<>();
                        temp.put("author", SharedPref.getString(ClubPostDetailsActivity.this, "email"));
                        temp.put("message", et_Comment.getEditableText().toString());
                        temp.put("time", Calendar.getInstance().getTime());


                        ArrayList<Comments> commentsArrayList = expandableListAdapter.getCommentArrayList();
                        int listPosition = expandableListAdapter.getSelectedCommentPosition();
                        commentsArrayList.get(listPosition).getReply().add(temp);
                        expandableListAdapter.setCommentArrayList(commentsArrayList);

                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(id).update("comment", commentsArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Test", "success");
                            }
                        });

                        selected_replyTV.setText("");
                        replyCV.setVisibility(View.GONE);

                        expandableListAdapter.setReplyingForComment(false);
                    }

                    et_Comment.setText("");
                    CommonUtils.hideKeyboard(ClubPostDetailsActivity.this);
                }
            }
        });

        expandableListView =  findViewById(R.id.commentEV);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                setListViewHeight(commentsArrayList.size());
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeight(commentsArrayList.size());
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }

    void setUpFirestore(){
        listenerRegistration= FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                post = CommonUtils.getClubPostFromSnapshot(getApplicationContext(),documentSnapshot);
                updateData();

                ArrayList<HashMap<Object,Object>> comment_data=(ArrayList<HashMap<Object,Object>>) documentSnapshot.get("comment");
                commentsArrayList.clear();
                for(HashMap<Object,Object> i:comment_data){
                    Comments tempComment=new Comments((String)i.get("author"),(String)i.get("message"),((Timestamp)i.get("time")).toDate(),(ArrayList<HashMap<String, Object>>) i.get("reply"));
                    commentsArrayList.add(tempComment);
                }

                expandableListAdapter=null;
                Collections.sort(commentsArrayList);
                expandableListAdapter=new CustomExpandableListAdapter(ClubPostDetailsActivity.this,commentsArrayList,post,(Activity)ClubPostDetailsActivity.this);
                expandableListView.setAdapter(expandableListAdapter);
                expandableListView.setNestedScrollingEnabled(true);
                setListViewHeight(commentsArrayList.size());
            }
        });
    }

    void updateData(){
        authorTV.setText(CommonUtils.getNameFromEmail(post.getAuthor()));
        nameTV.setText(club.getName());
        Glide.with(ClubPostDetailsActivity.this).load(club.getDp_url()).into(userImageIV);

        titleTV.setText(post.getTitle());
        timeTV.setText(CommonUtils.getTime(post.getTime()));
        descriptionTV.setText(post.getDescription().trim());

        if(post.getImg_urls() != null && post.getImg_urls().size() != 0) {
            viewPager.setVisibility(View.VISIBLE);

            final ImageAdapter imageAdapter = new ImageAdapter(ClubPostDetailsActivity.this, post.getImg_urls(),0);
            viewPager.setAdapter(imageAdapter);

            if(post.getImg_urls().size()==1){
                current_imageTV.setVisibility(View.GONE);
            }
            else {
                current_imageTV.setVisibility(View.VISIBLE);
                current_imageTV.setText(String.valueOf(1)+" / "+String.valueOf(post.getImg_urls().size()));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        current_imageTV.setText(String.valueOf(pos+1)+" / "+String.valueOf(post.getImg_urls().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
        else {
            viewPager.setVisibility(View.GONE);
            current_imageTV.setVisibility(View.GONE);
        }

        ArrayList<String> fileName = post.getFileName();
        ArrayList<String> fileUrl = post.getFileUrl();

        if(fileName != null && fileName.size() > 0){
            attachmentsTV.setVisibility(View.VISIBLE);
            attachmentsChipGroup.setVisibility(View.VISIBLE);
            attachmentsChipGroup.removeAllViews();

            for(int i=0; i<fileName.size(); i++){
                Chip chip = getFilesChip(attachmentsChipGroup, fileName.get(i), fileUrl.get(i));
                attachmentsChipGroup.addView(chip);
            }
        }
        else {
            attachmentsTV.setVisibility(View.GONE);
            attachmentsChipGroup.setVisibility(View.GONE);
        }

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        descriptionTV.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                String url = text.toString();
                if (!url.startsWith("http") && !url.startsWith("https")) {
                    url = "http://" + url;
                }
                CommonUtils.openCustomBrowser(getApplicationContext(),url);
            }
        });

        try{

            if (post.getLike()!=null && post.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                likeIV.setImageResource(R.drawable.blue_heart);
            } else {
                likeIV.setImageResource(R.drawable.heart);
            }
        }catch (Exception e){

            likeIV.setImageResource(R.drawable.heart);
        }


        try {
            likeTV.setText(Integer.toString(post.getLike().size()));
        }
        catch (Exception e) {
            e.printStackTrace();
            likeTV.setText("0");
        }

        try {
            commentTV.setText(Integer.toString(post.getComment().size()));
        } catch (Exception e) {
            e.printStackTrace();
            commentTV.setText("0");
        }

        likeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!post.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                        likeIV.setImageResource(R.drawable.blue_heart);
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(post.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(), "email")));
                    } else {
                        likeIV.setImageResource(R.drawable.heart);
                        FirebaseFirestore.getInstance().collection(Constants.collection_post_club).document(post.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(), "email")));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi all! New posts from " + club.getName() + ". Check it out: https://ssnportal.cf/share.html?type=4&vca=" + club.getId() + "&vac=" + post.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
    }

    /*****************************************************************/
    //Files

    private Chip getFilesChip(final ChipGroup entryChipGroup, final String file_name, final String url) {
        final Chip chip = new Chip(this);
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.file_name_chip));
        chip.setText(file_name);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtils.hasPermissions(ClubPostDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(ClubPostDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else{
                    Toast toast = Toast.makeText(ClubPostDetailsActivity.this, "Downloading...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    try {
                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        Uri downloadUri = Uri.parse(url);
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file_name)
                                .setTitle(file_name)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                        dm.enqueue(request);
                    }
                    catch (Exception ex) {
                        toast = Toast.makeText(getApplicationContext(),"Download failed!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        ex.printStackTrace();
                    }
                }
            }
        });

        return chip;
    }

    /*****************************************************************/

    private void setListViewHeight(int group) {
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(expandableListView.getWidth(), View.MeasureSpec.EXACTLY);

        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            View groupItem = expandableListAdapter.getGroupView(i, false, null, expandableListView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += groupItem.getMeasuredHeight();

            if (((expandableListView.isGroupExpanded(i)) && (i != group)) || ((!expandableListView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < expandableListAdapter.getChildrenCount(i); j++) {
                    View listItem = expandableListAdapter.getChildView(i, j, false, null, expandableListView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight();
                }
                //Add Divider Height
                totalHeight += expandableListView.getDividerHeight() * (expandableListAdapter.getChildrenCount(i) - 1);
            }
        }
        //Add Divider Height
        totalHeight += expandableListView.getDividerHeight() * (expandableListAdapter.getGroupCount() - 1);

        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        int height = totalHeight + (expandableListView.getDividerHeight() * (expandableListAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;

        params.height = height;
        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }

    /*****************************************************************/

    @Override
    protected void onResume() {
        super.onResume();
        setUpFirestore();
    }

    @Override
    public void onStop() {
        super.onStop();
        listenerRegistration.remove();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(ClubPostDetailsActivity.this);
    }
}
