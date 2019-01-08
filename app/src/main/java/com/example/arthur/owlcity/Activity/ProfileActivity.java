package com.example.arthur.owlcity.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.example.arthur.owlcity.Class.CardInfo;
import com.example.arthur.owlcity.Class.GlideApp;
import com.example.arthur.owlcity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private TextView userIdTextView, nameTextView, emailTextView,
                        cardNoTextView, cardNameTextView;
    private LinearLayout layout2, layout3;
    private ImageView profileImgView;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private CardInfo cardInfo;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ProgressBar loadingProgress;
    private String profileImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        userIdTextView = findViewById(R.id.userIdText);
        nameTextView = findViewById(R.id.userNameText);
        emailTextView = findViewById(R.id.emailText);
        cardNoTextView = findViewById(R.id.cardNoText);
        cardNameTextView = findViewById(R.id.cardNameText);
        profileImgView = findViewById(R.id.profileImg);
        loadingProgress = findViewById(R.id.progressBar);

        layout2.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.INVISIBLE);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null){
            userIdTextView.setText(firebaseUser.getUid());
            nameTextView.setText(firebaseUser.getDisplayName());
            emailTextView.setText(firebaseUser.getEmail());

            //define a reference for Glide API
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(firebaseUser.getPhotoUrl().toString());

            //execute Glide API to retrieve and display image
            GlideApp.with(this).load(storageReference).apply(RequestOptions.circleCropTransform()).into(profileImgView);

            //define Firebase realtime database reference
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("card");
            //define database query. SELECT * FROM card WHERE cardownerid = UiD
            Query query = databaseReference.orderByChild("cardOwnerId").equalTo(firebaseUser.getUid());

            //execute database query
            query.addListenerForSingleValueEvent(valueEventListener);

        } else {
            makeToast("No user log in");
        }



    }

    //Firebase retireve data
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //if record found
            if(dataSnapshot.exists()){
                layout2.setVisibility(View.VISIBLE);
                //hide "Add new card section"
                layout3.setVisibility(View.GONE);

                //store all the record value into a CardInfo object
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    cardInfo = snapshot.getValue(CardInfo.class);
                }

                //display only the CARD NUMBER and CARD NAME
                cardNoTextView.setText(String.valueOf(cardInfo.getCardNo()));
                cardNameTextView.setText(cardInfo.getCardName());

            } else {
                layout2.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //launch phone gallery to choose image
    public void changePic (View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //after successfully choosing an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){

            try {

                imageUri = data.getData();

                uploadToFirebase();

            } catch (Exception e) {
                e.printStackTrace();
                makeToast("Error displaying image");
            }

        }
    }

    //to upload new image to Firebase
    private void uploadToFirebase() {
        if (imageUri != null) {

            //Hide the profile image and show the progress bar
            profileImgView.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);

            profileImg = "profile/" + System.currentTimeMillis() + "." + getFileExtension(imageUri);

            //Set up a Firebase Storage reference for files to be uploaded
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            //Name the file name after the "profileImg" variable
            StorageReference fileReference = storageReference.child(profileImg);

            //Upload the image via the URI
            fileReference.putFile(imageUri)
                    //If success
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Make a 5 seconds delay using Handler to provide success feedback for users
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadingProgress.setProgress(0);
                                }
                            }, 5000);

                            profileImgView.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                            //Update Firebase Realtime database in user/UiD/profileImg
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user/" + firebaseUser.getUid() + "/profileImg");

                            databaseReference.setValue(profileImg);

                            //Update Firebase Authentication information
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(profileImg))
                                    .build();

                            firebaseUser.updateProfile(profileUpdate)
                                    //if successful
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                makeToast("Profile picture update successfully");

                                                //restart the activity to load updated photo
                                                recreate();

                                            }
                                        }
                                    });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToast("Failed to upload");
                        }
                    })
                    //In progress, update the progress bar by calculating the ratio between the data byte transferred
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            loadingProgress.setProgress((int)progress);
                        }
                    });
        }
    }

    //to retrieve the file extension from the image (.jpg .png)
    public String getFileExtension (Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void updateProfile (View view){
        intent = new Intent(getApplicationContext(), UpdateAccountSelectionActivity.class);
        startActivity(intent);
    }

    public void updateCard(View view){
        intent = new Intent(getApplicationContext(), UpdateCardInfoActivity.class);
        startActivity(intent);
    }

    public void addCard(View view){
        intent = new Intent(getApplicationContext(), AddCardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void makeToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
