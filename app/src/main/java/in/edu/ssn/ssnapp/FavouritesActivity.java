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

// Extends Base activity for darkmode variable and status bar.
public class FavouritesActivity extends BaseActivity {

    ListView lv_savedPost;
    SavedPostAdapter savedPostAdapter;
    DataBaseHelper dbHelper;
    ImageView backIV;
    RelativeLayout layout_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if darkmode is enabled and open the appropriate layout.
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

        //Setting up adapter to assign post details to UI elements.
        savedPostAdapter = new SavedPostAdapter(this, new ArrayList<Post>());
        //Creating a instance of the Database helper.
        dbHelper = DataBaseHelper.getInstance(this);
        //Setting the adapter to the posts ListView.
        lv_savedPost.setAdapter(savedPostAdapter);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //Refresh The posts ListView
    @Override
    protected void onResume() {
        super.onResume();
        //Clear the ListView
        savedPostAdapter.clear();
        //Populate the list again with fresh set of Saved posts from the Database.
        ArrayList<Post> savedPostList = dbHelper.getSavedPostList();
        //Order the List
        Collections.reverse(savedPostList);
        //Add this to the ListView
        savedPostAdapter.addAll(savedPostList);
        //Notify That there has been a Change in the List's content.
        savedPostAdapter.notifyDataSetChanged();

        //If there are some saved posts
        if (savedPostList.size() > 0) {
            layout_progress.setVisibility(View.GONE);
            lv_savedPost.setVisibility(View.VISIBLE);
        }
        //If there are NO saved posts, show some message.
        else {
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
