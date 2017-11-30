package com.example.darius.taller_uber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverSignUpActivity extends AppCompatActivity implements URL_local {

    private View focusView = null;

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    final String[] modelos = {"Ford Fiesta", "Chevrolet S10",
        "Toyota Hilux", "Fiat Palio", "Renault Scenic"};

    final String[] colores = {"Negro", "Amarillo", "Azul", "Rojo",
        "Gris", "Blanco", "Verde"};
    final String[] estados = {"Excelente", "Bueno", "Destartalado", "PicaPiedra"};
    final String[] aire_options = {"Sí", "No"};
    final String[] musicas = {"Clasica", "Jazz", "Tango", "Rock", "Folklore", "Pop"};
//    private String[] modelos;
//    private String[] colores;
//    private String[] estados;
//    private String[] aire_options;
//    private String[] musicas;

    private String modelo = null;
    private String color = null;
    private String estado = null;
    private String aire = null;
    private String patente = null;
    private String musica = null;
    private LinearLayout modelo_layout, color_layout, patente_layout, estado_layout, aire_layout, musica_layout;
    private TextInputLayout pwLayout;
    private EditText nombre, apellido, email, password;
    private TextView modelo_label, color_label, patente_label, estado_label, aire_label, musica_label;
    private Button confirm_signup_button;
    private Comunicador comunicador;

    private boolean fb_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        load_layout_elements();
        AccessToken at = AccessToken.getCurrentAccessToken();
        if (at != null) {
            fb_register = true;
            load_registration_through_facebook();
        }

        configure_layout_elements();
        comunicador = new Comunicador(user, this);
//        requestCharSequences();
    }

    private void load_layout_elements() {
        nombre = (EditText) findViewById(R.id.nombre);
        apellido = (EditText) findViewById(R.id.apellido);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        pwLayout = (TextInputLayout) findViewById(R.id.pw_layout);
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

    /**
     * Fija los valores de email, nombre y apellido extraidos de facebook.
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

    private class onRequestSequencesSuccess extends RequestHandler {
        @Override
        public void run() {}
    }

    private class onRequestSequencesError extends RequestHandler {
        @Override
        public void run(){}
    }

//    /**
//     * requestCharSequences
//     * Solicita al app server los parametros de las opciones modelos,
//     * colores, estados, aire y musicas para que ingrese el usuario que se registra.
//     */
//    private void requestCharSequences(){
//        String url_modelos = url + "parameters/car/model";
//        String url_colores = url + "parameters/car/colour";
//        String url_estados = url + "parameters/car/state";
//        String url_aire = url + "parameters/car/air_conditioner";
//        String url_musicas = url + "parameters/car/music";
//
//        final JSONObject jsonObject = new JSONObject();
//        onRequestSequencesSuccess success = new onRequestSequencesSuccess();
//        onRequestSequencesError error = new onRequestSequencesError();
//        try {
//            jsonObject.put("nana","jaja");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        comunicador.requestFree(success, error,url_modelos,jsonObject,Request.Method.GET);
//        modelos = setFromJson(comunicador.getAnswerJSON());
//        comunicador.requestFree(success, error,url_colores,jsonObject,Request.Method.GET);
//        colores = setFromJson(comunicador.getAnswerJSON());
//        comunicador.requestFree(success, error,url_estados,jsonObject,Request.Method.GET);
//        estados = setFromJson(comunicador.getAnswerJSON());
//        comunicador.requestFree(success, error,url_aire,jsonObject,Request.Method.GET);
//        aire_options = setFromJson(comunicador.getAnswerJSON());
//        comunicador.requestFree(success, error,url_musicas,jsonObject,Request.Method.GET);
//        musicas = setFromJson(comunicador.getAnswerJSON());
//    }

    private String[] setFromJson(JSONObject jsonObject){
        String[] options;
        JSONArray arr;
        List<String> _options = new ArrayList<String>();
        try {
            arr = jsonObject.getJSONArray("parameters");

            for(int i = 0; i < arr.length(); i++){
                _options.add(arr.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        options =(String[])_options.toArray();
        return options;
    }

    private void popup_modelo() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Marca");

        final String modelo_previo = modelo;

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

        final String color_previo = color;
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

        final String estado_previo = estado;
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

        final String aire_previo = aire;
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

        final String musica_previa = musica;
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
        final String _email = email.getText().toString();
        final String _password = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!fb_register) {
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

            if (TextUtils.isEmpty(nombre.getText().toString())){
                nombre.setError("No tenes nombre?");
                focusView = nombre;
                cancel = true;
            }

            if (TextUtils.isEmpty(apellido.getText().toString())){
                apellido.setError("Decime tu apellido ameo");
                focusView = apellido;
                cancel = true;
            }
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
            if (!fb_register) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mAuth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                user = mAuth.getCurrentUser();
                                String newName = nombre.getText().toString() + " " + apellido.getText().toString();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(newName)
                                        .build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendSignUpRequest();
                                    }
                                });
                            }
                        });
                    }
                });
            } else {
                sendSignUpRequest();
            }
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
            driver_json.put("mail",email.getText().toString());
            car_json.put("model",modelo.toString());
            car_json.put("color",color.toString());
            car_json.put("patent",patente);
            car_json.put("year","2010");//TODO HARCODEADO, cambiar esto
            car_json.put("state",estado.toString());
            car_json.put("air_conditioner",aire.toString());
            car_json.put("music",musica.toString());
            driver_json.put("car",car_json);

            class onSuccess extends RequestHandler {
                @Override
                public void run() {
                    Intent intent = new Intent(DriverSignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            class onError extends RequestHandler {
                @Override
                public void run(){
                    //TODO mostrar mensaje de error y volver a pantalla principal
                    startActivity(new Intent(DriverSignUpActivity.this, LoginActivity.class));
                }
            }

            comunicador.requestFree(new onSuccess(), new onError(), url_user, driver_json, Request.Method.POST);

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
