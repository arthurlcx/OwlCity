package com.example.arthur.owlcity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arthur.owlcity.Activity.DatePickerActivity;
import com.example.arthur.owlcity.Activity.ReservationDetail;
import com.example.arthur.owlcity.Class.ClubPackage;
import com.example.arthur.owlcity.Class.GlideApp;
import com.example.arthur.owlcity.Class.ReservationInfo;
import com.example.arthur.owlcity.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MyReservationAdapter extends RecyclerView.Adapter<MyReservationAdapter.myViewHolder> {

    Context mContext;
    List<ReservationInfo> mData;


    public MyReservationAdapter(Context mContext, List<ReservationInfo> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.card_item_my_reservation,parent,false);

        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {


        holder.tv_title.setText(mData.get(position).getClubName() + "\n" + mData.get(position).getDate());

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ReservationDetail.class);
                intent.putExtra("Reservation ID", mData.get(position).getReservationID());
                mContext.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_photo,background_img;
        TextView tv_title;
        Button btn_more;


        public myViewHolder(View itemView){
            super(itemView);
            profile_photo = itemView.findViewById(R.id.profile_img);
            background_img=itemView.findViewById(R.id.card_background);
            tv_title = itemView.findViewById(R.id.card_title);
            btn_more = itemView.findViewById(R.id.btn_More);
        }
    }

    public void makeToast(String string){
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }


}
