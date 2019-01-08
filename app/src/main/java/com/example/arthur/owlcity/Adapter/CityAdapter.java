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

import com.example.arthur.owlcity.Activity.ClubActivity;
import com.example.arthur.owlcity.Class.City;
import com.example.arthur.owlcity.Class.GlideApp;
import com.example.arthur.owlcity.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

//custom adapter for recycler view
public class CityAdapter extends RecyclerView.Adapter<CityAdapter.myViewHolder> {

    Context mContext;
    List<City> mData;


    public CityAdapter(Context mContext, List<City> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate card view layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.card_item,parent,false);

        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {

        //calling Glide API to display images
        try {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(mData.get(position).getImageFileName());
            GlideApp.with(mContext).load(storageReference).into(holder.background_img);
        } catch (Exception e) {
            e.printStackTrace();
            makeToast("Error displaying image");

        }

        holder.profile_photo.setVisibility(View.GONE);

        //update UI text
        holder.tv_title.setText(mData.get(position).getCityName());

        //define onClick for the button "next"
        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ClubActivity.class);
                intent.putExtra("City Code", mData.get(position).getCityCode());

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
