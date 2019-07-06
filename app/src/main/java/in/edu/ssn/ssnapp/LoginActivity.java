package in.edu.ssn.ssnapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.edu.ssn.ssnapp.fragments.FeedFragment;
import in.edu.ssn.ssnapp.utils.FCMHelper;
import in.edu.ssn.ssnapp.utils.SharedPref;

public class LoginActivity extends BaseActivity {

    CardView signInCV;
    ImageView roadIV, skyIV, signBoardIV;

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();

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
                    mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("test_set", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                checkForSignin(user);
                            }
                            else
                                Log.d("test_set", "signInWithCredential:failure");
                        }
                    });
                }
                else
                    Toast.makeText(this, "Please use SSN mail ID", Toast.LENGTH_SHORT).show();
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
        String dp_url = (String) document.get("dp_url");
        String email = user.getEmail();
        String id = (String) document.get("id");
        String name = (String) document.get("name");
        Long year = (Long) document.get("year");

        SharedPref.putInt(getApplicationContext(),"clearance",0);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"dp_url", dp_url);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putInt(getApplicationContext(),"year", Integer.parseInt(year.toString()));
        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", true);

        Log.d("test_set", "signin");
        FCMHelper.SubscribeToTopic(this,dept);
        FCMHelper.UpdateFCM(this, SharedPref.getString(this,"FCMToken"));
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
        users.put("FCMToken", SharedPref.getString(this,"FCMToken"));
        FirebaseFirestore.getInstance().collection("user").document(id).set(users);
        FCMHelper.SubscribeToTopic(this, dept);

        SharedPref.putInt(getApplicationContext(),"clearance", 0);
        SharedPref.putString(getApplicationContext(),"dept", dept);
        SharedPref.putString(getApplicationContext(),"dp_url", dp_url);
        SharedPref.putString(getApplicationContext(),"email", email);
        SharedPref.putString(getApplicationContext(),"id", id);
        SharedPref.putString(getApplicationContext(),"name", name);
        SharedPref.putInt(getApplicationContext(),"year", year);
        SharedPref.putBoolean(getApplicationContext(),"is_logged_in", true);

        Log.d("test_set", "signup");
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    void initUI(){
        changeFont(regular,(ViewGroup) this.findViewById(android.R.id.content));
        signInCV = findViewById(R.id.signInCV);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        /*roadIV = findViewById(R.id.roadIV);
        skyIV = findViewById(R.id.skyIV);
        signBoardIV = findViewById(R.id.signBoardIV);*/
        /*Picasso.get().load("file:///android_asset/sky.png").into(skyIV);
        Picasso.get().load("file:///android_asset/road_with_grass.png").into(roadIV);
        Picasso.get().load("file:///android_asset/sign_board.png").into(signBoardIV);*/
    }
}

/*
    Sign-out
    Add all 2 lines, else account-cache occur

    mAuth.signOut();
    mGoogleSignInClient.signOut();
 */

/*
    Log.d("test_set",email);
    Log.d("test_set",user.getDisplayName());
    Log.d("test_set",user.getPhotoUrl().toString());
    Log.d("test_set",email.substring(x+1, x+4));
    Log.d("test_set",Integer.toString(Integer.parseInt(email.substring(x-5, x-3))+2000));
*/