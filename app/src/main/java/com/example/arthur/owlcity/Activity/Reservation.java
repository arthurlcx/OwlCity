package com.example.arthur.owlcity.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.arthur.owlcity.Activity.ClubInfo;
import com.example.arthur.owlcity.Class.Club;
import com.example.arthur.owlcity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Reservation extends AppCompatActivity {
    private TextView textView;
    private String location;
    private ListView listView;
    private ProgressBar progressBar;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private Club club;

    final ArrayList<String> clubs = new ArrayList<String>();
    String[] clubId = new String[10];
    int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Intent intent = getIntent();

        textView = findViewById(R.id.textView2);

        location = intent.getStringExtra("location");

        textView.setText(location);

        listView = findViewById(R.id.clubListView);

        progressBar = findViewById(R.id.progressBar3);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("club");

        Query query = mRef.orderByChild("location").equalTo(location);

        query.addValueEventListener(valueEventListener);
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

    private void showData(DataSnapshot dataSnapshot) {

        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

            club = snapshot.getValue(Club.class);

            clubs.add(club.getName());

            clubId[i] = club.getClubID();
            i++;
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(Reservation.this, android.R.layout.simple_list_item_1, clubs);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), ClubInfo.class);
                intent.putExtra("Club Name", clubs.get(position));
                intent.putExtra("Club ID", clubId[position]);

                startActivity(intent);
                onStop();

            }
        });



        }


    }







