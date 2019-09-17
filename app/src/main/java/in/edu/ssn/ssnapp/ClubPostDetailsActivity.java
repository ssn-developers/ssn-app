package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.adapters.CustomExpandableListAdapter;
import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.models.Club;
import in.edu.ssn.ssnapp.models.ClubPost;
import in.edu.ssn.ssnapp.models.Comments;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class ClubPostDetailsActivity extends AppCompatActivity {
    ImageView backIV, userImageIV, iv_like, iv_comment, iv_share;
    ViewPager viewPager;
    TextView tv_author, tv_name, tv_time, tv_title, tv_current_image,tv_attachments, tv_like, tv_comment;
    SocialTextView tv_description;
    ChipGroup attachmentsChipGroup;
    RelativeLayout textGroupRL;
    EditText et_Comment;

    Button postComment;
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
        setContentView(R.layout.activity_club_post_details);

        initUI();

        setUpFirestore();
    }

    void initUI() {
        id = getIntent().getStringExtra("data");
        club = getIntent().getParcelableExtra("club");
        post = new ClubPost();

        backIV = findViewById(R.id.backIV);
        userImageIV = findViewById(R.id.userImageIV);
        tv_author = findViewById(R.id.tv_author);
        tv_name= findViewById(R.id.tv_name);
        tv_time = findViewById(R.id.tv_time);
        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_current_image = findViewById(R.id.currentImageTV);
        tv_attachments = findViewById(R.id.tv_attachment);
        viewPager = findViewById(R.id.viewPager);
        attachmentsChipGroup = findViewById(R.id.attachmentsGroup);
        textGroupRL = findViewById(R.id.textGroupRL);
        textGroupRL.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        iv_like = findViewById(R.id.iv_like);
        tv_like = findViewById(R.id.tv_like);
        iv_comment = findViewById(R.id.iv_comment);
        tv_comment = findViewById(R.id.tv_comment);
        iv_share = findViewById(R.id.iv_share);
        et_Comment=findViewById(R.id.edt_comment);


        postComment=findViewById(R.id.btn_post_reply);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FirebaseFirestore.getInstance().collection("contact").document(i).update("number", FieldValue.arrayRemove(Integer.parseInt(edt_phone.getText().toString())))
                Comments temp=new Comments(SharedPref.getString(ClubPostDetailsActivity.this,"email"),et_Comment.getEditableText().toString(), Calendar.getInstance().getTime(), new ArrayList<HashMap<String, Object>>());
                FirebaseFirestore.getInstance().collection("post_club").document(id).update("comment", FieldValue.arrayUnion(temp));
                //FirebaseFirestore.getInstance().collection("post_club").document("").update()
            }
        });


        expandableListView =  findViewById(R.id.EV_comment);
        expandableListAdapter = new CustomExpandableListAdapter(this, new ArrayList<Comments>(),post);

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

    void setUpFirestore(){
        listenerRegistration= FirebaseFirestore.getInstance().collection("post_club").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                post.setId(documentSnapshot.getString("id"));
                post.setCid(documentSnapshot.getString("cid"));
                post.setAuthor(documentSnapshot.getString("author"));
                post.setTitle(documentSnapshot.getString("title"));
                post.setDescription(documentSnapshot.getString("description"));
                post.setTime(documentSnapshot.getTimestamp("time").toDate());

                ArrayList<String> images = (ArrayList<String>) documentSnapshot.get("img_urls");
                if(images != null && images.size() > 0)
                    post.setImg_urls(images);
                else
                    post.setImg_urls(null);

                try {
                    ArrayList<Map<String, String>> files = (ArrayList<Map<String, String>>) documentSnapshot.get("file_urls");
                    if (files != null && files.size() != 0) {
                        ArrayList<String> fileName = new ArrayList<>();
                        ArrayList<String> fileUrl = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            String name = files.get(i).get("name");
                            if(name.length() > 13)
                                name = name.substring(0,name.length()-13);
                            fileName.add(name);
                            fileUrl.add(files.get(i).get("url"));
                        }
                        post.setFileName(fileName);
                        post.setFileUrl(fileUrl);
                    }
                    else {
                        post.setFileName(null);
                        post.setFileUrl(null);
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    post.setFileName(null);
                    post.setFileUrl(null);
                }

                try {
                    ArrayList<String> like = (ArrayList<String>) documentSnapshot.get("like");
                    post.setLike(like);

                    if (like != null && like.size() != 0)
                        post.setLike(like);
                    else
                        post.setLike(new ArrayList<String>());
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    post.setLike(new ArrayList<String>());
                }

                try {
                    ArrayList<Comments> comments = (ArrayList<Comments>) documentSnapshot.get("comment");
                    if (comments != null && comments.size() > 0)
                        post.setComment(comments);
                    else
                        post.setComment(null);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    post.setComment(null);
                }

                updateData();

                ArrayList<HashMap<Object,Object>> comment_data=(ArrayList<HashMap<Object,Object>>) documentSnapshot.get("comment");
                commentsArrayList.clear();
                for(HashMap<Object,Object> i:comment_data){
                    Comments tempComment=new Comments((String)i.get("author"),(String)i.get("message"),((Timestamp)i.get("time")).toDate(),(ArrayList<HashMap<String, Object>>) i.get("reply"));
                    commentsArrayList.add(tempComment);
                }

                expandableListAdapter=null;
                Collections.sort(commentsArrayList);
                expandableListAdapter=new CustomExpandableListAdapter(ClubPostDetailsActivity.this,commentsArrayList,post);
                expandableListView.setAdapter(expandableListAdapter);
            }
        });
    }

    void updateData(){
        String author = "";
        String email = post.getAuthor();
        email = email.substring(0, email.indexOf("@"));
        for (int j = 0; j < email.length(); j++) {
            if (Character.isDigit(email.charAt(j))) {
                author = email.substring(0, j);
                break;
            }
        }
        if (author.isEmpty())
            author = email;

        try {
            tv_author.setText(author.substring(0, 1).toUpperCase() + author.substring(1));
        }
        catch (Exception e){
            e.printStackTrace();
            tv_author.setText(author);
        }

        tv_name.setText(club.getName());
        Glide.with(ClubPostDetailsActivity.this).load(club.getDp_url()).into(userImageIV);

        tv_title.setText(post.getTitle());

        Date time = post.getTime();
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

        tv_time.setText(timer);

        if(post.getDescription().length() > 100) {
            SpannableString ss = new SpannableString(post.getDescription().substring(0, 100) + "... see more");
            ss.setSpan(new RelativeSizeSpan(0.9f), ss.length() - 12, ss.length(), 0);
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#404040")), ss.length() - 12, ss.length(), 0);
            tv_description.setText(ss);
        }
        else
            tv_description.setText(post.getDescription().trim());

        if(post.getImg_urls() != null && post.getImg_urls().size() != 0) {
            viewPager.setVisibility(View.VISIBLE);

            final ImageAdapter imageAdapter = new ImageAdapter(ClubPostDetailsActivity.this, post.getImg_urls(),0);
            viewPager.setAdapter(imageAdapter);

            if(post.getImg_urls().size()==1){
                tv_current_image.setVisibility(View.GONE);
            }
            else {
                tv_current_image.setVisibility(View.VISIBLE);
                tv_current_image.setText(String.valueOf(1)+" / "+String.valueOf(post.getImg_urls().size()));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        tv_current_image.setText(String.valueOf(pos+1)+" / "+String.valueOf(post.getImg_urls().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
        else {
            viewPager.setVisibility(View.GONE);
            tv_current_image.setVisibility(View.GONE);
        }

        ArrayList<String> fileName = post.getFileName();
        ArrayList<String> fileUrl = post.getFileUrl();

        if(fileName != null && fileName.size() > 0){
            tv_attachments.setVisibility(View.VISIBLE);
            attachmentsChipGroup.setVisibility(View.VISIBLE);
            attachmentsChipGroup.removeAllViews();

            for(int i=0; i<fileName.size(); i++){
                Chip chip = getFilesChip(attachmentsChipGroup, fileName.get(i), fileUrl.get(i));
                attachmentsChipGroup.addView(chip);
            }
        }
        else {
            tv_attachments.setVisibility(View.GONE);
            attachmentsChipGroup.setVisibility(View.GONE);
        }

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_description.setOnHyperlinkClickListener(new SocialView.OnClickListener() {
            @Override
            public void onClick(@NonNull SocialView view, @NonNull CharSequence text) {
                String url = text.toString();
                if (!url.startsWith("http") && !url.startsWith("https")) {
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        try{

            if (post.getLike()!=null && post.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                iv_like.setImageResource(R.drawable.blue_heart);
            } else {
                iv_like.setImageResource(R.drawable.heart);
            }
        }catch (Exception e){

            iv_like.setImageResource(R.drawable.heart);
        }


        try {
            tv_like.setText(Integer.toString(post.getLike().size()));
        }
        catch (Exception e) {
            e.printStackTrace();
            tv_like.setText("0");
        }

        try {
            tv_comment.setText(Integer.toString(post.getComment().size()));
        } catch (Exception e) {
            e.printStackTrace();
            tv_comment.setText("0");
        }

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!post.getLike().contains(SharedPref.getString(getApplicationContext(), "email"))) {
                    iv_like.setImageResource(R.drawable.blue_heart);
                    FirebaseFirestore.getInstance().collection("post_club").document(post.getId()).update("like", FieldValue.arrayUnion(SharedPref.getString(getApplicationContext(), "email")));
                } else {
                    iv_like.setImageResource(R.drawable.heart);
                    FirebaseFirestore.getInstance().collection("post_club").document(post.getId()).update("like", FieldValue.arrayRemove(SharedPref.getString(getApplicationContext(), "email")));
                }
            }
        });

        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + club.getName() + ". Check it out: http://ssnportal.cf/share.html?id=" + post.getId();
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
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/SSN App/", file_name)
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
