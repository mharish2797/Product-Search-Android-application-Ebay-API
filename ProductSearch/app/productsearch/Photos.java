package com.example.productsearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Photos extends Fragment {

    LinearLayout progressBar;
    JSONObject parsed_item;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    ArrayList<String> dummy;
    boolean no_rec=false;
    LinearLayout photo_norec;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_photos, container, false);
        parsed_item=new JSONObject();
        dummy=new ArrayList<>();
        photo_norec=view.findViewById(R.id.photo_norec);
        recyclerView=(RecyclerView) view.findViewById(R.id.photo_recyclerview);
        RecyclePhotoAdapter recyclerAdapter=new RecyclePhotoAdapter(getContext(),dummy);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setVisibility(View.GONE);
        requestQueue= Volley.newRequestQueue(getContext());
        String str=this.getArguments().getString("xax");
        try {
            JSONObject jsonObject=new JSONObject(str);
            parse_photos(jsonObject.getString("title"));
//            parse_photos("blablablablalbablabla");
        } catch (JSONException e) { e.printStackTrace(); }
        progressBar=view.findViewById(R.id.photo_progressBar);

        return view;}


        public void populate(){
            progressBar.setVisibility(View.GONE);
            if(parsed_item.length()>0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                photo_norec.setVisibility(View.GONE);
                set_photos();

            }
            else{
                recyclerView.setVisibility(View.GONE);
                photo_norec.setVisibility(View.VISIBLE);
            }

        }

    public void set_photos(){
        ArrayList<String> pics=null;
        try {
            pics=(ArrayList<String>) parsed_item.get("pictures");
            RecyclePhotoAdapter radapter=new RecyclePhotoAdapter(getContext(),pics);
            recyclerView.swapAdapter(radapter,false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void parse_photos(String item){
        String url="http://prod-searcher.appspot.com/link_photos?item_id="+item;
        Log.d("photo_url",url);
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("photos",response.toString());
                try {
                    if(response.has("items") && response.getJSONArray("items").length()>0){
                        JSONArray items_array=response.getJSONArray("items");
                        ArrayList<String> pics=new ArrayList<String>();
                        for(int i=0;i<items_array.length();i++){
                            JSONObject item=items_array.getJSONObject(i);
                            if(item.has("link"))
                                pics.add(item.getString("link"));
                            Log.d("PICS",item.getString("link"));
                        }
                            parsed_item.put("pictures",pics);
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
