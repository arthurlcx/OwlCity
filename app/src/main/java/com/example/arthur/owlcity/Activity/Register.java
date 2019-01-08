package com.example.arthur.owlcity.Activity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arthur.owlcity.Class.GlideApp;
import com.example.arthur.owlcity.Class.User;
import com.example.arthur.owlcity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

//import com.example.arthur.owlcity.Manifest;

public class Register extends AppCompatActivity {

private EditText userEmail,userPassword,userPassword2,userName;
private Switch mySwitch;
private ProgressBar loadingProgress;
private Button regBtn;
private FirebaseAuth mAuth;
private ImageView imageView;

private String accountType;
private String email, password, password2, name, profileImg = null;

private static final int PICK_IMAGE_REQUEST = 1;
private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail=findViewById(R.id.regMail);
        userPassword=findViewById(R.id.regPass);
        userPassword2=findViewById(R.id.regPass2);
        userName=findViewById(R.id.regName);
        loadingProgress=findViewById(R.id.progressBar);
        regBtn = findViewById(R.id.regBtn);
        imageView = findViewById(R.id.imageView3);

        accountType = "member";

        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        //click register button
        regBtn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                email = userEmail.getText().toString ();
                password = userPassword.getText().toString();
                password2 = userPassword2.getText().toString();
                name = userName.getText().toString();

                //if all information invalid
                if(email.isEmpty() || name.isEmpty() || password.isEmpty()|| !password.equals(password2)){
                    showMessage("Please verify all fields");
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                else{

                    //if valid, start registration
                    uploadToFirebase();

                }
                
            }
        });

    }

    //create user object
    private void CreateUserAccount(final String email, final String name, final String password, final String profileImg) {

        //create a new Firebase Authentication account using email and password
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            showMessage("ACCOUNT CREATED");
                            //once Firebase Authentication registered success, next is store into Firebase database
                            insertToFirebase(name, password, email, profileImg);
                        }
                        else
                        {
                            showMessage("Failed to Create" + task.getException().getMessage());

                        }

                        regBtn.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.INVISIBLE);
                    }
                });

        }


        // insert into Firebase database as second copy
    private void insertToFirebase (String name, String password, String email, String profileImg){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");

        User user = new User(mAuth.getCurrentUser().getUid(), name, email, password, accountType, profileImg);

        try {
            databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user);

            //update user info in Firebase Authentication
            updateUserInfo(mAuth.getCurrentUser());

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    //update user profile info. name, email and profile photo url
    private void updateUserInfo(FirebaseUser currentUser) {

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName.getText().toString())
                .setPhotoUri(Uri.parse(profileImg))
                .build();

        currentUser.updateEmail(userEmail.getText().toString());

        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            showMessage("Registration Completed");
                            nextActivity();
                        }
                    }
                });
    }

    //send user to sign in page
    private void nextActivity(){
        Intent intent = new Intent(getApplicationContext(), SignIn.class);

        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    //open phone GALLERY to upload image
    public void uploadProfilePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //once finished selecting photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            try {

                //called Glide API to display image as preview
                GlideApp.with(this).load(data.getData()).apply(RequestOptions.circleCropTransform()).into(imageView);

                //store the image URI
                imageUri = data.getData();

            } catch (Exception e) {
                e.printStackTrace();
                showMessage("Error displaying image");
            }

        }
    }

    //get image file extension for naming image file (.jpg .png)
    public String getFileExtension (Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    //upload image into Firebase Storage
    public void uploadToFirebase(){
        if (imageUri != null) {

            regBtn.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);

            //set up the directory
            profileImg = "profile/" + System.currentTimeMillis() + "." + getFileExtension(imageUri);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference fileReference = storageReference.child(profileImg);

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //create a 5 seconds delay to esure the progress bar finishes. act as a user feedback
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingProgress.setProgress(0);
                                }
                            }, 5000);

                            showMessage("Image uploaded successfully");

                            //create user object
                            CreateUserAccount(email,name,password, profileImg);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("Failed to upload");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //during uploading, update the progress bar byt the ratio of the byte data transferred
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            loadingProgress.setProgress((int)progress);
                        }
                    });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
            builder.setTitle("Wait a minute")
                    .setMessage("You haven't upload a profile picture. Do you want to do that now?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    uploadProfilePicture(imageView);
                                }
                            }
                    )
                    .setNegativeButton("No, I'll use the default profile picture first",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                    profileImg = "profile/user.png";
                                    CreateUserAccount(email,name,password, profileImg);
                                }
                            }
                    );
            builder.create();
            builder.show();
        }

    }
}
