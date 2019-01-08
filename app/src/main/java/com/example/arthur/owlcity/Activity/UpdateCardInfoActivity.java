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
import com.example.arthur.owlcity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateCardInfoActivity extends AppCompatActivity {

        private EditText cardNoEditText, nameEditText, ccvEditText;
        private FirebaseUser firebaseUser;
        private CardInfo cardInfo;
        private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_card_info);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cardNoEditText = findViewById(R.id.cardNoEditText);
        nameEditText = findViewById(R.id.nameEditText);
        ccvEditText = findViewById(R.id.ccvEditText);

        //retrieve card info for the current user
        databaseReference = FirebaseDatabase.getInstance().getReference("card");
        Query query = databaseReference.orderByChild("cardOwnerId").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    //retieve database data
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    cardInfo = snapshot.getValue(CardInfo.class);

                }

                cardNoEditText.setText(String.valueOf(cardInfo.getCardNo()));
                nameEditText.setText(cardInfo.getCardName());
                ccvEditText.setText(String.valueOf(cardInfo.getCcv()));

            } else {
                makeToast("No record found");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //button pressed to update card in Firebase database
    public void updateCard (View view){


        CardInfo newCardInfo = new CardInfo(cardInfo.getCardId(), Long.parseLong(cardNoEditText.getText().toString()), nameEditText.getText().toString(),
                                            Long.parseLong(ccvEditText.getText().toString()), firebaseUser.getUid() );

        //Update values inside Firebase database
        databaseReference.child(cardInfo.getCardId()).setValue(newCardInfo)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                AlertDialog alertDialog = new AlertDialog.Builder(UpdateCardInfoActivity.this).create();
                                                                alertDialog.setTitle("Update successful");
                                                                alertDialog.setMessage("Card updated successfully");
                                                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int which) {
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
                                                                makeToast("Error updating");
                                                            }
                                                        });
    }

    public void removeCard(View view){
        //show alert dialog to make confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateCardInfoActivity.this);
        builder.setTitle("Remove Card")
                .setMessage("Your card information will be lost permenantly. Dp you sure you still want to continue?")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Yes, remove from database
                                databaseReference.child(cardInfo.getCardId()).removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                AlertDialog alertDialog = new AlertDialog.Builder(UpdateCardInfoActivity.this).create();
                                                alertDialog.setTitle("Remove successful");
                                                alertDialog.setMessage("Card removed successfully");
                                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
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
                                                makeToast("Error removing card");
                                            }
                                        });
                            }
                        }
                )
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //No, dialog dismisses
                                dialog.dismiss();

                            }
                        }
                );
        builder.create();
        builder.show();


    }

    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


}

