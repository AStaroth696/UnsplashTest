package com.example.android.unsplashtest.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.android.unsplashtest.R;

/**
 * Class that provides network state info
 */
public class NetworkStateInfo {
    //Returns true if network is available
    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            //Show toast if network is not available
            Toast.makeText(context, context.getResources().getString(R.string.connectivity_error),
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
