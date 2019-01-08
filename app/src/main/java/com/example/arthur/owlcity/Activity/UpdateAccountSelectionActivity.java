package com.example.arthur.owlcity.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.example.arthur.owlcity.R;

public class UpdateAccountSelectionActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_selection);

    }

    //clicked to pass name to be updates
    public void changeName (View view){
        intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
        intent.putExtra("Selection", "name");
        startActivity(intent);
    }

    //clicked to pass email to be updates

    public void changeEmail (View view){
        intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
        intent.putExtra("Selection", "email");
        startActivity(intent);
    }

    //clicked to pass password to be updates

    public void changePassword (View view){
        intent = new Intent(getApplicationContext(), UpdateAccountActivity.class);
        intent.putExtra("Selection", "password");
        startActivity(intent);
    }

    //if back is pressed, it will recreate profile activity to update the information changed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
    }
}
