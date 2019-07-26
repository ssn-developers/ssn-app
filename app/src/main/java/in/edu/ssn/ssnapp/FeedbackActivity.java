package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.utils.SharedPref;

public class FeedbackActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference feedbackcolref = db.collection("feedback");

    EditText feedback;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        feedback = (EditText) findViewById(R.id.feedback_text);
        submit = (Button) findViewById(R.id.submit_button);

        submit.setOnClickListener(submit_pressed);

    }

    View.OnClickListener submit_pressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Map<String, Object> feedback_details = new HashMap<>();
            feedback_details.put("email", SharedPref.getString(getApplicationContext(), "email"));
            feedback_details.put("text", feedback.getText().toString());

            if (!(feedback.getText().toString().isEmpty())) {
                feedbackcolref.add(feedback_details)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                if (documentReference != null) {
                                    Toast.makeText(FeedbackActivity.this, "Upload successful !!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FeedbackActivity.this, "Upload failure", Toast.LENGTH_SHORT).show();

                            }
                        });
                hide_keyboard(view);
            }else{
                Toast.makeText(FeedbackActivity.this, "Pls Provide Some Feedback", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public void hide_keyboard(View v) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
