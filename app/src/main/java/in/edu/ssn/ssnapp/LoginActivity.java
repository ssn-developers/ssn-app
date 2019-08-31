package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    CardView cv_student, cv_faculty, cv_club, cv_event;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 111;
    RelativeLayout layout_progress;
    int clearance;
    String access;
    Boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cv_student = findViewById(R.id.cv_student);
        cv_faculty = findViewById(R.id.cv_faculty);
        cv_club = findViewById(R.id.cv_club);
        cv_event = findViewById(R.id.cv_event);
        layout_progress = findViewById(R.id.layout_progress);

        initGoogleSignIn();

        cv_student.setOnClickListener(this);
        cv_faculty.setOnClickListener(this);
        cv_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Club member login");
            }
        });
        cv_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Event feature");
            }
        });
    }

    /************************************************************************/
    // Google Signin

    public void initGoogleSignIn() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount acct = task.getResult(ApiException.class);
                Pattern pat_s = Pattern.compile("@[a-z]{2,8}(.ssn.edu.in)$");
                Matcher m_s = pat_s.matcher(acct.getEmail());

                Pattern pat_f = Pattern.compile("(@ssn.edu.in)$");
                Matcher m_f = pat_f.matcher(acct.getEmail());

                if(clearance == 0) {
                    if (m_s.find()) {
                        if(acct.getEmail().endsWith("@cse.ssn.edu.in")) {
                            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                            mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        /*if (access.equals("CA"))
                                            checkForClub(user);
                                        else*/
                                        signInStudent(user);
                                    } else {
                                        Log.d("test_set", "signInWithCredential:failure");
                                        layout_progress.setVisibility(View.GONE);
                                        flag = true;
                                    }
                                }
                            });
                        }
                        else {
                            flag=true;
                            layout_progress.setVisibility(View.GONE);
                            Toast toast = Toast.makeText(this, "App is currently available to CSE dept only\nIt will be available to other dept soon & stay tuned", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                    else {
                        layout_progress.setVisibility(View.GONE);
                        flag = true;
                        Toast toast = Toast.makeText(this, "Please use SSN mail ID", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                else if(clearance == 1){
                    if(m_f.find()){
                        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    checkForFaculty(user);
                                }
                                else {
                                    Log.d("test_set", "signInWithCredential:failure");
                                    layout_progress.setVisibility(View.GONE);
                                    flag = true;
                                }
                            }
                        });
                    }
                    else {
                        Toast toast = Toast.makeText(this, "Please use SSN mail ID", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        layout_progress.setVisibility(View.GONE);
                        flag = true;
                    }
                }
            }
            catch (ApiException e) {
                layout_progress.setVisibility(View.GONE);
                flag = true;
                Log.d("test_set","error" + e.getMessage());
            }
        }
        else{
            layout_progress.setVisibility(View.GONE);
            flag = true;
        }
    }

    /*****************************************************************/
    //Student signin and signup

    public void signInStudent(FirebaseUser user) {
        String email = user.getEmail();
        String id = user.getUid();

        String[] split = email.split("@");
        int y = split[1].indexOf(".");
        String dept = split[1].substring(0, y);

        String dp_url = user.getPhotoUrl().toString();
        String name = user.getDisplayName();

        int year = Integer.parseInt(split[0].substring(split[0].length() - 5, split[0].length() - 3)) + 2000;

        Map<String, Object> users = new HashMap<>();
        users.put("access", "");
        users.put("dept", dept);
        users.put("dp_url", dp_url);
        users.put("email", email);
        users.put("id", id);
        users.put("name", name);
        users.put("year", year);
        users.put("FCMToken", SharedPref.getString(this,"dont_delete","FCMToken"));
        FirebaseFirestore.getInstance().collection("student").document(email).set(users);

        SharedPref.putInt(getApplicationContext(), "clearance", 0);
        SharedPref.putString(getApplicationContext(), "dept", dept);
        SharedPref.putString(getApplicationContext(), "dp_url", dp_url);
        SharedPref.putString(getApplicationContext(), "email", email);
        SharedPref.putString(getApplicationContext(), "id", id);
        SharedPref.putString(getApplicationContext(), "name", name);
        SharedPref.putInt(getApplicationContext(), "year", year);
        SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in",2);

        SubscribeToAlerts(this);
        setUpNotification();

        layout_progress.setVisibility(View.GONE);
        flag = true;
        startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
        finish();
    }

    public void checkForFaculty(final FirebaseUser user) {
        String email = user.getEmail();
        FirebaseFirestore.getInstance().collection("faculty").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    signInFaculty(user, document);
                }
                else {
                    flag=true;
                    layout_progress.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(LoginActivity.this, "Please contact Admin for login issues!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    public void signInFaculty(FirebaseUser user, DocumentSnapshot document) {
        String email = user.getEmail();
        String id = user.getUid();
        String dept = document.getString("dept");
        String access = document.getString("access");
        String position = document.getString("position");
        String name = document.getString("name");
        String dp_url = user.getPhotoUrl().toString();
        FirebaseFirestore.getInstance().collection("faculty").document(email).update("FCMToken",SharedPref.getString(getApplicationContext(),"dont_delete","FCMToken"));

        SharedPref.putInt(getApplicationContext(), "clearance", 1);
        SharedPref.putString(getApplicationContext(), "email", email);
        SharedPref.putString(getApplicationContext(), "id", id);
        SharedPref.putString(getApplicationContext(), "position", position);
        SharedPref.putString(getApplicationContext(), "access", access);
        SharedPref.putString(getApplicationContext(), "dept", dept);
        SharedPref.putString(getApplicationContext(), "dp_url", dp_url);
        SharedPref.putString(getApplicationContext(), "name", name);
        SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in",2);

        SubscribeToAlerts(this);
        setUpNotification();

        layout_progress.setVisibility(View.GONE);
        flag = true;
        startActivity(new Intent(getApplicationContext(), FacultyHomeActivity.class));
        finish();
    }

    public void checkForClub(final FirebaseUser user) {
        String email = user.getEmail();
        FirebaseFirestore.getInstance().collection("student").whereEqualTo("email", email).whereEqualTo("access","CA").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    signInClub(user, document);
                }
                else {
                    flag = true;
                    layout_progress.setVisibility(View.GONE);
                    Toast toast = Toast.makeText(LoginActivity.this, "Please contact Admin for login issues!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            }
        });
    }

    public void signInClub(FirebaseUser user, DocumentSnapshot document) {
        String dept = (String) document.get("dept");
        String email = user.getEmail();
        String id = (String) document.get("id");
        String name = (String) document.get("name");
        String access = (String) document.get("access");

        Long year = (Long) document.get("year");
        SharedPref.putInt(getApplicationContext(),"year", Integer.parseInt(year.toString()));

        SharedPref.putInt(getApplicationContext(),"clearance",clearance);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putString(getApplicationContext(), "access", access);
        SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in",2);

        layout_progress.setVisibility(View.GONE);
        flag = true;
        setUpNotification();

        //Navigate to Club UI
        //startActivity(new Intent(getApplicationContext(), StudentHomeActivity.class));
        //finish();
    }

    //*****************************************************************************************************************************

    public void setUpNotification() {
        SharedPref.putBoolean(getApplicationContext(), "switch_all", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_dept", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_bus", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_club", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_exam", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_workshop", true);
    }

    public void SubscribeToAlerts(Context context){
        FCMHelper.SubscribeToTopic(context, Constants.BUS_ALERTS);
        FCMHelper.SubscribeToTopic(context, Constants.CLUB_ALERTS);
        FCMHelper.SubscribeToTopic(context, Constants.EXAM_CELL_ALERTS);
        FCMHelper.SubscribeToTopic(context, Constants.WORKSHOP_ALERTS);

        if(clearance==0)
            FCMHelper.SubscribeToTopic(context,SharedPref.getString(context,"dept") + SharedPref.getInt(context,"year"));
        else if(clearance==1)
            FCMHelper.SubscribeToTopic(context,SharedPref.getString(context,"dept"));
    }

    void showDialog(String text){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog2, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView tv_ok = dialogView.findViewById(R.id.tv_ok);
        TextView tv_message = dialogView.findViewById(R.id.tv_message);

        tv_message.setText(text + " will be updated and notified shortly. Please try again later.");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (flag) {
            switch (v.getId()) {
                case R.id.cv_student:
                    clearance = 0;
                    access = "";
                    break;
                case R.id.cv_faculty:
                    clearance = 1;
                    break;
                /*case R.id.cv_club:
                    clearance = 0;
                    access = "CA";
                    showDialog("Club member login");
                    break;
                case R.id.cv_event:
                    clearance = 2;
                    showDialog("Event feature");
                    break;*/
            }

            mAuth.signOut();
            mGoogleSignInClient.signOut();
            layout_progress.setVisibility(View.VISIBLE);
            flag = false;
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(startMain);
        finish();
    }
}
