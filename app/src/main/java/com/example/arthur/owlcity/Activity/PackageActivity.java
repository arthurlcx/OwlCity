package com.example.arthur.owlcity.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.arthur.owlcity.Adapter.PackageAdapter;
import com.example.arthur.owlcity.Class.ClubPackage;
import com.example.arthur.owlcity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PackageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<ClubPackage> myPackageList = new ArrayList<>();
    DatabaseReference firebaseReference;
    Intent intent;
    ClubPackage clubPackage;

    private String clubId;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        //receive data from previous activity
        intent = getIntent();
        clubId = intent.getStringExtra("Club ID");

        recyclerView = findViewById(R.id.rv_list);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        //set up Firebase database reference
        firebaseReference = FirebaseDatabase.getInstance().getReference("Package");
        //SELECT * FROM package WEHRE clubId = desired club id
        Query query = firebaseReference.orderByChild("clubID").equalTo(clubId);
        query.addListenerForSingleValueEvent(valueEventListener);

    }

    //retrieve data from database
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    clubPackage = snapshot.getValue(ClubPackage.class);
                    //for each record matched, add into array list
                    myPackageList.add(clubPackage);

                }

                //attach the array list into a custom array adapter
                PackageAdapter packageAdapter = new PackageAdapter(PackageActivity.this, myPackageList);
                //display array list
                recyclerView.setAdapter(packageAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(PackageActivity.this));

            }else{
                makeToast("No available package.");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void makeToast (String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
