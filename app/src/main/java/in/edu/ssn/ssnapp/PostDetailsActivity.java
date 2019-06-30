package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.edu.ssn.ssnapp.models.Post;

public class PostDetailsActivity extends BaseActivity {

    Post post;
    ImageView backIV, userImageIV;
    TextView tv_author, tv_position, tv_time, tv_title, tv_description, tv_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        initUI();

        post = getIntent().getParcelableExtra("post");
        String time = getIntent().getStringExtra("time");

        tv_title.setText(post.getTitle());
        tv_description.setText(post.getDescription());
        tv_author.setText(post.getAuthor());
        tv_position.setText(post.getPosition());
        Picasso.get().load(post.getAuthor_image_url()).into(userImageIV);
        //TODO images, files upload

        tv_time.setText(time);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO share functionality
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
        tv_share = findViewById(R.id.tv_share);
    }
}
