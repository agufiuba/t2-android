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

    Comunicador(final FirebaseUser user, final Context context) {
        this.user = user;
        this.queue = Volley.newRequestQueue(context);
    }

    public void requestAuthenticated(final Runnable onSuccess,
                                     final Runnable onError,
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
                                return headers;
                            }
                        };
                    queue.add(jsonObjectRequest);
                }
            });
    }

    public void requestFree(final Runnable onSuccess,
                            final Runnable onError,
                            final String url,
                            final JSONObject params,
                            final int method) {
        JsonObjectRequest postRequest =
            new JsonObjectRequest(method, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Respuesta: ", response.toString());
                        try {
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
                        onError.run();
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                }
            });
        queue.add(postRequest);
    }
}
