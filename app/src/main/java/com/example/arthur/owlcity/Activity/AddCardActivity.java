package com.example.arthur.owlcity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arthur.owlcity.Class.CardInfo;
import com.example.arthur.owlcity.Class.User;
import com.example.arthur.owlcity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddCardActivity extends AppCompatActivity {

    private EditText cardNoEditText, nameEditText, ccvEditText;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cardNoEditText = findViewById(R.id.cardNoEditText);
        nameEditText = findViewById(R.id.nameEditText);
        ccvEditText = findViewById(R.id.ccvEditText);

    }
    //add new card into Firebase database
    public void addCard (View view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("card");

        //create new unique key as primary key
        String ID = databaseReference.push().getKey();

        CardInfo cardInfo = new CardInfo(ID, Long.parseLong(cardNoEditText.getText().toString()), nameEditText.getText().toString(),
                                            Long.parseLong(ccvEditText.getText().toString()), firebaseUser.getUid() );

        databaseReference.child(ID).setValue(cardInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            AlertDialog alertDialog = new AlertDialog.Builder(AddCardActivity.this).create();
                                            alertDialog.setTitle("Successful");
                                            alertDialog.setMessage("Card added successfully");
                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //once success, prompt alert dialog and bring user back tp profile
                                                            dialog.dismiss();
                                                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                            alertDialog.show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            makeToast("Error adding card");
                                        }
                                    });
    }

    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
