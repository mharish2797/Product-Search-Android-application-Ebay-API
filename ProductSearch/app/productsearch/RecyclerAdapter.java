package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Context cv;
    private List<JSONObject> data;

    public RecyclerAdapter(Context cv, List<JSONObject> data) {
        this.cv = cv;
        this.data = data;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater layoutInflater=LayoutInflater.from(cv);
        view=layoutInflater.inflate(R.layout.card_view_item_product,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        sharedPreferences = cv.getSharedPreferences("harishmo", Context.MODE_PRIVATE); // 0 - for private mode
        editor = sharedPreferences.edit();

        String title="";
        final Intent intent=new Intent(cv,SingleItemDetails.class);

        try {
             title=data.get(i).getString("title");
            viewHolder.a_title.setText(title.toUpperCase());
            viewHolder.a_zip.setText("Zip: "+data.get(i).getString("zip")+"\n"+data.get(i).getString("shipping"));
            viewHolder.a_condition.setText(data.get(i).getString("condition").trim());
            viewHolder.a_price.setText(data.get(i).getString("price"));

            String imgv=data.get(i).getString("image");
            Picasso.with(cv).load(imgv).error(R.drawable.broken_image).into(viewHolder.a_image);
            intent.putExtra("item_object",data.get(i).toString());


            if(sharedPreferences.contains(data.get(i).getString("id")))
                viewHolder.a_cart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cart_remove, 0);

            else
                viewHolder.a_cart.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.cart_plus,0);


            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkService ns=new NetworkService();
                    if(!ns.isNetworkAvailable(cv)) {
                        Toast.makeText(cv,"No Network Connection",Toast.LENGTH_LONG).show();
                    }
                    else {
                        int start=0,end=0;
                        Layout l=viewHolder.a_title.getLayout();
                        String trim= "";
                        try {
                            trim = data.get(i).getString("title");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(l!=null)
                        {
                            end=l.getEllipsisStart(2);
                            if(end!=0)
                            {start=l.getLineStart(2);
                            trim=trim.substring(0,start+end)+"...";}
                        }
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("trimmed",trim);
                        cv.startActivity(intent);
                    }
                }
            });

            viewHolder.a_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String trim=data.get(i).getString("title");
                        String itemid=data.get(i).getString("id");
                        Layout l=viewHolder.a_title.getLayout();
                        int start=0,end=0;
                        if(l!=null)
                        {
                            end=l.getEllipsisStart(2);
                            if(end!=0)
                            {start=l.getLineStart(2);
                                trim=trim.substring(0,start+end)+"...";

                            }
                        }

                        if(!sharedPreferences.contains(itemid)) {
                            editor.putString(itemid, data.get(i).toString());
                            editor.commit();
                            Toast.makeText(cv,trim+" was added to wish list",Toast.LENGTH_LONG).show();
                            viewHolder.a_cart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cart_remove, 0);
                        }
                        else{
                            editor.remove(itemid);
                            editor.commit();
                            Toast.makeText(cv,trim+" was removed from wish list",Toast.LENGTH_LONG).show();
                            viewHolder.a_cart.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.cart_plus,0);}


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//

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
        TextView a_title,a_zip,a_cart,a_condition,a_price;
        ImageView a_image;
        CardView cardView;
        public ViewHolder(View itemV){
            super(itemV);
            cardView=(CardView) itemV.findViewById(R.id.a_cardview);
            a_title=(TextView) itemV.findViewById(R.id.a_prod_title);
            a_zip=(TextView) itemV.findViewById(R.id.a_zip);
            a_cart=(TextView)itemV.findViewById(R.id.a_cart);
            a_condition=(TextView) itemV.findViewById(R.id.a_condition);
            a_price=(TextView) itemV.findViewById(R.id.a_price);
            a_image=(ImageView) itemV.findViewById(R.id.a_prod_img);
        }
    }
}
