package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.models.Feedback;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

// Extends Base activity for darkmode variable and status bar.
public class FeedbackActivity extends BaseActivity {

    FirebaseFirestore db;

    EditText et_feedback;
    TextView text1TV, buttonTV;
    LottieAnimationView lottie;
    CardView submitCV;
    ImageView backIV;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if darkmode is enabled and open the appropriate layout.
        if (darkModeEnabled) {
            setContentView(R.layout.activity_feedback_dark);
            getWindow().setStatusBarColor(getResources().getColor(R.color.darkColorLight));
        } else {
            setContentView(R.layout.activity_feedback);
        }

        //Create a firebase instance.
        db = FirebaseFirestore.getInstance();

        et_feedback = findViewById(R.id.et_feedback);
        submitCV = findViewById(R.id.submitCV);
        lottie = findViewById(R.id.lottie);
        text1TV = findViewById(R.id.text1TV);
        buttonTV = findViewById(R.id.buttonTV);
        backIV = findViewById(R.id.backIV);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Submit the feedback to the firebase account.
        submitCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Active Internet connection
                if (!CommonUtils.alerter(getApplicationContext())) {
                    if (buttonTV.getText().equals("Submit")) {
                        String et_text = et_feedback.getEditableText().toString().trim();
                        //if the feedback is not an empty string
                        if (et_text.length() > 1) {
                            //add feedback to Firestore collection.
                            final Map<String, Object> feedback_details = new HashMap<>();
                            feedback_details.put("email", SharedPref.getString(getApplicationContext(), "email"));
                            feedback_details.put("text", et_text);
                            feedback_details.put("time", FieldValue.serverTimestamp());

                            lottie.setVisibility(View.VISIBLE);
                            text1TV.setVisibility(View.VISIBLE);
                            et_feedback.setVisibility(View.INVISIBLE);
                            buttonTV.setText("Continue");
                            CommonUtils.hideKeyboard(FeedbackActivity.this);

                            db.collection(Constants.collection_feedback).add(feedback_details);
                        }
                        //if the feedback is an empty string
                        else {
                            Toast toast = Toast.makeText(FeedbackActivity.this, "Feedback cannot be empty!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else
                        onBackPressed();
                }
                //No Active Internet Connection
                else {
                    Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                    intent.putExtra("key", "feedback");
                    startActivity(intent);
                    Bungee.fade(FeedbackActivity.this);
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
