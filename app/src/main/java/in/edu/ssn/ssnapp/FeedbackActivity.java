package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class FeedbackActivity extends AppCompatActivity {

    FirebaseFirestore db;

    EditText et_feedback;
    TextView tv_text1, tv_button;
    LottieAnimationView lottie;
    CardView submitCV;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        db = FirebaseFirestore.getInstance();

        et_feedback = findViewById(R.id.et_feedback);
        submitCV = findViewById(R.id.submitCV);
        lottie = findViewById(R.id.lottie);
        tv_text1 = findViewById(R.id.tv_text1);
        tv_button = findViewById(R.id.tv_button);
        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CommonUtils.alerter(getApplicationContext())) {
                    if (tv_button.getText().equals("Submit")) {
                        String et_text = et_feedback.getEditableText().toString().trim();
                        final Map<String, Object> feedback_details = new HashMap<>();
                        feedback_details.put("email", SharedPref.getString(getApplicationContext(), "email"));
                        feedback_details.put("text", et_text);
                        feedback_details.put("time", FieldValue.serverTimestamp());

                        if (et_text.length() > 0) {
                            lottie.setVisibility(View.VISIBLE);
                            tv_text1.setVisibility(View.VISIBLE);
                            et_feedback.setVisibility(View.INVISIBLE);
                            tv_button.setText("Continue");
                            CommonUtils.hideKeyboard(FeedbackActivity.this);

                            db.collection("feedback").add(feedback_details);
                        }
                        else {
                            Toast toast = Toast.makeText(FeedbackActivity.this, "Feedback cannot be empty!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                    }
                    else
                        onBackPressed();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key","feedback");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bungee.slideRight(FeedbackActivity.this);
    }
}
