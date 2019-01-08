package com.example.arthur.owlcity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.owlcity.Class.ReservationInfo;
import com.example.arthur.owlcity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class ReservationDetail extends AppCompatActivity {

    private TextView reservationIdTextView, dateTextView, clubNameTextView, packageDetailTextView;
    private String reservationId, clubName, date, packageDetails;

    private ReservationInfo reservationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //button to generate QR code for E-Pass
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReservationDetail.this);
                LayoutInflater inflater = (ReservationDetail.this).getLayoutInflater();
                builder.setTitle("E-Pass");
                //inflate the alert dialog with alert_qr layout
                View v = inflater.inflate(R.layout.alert_qr, null);
                builder.setView(v).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                try {

                    TextView qrText = v.findViewById(R.id.idTextView);
                    qrText.setText(reservationId);
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("Error reading text");
                }


                //calling ZXING API to generate QR Code
                try {
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.encodeBitmap(reservationId, BarcodeFormat.QR_CODE, 400, 400);
                    ImageView imageViewQrCode = v.findViewById(R.id.qrImageView);
                    imageViewQrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                    makeToast("Error generating QR code");
                }

                builder.create();
                builder.show();
            }

        });


        reservationIdTextView = findViewById(R.id.reservationIdTextView);
        dateTextView = findViewById(R.id.dateTextView);
        clubNameTextView = findViewById(R.id.clubNameTextView);
        packageDetailTextView = findViewById(R.id.packageDetailTextView);

        Intent intent = getIntent();

        reservationId = intent.getStringExtra("Reservation ID");



        //set up Firebase database to retireve reservation information
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("reservationInfo");
        //SELECT * FROM reservationInfo WHERE reservationId = this reservation Id
        Query query = firebaseReference.orderByChild("reservationID").equalTo(reservationId);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    //retrieve data from database
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    reservationInfo = snapshot.getValue(ReservationInfo.class);
                }

                //assign value into UI
                assignValue(reservationInfo);
            }else {
                makeToast("No data found");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //update the UI with the retrieved information
    public void assignValue (ReservationInfo reservationInfo){
        reservationIdTextView.setText(reservationId);
        dateTextView.setText(reservationInfo.getDate());
        clubNameTextView.setText(reservationInfo.getClubName());
        packageDetailTextView.setText(reservationInfo.getReservationDetails());
    }

    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
