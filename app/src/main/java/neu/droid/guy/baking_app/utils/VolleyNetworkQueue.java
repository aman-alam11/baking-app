package neu.droid.guy.baking_app.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyNetworkQueue {
    private static VolleyNetworkQueue mVolleyNetworkInstance;
    private RequestQueue mNetworkQueue;

    private VolleyNetworkQueue(Context context) {
        mNetworkQueue = Volley.newRequestQueue(context);
    }

    /**
     * If the Volley Networking Queue is not created yet, create one
     * Else return the same VolleyNetworkQueue instance that has the already instantiated Request Queue
     *
     * @param context The activity context which is requesting the networking capabilities
     * @return
     */
    public static VolleyNetworkQueue getInstance(Context context) {
        if (mVolleyNetworkInstance == null) {
            mVolleyNetworkInstance = new VolleyNetworkQueue(context);
        }
        return mVolleyNetworkInstance;
    }

    /**
     * Returns the common request queue
     *
     * @return The Request Queue that will be returned where all network requests will be appended
     */
    public RequestQueue getRequestQueue() {
        return mNetworkQueue;
    }


}
