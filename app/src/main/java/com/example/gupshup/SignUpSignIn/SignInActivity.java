package com.example.gupshup.SignUpSignIn;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gupshup.MainActivity;
import com.example.gupshup.Models.UserModel;
import com.example.gupshup.R;
import com.example.gupshup.SignupOptions.FacebookLogin;
import com.example.gupshup.databinding.ActivitySigninBinding;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
        ActivitySigninBinding binding;
        ProgressDialog dialog ;
        FirebaseAuth auth;

    GoogleSignInClient googleSignInClient;
    SignInClient oneTapClient;

    private static final int REQ_ONE_TAP = 2;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());



        oneTapClient = Identity.getSignInClient(this);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Logging into your account");
        dialog.setMessage("Please wait... ");


        firebaseDatabase = FirebaseDatabase.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = FirebaseAuth.getInstance();

        binding.btnSignIn.setOnClickListener(view -> {

            dialog.show();

            auth.signInWithEmailAndPassword(binding.etEmail.getText().toString(), binding.etPassward.getText().toString())
                    .addOnCompleteListener(task -> {

                        dialog.dismiss();

                        if(task.isSuccessful()){

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);

                        }

                        else
                        {
                            Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }


                    });

            });
            binding.tvCreateHaveAccount.setOnClickListener(view -> {
                Intent intent = new Intent(SignInActivity.this, SignUp.class);
                SignInActivity.this.startActivity(intent);
                SignInActivity.this.finish();

            });
        binding.btnGoogle.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, REQ_ONE_TAP);
        });
        binding.btnFacebook.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, FacebookLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);
                    Toast.makeText(this, "Signed into your Account", Toast.LENGTH_SHORT).show();
                    GoogleSignInWithGoogle(account.getIdToken());

                }
                catch (ApiException e) {
                e.getStatusCode();
            }
        }
        else
        {
            Toast.makeText(this, "please try again...", Toast.LENGTH_SHORT).show();
        }

    }


    private void GoogleSignInWithGoogle(String idToken){
        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                        UserModel userModel = new UserModel();
                        assert user != null;
                        userModel.setUserId(user.getUid());
                        userModel.setUsername(user.getDisplayName());
                        userModel.setPicture(Objects.requireNonNull(user.getPhotoUrl()).toString());
                        firebaseDatabase.getReference().child("users").child(user.getUid()).setValue(userModel);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });

    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
