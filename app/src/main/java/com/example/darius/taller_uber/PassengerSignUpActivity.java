package com.example.darius.taller_uber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PassengerSignUpActivity extends AppCompatActivity {

    private EditText nombre;
    private EditText apellido;
    private EditText password;
    private EditText email;
    private Button confirmar;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RequestQueue queue;
    String url = "http://192.168.1.12:3000/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_passenger_sign_up);
        load_layout_elements();
        configure_layout_elements();
        queue = Volley.newRequestQueue(this);
    }

    /**
     * load_layout_elements
     * Carga los elementos de la interfaz y los asocia a un atributo.
     */
    private void load_layout_elements(){
        email = (EditText) findViewById(R.id.email);
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        password = (EditText) findViewById(R.id.password);
        confirmar = (Button) findViewById(R.id.confirm_signup_button);
    }

    private void configure_layout_elements(){
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    /**
     * Intenta registrar un usuario nuevo con email y contraseña al firebase.
     * En caso de éxito, desencadena la actividad de ingreso de los métodos de pago.
     */
    private void attemptRegister() {

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

        if(!cancel){
            mAuth = FirebaseAuth.getInstance();
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

    private void sendSignUpRequest() {
        try {
            View focusView = email;
            final JSONObject driver_json = new JSONObject();
            final JSONObject car_json = new JSONObject();
            driver_json.put("type","passenger");
            driver_json.put("name",nombre.getText().toString());
            driver_json.put("last_name",apellido.getText().toString());
            driver_json.put("id",email.getText().toString());

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, driver_json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Respuesta: ", response.toString());
                        if (response.toString() == "POST user OK"){
                            startActivity(new Intent(PassengerSignUpActivity.this, MainActivity.class));
                        }
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

}