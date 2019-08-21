package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerSimAdapter extends RecyclerView.Adapter<RecyclerSimAdapter.ViewHolder>{

    private Context cv;
    private ArrayList<JSONObject> data;

    public RecyclerSimAdapter(Context cv, ArrayList<JSONObject> data) {
        this.cv = cv;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater layoutInflater=LayoutInflater.from(cv);
        view=layoutInflater.inflate(R.layout.card_view_similar,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        try {
            final String view_url=data.get(i).getString("view");
            String title=data.get(i).getString("title").toUpperCase();
            viewHolder.a_title.setText(title);
           viewHolder.a_ship.setText(data.get(i).getString("ship"));
           if(data.get(i).getInt("time")<2)
            viewHolder.a_time.setText(Integer.toString(
                    data.get(i).getInt("time"))+" Day Left");
            else viewHolder.a_time.setText(Integer.toString(data.get(i).getInt("time"))+" Days Left");
            viewHolder.a_price.setText(data.get(i).getString("price"));

            String imgv=data.get(i).getString("pic");
            Picasso.with(cv).load(imgv).error(R.drawable.broken_image).into(viewHolder.a_image);

            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(view_url));
                    cv.startActivity(browserIntent);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView a_title,a_ship,a_price,a_time;
        ImageView a_image;
        CardView cardView;
        public ViewHolder(View itemV){
            super(itemV);
            cardView=(CardView) itemV.findViewById(R.id.sim_cardview);
            a_title=(TextView) itemV.findViewById(R.id.sim_prod_title);
            a_ship=(TextView) itemV.findViewById(R.id.sim_prod_ship);
            a_price=(TextView) itemV.findViewById(R.id.sim_prod_price);
            a_image=(ImageView) itemV.findViewById(R.id.sim_prod_img);
            a_time=(TextView) itemV.findViewById(R.id.sim_prod_time);
        }
    }
}
