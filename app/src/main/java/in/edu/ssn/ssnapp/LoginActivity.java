package in.edu.ssn.ssnapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.view.ViewGroup;
import android.widget.ImageView;

public class LoginActivity extends BaseActivity {

    CardView signInCV;
    ImageView roadIV, skyIV, signBoardIV;
    //FirebaseAuth mAuth;
    //GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();

        /*signInCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

                mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                signIn();

                //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });*/
    }

    /*private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.d("test_set", e.getMessage());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("test_set", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("test_set", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            Log.d("test_set", "signInWithCredential:failure");
                        }
                    }
                });
    }*/

    void initUI(){
        changeFont(regular,(ViewGroup) this.findViewById(android.R.id.content));
        signInCV = findViewById(R.id.signInCV);
        //mAuth = FirebaseAuth.getInstance();
        /*roadIV = findViewById(R.id.roadIV);
        skyIV = findViewById(R.id.skyIV);
        signBoardIV = findViewById(R.id.signBoardIV);*/
        /*Picasso.get().load("file:///android_asset/sky.png").into(skyIV);
        Picasso.get().load("file:///android_asset/road_with_grass.png").into(roadIV);
        Picasso.get().load("file:///android_asset/sign_board.png").into(signBoardIV);*/
    }
}

//FirebaseAuth.getInstance().signOut();