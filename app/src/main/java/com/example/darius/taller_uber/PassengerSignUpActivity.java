package com.example.darius.taller_uber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PassengerSignUpActivity extends AppCompatActivity implements URL_local {

    private EditText nombre;
    private EditText apellido;
    private EditText password;
    private EditText email;
    private Button confirmar;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RequestQueue queue;
    private TextInputLayout pwLayout;
    private boolean fb_register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_passenger_sign_up);
        load_layout_elements();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        configure_layout_elements();
        if (user != null && user.getProviders().contains("facebook.com")){
            fb_register = true;
            load_registration_through_facebook();
        }
        queue = Volley.newRequestQueue(this);
    }

    /**
     * load_layout_elements
     * Carga los elementos de la interfaz y los asocia a un atributo.
     */
    private void load_layout_elements() {
        email = (EditText) findViewById(R.id.email);
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        password = (EditText) findViewById(R.id.password);
        confirmar = (Button) findViewById(R.id.confirm_signup_button);
        pwLayout = (TextInputLayout) findViewById(R.id.password_layout);
    }

    private void configure_layout_elements() {
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    /**
     * Fija los valores de email, nombre y apellido extraidos de facebook.
     * La unica instancia en la que user es distinto de null en esta actividad
     * es cuando el usuario ingresó mediante facebook.
     */
    private void load_registration_through_facebook() {
        email.setText(user.getEmail());
        email.setKeyListener(null);
        nombre.setText(get_user_last_name());
        nombre.setKeyListener(null);
        apellido.setText(get_user_first_name());
        apellido.setKeyListener(null);
        pwLayout.setVisibility(View.GONE);
    }

    /**
     * Intenta registrar un usuario nuevo con email y contraseña al firebase.
     * En caso de éxito, desencadena la actividad de ingreso de los métodos de pago.
     */
    private void attemptRegister() {
        if (fb_register){
            sendSignUpRequest();
        } else {
            // Reset errors.
            email.setError(null);
            password.setError(null);

            // Store values at the time of the login attempt.
            String _email = email.getText().toString();
            String _password = password.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(_password) && !isPasswordValid(_password)) {
                password.setError(getString(R.string.error_invalid_password));
                focusView = password;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(_email)) {
                email.setError(getString(R.string.error_field_required));
                focusView = email;
                cancel = true;
            } else if (!isEmailValid(_email)) {
                email.setError(getString(R.string.error_invalid_email));
                focusView = email;
                cancel = true;
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            }

            if (!cancel) {
                mAuth.createUserWithEmailAndPassword(_email, _password);
                mAuth.signInWithEmailAndPassword(_email, _password);
                user = mAuth.getCurrentUser();
                String newName = nombre.getText().toString() + " " + apellido.getText().toString();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();
                user.updateProfile(profileUpdates);
                //TODO cambiar la actividad
                //startActivity(new Intent(PassengerSignUpActivity.this, PaymentActivity.class));
                sendSignUpRequest();
            }
        }
    }

    private void sendSignUpRequest() {
        try {
            final JSONObject passenger_json = new JSONObject();
            passenger_json.put("type", "passenger");
            passenger_json.put("name", nombre.getText().toString());
            passenger_json.put("last_name", apellido.getText().toString());
            passenger_json.put("mail", email.getText().toString());

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url_user, passenger_json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Respuesta: ", response.toString());
                        startActivity(new Intent(PassengerSignUpActivity.this, MainActivity.class));
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error: ", error.getMessage());
                }
            });
            queue.add(postRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private String get_user_first_name() {
        String name;
        int i;

        name = user.getDisplayName();
        i = name.indexOf(" ");
        return name.substring(i + 1, name.length());
    }

    private String get_user_last_name() {
        String name;
        int i;

        name = user.getDisplayName();
        i = name.indexOf(" ");
        return name.substring(0, i);
    }
}
