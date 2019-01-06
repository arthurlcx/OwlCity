package com.example.arthur.owlcity.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.arthur.owlcity.Activity.MainActivity;
import com.example.arthur.owlcity.Adapter.MyReservationAdapter;
import com.example.arthur.owlcity.Class.ReservationInfo;
import com.example.arthur.owlcity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReservationFragment extends Fragment {

    RecyclerView recyclerView;
    List<ReservationInfo> myReservationList = new ArrayList<>();
    DatabaseReference firebaseReference;
    FirebaseUser firebaseUser;

    ReservationInfo reservationInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reservation, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_list);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Window w = getActivity().getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        MyReservationAdapter myReservationAdapter = new MyReservationAdapter(getActivity(), myReservationList);
        recyclerView.setAdapter(myReservationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (firebaseUser != null) {

            firebaseReference = FirebaseDatabase.getInstance().getReference("reservationInfo");
            Query query = firebaseReference.orderByChild("reservationOwnerId").equalTo(firebaseUser.getUid());

            query.addListenerForSingleValueEvent(valueEventListener);

        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error. You are not logged in");
            alertDialog.setMessage("Please log in to view your reservation");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Back to home",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }

    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            try {
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        reservationInfo = snapshot.getValue(ReservationInfo.class);

                        myReservationList.add(reservationInfo);
                    }

                    MyReservationAdapter myReservationAdapter = new MyReservationAdapter(getActivity(), myReservationList);
                    recyclerView.setAdapter(myReservationAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


                } else {
                    makeToast("No Data");
                }
            } catch (Exception e) {
                e.printStackTrace();

                makeToast("Error retrieving reservation from reservation list");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void makeToast (String string){
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }


}
