package com.example.arthur.owlcity.Activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.arthur.owlcity.Class.User;
import com.example.arthur.owlcity.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";

    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth firebaseAuth;

    private Intent intent;

    private ProgressBar progressBar;


    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            signOut();
        }

        progressBar = findViewById(R.id.progressBar2);

        progressBar.setVisibility(View.INVISIBLE);


    }

    // got to register activity
    public void goToRegister (View view){
        intent = new Intent(getApplicationContext(), Register.class);

        startActivity(intent);
        onStop();
    }

    //sign in button clicked
    public void signIn (View view){


        if (firebaseAuth.getCurrentUser() != null) {
                    // if already signed in

                    intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    // not signed in

                    //initiate Firebase Authentication Pre-built signin UI
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);

                }
    }

    //after sign in UI completed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                //if correct email and password
                makeToast("Welcome Back!");
                //declare current user as the user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //determine the account type (member or admin)
                checkAccountType(user);
            }
        }
    }

    //check the account type vai database
    public void checkAccountType(FirebaseUser user){
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("user");
        Query query = firebaseReference.orderByChild("userId").equalTo(user.getUid());

        query.addListenerForSingleValueEvent(valueEventListener);
    }

    //retrive data from database
    ValueEventListener valueEventListener = new ValueEventListener() {
        String accountType;
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                User user = snapshot.getValue(User.class);

                accountType = user.getAccountType();
            }

            //pass the result to be process
            nextActivity(accountType);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void nextActivity (String accountType){
        Intent intent;
        progressBar.setVisibility(View.VISIBLE);

        //if account is member, bring to members page
        if(accountType.equals("member")){
            //if account is member, bring to members page

            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            //if account is admin, bring to admin page

            intent = new Intent(getApplicationContext(), adminActivity.class);
        }

        startActivity(intent);
        onStop();
    }

    //continue as guest button clicked
    public void signInAnnoym (View view){
        if (firebaseAuth.getCurrentUser() == null) {
            //initiate Firebase Authentication for anonymous sign in
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(SignIn.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        } else {
            makeToast("Already Signed In");

        }
    }

    //to sign out
    public void signOut(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }


    public void makeToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }


}
