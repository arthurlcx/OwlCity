package com.example.arthur.owlcity.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.owlcity.Class.ReservationInfo;
import com.example.arthur.owlcity.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.util.List;

import static java.security.AccessController.getContext;

public class adminActivity extends AppCompatActivity {

    private TextView resultTextView, logoutTextView;
    private Button btnScan;

    private ReservationInfo reservationInfo;

    private final int BARCODE_RECO_REQ_CODE = 200;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;

    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (ContextCompat.checkSelfPermission(adminActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    adminActivity.this, Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions(adminActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }

        }



        resultTextView = findViewById(R.id.resultTextView);
        btnScan = findViewById(R.id.btnScan);
        logoutTextView = findViewById(R.id.logoutTextView);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    makeToast("clicked");

                    Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, BARCODE_RECO_REQ_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    makeToast("error click");
                }
            }
        });

        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(adminActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                makeToast("Successfully logged out");
                            }
                        });

                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
                finish();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BARCODE_RECO_REQ_CODE){
            if(resultCode == adminActivity.RESULT_OK){
                Bitmap photo = (Bitmap)data.getExtras().get("data");
                barcodeRecognition(photo);
            } else {
                makeToast("Error result code ");
            }
        } else {
            makeToast("error request code 1");
        }
    }

    private void barcodeRecognition(Bitmap photo) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);

        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();

        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);



        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        // Task completed successfully
                        // ...

                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();

                            retrieveReservationInfo(rawValue);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...

                        makeToast("Error reading QR Code");
                    }
                });


    }

    private void retrieveReservationInfo(String rawValue) {
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("reservationInfo");
        Query query = firebaseReference.orderByChild("reservationID").equalTo(rawValue);

        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    reservationInfo = snapshot.getValue(ReservationInfo.class);
                    snapshot.getRef().removeValue();
                }

                updateUI(reservationInfo);
            } else {
                makeToast("No data found");
                resultTextView.setText("No Result. Invalid E-Pass.");
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void updateUI(ReservationInfo reservationInfo) {
        if (reservationInfo != null){
            result = reservationInfo.getDate() + "\n" +
                                    reservationInfo.getReservationID() + "\n" +
                                    reservationInfo.getClubName() + "\n" +
                                    reservationInfo.getReservationDetails() + "\n" +
                                    reservationInfo.getReservationOwner();

            resultTextView.setText(result);

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Reservation Details");
            alertDialog.setMessage(result);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        }
    }


    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

}
