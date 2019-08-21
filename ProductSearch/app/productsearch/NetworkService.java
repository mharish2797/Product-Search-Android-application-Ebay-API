package com.example.productsearch;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkService {
    public boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
