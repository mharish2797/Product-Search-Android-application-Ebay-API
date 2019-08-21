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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Similar extends Fragment  {
    LinearLayout progressBar;
    ArrayList<JSONObject> parsed_items;
    ArrayList<JSONObject> true_copy;
    RequestQueue requestQueue;
    RecyclerView recyclerView;
    ArrayList<JSONObject> dummy;
    boolean no_rec=false;
    Spinner category,order;
    String actual_key;
    Integer actual_order;
    LinearLayout sim_display,spinners,sim_norec;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Toast.makeText(getContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();

        View view=inflater.inflate(R.layout.fragment_similar, container, false);
        sim_display=view.findViewById(R.id.sim_display);
        sim_norec=view.findViewById(R.id.sim_norec);
        spinners=view.findViewById(R.id.simi_spinners);
        spinners.setVisibility(View.GONE);
        sim_norec.setVisibility(View.GONE);
        sim_display.setVisibility(View.GONE);

        category=view.findViewById(R.id.categ_spinner);
        final List<String> categ_list=new ArrayList<>();
        categ_list.add("Default");
        categ_list.add("Name");
        categ_list.add("Price");
        categ_list.add("Day");
        ArrayAdapter<String> categ_adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,categ_list);
        categ_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categ_adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),categ_list.get(position),Toast.LENGTH_LONG).show();
                actual_key=categ_list.get(position);
                order.setEnabled(true);
                switch (position){
                    case 0:
                        parsed_items= (ArrayList<JSONObject>) true_copy.clone();
                        order.setEnabled(false);
                        break;
                    case 1:
                        actual_key="title";
                        sort_list_json_objects(actual_key,actual_order);
                        break;
                    case 2:
                        actual_key="price";
                        sort_list_json_objects(actual_key,actual_order);
                        break;
                    case 3:
                        actual_key="time";
                        sort_list_json_objects(actual_key,actual_order);
                        break;
                }
                RecyclerSimAdapter radapter=new RecyclerSimAdapter(getContext(),parsed_items);
                recyclerView.swapAdapter(radapter,false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        order=view.findViewById(R.id.order_spinner);
        final List<String> order_list=new ArrayList<>();
        order_list.add("Ascending");
        order_list.add("Descending");
        ArrayAdapter<String> order_adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,order_list);
        order_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(order_adapter);
        order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getContext(),order_list.get(position),Toast.LENGTH_LONG).show();
            actual_order=position;
            sort_list_json_objects(actual_key,actual_order);
            RecyclerSimAdapter radapter=new RecyclerSimAdapter(getContext(),parsed_items);
            recyclerView.swapAdapter(radapter,false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });

        parsed_items=new ArrayList<JSONObject>();
        true_copy=new ArrayList<JSONObject>();
        dummy=new ArrayList<JSONObject>();
        recyclerView=(RecyclerView) view.findViewById(R.id.sim_recyclerview);
        RecyclerSimAdapter recyclerAdapter=new RecyclerSimAdapter(getContext(),dummy);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(recyclerAdapter);
        requestQueue= Volley.newRequestQueue(getContext());
        String str=this.getArguments().getString("xax");
        try {
            JSONObject jsonObject=new JSONObject(str);
            parse_similar(jsonObject.getString("id"));
//            parse_similar("12");
        } catch (JSONException e) { e.printStackTrace(); }

        progressBar=view.findViewById(R.id.similar_progressBar);

        return view;
    }

    public void populate(){
        progressBar.setVisibility(View.GONE);
        spinners.setVisibility(View.VISIBLE);
        if(parsed_items.size()>0)
        {sim_display.setVisibility(View.VISIBLE);
        set_similar();
        sim_norec.setVisibility(View.GONE);
        category.setEnabled(true);
        }
        else{
            sim_norec.setVisibility(View.VISIBLE);
            sim_display.setVisibility(View.GONE);
            category.setEnabled(false);
            order.setEnabled(false);
            }

    }


    public void sort_list_json_objects(final String key,final int ord){
        Collections.sort( parsed_items, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
//            private static final String KEY_NAME = key;

            @Override
            public int compare(JSONObject a, JSONObject b) {

                if(key.equals("time")){
                    int valA=0;
                    int valB=0;
                    try {
                        valA=a.getInt(key);
                        valB=b.getInt(key);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(ord<1)
                        return valA-valB;
                    else
                        return valB-valA;
                }
                else{
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get(key);
                        valB = (String) b.get(key);
                    }
                    catch (JSONException e) {
                        //do something
                    }
                    if(ord<1)
                        return valA.compareTo(valB);
                    else
                        return -1*(valA.compareTo(valB));

                }
            }
        });
    }
    public void set_similar(){

        if(parsed_items.size()>0) {
            category.setEnabled(true);
            order.setEnabled(true);
            true_copy = (ArrayList<JSONObject>) parsed_items.clone();

            RecyclerSimAdapter radapter = new RecyclerSimAdapter(getContext(), parsed_items);
            recyclerView.swapAdapter(radapter, false);
        }
        else{
            category.setEnabled(false);
            order.setEnabled(false);
        }
    }


    public void parse_similar(String item){
        String url="http://prod-searcher.appspot.com/link_similar_item?item_id="+item;
        Log.d("similar_url",url);
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d("similars",response.toString());
                try {
                    if(response.has("getSimilarItemsResponse")
                            && response.getJSONObject("getSimilarItemsResponse").has("itemRecommendations")
                            && response.getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").has("item")
                            && response.getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item").length()>0){
                        JSONArray items_array=response.getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item");
                        for(int i=0;i<items_array.length();i++){
                            JSONObject parser=items_array.getJSONObject(i);
                            JSONObject temp_obj=new JSONObject();
                            String temp="N/A";
                            if (parser.has("imageURL"))
                                temp=parser.getString("imageURL");
                            temp_obj.put("pic",temp);


                            temp="N/A";
                            if (parser.has("title"))
                                temp=parser.getString("title");
                            temp_obj.put("title",temp);

                            temp="N/A";
                            if (parser.has("viewItemURL"))
                                temp=parser.getString("viewItemURL");
                            temp_obj.put("view",temp);

                            temp="N/A";
                            if (parser.has("timeLeft"))
                            { String dummer=parser.getString("timeLeft");
                                temp=dummer.substring(dummer.indexOf('P')+1,dummer.indexOf('D'));}
                            temp_obj.put("time",Integer.parseInt(temp));

                            temp="N/A";
                            if (parser.has("buyItNowPrice") && parser.getJSONObject("buyItNowPrice").has("__value__"))
                                temp=parser.getJSONObject("buyItNowPrice").getString("__value__");
                            temp_obj.put("price","$"+temp);

                            temp="N/A";
                            if (parser.has("shippingCost") && parser.getJSONObject("shippingCost").has("__value__"))
                            {temp=parser.getJSONObject("shippingCost").getString("__value__");
                            if(Float.parseFloat(temp)==0) temp="Free Shipping";
                            else temp="$"+temp;
                            }

                            temp_obj.put("ship",temp);

                            parsed_items.add(temp_obj);
                        }
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
