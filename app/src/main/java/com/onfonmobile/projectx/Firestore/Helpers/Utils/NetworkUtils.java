package com.onfonmobile.projectx.Firestore.Helpers.Utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;  // ✅ Correct import

public class NetworkUtils {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
