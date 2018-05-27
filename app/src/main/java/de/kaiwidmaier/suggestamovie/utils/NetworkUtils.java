package de.kaiwidmaier.suggestamovie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Kai on 20.03.2018.
 */

public class NetworkUtils {

  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connectivityManager != null;
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  public static boolean loadThumbnail(Context context){
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    boolean allowLoading = sharedPref.getBoolean("thumbnail_loading", true);

    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    assert connManager != null;
    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    return allowLoading || wifi.isConnected();
  }

}
