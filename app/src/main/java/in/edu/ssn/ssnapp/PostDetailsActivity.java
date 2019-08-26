package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.hendraanggrian.appcompat.widget.SocialView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.edu.ssn.ssnapp.adapters.ImageAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Post;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class PostDetailsActivity extends BaseActivity {

    final static String TAG="PostDetails";
    Post post;
    ImageView backIV, userImageIV, shareIV,bookmarkIV;
    ViewPager imageViewPager;
    TextView tv_author, tv_position, tv_time, tv_title, tv_current_image,tv_attachments;
    SocialTextView tv_description;
    ChipGroup attachmentsChipGroup, yearChipGroup, deptChipGroup;
    RelativeLayout textGroupRL, layout_receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        post = getIntent().getParcelableExtra("post");
        String time = getIntent().getStringExtra("time");

        initUI();

        tv_title.setText(post.getTitle().trim());
        tv_description.setText(post.getDescription().trim());
        tv_author.setText(post.getAuthor().trim());
        tv_position.setText(post.getPosition().trim());
        Picasso.get().load(post.getAuthor_image_url()).placeholder(R.drawable.ic_user_white).into(userImageIV);

        if(post.getImageUrl()!=null && post.getImageUrl().size()!=0){
            imageViewPager.setVisibility(View.VISIBLE);
            tv_current_image.setVisibility(View.VISIBLE);
            final ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), post.getImageUrl(),false);
            imageViewPager.setAdapter(imageAdapter);

            if(post.getImageUrl().size()==1)
                tv_current_image.setVisibility(View.GONE);
            else {
                tv_current_image.setVisibility(View.VISIBLE);
                tv_current_image.setText(String.valueOf(1)+" / "+String.valueOf(post.getImageUrl().size()));
                imageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int pos) {
                        tv_current_image.setText(String.valueOf(pos+1)+" / "+String.valueOf(post.getImageUrl().size()));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
        else {
            imageViewPager.setVisibility(View.GONE);
            tv_current_image.setVisibility(View.GONE);
        }

        ArrayList<String> fileName = post.getFileName();
        ArrayList<String> fileUrl = post.getFileUrl();

        if(fileName != null && fileName.size() > 0){
            tv_attachments.setVisibility(View.VISIBLE);
            attachmentsChipGroup.setVisibility(View.VISIBLE);

            for(int i=0; i<fileName.size(); i++){
                Chip chip = getFilesChip(attachmentsChipGroup, fileName.get(i), fileUrl.get(i));
                attachmentsChipGroup.addView(chip);
            }
        }
        else {
            tv_attachments.setVisibility(View.GONE);
            attachmentsChipGroup.setVisibility(View.GONE);
        }

        List<String> depts = post.getDept();
        List<String> year = post.getYear();

        if(SharedPref.getInt(getApplicationContext(),"clearance") == 1){
            if(depts != null && depts.size() != 0){
                layout_receive.setVisibility(View.VISIBLE);

                for(int i=0; i<depts.size(); i++){
                    Chip dept_chip = getDataChip(deptChipGroup, depts.get(i).toUpperCase());
                    deptChipGroup.addView(dept_chip);
                }

                for(int i=0; i<year.size(); i++){
                    Chip year_chip = getDataChip(yearChipGroup, year.get(i));
                    yearChipGroup.addView(year_chip);
                }
            }
            else
                layout_receive.setVisibility(View.GONE);
        }
        else
            layout_receive.setVisibility(View.GONE);

        tv_time.setText(time);

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

        shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hello! New posts from " + post.getAuthor().trim() + ". Check it out: http://ssnportal.cf/" + post.getId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        bookmarkIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(checkSavedPost(post))
                        unSavePost(post);
                    else
                        savePost(post);

            }
        });
    }

    void initUI(){
        backIV = findViewById(R.id.backIV);
        userImageIV = findViewById(R.id.userImageIV);
        tv_author = findViewById(R.id.tv_author);
        tv_position = findViewById(R.id.tv_position);
        tv_time = findViewById(R.id.tv_time);
        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        shareIV = findViewById(R.id.shareIV);
        tv_current_image = findViewById(R.id.currentImageTV);
        tv_attachments = findViewById(R.id.tv_attachment);
        imageViewPager = findViewById(R.id.viewPager);
        attachmentsChipGroup = findViewById(R.id.attachmentsGroup);
        yearChipGroup = findViewById(R.id.yearGroup);
        deptChipGroup = findViewById(R.id.deptGroup);
        textGroupRL = findViewById(R.id.textGroupRL);
        layout_receive = findViewById(R.id.layout_receive);
        textGroupRL.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        bookmarkIV=findViewById(R.id.bookmarkIV);

        if(checkSavedPost(post))
            bookmarkIV.setImageResource(R.drawable.ic_bookmark_saved);
        else
            bookmarkIV.setImageResource(R.drawable.ic_bookmark_unsaved);

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
                if(!CommonUtils.hasPermissions(PostDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    ActivityCompat.requestPermissions(PostDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else{
                    Toast toast = Toast.makeText(PostDetailsActivity.this, "Downloading...", Toast.LENGTH_SHORT);
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
                        Crashlytics.log("stackTrace: "+ex.getStackTrace()+" \n Error: "+ex.getMessage());
                    }

                }


            }
        });

        return chip;
    }

    /*****************************************************************/
    //Year & Dept

    private Chip getDataChip(final ChipGroup entryChipGroup, final String data) {
        final Chip chip = new Chip(this);
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.year_chip));
        chip.setChipCornerRadius(30f);
        chip.setText(data);
        return chip;
    }

    public Boolean savePost(Post post){
        try{

            DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(this);
            dataBaseHelper.addPost(post);

            bookmarkIV.setImageResource(R.drawable.ic_bookmark_saved);

        }catch (Exception e){
            Log.d(TAG,e.getMessage());
            Crashlytics.log("stackTrace: "+e.getStackTrace()+" \n Error: "+e.getMessage());
            return false;
        }

        Toast toast = Toast.makeText(this, "Saved post", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
        return true;
    }

    void unSavePost(Post post){
        // updating the post status to not saved in shared preference
        DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(this);
        dataBaseHelper.deletePost(post.getId());
        bookmarkIV.setImageResource(R.drawable.ic_bookmark_unsaved);
    }

    Boolean checkSavedPost(Post post){
        DataBaseHelper dataBaseHelper=DataBaseHelper.getInstance(this);
        return dataBaseHelper.checkPost(post.getId());
    }
}
