package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class SingleItemDetails extends AppCompatActivity {


    private static final String TAG="SingleItemActivity";
    private  SectionsPageAdapter msectSectionsPageAdapter;
    private ViewPager mViewPager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton fab;
    JSONObject jsonObject;
    Bundle bundle;
    String data="hi";
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.information_variant,
            R.drawable.truck_delivery,
            R.drawable.google,
            R.drawable.equal,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_details);
        fab=findViewById(R.id.fab);

        sharedPreferences = getApplicationContext().getSharedPreferences("harishmo", Context.MODE_PRIVATE); // 0 - for private mode
        editor = sharedPreferences.edit();

        bundle=getIntent().getExtras();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        
        myToolbar.setTitle(bundle.getString("trimmed"));
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.arrow_left);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        Log.d(TAG,"Starting");

        try {
            data=bundle.getString("item_object");
            setData(data);
            jsonObject=new JSONObject(data);
            if(sharedPreferences.contains(jsonObject.getString("id")))
                fab.setImageResource(R.drawable.cart_remove);

            else
                fab.setImageResource(R.drawable.cart_plus);
//            Toast.makeText(this,jsonObject.toString(),Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msectSectionsPageAdapter=new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager=(ViewPager) findViewById(R.id.single_container);

        setupViewPager(mViewPager,bundle.getString("item_object"));
        tabLayout=(TabLayout) findViewById(R.id.single_tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    JSONObject jbb=new JSONObject(data);
                    String itemid=jbb.getString("id");
                    if(!sharedPreferences.contains(itemid))
                    {
                        Toast.makeText(getApplicationContext(),bundle.getString("trimmed")+" was added to wish list",Toast.LENGTH_LONG).show();
                editor.putString(itemid,data);
                editor.commit();
                fab.setImageResource(R.drawable.cart_remove);}
                else{
                        Toast.makeText(getApplicationContext(),bundle.getString("trimmed")+" was removed from wish list",Toast.LENGTH_LONG).show();
                        editor.remove(itemid);
                    editor.commit();
                fab.setImageResource(R.drawable.cart_plus);}

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager, String datum){
        SectionsPageAdapter adapter=new SectionsPageAdapter(getSupportFragmentManager());
        Bundle xb=new Bundle();
        xb.putString("xax",datum);
        Product product=new Product();
        product.setArguments(xb);
        Shipping shipping=new Shipping();
        shipping.setArguments(xb);
        Photos photos=new Photos();
        photos.setArguments(xb);
        Similar similar=new Similar();
        similar.setArguments(xb);
        adapter.addFragment(product,"Product");
        adapter.addFragment(shipping,"Shipping");
        adapter.addFragment(photos,"Photos");
        adapter.addFragment(similar,"Similar");

        viewPager.setAdapter(adapter);
    }

    public void setData(String x){
        data=x;
    }
    public String getData(){
        return data;
    }

    public void fbclick(View view) {
        try {Log.d("FB",bundle.getString("item_object"));

            JSONObject jb=new JSONObject(bundle.getString("item_object"));

        String fb_url="https://www.facebook.com/dialog/share?app_id=2351877448356992&display=popup";
        String quote="Buy "+jb.getString("title")+" for "+jb.getString("price")+" from Ebay!";
        String href=jb.getString("vurl");
        fb_url+="&href="+href+"&quote="+quote+"&hashtag=%23CSCI571Spring2019Ebay";
        Intent fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fb_url));
        startActivity(fbIntent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
