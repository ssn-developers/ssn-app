package in.edu.ssn.ssnapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class HelpLineActivity extends AppCompatActivity {

    ImageView iv_back;
    TextView tv_name1, tv_email1, tv_name2, tv_email2;
    CircleImageView userImageIV1, userImageIV2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_line);

        iv_back = findViewById(R.id.iv_back);
        tv_name1 = findViewById(R.id.tv_name1);
        tv_email1 = findViewById(R.id.tv_email1);
        userImageIV1 = findViewById(R.id.userImageIV1);
        tv_name2 = findViewById(R.id.tv_name2);
        tv_email2 = findViewById(R.id.tv_email2);
        userImageIV2 = findViewById(R.id.userImageIV2);

        updateAdmin();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void updateAdmin(){
        Map<String, ?> map = SharedPref.getSharePref(getApplicationContext(),"faculty_access").getAll();
        Boolean flag=false;
        for (Map.Entry<String, ?> entry: map.entrySet()) {
            if(entry.getValue().toString().equals("AD")) {
                String email = entry.getKey();
                String url = SharedPref.getString(getApplicationContext(),"faculty_dp_url", email);
                String name = SharedPref.getString(getApplicationContext(),"faculty_name", email);

                if(!flag) {
                    tv_name1.setText(name);
                    tv_email1.setText(email);
                    try {
                        if (url != null)
                            Glide.with(this).load(url).placeholder(R.drawable.ic_user_white).into(userImageIV1);
                        else
                            userImageIV1.setImageResource(R.drawable.ic_user_white);
                    } catch (Exception e) {
                        e.printStackTrace();
                        userImageIV1.setImageResource(R.drawable.ic_user_white);
                    }
                    flag=true;
                }
                else{
                    tv_name2.setText(name);
                    tv_email2.setText(email);
                    try {
                        if (url != null)
                            Glide.with(this).load(url).placeholder(R.drawable.ic_user_white).into(userImageIV2);
                        else
                            userImageIV2.setImageResource(R.drawable.ic_user_white);
                    } catch (Exception e) {
                        e.printStackTrace();
                        userImageIV2.setImageResource(R.drawable.ic_user_white);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(HelpLineActivity.this);
    }
}
