package in.edu.ssn.ssnapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class SyllabusActivity extends BaseActivity {

    CardView annaunivCV,autonomousCV;
    String dept;
    ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        annaunivCV = (CardView)findViewById(R.id.link_1CV);
        autonomousCV = (CardView)findViewById(R.id.link_2CV);
        backIV = (ImageView)findViewById(R.id.backIV);
        dept = SharedPref.getString(getApplicationContext(),"dept");


        annaunivCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dept.equals("bme"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=1oTHKp2hj_YXx0mGRMi0DE5rd2dQxScXO");
                else if (dept.equals("cse"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=17RwY3JrcNP01n93tE41Osn-VF3Im1dJ9");
                else if (dept.equals("it"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=1_pRS9hSpsU5zFpWExfJ-Y4ZlnBoj3od0");
                else if (dept.equals("eee"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=1fNUgEV36A-_BcsW-eWPEiCmbeWVEkR5S");
                else if (dept.equals("ece"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=15bj27Y004iU89ww7oF4LUm0DJEjHeedQ");
                else if (dept.equals("che"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=1VyaaKuOF0bWuClOFw05cIXio0sl6DDVj");
                else if (dept.equals("mec"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=1mx_RG0dtR2fHxryFwItFaN_sE4nilfBN");
                else if (dept.equals("civ"))
                    openSyllabus("https://drive.google.com/uc?export=download&id=1x7FpCqZEkBWbrHBOiIYnz9LVFLq9cgLP");

            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void openSyllabus (String url){
        if (!CommonUtils.alerter(getApplicationContext())) {
            Intent i = new Intent(getApplicationContext(), PdfViewerActivity.class);
            i.putExtra(Constants.PDF_URL, url);
            startActivity(i);
            Bungee.fade(SyllabusActivity.this);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
            intent.putExtra("key", "home");
            startActivity(intent);
            Bungee.fade(SyllabusActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
