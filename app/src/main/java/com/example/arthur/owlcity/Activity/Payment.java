package com.example.arthur.owlcity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.owlcity.Class.CardInfo;
import com.example.arthur.owlcity.Class.ReservationInfo;
import com.example.arthur.owlcity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Payment extends AppCompatActivity {

    private static final String TAG = "Payment";

    private String info;
    private EditText cardNo;
    private EditText cardName;
    private EditText ccv;

    private String clubName;
    private String date;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseUser firebaseUser;

    private CardInfo cardInfo;
    private Reservation reservation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Authenticating", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                try {
                DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("card");
                Query query = firebaseReference.orderByChild("cardOwnerId").equalTo(firebaseUser.getUid());
                query.addListenerForSingleValueEvent(valueEventListener);
                } catch (Exception e) {
                    makeToast("Error calling query");
                    e.printStackTrace();
                }


            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();


        String packageName = intent.getStringExtra("packageName");
        String packageDesc = intent.getStringExtra("packageDesc");
        String packageSeat = intent.getStringExtra("packageSeat");
        String packagePrice = intent.getStringExtra("packagePrice");
        clubName = intent.getStringExtra("clubName");
        date = intent.getStringExtra("date");


        TextView textView = findViewById(R.id.info);
        cardNo = findViewById(R.id.cardNo);
        cardName = findViewById(R.id.cardName);
        ccv = findViewById(R.id.cardCcv);

        info = packageName + "\n" + packageDesc + "\n"
                + packageSeat + "\n" + packagePrice;

        textView.setText(info);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            showData(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            try {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cardInfo = snapshot.getValue(CardInfo.class);
                }

                validateCardInfo(cardInfo);
            } catch (Exception e) {
                makeToast("Error Retrieving Card Information");
                e.printStackTrace();
            }
        } else {
            makeToast("Card not registered");
        }

    }

    public void validateCardInfo (CardInfo cardInfo){
        Log.i(TAG, "validateCardInfo: " + cardInfo.toString());
        Long dCardNo = Long.parseLong(cardNo.getText().toString());
        Long dCcv = Long.parseLong(ccv.getText().toString());

        if((dCardNo.equals(cardInfo.getCardNo())) &&
                (cardName.getText().toString().equals(cardInfo.getCardName())) &&
                (dCcv.equals(cardInfo.getCcv()))){

            saveReservationInfo();

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Incorrect Credentials");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private void saveReservationInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reservationInfo");

        String ID = databaseReference.push().getKey();

        ReservationInfo reservationInfo = new ReservationInfo(ID, firebaseAuth.getInstance().getCurrentUser().getUid(), cardInfo.getCardName(),
                                                                 clubName, info, date);

        try {
            databaseReference.child(ID).setValue(reservationInfo);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Payment Successful");
            alertDialog.setMessage("Reservation is completed");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Back To Home",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error inserting");
        }
    }


    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }



}
