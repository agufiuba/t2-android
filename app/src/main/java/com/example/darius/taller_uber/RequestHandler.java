package com.example.darius.taller_uber;

import org.json.JSONObject;

/**
 * Created by darius on 07/11/17.
 */

public abstract class RequestHandler implements Runnable {
    protected JSONObject jsonRecv;

    public void run(){}

    public void setJson(JSONObject json){
        this.jsonRecv = json;
    }

    public JSONObject getJson(){
        return this.jsonRecv;
    }
}
