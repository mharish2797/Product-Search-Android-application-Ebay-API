package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.util.List;
import java.util.regex.Pattern;



public class search extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

EditText keyword,miles;
AutoCompleteTextView zipcode;
CheckBox c_new,c_used,c_unspecified,c_local,c_ship,enable_search;
RadioGroup radioGroup;
Button search,clear;
Spinner category;
LinearLayout visible_layout;
TextView mand_keyword,mand_zip;
RequestQueue requestQueue;
List<String> zips;
String[] random={"rrf","rrf","rrf","rrf","rrf"};
String actual_zip="error";
ArrayAdapter<String> zipadapt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search, container, false);

        radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup);
        radioGroup.check(R.id.cur_loc);
        category=(Spinner)view.findViewById(R.id.spinner);
        List<String> categ_list=new ArrayList<>();
        zips=new ArrayList<>();
        categ_list.add("All");
        categ_list.add("Art");
        categ_list.add("Baby");
        categ_list.add("Books");
        categ_list.add("Clothing, Shoes & Accessories");
        categ_list.add("Computers/Tablets & Networking");
        categ_list.add("Health & Beauty");
        categ_list.add("Music");
        categ_list.add("Video Games & Consoles");
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,categ_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        mand_keyword=(TextView)view.findViewById(R.id.mand_keyword);
        mand_zip=(TextView)view.findViewById(R.id.mand_zip) ;
        keyword=(EditText) view.findViewById(R.id.keyword);

        miles=(EditText) view.findViewById(R.id.editmiles);

        radioGroup.setOnCheckedChangeListener(this);
        requestQueue= Volley.newRequestQueue(getActivity());

        zipcode=(AutoCompleteTextView) view.findViewById(R.id.zipcode);
        zipcode.setThreshold(3);
        zipcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()>2)
                    getAutocompletecontent(s);
                else
                    zipcode.dismissDropDown();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        zipcode.setEnabled(false);

        c_new=(CheckBox)view.findViewById(R.id.c_new);
        c_used=(CheckBox)view.findViewById(R.id.c_used);
        c_unspecified=(CheckBox)view.findViewById(R.id.c_unspecified);
        c_local=(CheckBox)view.findViewById(R.id.c_local);
        c_ship=(CheckBox)view.findViewById(R.id.c_ship);
        enable_search=(CheckBox)view.findViewById(R.id.enable_search);
        search=(Button)view.findViewById(R.id.search_btn);
        clear=(Button)view.findViewById(R.id.clear_btn);
        search.setOnClickListener(this);
        clear.setOnClickListener(this);
        visible_layout=(LinearLayout)view.findViewById(R.id.visible_layout);
        enable_search.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    visible_layout.setVisibility(View.VISIBLE);

                else
                { visible_layout.setVisibility(View.GONE);
                    clear_sub_nearby();
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }


    public void clear_sub_nearby(){
        miles.setText("");
        radioGroup.check(R.id.cur_loc);
        mand_zip.setVisibility(View.GONE);
    }

    public void getAutocompletecontent(CharSequence s){
//        String url="http://prod-searcher.appspot.com/link_autocomplete?zipcode="+s.toString();
        String url="http://api.geonames.org/postalCodeSearchJSON?postalcode_startsWith="+s.toString()+"&username=mharish2797&country=US&maxRows=5";
//        Toast.makeText(getActivity(),url,Toast.LENGTH_SHORT).show();
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    zips.clear();
                    JSONArray jsonArray=response.getJSONArray("postalCodes");
                    for(int k=0;k<jsonArray.length();k++){
                        JSONObject jsonObject=jsonArray.getJSONObject(k);
                        zips.add(jsonObject.getString("postalCode"));
//                        Toast.makeText(getActivity(),jsonObject.getString("postalCode"),Toast.LENGTH_LONG).show();
                        Log.d(jsonObject.getString("postalCode"),"df");
                    }
                    zipadapt=new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item,zips);
                    zipcode.setAdapter(zipadapt);
                    zipcode.showDropDown();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(reqer);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_btn:
                onSearch();
                break;
            case R.id.clear_btn:
                onClear();
                break;
        }
    }

    public void getCurrLoc(){
        String url="http://ip-api.com/json";
        JsonObjectRequest reqer=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    actual_zip=response.getString("zip");
//                        Toast.makeText(getActivity(),actual_zip,Toast.LENGTH_LONG).show();
                    } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(reqer);
    }

    public String getParams(){
        String[] categ_values = {"0", "550","2984","267","11450","58058","26395","11233","1249"};

        String result="http://prod-searcher.appspot.com/link_all_items?";
        //http://prod-searcher.appspot.com/link_all_items?keyword=car&category=0&cnew=false&cused=false&cunspecified=false&slocal=false&sship=false&miles=10&zipcode=90007
        result+="keyword="+keyword.getText().toString();
        result+="&category="+categ_values[category.getSelectedItemPosition()];
        result+="&cnew="+c_new.isChecked();
        result+="&cused="+c_used.isChecked();
        result+="&cunspecified="+c_unspecified.isChecked();
        result+="&slocal="+c_local.isChecked();
        result+="&sship="+c_ship.isChecked();
        String att_miles="10";
        if (miles.getText().toString().trim().length()>0)
            att_miles=miles.getText().toString().trim();
        if(!actual_zip.equals("error"))
        {result+="&miles="+att_miles;
        result+="&zipcode="+actual_zip;}
        /*********************************************************************************************************/
        else{
            result+="&miles=0";
            result+="&zipcode=90007";
        }
//        zipcode.setText(actual_zip);
//        Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
        return result;
    }

    public void onSearch(){

        if(mandatory_fields())
        {

            NetworkService ns=new NetworkService();

            if(!ns.isNetworkAvailable(getContext())) {
                Toast.makeText(getContext(),"No Network Connection",Toast.LENGTH_LONG).show();
            }
            else{

            if(enable_search.isChecked()){
            if(radioGroup.getCheckedRadioButtonId()==R.id.cur_loc) {
                getCurrLoc();
            }
            else
                actual_zip=zipcode.getText().toString().trim();
            }
            else
                actual_zip="error";

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent=new Intent(getActivity(),All_items.class);
                        intent.putExtra("api_call",getParams());
                        intent.putExtra("just_keyword",keyword.getText().toString());
                        startActivity(intent);
                    }
                }
            };
            timerThread.start();

        }
        }

        else
            Toast.makeText(getActivity(),"Please fix all fields with errors",Toast.LENGTH_LONG).show();
    }

    public boolean mandatory_fields(){

        boolean req_flag=true;
        String temp_zip=zipcode.getText().toString().trim();
        if(keyword.getText().toString().trim().equals(""))
        {mand_keyword.setVisibility(View.VISIBLE);
            req_flag=false;
        }
        else
            mand_keyword.setVisibility(View.GONE);


        if(radioGroup.getCheckedRadioButtonId()==R.id.zip_loc && (temp_zip.equals("") || !temp_zip.matches("\\d+") || temp_zip.length()!=5) ){
            mand_zip.setVisibility(View.VISIBLE);
            req_flag=false;
        }
        else
            mand_zip.setVisibility(View.GONE);
        return req_flag;
    }

    public void clear_mandatory_fields(){
        mand_zip.setVisibility(View.GONE);
        mand_keyword.setVisibility(View.GONE);
    }
    public void onClear(){
        clear_mandatory_fields();
        keyword.setText("");
//
//        c_new.setChecked(false);
//        c_unspecified.setChecked(false);
//        c_used.setChecked(false);
//
//        c_ship.setChecked(false);
//        c_local.setChecked(false);
        category.setSelection(0);


        enable_search.setChecked(false);

        //Toast.makeText(getActivity(),"Clear Button",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.cur_loc:
                zipcode.setText("");
                zipcode.setEnabled(false);
               // Toast.makeText(getActivity(),"Current Location",Toast.LENGTH_SHORT).show();
                break;
            case R.id.zip_loc:
                zipcode.setEnabled(true);
             //   Toast.makeText(getActivity(),"Zip Location",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
