package com.example.darius.taller_uber;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by darius on 07/11/17.
 */

public abstract class RequestHandler implements Runnable {
    protected JSONObject jsonRecv;
    protected VolleyError volleyError;

    public void run(){}

    public void setJson(JSONObject json){
        this.jsonRecv = json;
    }

    public void setError(VolleyError volleyError){ this.volleyError = volleyError; }

    public JSONObject getJson(){
        return this.jsonRecv;
    }

    public VolleyError getVolleyError() { return this.volleyError; }
}
