package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import com.sahurjt.objectcsv.CsvDelimiter;
import com.sahurjt.objectcsv.CsvHolder;
import com.sahurjt.objectcsv.ObjectCsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.models.Faculty;
import in.edu.ssn.ssnapp.onboarding.OnboardingActivity;
import in.edu.ssn.ssnapp.utils.CommonUtils;
import in.edu.ssn.ssnapp.utils.Constants;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import spencerstudios.com.bungeelib.Bungee;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    CardView cv_student, cv_faculty;
    ImageView iv_student, iv_faculty, iv_gate, iv_road;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 111;
    RelativeLayout layout_progress;
    int clearance;
    Boolean flag = true;
    String flag1="", flag2="";
    View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }

    void initUI(){
        cv_student = findViewById(R.id.cv_student);     cv_student.setOnClickListener(this);
        cv_faculty = findViewById(R.id.cv_faculty);     cv_faculty.setOnClickListener(this);
        iv_student = findViewById(R.id.iv_student);
        iv_faculty = findViewById(R.id.iv_faculty);
        iv_gate = findViewById(R.id.iv_gate);
        iv_road = findViewById(R.id.iv_road);
        layout_progress = findViewById(R.id.layout_progress);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_gate.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.ZoomIn).duration(500).playOn(iv_gate);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_road.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInDown).duration(800).playOn(iv_road);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                iv_student.setVisibility(View.VISIBLE);
                                iv_faculty.setVisibility(View.VISIBLE);

                                YoYo.with(Techniques.ZoomInDown).duration(500).playOn(iv_student);
                                YoYo.with(Techniques.ZoomInDown).duration(500).playOn(iv_faculty);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        cv_student.setVisibility(View.VISIBLE);
                                        cv_faculty.setVisibility(View.VISIBLE);

                                        YoYo.with(Techniques.BounceIn).duration(1000).playOn(cv_student);
                                        YoYo.with(Techniques.BounceIn).duration(1000).playOn(cv_faculty);
                                    }
                                },500);
                            }
                        },500);
                    }
                },300);
            }
        }, 300);
    }

    /************************************************************************/
    // Google Signin

    public void initGoogleSignIn() {
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        mAuth.signOut();
        mGoogleSignInClient.signOut();
        layout_progress.setVisibility(View.VISIBLE);
        flag = false;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                Pattern pat_s1 = Pattern.compile("(@ssn.in)$");
                Matcher m_s1 = pat_s1.matcher(acct.getEmail());

                Pattern pat_f = Pattern.compile("(@ssn.edu.in)$");
                Matcher m_f = pat_f.matcher(acct.getEmail());

                if(clearance == 0) {
                    if (m_s.find() || m_s1.find()) {
                        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    promptDetailsDialog(user);
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
                        layout_progress.setVisibility(View.GONE);
                        flag = true;
                        Toast toast = Toast.makeText(this, "Please use SSN mail ID", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                else if(clearance == 1){
                    if(!m_f.find()){
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
        String dp_url = user.getPhotoUrl().toString();
        String name = user.getDisplayName();
        String dept = flag2;
        int year = Integer.parseInt(flag1);

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
        Bungee.slideLeft(LoginActivity.this);
    }

    public void checkForFaculty(final FirebaseUser user) {
        String email = user.getEmail();

        String name = SharedPref.getString(getApplicationContext(),"faculty_name", email);
        if(name!=null)
            signInFaculty(user);
        else{
            flag=true;
            layout_progress.setVisibility(View.GONE);
            user.delete();
            Toast toast = Toast.makeText(LoginActivity.this, "Please contact Admin for login issues!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void signInFaculty(FirebaseUser user) {
        String email = user.getEmail();
        String id = user.getUid();
        String dept = SharedPref.getString(getApplicationContext(),"faculty_dept", email);
        String access = SharedPref.getString(getApplicationContext(),"faculty_access", email);
        String name = SharedPref.getString(getApplicationContext(),"faculty_name", email);
        String position = SharedPref.getString(getApplicationContext(),"faculty_position", email);
        String dp_url = user.getPhotoUrl().toString();

        SharedPref.putInt(getApplicationContext(), "clearance", 1);
        SharedPref.putString(getApplicationContext(), "email", email);
        SharedPref.putString(getApplicationContext(), "id", id);
        SharedPref.putString(getApplicationContext(), "position", position);
        SharedPref.putString(getApplicationContext(), "access", access);
        SharedPref.putString(getApplicationContext(), "dept", dept);
        SharedPref.putString(getApplicationContext(), "dp_url", dp_url);
        SharedPref.putString(getApplicationContext(), "name", name);
        SharedPref.putInt(getApplicationContext(),"dont_delete","is_logged_in",2);

        layout_progress.setVisibility(View.GONE);
        flag = true;
        startActivity(new Intent(getApplicationContext(), FacultyHomeActivity.class));
        finish();
        Bungee.slideLeft(LoginActivity.this);
    }

    /************************************************************************/

    public void setUpNotification() {
        SharedPref.putBoolean(getApplicationContext(), "switch_all", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_dept", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_bus", true);
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
            SharedPref.putBoolean(getApplicationContext(), "switch_place", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_exam", true);
        SharedPref.putBoolean(getApplicationContext(), "switch_event", true);
    }

    public void SubscribeToAlerts(Context context){
        FCMHelper.SubscribeToTopic(context, Constants.BUS_ALERTS);
        FCMHelper.SubscribeToTopic(context, SharedPref.getString(context, "dept") + SharedPref.getInt(context, "year"));
        FCMHelper.SubscribeToTopic(context, SharedPref.getString(context, "dept") + SharedPref.getInt(context, "year") + "exam");
        if (SharedPref.getInt(getApplicationContext(), "year") == Integer.parseInt(Constants.fourth))
            FCMHelper.SubscribeToTopic(context, SharedPref.getString(context, "dept") + SharedPref.getInt(context, "year") + "place");
        FCMHelper.SubscribeToTopic(context, SharedPref.getString(context, "dept") + SharedPref.getInt(context, "year") + "event");

        // add user property in firebase analytics
        try{
            CommonUtils.addUserProperty(LoginActivity.this,"student_department",SharedPref.getString(this, "dept"));
            CommonUtils.addUserProperty(LoginActivity.this,"student_year",Integer.toString(SharedPref.getInt(this,"year")));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /************************************************************************/

    public void promptDetailsDialog(final FirebaseUser user){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.prompt_details, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        TextView tv_ok = dialogView.findViewById(R.id.tv_ok);
        TextView tv_cancel= dialogView.findViewById(R.id.tv_cancel);

        TextView tv_1= dialogView.findViewById(R.id.tv_1);  tv_1.setOnClickListener(this);
        TextView tv_2= dialogView.findViewById(R.id.tv_2);  tv_2.setOnClickListener(this);
        TextView tv_3= dialogView.findViewById(R.id.tv_3);  tv_3.setOnClickListener(this);
        TextView tv_4= dialogView.findViewById(R.id.tv_4);  tv_4.setOnClickListener(this);

        TextView tv_cse= dialogView.findViewById(R.id.tv_cse);  tv_cse.setOnClickListener(this);
        TextView tv_it= dialogView.findViewById(R.id.tv_it);    tv_it.setOnClickListener(this);
        TextView tv_ece= dialogView.findViewById(R.id.tv_ece);  tv_ece.setOnClickListener(this);
        TextView tv_eee= dialogView.findViewById(R.id.tv_eee);  tv_eee.setOnClickListener(this);
        TextView tv_bme= dialogView.findViewById(R.id.tv_bme);  tv_bme.setOnClickListener(this);
        TextView tv_che= dialogView.findViewById(R.id.tv_che);  tv_che.setOnClickListener(this);
        TextView tv_civ= dialogView.findViewById(R.id.tv_civ);  tv_civ.setOnClickListener(this);
        TextView tv_mec= dialogView.findViewById(R.id.tv_mec);  tv_mec.setOnClickListener(this);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag1.equals("")){
                    Toast toast = Toast.makeText(LoginActivity.this, "Please choose your current year", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else if(flag2.equals("")){
                    Toast toast = Toast.makeText(LoginActivity.this, "Please choose your department", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    signInStudent(user);
                    alertDialog.dismiss();
                }
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_progress.setVisibility(View.GONE);
                flag = true;
                alertDialog.dismiss();
            }
        });
    }

    public class updateFaculty extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Glide.with(LoginActivity.this).asFile().load("https://ssnportal.cf/scripts/data_faculty.csv").into(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                    File dir = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"SSN-App");
                    if(!dir.exists())
                        dir.mkdir();

                    File file = new File(dir,"data_faculty.csv");
                    try {
                        FileInputStream inStream = new FileInputStream(resource);
                        FileOutputStream outStream = new FileOutputStream(file);
                        FileChannel inChannel = inStream.getChannel();
                        FileChannel outChannel = outStream.getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                        inStream.close();
                        outStream.close();

                        Log.d("test_set", "download done");

                        if(file.exists()) {
                            try {
                                CsvHolder<Faculty> holder = ObjectCsv.getInstance().from(file.getPath()).with(CsvDelimiter.COMMA).getCsvHolderforClass(Faculty.class);
                                List<Faculty> models = holder.getCsvRecords();

                                for (Faculty m : models) {
                                    String email = m.getEmail();
                                    SharedPref.putString(getApplicationContext(), "faculty_access", email, m.getAccess());
                                    SharedPref.putString(getApplicationContext(), "faculty_dept", email, m.getDept());
                                    SharedPref.putString(getApplicationContext(), "faculty_name", email, m.getName());
                                    SharedPref.putString(getApplicationContext(), "faculty_position", email, m.getPosition());
                                }

                                file.delete();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Log.d("test_set", e.getMessage());
                            }
                        }
                        else{
                            Log.d("test_set","Not Found");
                        }
                    }
                    catch (Exception e) {
                        Log.d("test_set", e.getMessage());
                    }
                }
            });
            return null;
        }
    }

    /************************************************************************/

    @Override
    public void onClick(View v) {
        if (flag) {
            switch (v.getId()) {
                case R.id.cv_student:
                    clearance = 0;
                    break;
                case R.id.cv_faculty:
                    clearance = 1;

                    Calendar calendar = Calendar.getInstance();
                    Long current = calendar.getTimeInMillis();
                    Long prev = SharedPref.getLong(getApplicationContext(),"dont_delete","db_update");
                    if(current - prev > 7200000) {
                        new updateFaculty().execute();
                        SharedPref.putLong(getApplicationContext(), "dont_delete", "db_update", current);
                    }
                    break;
            }
            if(!CommonUtils.alerter(getApplicationContext()))
                initGoogleSignIn();
            else{
                Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                intent.putExtra("key","login");
                startActivity(intent);
                Bungee.fade(LoginActivity.this);
            }
        }
        else{
            switch (v.getId()){
                case R.id.tv_1:
                case R.id.tv_2:
                case R.id.tv_3:
                case R.id.tv_4:
                    String ids[]={"tv_1","tv_2","tv_3","tv_4"};
                    for(String id:ids) {
                        int identifier = getResources().getIdentifier(id,"id",getPackageName());
                        TextView tv = dialogView.findViewById(identifier);
                        if(identifier != v.getId())
                            tv.setBackgroundResource(R.drawable.bus_alert_detail_bg);
                        else {
                            tv.setBackgroundResource(R.drawable.bus_alert_detail_selected_bg);
                            flag1 = CommonUtils.getYear(tv.getTag().toString());
                        }
                    }
                    break;
                case R.id.tv_cse:
                case R.id.tv_it:
                case R.id.tv_eee:
                case R.id.tv_ece:
                case R.id.tv_bme:
                case R.id.tv_che:
                case R.id.tv_civ:
                case R.id.tv_mec:
                    String dids[]={"cse","it","eee","ece","bme","che","civ","mec"};
                    for(String id:dids) {
                        int identifier = getResources().getIdentifier("tv_"+id,"id",getPackageName());
                        TextView tv = dialogView.findViewById(identifier);
                        if(identifier != v.getId())
                            tv.setBackgroundResource(R.drawable.bus_alert_detail_bg);
                        else {
                            tv.setBackgroundResource(R.drawable.bus_alert_detail_selected_bg);
                            flag2 = id;
                        }
                    }

                    break;
            }
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
