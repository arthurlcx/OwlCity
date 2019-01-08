package com.example.arthur.owlcity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arthur.owlcity.Class.Club;
import com.example.arthur.owlcity.Class.GlideApp;
import com.example.arthur.owlcity.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ClubInfo extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseReference;

    private TextView description;
    private TextView operatingHour;
    private TextView contact;
    private TextView address;
    private ImageView imageView;
    private double latitude;
    private double longitude;

    private String clubId;
    private String clubName;

    private Club club;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        description = findViewById(R.id.description);
        operatingHour = findViewById(R.id.operatingHour);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        imageView = findViewById(R.id.imageView);

        //floating button clicked, brings to view package
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PackageActivity.class);
                intent.putExtra("Club ID", clubId);
                startActivity(intent);
            }
        });

        //floating button clicked, brings user to map showing where the club is
        FloatingActionButton mapfab = (FloatingActionButton) findViewById(R.id.mapfab);
        mapfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClubMapActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("clubName", clubName);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        //receive club name from previous activity
        clubName = intent.getStringExtra("Club Name");

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(clubName);

        clubId = intent.getStringExtra("Club ID");

        //establish Firebase database connecting
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference("club");

        //query - (SELECT * FROM club WHERE clubID = desired culb id
        Query query = firebaseReference.orderByChild("clubID").equalTo(clubId);

        query.addListenerForSingleValueEvent(valueEventListener);

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

    //retrieve data from database and store into an Club object
    public void showData(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
            club = snapshot.getValue(Club.class);
        }

        displayInfo(club);
    }

    //display and update the UI with retrieved information
    public void displayInfo (Club club){
        description.setText(club.getDesc());
        operatingHour.setText(club.getOpHour());
        contact.setText(club.getContact());
        address.setText(club.getClubAdd());

        latitude = club.getLat();
        longitude = club.getLongitude();

        storageReference = FirebaseStorage.getInstance().getReference(club.getClubImg());

        //called Glide API to display images
        GlideApp.with(this).load(storageReference).into(imageView);

    }
}
