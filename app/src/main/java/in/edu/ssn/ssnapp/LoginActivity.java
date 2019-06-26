package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;

public class LoginActivity extends BaseActivity {

    CardView signInCV;

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
    }

}
