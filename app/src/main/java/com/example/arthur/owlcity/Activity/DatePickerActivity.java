package com.example.arthur.owlcity.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.owlcity.Class.Club;
import com.example.arthur.owlcity.Class.ClubPackage;
import com.example.arthur.owlcity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button button;
    private String date, packageId;
    private TextView textView;

    private Intent intent;
    private ClubPackage clubPackage;
    private DatabaseReference firebaseReference;

    private String packName, packDesc, packSeat, packPrice, clubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        button = findViewById(R.id.btnNext);
        calendarView = findViewById(R.id.calendarView);
        textView = findViewById(R.id.textView3);

        intent = getIntent();
        packageId = intent.getStringExtra("Pack ID");

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("d/M/yyyy");
        date = df.format(c);

        textView.setText(date);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                date = Integer.toString(dayOfMonth) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
                textView.setText(date);

            }
        });

        firebaseReference = FirebaseDatabase.getInstance().getReference("Package");
        Query query = firebaseReference.orderByChild("packID").equalTo(packageId);

        query.addListenerForSingleValueEvent(valueEventListener);

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                clubPackage = snapshot.getValue(ClubPackage.class);

            }

            assignValue(clubPackage);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void assignValue(ClubPackage clubPackage){
        packName = clubPackage.getPackName();
        packDesc = clubPackage.getPackDesc();
        packSeat = clubPackage.getPackSeat();
        packPrice = clubPackage.getPackPrice();
        clubName = clubPackage.getClubName();

    }

    public void goToPayment(View view){
        intent = new Intent(getApplicationContext(), Payment.class);
        intent.putExtra("packageName", packName);
        intent.putExtra("packageDesc", packDesc);
        intent.putExtra("packageSeat", packSeat);
        intent.putExtra("packagePrice", packPrice);
        intent.putExtra("clubName", clubName);
        intent.putExtra("date", date);

        startActivity(intent);

    }


    public void makeToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
