package com.example.hrm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    String current_user_id;

    //variables
//    EditText mainUser = findViewById(R.id.Username);
//    EditText mainPass = findViewById(R.id.password);
//    Button mainSignIn = findViewById(R.id.manual_sign_button);
//
//    TextView mainRegister = findViewById(R.id.registered);

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser Cuser = mAuth.getCurrentUser();

        if(Cuser != null) {
            Intent intent = new Intent(getApplicationContext(),PropertyPage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();
        //authentication request
        createRequest();

//        mainRegister.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this,Loginpage.class);
//            startActivity(intent);
//        });
//
//        mainSignIn.setOnClickListener(v -> {
//            String mUsername = mainUser.getText().toString();
//            String mPass = mainPass.getText().toString();
//
//            if(!mUsername.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mUsername).matches()) {
//                if(!mPass.isEmpty()) {
//                    mAuth.createUserWithEmailAndPassword(mUsername,mPass)
//                            .addOnCompleteListener(task -> {
//                                Toast.makeText(MainActivity.this, "Sign Up Sucsessfull", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(),Frontpage.class);
//                                startActivity(intent);
//                            }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show());
//
//                }else {
//                    mainPass.setError("Please Fill All The Field");
//                }
//            }else if(mUsername.isEmpty()) {
//                mainUser.setError("Please Fill All The Fields");
//            }else {
//                mainUser.setError("Please Enter Correct Email");
//            }
//        });
//
        findViewById(R.id.sign_in_google_button).setOnClickListener(v -> signIn());

        findViewById(R.id.click_here).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,PropertyPage.class);
            startActivity(intent);
        });
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = (GoogleSignInAccount) ((Task<?>) task).getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Authentication Success", Toast.LENGTH_LONG).show();
                        updateUI(user);
                    }else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(getApplicationContext(),PropertyPage.class);
        startActivity(intent);
    }
} 