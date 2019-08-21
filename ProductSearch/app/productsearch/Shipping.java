package com.example.productsearch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.drm.DrmStore;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Shipping extends Fragment {
    LinearLayout progressBar;
    JSONObject parsed_item;
    RequestQueue requestQueue;
    LinearLayout soldby_head,ship_info_head,return_policy_head,swish,ship_norec;
    TableLayout sold,ship,returning;
    String spacer="\t\t\t\t\t";
    TableRow pops;
    boolean no_rec=false;
    int global_counter;
    CircularScoreView circularScoreView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_shipping, container, false);
        global_counter=0;
        parsed_item=new JSONObject();
        ship_norec=view.findViewById(R.id.ship_norec);
        ship_norec.setVisibility(View.GONE);
        swish=view.findViewById(R.id.ship_swish);
        pops=view.findViewById(R.id.populars);
        requestQueue= Volley.newRequestQueue(getContext());
        sold=view.findViewById(R.id.sold);
        ship=view.findViewById(R.id.ship);
        returning=view.findViewById(R.id.returning);
        soldby_head=view.findViewById(R.id.soldby_head);
        ship_info_head=view.findViewById(R.id.ship_info_head);
        return_policy_head=view.findViewById(R.id.return_policy_head);
        soldby_head.setVisibility(View.GONE);
        ship_info_head.setVisibility(View.GONE);
        return_policy_head.setVisibility(View.GONE);
         circularScoreView = view.findViewById(R.id.scoreview);

        String str=this.getArguments().getString("xax");
        try {
            JSONObject jsonObject=new JSONObject(str);
            Log.d("SingleItem",str);
            parse_from_bundle(jsonObject);
            parse_ship_items(jsonObject.getString("id"));
//            parse_ship_items("122");
        } catch (JSONException e) { e.printStackTrace(); }
        progressBar=view.findViewById(R.id.ship_progressBar);

        return view;
    }


    public void populate(){
        progressBar.setVisibility(View.GONE);
        if(parsed_item.length()>0)
        {
            ship_norec.setVisibility(View.GONE);
            swish.setVisibility(View.VISIBLE);
            set_params();
        }
        else{
            swish.setVisibility(View.GONE);
            ship_norec.setVisibility(View.VISIBLE);
        }

    }

    public TableRow row_setter(String left,String right){
        TableRow tableRow=new TableRow(getContext());
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0,20,0,20);
//        LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView left_text=new TextView(getContext());
        left_text.setText(spacer+left+spacer);
        left_text.setTextSize(15);
        left_text.setTextColor(Color.BLACK);
        TextView right_text=new TextView(getContext());
        if(left.equals("Feedback Star")){
            Log.d("Fb",right);
            String colorer=right.toLowerCase();
            Drawable mDrawable;
            if(right.contains("shooting")){
                colorer=colorer.substring(0,colorer.indexOf("shooting"));
                mDrawable = ContextCompat.getDrawable(getContext(),R.drawable.star_circle);
                Drawable mutableDrawable = DrawableCompat.wrap(mDrawable);
                switch(colorer){
                    case "red":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.red));
                                    break;
                    case "blue":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.blue));
                        break;
                    case "green":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.green));
                        break;
                    case "yellow":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.yellow));
                        break;
                    case "purple":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.purple));
                        break;
                    case "turquoise":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.turquoise));
                        break;
                    case "silver":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.silver));
                        break;
                        default:    DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.black));

                }

                right_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_circle,0,0,0);
            }
            else{

                 mDrawable = ContextCompat.getDrawable(getContext(),R.drawable.star_circle_outline);
                Drawable mutableDrawable = DrawableCompat.wrap(mDrawable);
                switch(colorer){
                    case "red":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.red));
                        break;
                    case "blue":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.blue));
                        break;
                    case "green":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.green));
                        break;
                    case "yellow":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.yellow));
                        break;
                    case "purple":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.purple));
                        break;
                    case "turquoise":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.turquoise));
                        break;
                    case "silver":     DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.silver));
                        break;
                    default:    DrawableCompat.setTint(mutableDrawable, ContextCompat.getColor(getContext(), R.color.black));

                }

                right_text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star_circle_outline,0,0,0);
            }

        }

        else if(left.equals("Store Name")){
            right_text.setClickable(true);
            right_text.setText(Html.fromHtml(right));
            right_text.setMovementMethod(LinkMovementMethod.getInstance());
            right_text.setTextSize(15);
        }
        else{
            right_text.setText(right);
            right_text.setTextSize(15);
            final float inPixels= getResources().getDimension(R.dimen.text_view_height);
            int pix=Math.round(inPixels);
            right_text.setWidth(pix);
        }

        tableRow.addView(left_text);
        tableRow.addView(right_text);

        tableRow.setLayoutParams(tableRowParams);
        return tableRow;
    }
    public void set_params(){

        set_sold();
        set_ship();
        set_return();
    }
    public void set_sold(){
        try {
    if(parsed_item.has("store_name") || parsed_item.has("positive_fb_percent")|| parsed_item.has("fb_score")|| parsed_item.has("fb_rating_star"))
    {
        soldby_head.setVisibility(View.VISIBLE);
        if(parsed_item.has("store_name"))
        {
            if(parsed_item.has("store_url")){
                String name="<a href='"+parsed_item.getString("store_url")+"'>"+parsed_item.getString("store_name")+"</a>";
                sold.addView(row_setter("Store Name", name),global_counter);

            }
            else sold.addView(row_setter("Store Name", parsed_item.getString("store_name")),global_counter);
            global_counter+=1;
        }

        if(parsed_item.has("fb_score"))
        {sold.addView(row_setter("Feedback Score", parsed_item.getString("fb_score")),global_counter);
            global_counter+=1;
        }

        if(parsed_item.has("positive_fb_percent"))
        {
            pops.setVisibility(View.VISIBLE);
            circularScoreView.setScore(parsed_item.getInt("positive_fb_percent"));
        }

        if(parsed_item.has("fb_rating_star"))
            {global_counter+=1;
            sold.addView(row_setter("Feedback Star", parsed_item.getString("fb_rating_star")),global_counter);

        }

    }

    } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void set_ship(){
        try {
            if(parsed_item.has("ship") || parsed_item.has("global_shipping")|| parsed_item.has("handling_time")|| parsed_item.has("condition_description"))
            {
                ship_info_head.setVisibility(View.VISIBLE);
                if(parsed_item.has("ship"))
                    ship.addView(row_setter("Shipping Cost", parsed_item.getString("ship")));

                if(parsed_item.has("global_shipping"))
                    ship.addView(row_setter("Global Shipping", parsed_item.getString("global_shipping")));

                if(parsed_item.has("handling_time"))
                    ship.addView(row_setter("Handling Time", parsed_item.getString("handling_time")));

                if(parsed_item.has("condition_description"))
                    ship.addView(row_setter("Condition", parsed_item.getString("condition_description")));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void set_return(){
        try {
            if(parsed_item.has("policy") || parsed_item.has("returns_within")|| parsed_item.has("refund")|| parsed_item.has("shipped_by"))
            {
                return_policy_head.setVisibility(View.VISIBLE);
                if(parsed_item.has("policy"))
                    returning.addView(row_setter("Policy", parsed_item.getString("policy")));

                if(parsed_item.has("returns_within"))
                    returning.addView(row_setter("Returns Within", parsed_item.getString("returns_within")));

                if(parsed_item.has("refund"))
                    returning.addView(row_setter("Refund Mode", parsed_item.getString("refund")));

                if(parsed_item.has("shipped_by"))
                    returning.addView(row_setter("Shipped By", parsed_item.getString("shipped_by")));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public void parse_ship_items(String item){
        String url="http://prod-searcher.appspot.com/link_single_item?item_id="+item;
        Log.d("ship_details",url);
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("single",response.toString());
                try {
                    if(response.has("Ack") && response.getString("Ack").equals("Success")){
                        JSONObject item=response.getJSONObject("Item");

                        if(item.has("Storefront")) {
                            JSONObject storefront=item.getJSONObject("Storefront");
                            if (storefront.has("StoreURL"))
                            parsed_item.put("store_url", storefront.getString("StoreURL"));
                            if (storefront.has("StoreName"))
                                parsed_item.put("store_name", storefront.getString("StoreName"));
                        }

                        if(item.has("Seller")) {
                            JSONObject seller=item.getJSONObject("Seller");
                            if (seller.has("FeedbackRatingStar"))
                                parsed_item.put("fb_rating_star", seller.getString("FeedbackRatingStar"));
                            if (seller.has("FeedbackScore"))
                                parsed_item.put("fb_score", seller.getInt("FeedbackScore"));
                            if (seller.has("PositiveFeedbackPercent"))
                                parsed_item.put("positive_fb_percent", seller.getDouble("PositiveFeedbackPercent"));
                        }

                        if(item.has("GlobalShipping")){
                            if(item.getBoolean("GlobalShipping")== false)
                                parsed_item.put("global_shipping","No");
                            else
                                parsed_item.put("global_shipping","Yes");
                        }

                        if(item.has("HandlingTime")){
                            if(item.getInt("HandlingTime")<2)
                                parsed_item.put("handling_time",Integer.toString(item.getInt("HandlingTime"))+" day");
                            else
                                parsed_item.put("handling_time",Integer.toString(item.getInt("HandlingTime"))+" days");
                        }

                        if(item.has("ConditionDescription"))
                            parsed_item.put("condition_description",item.getString("ConditionDescription"));

                        if(item.has("ReturnPolicy")) {
                            JSONObject returnpolicy=item.getJSONObject("ReturnPolicy");
                            if (returnpolicy.has("ReturnsWithin"))
                                parsed_item.put("returns_within", returnpolicy.getString("ReturnsWithin"));
                            if (returnpolicy.has("ReturnsAccepted"))
                                parsed_item.put("policy", returnpolicy.getString("ReturnsAccepted"));
                            if (returnpolicy.has("Refund"))
                                parsed_item.put("refund", returnpolicy.getString("Refund"));
                            if (returnpolicy.has("ShippingCostPaidBy"))
                                parsed_item.put("shipped_by", returnpolicy.getString("ShippingCostPaidBy"));
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
