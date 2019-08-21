package com.example.productsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class wish extends Fragment {

    RequestQueue requestQueue;
    boolean no_rec=false;
    List<JSONObject> parsed_items;
    RecyclerView recyclerView;
    LinearLayout w_display,norec;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    TextView t_item,t_price;
    Double total_cost=0.0;
    DecimalFormat df = new DecimalFormat("####0.00");

    String wish_bar_start="Wishlist total(", wish_bar_end=" items):";

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_wish, container, false);

        t_item=view.findViewById(R.id.total_items);
        t_price=view.findViewById(R.id.total_price);
        w_display=(LinearLayout)view.findViewById(R.id.wish_display);
        norec=(LinearLayout)view.findViewById(R.id.wish_norec);
        norec.setVisibility(View.VISIBLE);
        w_display.setVisibility(View.GONE);
        parsed_items=new ArrayList<>();
        get_local_storage();
        recyclerView=(RecyclerView) view.findViewById(R.id.w_recyclerview);
        RecyclerAdapter recyclerAdapter=new RecyclerAdapter(getContext(),parsed_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(recyclerAdapter);
        if(parsed_items.size()>0)
            populate_view();
        else
            display_void();
        listener=new SharedPreferences.OnSharedPreferenceChangeListener(){
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                get_local_storage();
                if (parsed_items.size()>0)
                        populate_view();
                else
                    display_void();
            }
        };

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        return view;
    }

    public void get_local_storage(){
        sharedPreferences = getContext().getSharedPreferences("harishmo", Context.MODE_PRIVATE); // 0 - for private mode
        parsed_items=new ArrayList<>();
        Map<String, ?> allEntries = sharedPreferences.getAll();
        total_cost=0.0;
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            try {
                JSONObject jsp = new JSONObject(entry.getValue().toString());
                String pprice="";
                pprice=jsp.getString("price");
                if(!pprice.equals(""))
                total_cost+=Double.parseDouble(pprice.substring(pprice.indexOf("$")+1));
                parsed_items.add(jsp);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void display_void(){
        t_item.setText(wish_bar_start+"0"+wish_bar_end);
        t_price.setText("$0.00");
        w_display.setVisibility(View.GONE);
        norec.setVisibility(View.VISIBLE);
    }

    public void populate_view(){
        t_item.setText(wish_bar_start+Integer.toString(parsed_items.size())+wish_bar_end);
        t_price.setText("$"+df.format(total_cost));
        w_display.setVisibility(View.VISIBLE);
        norec.setVisibility(View.GONE);
        RecyclerAdapter radapter=new RecyclerAdapter(getContext(),parsed_items);
        recyclerView.swapAdapter(radapter,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        get_local_storage();
        if (parsed_items.size()>0)
        populate_view();
        else
            display_void();

    }
}
