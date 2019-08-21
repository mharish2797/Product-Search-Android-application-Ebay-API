package com.example.productsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Product extends Fragment {
    LinearLayout progressBar;
    JSONObject parsed_item;
    RequestQueue requestQueue;
    LinearLayout img_gallery,prod_highlight_head,prod_specs_head,swish,prod_norec;
    LayoutInflater layoutInflater;
    TableRow sub,pric,bran;
    boolean no_rec=false;
    TextView p_title,p_price_ship,prod_highlight_subtitle,prod_highlight_brand,prod_highlight_price,prod_specs;
    String spacer="\t\t\t\t\t";
    HorizontalScrollView hsv;
    String bullets="\u2022";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_product, container, false);
        parsed_item=new JSONObject();

        p_title=view.findViewById(R.id.p_title);
        p_title.setVisibility(View.GONE);

        hsv=view.findViewById(R.id.hsv);
        hsv.setVisibility(View.GONE);
        p_price_ship=view.findViewById(R.id.prod_price_ship);
        p_price_ship.setVisibility(View.GONE);
        swish=view.findViewById(R.id.prod_swish);
        swish.setVisibility(View.GONE);
        sub=view.findViewById(R.id.sub_high);
        pric=view.findViewById(R.id.price_high);
        bran=view.findViewById(R.id.brand_high);
        prod_norec=view.findViewById(R.id.prod_norec);
        prod_norec.setVisibility(View.GONE);
        prod_highlight_head=view.findViewById(R.id.prod_highlight_head);
        prod_highlight_head.setVisibility(View.GONE);
        prod_highlight_brand=view.findViewById(R.id.prod_highlight_brand);
        prod_highlight_price=view.findViewById(R.id.prod_highlight_price);
        prod_highlight_subtitle=view.findViewById(R.id.prod_highlight_subtitle);
        prod_highlight_subtitle.setText("");
        prod_highlight_price.setText("");
        prod_highlight_brand.setText("");

        prod_specs_head=view.findViewById(R.id.prod_specs_head);
        prod_specs_head.setVisibility(View.GONE);
        prod_specs=view.findViewById(R.id.prod_specs);
        prod_specs.setText("");

        img_gallery=view.findViewById(R.id.img_gallery);
        layoutInflater=LayoutInflater.from(getContext());
        requestQueue= Volley.newRequestQueue(getContext());
        String str=this.getArguments().getString("xax");
        try {
            JSONObject jsonObject=new JSONObject(str);
            Log.d("SingleItem",str);
            parse_from_bundle(jsonObject);
            parse_single_item(jsonObject.getString("id"));
//            parse_single_item("1122");
        } catch (JSONException e) { e.printStackTrace(); }
        progressBar=view.findViewById(R.id.prod_progressBar);
        return view;

    }


    public void populate(){
        progressBar.setVisibility(View.GONE);

        if(parsed_item.length()>0)
        {set_all_stuffs();
            swish.setVisibility(View.VISIBLE);
            prod_norec.setVisibility(View.GONE);
        }
        else{
            swish.setVisibility(View.GONE);
            prod_norec.setVisibility(View.VISIBLE);
        }

    }

    public void set_all_stuffs(){
        set_images();
        set_title();
        set_price_ship();
        set_highlight();
        set_specs();
    }
    public void set_images() {
        if(parsed_item.has("pictures")) {

            ArrayList<String> temp_pics = null;
            try {
                temp_pics = (ArrayList<String>) parsed_item.get("pictures");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < temp_pics.size(); i++) {
                hsv.setVisibility(View.VISIBLE);

                View view = layoutInflater.inflate(R.layout.prod_images,
                        img_gallery, false);
                ImageView img = (ImageView) view
                        .findViewById(R.id.single_prod_imv);
                Picasso.with(getContext()).load(temp_pics.get(i)).error(R.drawable.broken_image).into(img);
//            img.setImageResource(mImgIds[i]);
                img_gallery.addView(view);
            }
        }
    }

    public void set_title(){
        if(parsed_item.has("title")){
            try {
                String titls=parsed_item.getString("title");
                p_title.setText(titls);
                p_title.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else p_title.setVisibility(View.GONE);

    }
    public void set_price_ship(){
        String topaste="";
        if(parsed_item.has("price") || parsed_item.has("ship")){
            try {
                if(parsed_item.has("price"))
                topaste+="<b><font color='#6200EE'>"+parsed_item.getString("price")+"</font> </b>";
                if(parsed_item.has("ship"))
                    topaste+="With "+parsed_item.getString("ship");
                p_price_ship.setText(Html.fromHtml(topaste+"\n"));
                p_price_ship.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else p_price_ship.setVisibility(View.GONE);

    }
    public void set_highlight(){
        if(parsed_item.has("brand") || parsed_item.has("price") || parsed_item.has("subtitle")){
            try {
                if(parsed_item.has("subtitle") && !parsed_item.getString("subtitle").trim().equals(""))
                    prod_highlight_subtitle.setText(parsed_item.getString("subtitle")+"\n");
                else
                    sub.setVisibility(View.GONE);
                if(parsed_item.has("price"))
                    prod_highlight_price.setText(parsed_item.getString("price")+"\n");
                else
                    pric.setVisibility(View.GONE);
                if(parsed_item.has("Brand"))
                {
                    String BRAND=parsed_item.getString("Brand");
                    prod_highlight_brand.setText(BRAND.substring(0, 1).toUpperCase() + BRAND.substring(1));
                }
                else
                    bran.setVisibility(View.GONE);
                prod_highlight_head.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else prod_highlight_head.setVisibility(View.GONE);


    }
    public void set_specs(){
        String topaste="\n";
        if(parsed_item.has("brand") || parsed_item.has("itemspecs")){
            try {
                if(parsed_item.has("Brand")) {
                    String BRAND=parsed_item.getString("Brand");
                    topaste += "" + spacer + bullets + " " +BRAND.substring(0, 1).toUpperCase() + BRAND.substring(1)  + "\n";
                }
                if(parsed_item.has("itemspecs")) {
                    ArrayList<String> specs=(ArrayList<String>) parsed_item.get("itemspecs");
                    for (int i=0;i<specs.size();i++)
                        topaste += "" + spacer +bullets+ " " + specs.get(i).substring(0, 1).toUpperCase() + specs.get(i).substring(1)+ "\n";
                }
                prod_specs.setText(topaste+"\n");
                prod_specs_head.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else prod_specs_head.setVisibility(View.GONE);


    }


    public void parse_from_bundle(JSONObject object){
        String ship="";
        try {
            if(!object.getString("shipping").equals("N/A"))
                ship=object.getString("shipping");
            parsed_item.put("ship",ship);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void parse_single_item(String item){
        String url="http://prod-searcher.appspot.com/link_single_item?item_id="+item;
        Log.d("single_details",url);
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    Log.d("single",response.toString());
                try {
                    if(response.has("Ack") && response.getString("Ack").equals("Success")){
                        JSONObject item=response.getJSONObject("Item");
                        ArrayList<String> pics=new ArrayList<String>();
                        if(item.has("PictureURL") && item.getJSONArray("PictureURL").length()>0) {
                            JSONArray js = item.getJSONArray("PictureURL");
                            for (int j = 0; j < js.length(); j++)
                                pics.add(js.getString(j));
                        }
                            parsed_item.put("pictures",pics);

                        String title="";
                        if(item.has("Title"))
                            title=item.getString("Title");
                        parsed_item.put("title",title);

                        String subtitle="";
                        if(item.has("Subtitle"))
                            subtitle=item.getString("Subtitle");
                        parsed_item.put("subtitle",subtitle);

                        String price="";
                        if(item.has("CurrentPrice")&&item.getJSONObject("CurrentPrice").has("Value"))
                            price=item.getJSONObject("CurrentPrice").getString("Value");
                        parsed_item.put("price","$"+price);

//                         name="";
                        if(item.has("ItemSpecifics")&&item.getJSONObject("ItemSpecifics").has("NameValueList")){
                            JSONArray nvl=item.getJSONObject("ItemSpecifics").getJSONArray("NameValueList");
                            ArrayList<String> specs=new ArrayList<>();
                            for(int h=0;h<nvl.length();h++)
                            {
                                String name=nvl.getJSONObject(h).getString("Name");
                                String value=nvl.getJSONObject(h).getJSONArray("Value").getString(0);
                                if(name.equals("Brand") )
                                    parsed_item.put(name,value);
                                else{
                                    specs.add(value);
                                }
                            }
                            if (specs.size()>0)
                            parsed_item.put("itemspecs",specs);
                        }
                        Log.d("parsed_single",parsed_item.toString());
                    }

                    populate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { error.printStackTrace(); }
        });
        requestQueue.add(reqer);
    }


}
