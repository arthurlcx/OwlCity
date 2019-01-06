package com.example.arthur.owlcity.Activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.arthur.owlcity.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ClubMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "ClubMapActivity";

    private GoogleMap mMap;

    private double latitude;
    private double longitude;
    private String clubName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent mapIntent = getIntent();

        latitude = mapIntent.getDoubleExtra("latitude", 0);
        longitude = mapIntent.getDoubleExtra("longitude", 0);
        clubName = mapIntent.getStringExtra("clubName");

        Log.i(TAG, "onCreate: " + latitude + " "  + longitude);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng club = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(club).title(clubName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(club, 17));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
}
