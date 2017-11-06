package com.example.darius.taller_uber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.sax2.Driver;

public class DriverSignUpActivity extends AppCompatActivity implements URL_local {

    View focusView = null;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    final CharSequence[] modelos = {"Ford Fiesta", "Chevrolet S10",
        "Toyota Hilux", "Fiat Palio", "Renault Scenic"};

    final CharSequence[] colores = {"Negro", "Amarillo", "Azul", "Rojo",
        "Gris", "Blanco", "Verde"};
    final CharSequence[] estados = {"Excelente", "Bueno", "Destartalado", "PicaPiedra"};
    final CharSequence[] aire_options = {"Sí", "No"};
    final CharSequence[] musicas = {"Clasica", "Jazz", "Tango", "Rock", "Folklore", "Pop"};

    CharSequence modelo = null;
    CharSequence color = null;
    CharSequence estado = null;
    CharSequence aire = null;
    String patente = null;
    CharSequence musica = null;
    LinearLayout modelo_layout, color_layout, patente_layout, estado_layout, aire_layout, musica_layout;

    EditText nombre, apellido, email, password;
    TextView modelo_label, color_label, patente_label, estado_label, aire_label, musica_label;
    Button confirm_signup_button;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        load_layout_elements();
        configure_layout_elements();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void load_layout_elements() {
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        modelo_layout = (LinearLayout) findViewById(R.id.modelo_layout);
        modelo_label = (TextView) findViewById(R.id.modelo_label);
        color_layout = (LinearLayout) findViewById(R.id.color_layout);
        color_label = (TextView) findViewById(R.id.color_label);
        patente_layout = (LinearLayout) findViewById(R.id.patente_layout);
        patente_label = (TextView) findViewById(R.id.patente_label);
        estado_layout = (LinearLayout) findViewById(R.id.estado_layout);
        estado_label = (TextView) findViewById(R.id.estado_label);
        aire_layout = (LinearLayout) findViewById(R.id.aire_layout);
        aire_label = (TextView) findViewById(R.id.aire_label);
        musica_layout = (LinearLayout) findViewById(R.id.musica_layout);
        musica_label = (TextView) findViewById(R.id.musica_label);
        confirm_signup_button = (Button) findViewById(R.id.confirm_signup_button);
    }

    private void configure_layout_elements() {
        modelo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_modelo();
            }
        });
        color_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_color();
            }
        });
        patente_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_patente();
            }
        });
        estado_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_estado();
            }
        });
        aire_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_aire();
            }
        });
        musica_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup_musica();
            }
        });
        confirm_signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    private void popup_modelo() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Marca");

        final CharSequence modelo_previo = modelo;

        alert.setSingleChoiceItems(modelos, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                modelo = modelos[i];
                modelo_label.setText(modelo);
            }
        });

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                modelo = modelo_previo;
                modelo_label.setText(modelo);
            }
        });

        alert.show();
    }

    private void popup_color() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Color del vehículo");

        final CharSequence color_previo = color;
        alert.setSingleChoiceItems(colores, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                color = colores[i];
                color_label.setText(color);
            }
        });

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                color = color_previo;
                color_label.setText(color);
            }
        });

        alert.show();
    }

    private void popup_patente() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Patente");
        final EditText input = new EditText(this);
        alert.setView(input);

        final String patente_previa = patente;
        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                patente = input.getText().toString();
                patente_label.setText(patente);
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                patente = patente_previa;
                patente_label.setText(patente);
            }
        });
        alert.show();
    }

    private void popup_estado() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Estado del vehículo");

        final CharSequence estado_previo = estado;
        alert.setSingleChoiceItems(estados, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                estado = estados[i];
                estado_label.setText(estado);
            }
        });

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                estado = estado_previo;
                estado_label.setText(estado);
            }
        });

        alert.show();
    }

    private void popup_aire() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tiene aire acondicionado");

        final CharSequence aire_previo = aire;
        alert.setSingleChoiceItems(aire_options, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aire = aire_options[i];
                aire_label.setText(aire);
            }
        });

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Nada
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                aire = aire_previo;
                aire_label.setText(aire);
            }
        });

        alert.show();
    }

    private void popup_musica() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Música habitual");

        final CharSequence musica_previa = musica;
        alert.setSingleChoiceItems(musicas, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                musica = musicas[i];
            }
        });

        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                musica_label.setText(musica);
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                musica = musica_previa;
            }
        });

        alert.show();
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

        // Chequeo si se indicó el modelo.
        if (modelo == null) {
            modelo_label.setError("Falta indicar el modelo");
            focusView = modelo_label;
            cancel = true;
        }

        // Chequeo si se indicó el color del vehículo.
        if (color == null) {
            color_label.setError("Falta indicar el color");
            focusView = color_label;
            cancel = true;
        }

        // Chequeo si se indicó la patente.
        if (patente == null) {
            patente_label.setError("Falta indicar la patente");
            focusView = patente_label;
            cancel = true;
        } else if (!isPatenteValid(patente)) {
            patente_label.setError("La patente es inválida");
            focusView = patente_label;
            cancel = true;
        }

        // Chequeo si se indicó el estado del vehículo.
        if (estado == null) {
            estado_label.setError("Falta indicar el estado");
            focusView = estado_label;
            cancel = true;
        }

        // Chequeo si se indicó si el vehiculo tiene aire acondicionado.
        if (aire == null) {
            aire_label.setError("Falta indicar si dispone de aire acondicionado.");
            focusView = aire_label;
            cancel = true;
        }

        // Chequeo si se indicó la musica que se escucha.
        if (musica == null) {
            musica_label.setError("Falta indicar la musica que se escucha.");
            focusView = musica_label;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        if (!cancel) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(_email, _password);
            mAuth.signInWithEmailAndPassword(_email, _password);
            user = mAuth.getCurrentUser();
            String newName = nombre.getText().toString() + " " + apellido.getText().toString();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();
            user.updateProfile(profileUpdates);
            sendSignUpRequest();
            //TODO cambiar la actividad
//            startActivity(new Intent(DriverSignUpActivity.this, MainActivity.class));
        }
    }

    private void sendSignUpRequest() {
        try {
            focusView = email;
            final JSONObject driver_json = new JSONObject();
            final JSONObject car_json = new JSONObject();
            driver_json.put("type","driver");
            driver_json.put("name",nombre.getText().toString());
            driver_json.put("last_name",apellido.getText().toString());
            driver_json.put("id",email.getText().toString());
            driver_json.put("facebook_acount","Darius Maitia");
            car_json.put("model",modelo.toString());
            car_json.put("color",color.toString());
            car_json.put("patent",patente);
            car_json.put("year","2010");//TODO HARCODEADO, cambiar esto
            car_json.put("state",estado.toString());
            car_json.put("air_conditioner",aire.toString());
            car_json.put("music",musica.toString());
            driver_json.put("car",car_json);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url_user, driver_json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Respuesta: ", response.toString());
                        if (response.toString() == "POST user OK"){
                            startActivity(new Intent(DriverSignUpActivity.this, MainActivity.class));
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error: ", error.getMessage());
                }
            });
            requestQueue.add(postRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isPatenteValid(String patente) {
        return (patente.length() >= 7);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
