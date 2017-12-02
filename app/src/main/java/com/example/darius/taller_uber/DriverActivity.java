package com.example.darius.taller_uber;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;

import org.json.JSONObject;

/**
 * ESTADO0: cuando el chofer todavía está libre y no solicitó que se le adjudique un viaje.
 * ESTADO1: cuando el chofer solicitó un viaje al servidor y está a la espera de instrucciones.
 * ESTADO2: cuando el chofer recibió instrucciones (i.e. un trayecto con un cliente)
 * ESTADO3: cuando el chofer recogió al cliente
 * ESTADO0: vuelta al estado 0.
 */
public class DriverActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startEstado0();
    }

    public void startEstado0() {
        stateButton.setText("Estoy Disponible");
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disponibilizar_chofer();
            }
        });
    }

    private void startEstado1() {

        //TODO recibir notificaciones mediante Firebase
        //en caso de recibir notificacion... marcar posicion de recogida
        ////show_pickup_coords(latLng);
        startEstado2();
    }

    private void startEstado2() {
        //Dar de baja como chofer disponible
    }

    /**
     * disponibilizar_chofer
     * Le comunica al servidor que el cliente chofer se encuentra disponible
     * para realizar una recogida.
     */
    private void disponibilizar_chofer() {
        String url = url_drivers;
        class onDisponibleDriverRequestSuccess extends RequestHandler {
            @Override
            public void run() {
                String TAG = "DISPONIBILIZAR_CHOFER";
                Log.d(TAG,"request exitoso");
                startEstado1();
            }
        }

        class onDisponibleDriverRequestFailure extends RequestHandler {
            @Override
            public void run() {
                String TAG = "DISPONIBILIZAR_CHOFER";
                Log.e(TAG,"request fallido. \nError: " + volleyError.getMessage());
                Snackbar.make(stateButton, "Error del servidor", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

        Comunicador comunicador = new Comunicador(user, this);
        comunicador.requestAuthenticated(
                new onDisponibleDriverRequestSuccess(),
                new onDisponibleDriverRequestFailure(), url,
                new JSONObject(), Request.Method.POST);
    }

}
