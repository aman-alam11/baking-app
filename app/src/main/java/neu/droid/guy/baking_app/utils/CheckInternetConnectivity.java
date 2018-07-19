package neu.droid.guy.baking_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Objects;

public class CheckInternetConnectivity {

    public static boolean isInternetConnectivityAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Objects.requireNonNull(manager).getActiveNetworkInfo() != null) {
            // Means not in airplane mode
            NetworkInfo info = manager.getActiveNetworkInfo();
            return info.isConnectedOrConnecting();
        }
        return false;
    }
}
