package com.example.darius.taller_uber;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by darius on 07/11/17.
 */

public class Comunicador {

    private static final String TAG = "Comunicador";

    private FirebaseUser user;
    private RequestQueue queue;
    private JSONObject answer;

    Comunicador(final FirebaseUser user, final Context context) {
        this.user = user;
        this.queue = Volley.newRequestQueue(context);
        answer = new JSONObject();
    }

    public JSONObject getAnswerJSON(){
        return answer;
    }

    public void requestAuthenticated(final RequestHandler onSuccess,
                                     final RequestHandler onError,
                                     final String url,
                                     final JSONObject params,
                                     final int method) {
        user.getIdToken(true).addOnSuccessListener(
            new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult result) {
                    final String idToken = result.getToken();
                    JsonObjectRequest jsonObjectRequest =
                        new JsonObjectRequest(method, url, params, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                VolleyLog.v("Response:%n %s", response);
                                try {
                                    onSuccess.setJson(response);
                                    onSuccess.run();
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.e("Error: ", error.getMessage());
                                try {
                                    onError.setError(error);
                                    onError.run();
                                } catch (Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            }
                        }) {
                            /**
                             * Request headers
                             */
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Authorization", idToken);
                                headers.put("Content-Type","application/json");
                                return headers;
                            }
                        };
                    queue.add(jsonObjectRequest);
                }
            });
    }

    public void requestAuthenticatedWithInstanceID(final RequestHandler onSuccess,
                                     final RequestHandler onError,
                                     final String url,
                                     final JSONObject params,
                                     final int method) {
        user.getIdToken(true).addOnSuccessListener(
                new OnSuccessListener<GetTokenResult>() {
                    @Override
                    public void onSuccess(GetTokenResult result) {
                        final String idToken = result.getToken();
                        JsonObjectRequest jsonObjectRequest =
                                new JsonObjectRequest(method, url, params, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        VolleyLog.v("Response:%n %s", response);
                                        try {
                                            onSuccess.setJson(response);
                                            onSuccess.run();
                                        } catch (Exception e) {
                                            Log.d(TAG, e.toString());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        VolleyLog.e("Error: ", error.getMessage());
                                        try {
                                            onError.setError(error);
                                            onError.run();
                                        } catch (Exception e) {
                                            Log.d(TAG, e.toString());
                                        }
                                    }
                                }) {
                                    /**
                                     * Request headers
                                     */
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> headers = new HashMap<String, String>();
                                        headers.put("Authorization", idToken);
                                        headers.put("Content-Type","application/json");
                                        headers.put("Session",FirebaseInstanceId.getInstance().getToken());
                                        return headers;
                                    }
                                };
                        queue.add(jsonObjectRequest);
                    }
                });
    }

    public void requestFree(final RequestHandler onSuccess,
                            final RequestHandler onError,
                            final String url,
                            final JSONObject params,
                            final int method) {
        JsonObjectRequest jsonObjectRequest =
            new JsonObjectRequest(method, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        VolleyLog.v("Response:%n %s", response);
                        try {
                            onSuccess.setJson(response);
                            onSuccess.run();
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error: ", error.getMessage());
                    try {
                        onError.setError(error);
                        onError.run();
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });
        queue.add(jsonObjectRequest);
    }
}
