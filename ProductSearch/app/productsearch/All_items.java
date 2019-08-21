package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Iterator;
import java.util.List;

public class All_items extends AppCompatActivity {
    RequestQueue requestQueue;
    List<JSONObject> parsed_items;
    RecyclerView recyclerView;
    LinearLayout display, all_progress,all_no_rec;
    TextView showing;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        }
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.arrow_left);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        display=(LinearLayout)findViewById(R.id.all_display);
        display.setVisibility(View.GONE);
        all_no_rec=(LinearLayout)findViewById(R.id.all_norec);
        all_no_rec.setVisibility(View.GONE);
        all_progress=(LinearLayout)findViewById(R.id.all_progress);
        showing=(TextView)findViewById(R.id.showing_content);
        parsed_items=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(this);
         bundle= getIntent().getExtras();
        String api_call=bundle.getString("api_call");
        parse_all_items(api_call);
       // Toast.makeText(this,api_call,Toast.LENGTH_SHORT).show();
        recyclerView=(RecyclerView) findViewById(R.id.a_recyclerview);
        RecyclerAdapter recyclerAdapter=new RecyclerAdapter(getApplicationContext(),parsed_items);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recyclerView.setAdapter(recyclerAdapter);


    }
public void populate_view(){
    all_progress.setVisibility(View.GONE);
    if(parsed_items.size()>0){
        all_no_rec.setVisibility(View.GONE);
        display.setVisibility(View.VISIBLE);
        String paster="Showing <font color='#F4511E'>"+Integer.toString(parsed_items.size())+"</font> results for <font color='#F4511E'>"+bundle.getString("just_keyword")+"</font>";
        showing.setText(Html.fromHtml(paster));
        RecyclerAdapter radapter=new RecyclerAdapter(getApplicationContext(),parsed_items);
        recyclerView.swapAdapter(radapter,false);
    }
    else{
        display.setVisibility(View.GONE);
        all_no_rec.setVisibility(View.VISIBLE);
    }

}
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (parsed_items.size()>0)
        {
            RecyclerAdapter radapter=new RecyclerAdapter(getApplicationContext(),parsed_items);
            recyclerView.swapAdapter(radapter,false);
        }
    }

    public void parse_all_items(String url){
        Log.d("all_items_url",url);
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("findItemsAdvancedResponse")){
                        JSONObject fiar=response.getJSONArray("findItemsAdvancedResponse").getJSONObject(0);
                        if(fiar.has("ack")&& fiar.getJSONArray("ack").getString(0).equals("Success") &&
                        fiar.has("searchResult") ){
                             JSONObject sRe= fiar.getJSONArray("searchResult").getJSONObject(0);
                             if(sRe.has("@count") && Integer.parseInt(sRe.getString("@count"))>0)
                             {
                                 JSONArray items=sRe.getJSONArray("item");
                                 for(int k=0;k<items.length();k++){
                                     JSONObject par_item=items.getJSONObject(k);
                                     JSONObject parsed=new JSONObject();
                                     String temp="N/A";
                                     if(par_item.has("itemId"))
                                         temp=par_item.getJSONArray("itemId").getString(0);
                                     parsed.put("id",temp);

                                     temp="N/A";
                                     if(par_item.has("title"))
                                         temp=par_item.getJSONArray("title").getString(0);
                                     parsed.put("title",temp);

                                     temp="N/A";
                                     if(par_item.has("galleryURL"))
                                         temp=par_item.getJSONArray("galleryURL").getString(0);
                                     parsed.put("image",temp);

                                     temp="N/A";
                                     if(par_item.has("viewItemURL"))
                                         temp=par_item.getJSONArray("viewItemURL").getString(0);
                                     parsed.put("vurl",temp);

                                     temp="N/A";
                                     if(par_item.has("postalCode"))
                                         temp=par_item.getJSONArray("postalCode").getString(0);
                                     parsed.put("zip",temp);

                                     temp="N/A";
                                     if(par_item.has("condition") && par_item.getJSONArray("condition").getJSONObject(0).has("conditionDisplayName"))
                                         temp=par_item.getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                                     temp=temp.replace("\n","").replace("\r","");
                                     if(!temp.trim().equals(""))
                                     parsed.put("condition",temp);
                                     else
                                         parsed.put("condition","N/A");
                                     temp="N/A";
                                     if(par_item.has("sellingStatus") && par_item.getJSONArray("sellingStatus").getJSONObject(0).has("currentPrice"))
                                         temp=par_item.getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                                     parsed.put("price","$"+temp.replace("\n",""));

                                     temp="N/A";
                                     if(par_item.has("shippingInfo")) {
                                         JSONObject sinfo=par_item.getJSONArray("shippingInfo").getJSONObject(0);
                                         if(sinfo.has("shippingType")) {
                                             if (sinfo.getJSONArray("shippingType").getString(0).equals("Free"))
                                                 temp = "Free Shipping";
                                             else if(sinfo.has("shippingServiceCost")){
                                                 float service_cost=Float.parseFloat(sinfo.getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__"));
                                                 if (service_cost==0.0) temp = "Free Shipping";
                                                 else temp="$"+service_cost;
                                             }
                                         }
                                         else if(sinfo.has("shippingServiceCost")) {
                                             float service_cost = Float.parseFloat(sinfo.getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__"));
                                             if (service_cost == 0.0) temp = "Free Shipping";
                                             else temp = "$" + service_cost;
                                         }
                                     }
                                     parsed.put("shipping",temp);

                                     Iterator<String> keys=parsed.keys();
                                     while(keys.hasNext()){
                                         String kkey=keys.next();
//                                         Log.d("lf",kkey+":"+parsed.get(kkey));
                                     }
                                     parsed_items.add(parsed);
                                 }

                             }
                        }
                    }
                    populate_view();
                } catch (JSONException e) { e.printStackTrace();}  } }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { error.printStackTrace(); }
        });
        requestQueue.add(reqer);
    }
}
