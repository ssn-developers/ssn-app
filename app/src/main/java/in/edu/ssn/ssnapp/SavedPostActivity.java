package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

import in.edu.ssn.ssnapp.adapters.SavedPostAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Post;
import spencerstudios.com.bungeelib.Bungee;

public class SavedPostActivity extends BaseActivity {

    ListView lv_savedPost;
    SavedPostAdapter savedPostAdapter;
    DataBaseHelper dbHelper;
    ImageView iv_back;
    RelativeLayout layout_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(darkModeEnabled){
            setContentView(R.layout.activity_saved_post_dark);
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        }else {
            setContentView(R.layout.activity_saved_post);
        }

        lv_savedPost = findViewById(R.id.lv_items);
        layout_progress = findViewById(R.id.layout_progress);
        iv_back = findViewById(R.id.iv_back);
        savedPostAdapter = new SavedPostAdapter(this, new ArrayList<Post>());
        dbHelper=DataBaseHelper.getInstance(this);
        lv_savedPost.setAdapter(savedPostAdapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        savedPostAdapter.clear();
        ArrayList<Post> savedPostList=dbHelper.getSavedPostList();
        Collections.reverse(savedPostList);
        savedPostAdapter.addAll(savedPostList);
        savedPostAdapter.notifyDataSetChanged();

        if(savedPostList.size() > 0){
            layout_progress.setVisibility(View.GONE);
            lv_savedPost.setVisibility(View.VISIBLE);
        }
        else{
            layout_progress.setVisibility(View.VISIBLE);
            lv_savedPost.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(SavedPostActivity.this);
    }
}
