package com.example.arthur.owlcity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Freezable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.owlcity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateAccountActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private EditText newValue, password;
    private Button btnSubmit;
    private String selection, input;
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        textViewTitle = findViewById(R.id.textView);
        newValue = findViewById(R.id.newValue);
        password = findViewById(R.id.password);

        Intent intent = getIntent();
        selection = intent.getStringExtra("Selection");

        textViewTitle.setText("Enter new " + selection);

        btnSubmit = findViewById(R.id.btnSubmit);

        //if updating password, make the edit text box to hide the inputted value
        if(selection.equals("password")){
            newValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //To re-authenticate user using Firebase Re-authentication
                AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), password.getText().toString());

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //save the inputted text into variable input
                                input = newValue.getText().toString();
                                checkSelection();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToast("Incorrect credential");

                            }
                        });
            }
        });


    }

    public void checkSelection(){
        switch (selection){
            case "name" :
                updateName();
                break;
            case "email":
                updateEmail();
                break;
            case "password":
                updatePassword();
                break;
        }
    }

    //update name
    public void updateName(){
        //set up update to Firebase User displayName to new value
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(input)
                .build();

        firebaseUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    //update Firebase Realtime Database value
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseReference = FirebaseDatabase.getInstance().getReference("user/" + firebaseUser.getUid() + "/name");
                        firebaseReference.setValue(input);

                        makeAlert();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        makeToast("Something went wrong. Please try again");
                    }
                });
    }

    //update email
    public void updateEmail() {
        firebaseUser.updateEmail(input)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseReference = FirebaseDatabase.getInstance().getReference("user/" + firebaseUser.getUid() + "/email");
                        firebaseReference.setValue(input);

                        makeAlert();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeToast("Something went wrong. Please try again");

                    }
                });
    }

    //update password
    public void updatePassword(){
        firebaseUser.updatePassword(input)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseReference = FirebaseDatabase.getInstance().getReference("user/" + firebaseUser.getUid() + "/password");
                        firebaseReference.setValue(input);

                        makeAlert();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        makeToast("Something went wrong. Please try again");

                    }
                });
    }

    //make custom alert dialog
    public void makeAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Successful");
        alertDialog.setMessage("Successfully updated " + selection);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

}
