package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;
import pl.droidsonroids.gif.GifImageView;

public class LogoutActivity extends AppCompatActivity {

    CardView signInCV;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 111;
    GifImageView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        initGoogleSignIn();

        signInCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    /************************************************************************/
    // Google Signin

    public void initGoogleSignIn(){
        progress = findViewById(R.id.progress);
        signInCV = findViewById(R.id.signInCV);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(LogoutActivity.this, gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount acct = task.getResult(ApiException.class);
                Pattern pat = Pattern.compile("@[a-z]{2,3}(.ssn.edu.in)$");
                Matcher m = pat.matcher(acct.getEmail());

                if (m.find()) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
                    progress.setVisibility(View.VISIBLE);
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("test_set", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                checkForSignin(user);
                            }
                            else {
                                Log.d("test_set", "signInWithCredential:failure");
                                progress.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(this, "Please use SSN mail ID", Toast.LENGTH_SHORT).show();
                }
            }
            catch (ApiException e) {
                Log.d("test_set", e.getMessage());
            }
        }
    }

    public void checkForSignin(final FirebaseUser user){
        String id = user.getUid();

        FirebaseFirestore.getInstance().collection("user").whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().isEmpty())
                        signUp(user);
                    else {
                        List<DocumentSnapshot> document = task.getResult().getDocuments();
                        signIn(user, document.get(0));
                    }
                }
            }
        });
    }

    public void signIn(FirebaseUser user, DocumentSnapshot document){
        String dept = (String) document.get("dept");
        String email = user.getEmail();
        String id = (String) document.get("id");
        String name = (String) document.get("name");
        Long year = (Long) document.get("year");

        SharedPref.putInt(getApplicationContext(),"clearance",0);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putInt(getApplicationContext(),"year", Integer.parseInt(year.toString()));
        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", true);

        Log.d("test_set", "signin");
        progress.setVisibility(View.GONE);
        FCMHelper.SubscribeToTopic(this,dept);
        FCMHelper.UpdateFCM(this,SharedPref.getString(this,"FCMToken"));
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    public void signUp(FirebaseUser user){
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
        users.put("clearance", 0);
        users.put("dept", dept);
        users.put("dp_url", dp_url);
        users.put("email", email);
        users.put("id", id);
        users.put("name", name);
        users.put("year", year);
        users.put("FCMToken",SharedPref.getString(this,"FCMToken"));
        FirebaseFirestore.getInstance().collection("user").document(id).set(users);
        FCMHelper.SubscribeToTopic(this,dept);

        SharedPref.putInt(getApplicationContext(),"clearance", 0);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putInt(getApplicationContext(),"year", year);
        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", true);

        Log.d("test_set", "signup");
        progress.setVisibility(View.GONE);
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(startMain);
        finish();
    }
}
