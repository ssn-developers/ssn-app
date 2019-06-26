package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class LoginActivity extends BaseActivity {

    CardView signInCV;
    ImageView roadIV, skyIV, signBoardIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();

        signInCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
    }

    void initUI(){
        changeFont(regular,(ViewGroup) this.findViewById(android.R.id.content));
        signInCV = findViewById(R.id.signInCV);
        roadIV = findViewById(R.id.roadIV);
        skyIV = findViewById(R.id.skyIV);
        signBoardIV = findViewById(R.id.signBoardIV);
        Picasso.get().load("file:///android_asset/sky.png").into(skyIV);
        Picasso.get().load("file:///android_asset/road_with_grass.png").into(roadIV);
        Picasso.get().load("file:///android_asset/sign_board.png").into(signBoardIV);
    }

}
