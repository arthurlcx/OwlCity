package com.example.arthur.owlcity.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;

import com.example.arthur.owlcity.Adapter.ClubAdapter;
import com.example.arthur.owlcity.Class.Club;
import com.example.arthur.owlcity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClubActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Club> myClubList = new ArrayList<>();
    DatabaseReference firebaseReference;
    Intent intent;
    Club club;

    private String cityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        //receive city code value from previous activity
        intent = getIntent();
        cityCode = intent.getStringExtra("City Code");

        recyclerView = findViewById(R.id.rv_list);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //establish Firebase database reference
        firebaseReference = FirebaseDatabase.getInstance().getReference("club");
        //query - (SELECT * FROM club WHERE cityCode = desired city
        Query query = firebaseReference.orderByChild("cityCode").equalTo(cityCode);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                Club club = snapshot.getValue(Club.class);

                //for each matched records, add into our array list
                myClubList.add(club);
            }

            //adapt the array list into our custom array adapter
            ClubAdapter cityAdapter = new ClubAdapter(ClubActivity.this, myClubList);
            recyclerView.setAdapter(cityAdapter);
            //display array list
            recyclerView.setLayoutManager(new LinearLayoutManager(ClubActivity.this));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}
