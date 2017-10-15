package com.example.darius.taller_uber;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import static com.facebook.FacebookSdk.getCacheDir;

/**
 * Created by darius on 12/10/17.
 * Siguiendo la documentación: https://developer.android.com/training/volley/requestqueue.html
 */

public class RequestManager {

    private static RequestManager mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static String url = "http://172.18.0.1:3000/user/";

    private RequestManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestManager(context);
        }
        return mInstance;
    }

    public static String getUrl(){
        return url;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}


