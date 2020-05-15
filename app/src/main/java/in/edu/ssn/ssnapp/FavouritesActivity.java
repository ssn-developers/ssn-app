package in.edu.ssn.ssnapp;

import android.os.Build;
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

public class FavouritesActivity extends BaseActivity {

    ListView lv_savedPost;
    SavedPostAdapter savedPostAdapter;
    DataBaseHelper dbHelper;
    ImageView backIV;
    RelativeLayout layout_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (darkModeEnabled) {
            setContentView(R.layout.activity_favourites_dark);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        } else {
            setContentView(R.layout.activity_favourites);
        }

        lv_savedPost = findViewById(R.id.lv_items);
        layout_progress = findViewById(R.id.layout_progress);
        backIV = findViewById(R.id.backIV);
        savedPostAdapter = new SavedPostAdapter(this, new ArrayList<Post>());
        dbHelper = DataBaseHelper.getInstance(this);
        lv_savedPost.setAdapter(savedPostAdapter);

        backIV.setOnClickListener(new View.OnClickListener() {
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
        ArrayList<Post> savedPostList = dbHelper.getSavedPostList();
        Collections.reverse(savedPostList);
        savedPostAdapter.addAll(savedPostList);
        savedPostAdapter.notifyDataSetChanged();

        if (savedPostList.size() > 0) {
            layout_progress.setVisibility(View.GONE);
            lv_savedPost.setVisibility(View.VISIBLE);
        } else {
            layout_progress.setVisibility(View.VISIBLE);
            lv_savedPost.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(FavouritesActivity.this);
    }
}
