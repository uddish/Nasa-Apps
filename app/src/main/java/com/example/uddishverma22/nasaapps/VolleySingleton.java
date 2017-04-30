package com.example.uddishverma22.nasaapps;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by uddishverma22 on 05/04/17.
 */

public class VolleySingleton {

    private static VolleySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mctx;

    public VolleySingleton(Context context)
    {
        mctx = context;
        requestQueue = getRequestQueue();
    }


    public static synchronized VolleySingleton getInstance(Context context) {

        if(mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T>void addToRequestQueue(Request<T> request)
    {
        requestQueue.add(request);
    }

}
