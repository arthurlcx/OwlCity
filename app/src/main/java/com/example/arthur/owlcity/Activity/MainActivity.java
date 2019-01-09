package com.example.arthur.owlcity.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arthur.owlcity.Class.GlideApp;
import com.example.arthur.owlcity.Class.User;
import com.example.arthur.owlcity.Fragment.HomeFragment;
import com.example.arthur.owlcity.Fragment.MakeReservationFragment;
import com.example.arthur.owlcity.Fragment.ReservationFragment;
import com.example.arthur.owlcity.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int RC_SIGN_IN = 123;

    private Intent intent;

    private User user;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private TextView navName, navEmail;
    private ImageView navImg;
    private Menu nav_menu;

    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        commitFragment(new HomeFragment());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                updateNavHeader();
            }
        };

        navigationView = findViewById(R.id.nav_view);
        nav_menu = navigationView.getMenu();
        View headerView = navigationView.getHeaderView(0);

        navName = headerView.findViewById(R.id.navName);
        navEmail = headerView.findViewById(R.id.navEmail);
        navImg = headerView.findViewById(R.id.navImg);

        updateNavHeader();



    }

    public void updateNavHeader(){
        if (firebaseUser != null) {

            if (firebaseUser.isAnonymous()) {
                navName.setText("Guest");
                navEmail.setText("");
                navImg.setImageResource(R.drawable.user);

                nav_menu.findItem(R.id.nav_register).setVisible(false);

            } else {
                navName.setText(firebaseUser.getDisplayName());
                navEmail.setText(firebaseUser.getEmail());

                retrieveProfilePic();

            }
        }
    }

    public void retrieveProfilePic(){
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference("user");
        Query query = firebaseReference.orderByChild("userId").equalTo(firebaseUser.getUid());

        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    user = snapshot.getValue(User.class);
                }

                StorageReference storageReference = FirebaseStorage.getInstance().getReference(user.getProfileImg());

                GlideApp.with(MainActivity.this).load(storageReference).apply(RequestOptions.circleCropTransform()).into(navImg);

            } else {
                makeToast("No record found");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exiting OwlCity")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }

                    }) 
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_home) {

            fragment = new HomeFragment();

        } else if (id == R.id.nav_makeReservation) {

            fragment = new MakeReservationFragment();

        } else if (id == R.id.nav_myLocation) {

            intent = new Intent(getApplicationContext(), UserLocationMapsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_myReservation) {
            fragment = new ReservationFragment();

        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            makeToast("Logged Out");
                        }
                    });

            intent = new Intent(getApplicationContext(), SignIn.class);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_register) {
            intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {

            intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);

        }


        if (fragment != null){
            commitFragment(fragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void commitFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
    }

    public void makeToast(String string){
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }


}
