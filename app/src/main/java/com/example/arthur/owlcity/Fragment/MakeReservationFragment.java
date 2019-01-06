package com.example.arthur.owlcity.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.arthur.owlcity.Adapter.CityAdapter;
import com.example.arthur.owlcity.Class.City;
import com.example.arthur.owlcity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MakeReservationFragment extends Fragment {

    RecyclerView recyclerView;
    List<City> myReservationList = new ArrayList<>();
    DatabaseReference firebaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    City city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_make_reservation, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_list);

        Window w = getActivity().getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        firebaseReference = FirebaseDatabase.getInstance().getReference("city");

        firebaseReference.addListenerForSingleValueEvent(valueEventListener);

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    city = snapshot.getValue(City.class);

                    myReservationList.add(city);
                }

                CityAdapter myReservationAdapter = new CityAdapter(getActivity(), myReservationList);
                recyclerView.setAdapter(myReservationAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            } else {
                makeToast("No Data");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void makeToast(String string){
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();

    }
}
