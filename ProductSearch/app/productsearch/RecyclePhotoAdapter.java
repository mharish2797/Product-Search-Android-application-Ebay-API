package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class RecyclePhotoAdapter extends RecyclerView.Adapter<RecyclePhotoAdapter.ViewHolder>{

    private Context cv;
    private ArrayList<String> data;

    public RecyclePhotoAdapter(Context cv, ArrayList<String> data) {
        this.cv = cv;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater layoutInflater=LayoutInflater.from(cv);
        view=layoutInflater.inflate(R.layout.card_view_photos,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {

            String imgv=data.get(i);
            Picasso.with(cv).load(imgv).error(R.drawable.broken_image).into(viewHolder.a_image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView a_image;
        CardView cardView;
        public ViewHolder(View itemV){
            super(itemV);
            cardView=(CardView) itemV.findViewById(R.id.photo_card_view);

            a_image=(ImageView) itemV.findViewById(R.id.p_photo_img);
        }
    }
}
