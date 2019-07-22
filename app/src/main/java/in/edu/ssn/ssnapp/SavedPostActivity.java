package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import in.edu.ssn.ssnapp.adapters.NotifyAdapter;
import in.edu.ssn.ssnapp.adapters.SavedPostAdapter;
import in.edu.ssn.ssnapp.database.DataBaseHelper;
import in.edu.ssn.ssnapp.models.Post;

public class SavedPostActivity extends AppCompatActivity {

    ListView lv_savedPost;
    SavedPostAdapter savedPostAdapter;
    DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_post);

        lv_savedPost = findViewById(R.id.lv_items);
        savedPostAdapter = new SavedPostAdapter(this, new ArrayList<Post>());
        dbHelper=DataBaseHelper.getInstance(this);
        lv_savedPost.setAdapter(savedPostAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        savedPostAdapter.clear();
        ArrayList<Post> savedPostList=dbHelper.getSavedPostList();
        Collections.reverse(savedPostList);
        savedPostAdapter.addAll(savedPostList);
        savedPostAdapter.notifyDataSetChanged();
    }
}
