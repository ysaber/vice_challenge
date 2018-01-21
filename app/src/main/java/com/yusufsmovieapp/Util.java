package com.yusufsmovieapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Helper methods to be used throughout the app
 */
@SuppressWarnings("WeakerAccess")
public class Util {


    /**
     * Check if the device has an active internet connection.
     */
    public static boolean isOnline() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) MoviesApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (connectivityManager != null) {
            netInfo = connectivityManager.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    /**
     * Show a long toast
     */
    public static void longToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
