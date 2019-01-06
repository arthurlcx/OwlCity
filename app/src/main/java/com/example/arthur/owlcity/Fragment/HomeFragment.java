package com.example.arthur.owlcity.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.arthur.owlcity.Activity.Register;
import com.example.arthur.owlcity.Activity.Reservation;
import com.example.arthur.owlcity.Activity.UserLocationMapsActivity;
import com.example.arthur.owlcity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment  {

    CardView cardClub, cardReservation, cardRide, cardRegister;
    Fragment fragment;
    Intent intent;

    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardClub = view.findViewById(R.id.cardClub);
        cardReservation = view.findViewById(R.id.cardReservation);
        cardRide = view.findViewById(R.id.cardRide);
        cardRegister = view.findViewById(R.id.cardRegister);

        cardClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new MakeReservationFragment();
                commitFragment(fragment);

            }
        });

        cardReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new ReservationFragment();
                commitFragment(fragment);

            }
        });

        cardRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), UserLocationMapsActivity.class);
                startActivity(intent);
            }
        });

        cardRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity(), Register.class);
                startActivity(intent);
            }
        });


    }

    public void commitFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
    }

    public void makeToast (String string){
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }



}
