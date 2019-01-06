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

import com.example.arthur.owlcity.Activity.ClubInfo;
import com.example.arthur.owlcity.Class.item;
import com.example.arthur.owlcity.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.myViewHolder> {

    Context mContext;
    List<item> mData;

    private DatabaseReference firebaseReference;


    public Adapter(Context mContext, List<item> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.card_item,parent,false);

        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, final int position) {

        holder.background_img.setImageResource(mData.get(position).getBackground());
        holder.profile_photo.setImageResource(mData.get(position).getProfilePhoto());
        holder.tv_title.setText(mData.get(position).getProfileName());

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseReference = FirebaseDatabase.getInstance().getReference("club");
                Query query = firebaseReference.orderByChild("");


                Intent intent = new Intent(mContext, ClubInfo.class);
                intent.putExtra("Club Name", mData.get(position).getProfileName());
//                intent.putExtra("Club ID", );

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


}
