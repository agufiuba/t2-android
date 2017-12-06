package com.example.darius.taller_uber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

/**
 * ESTADO0: cuando el chofer todavía está libre y no solicitó que se le adjudique un viaje.
 * ESTADO1: cuando el chofer solicitó un viaje al servidor y está a la espera de instrucciones.
 * ESTADO2: cuando el chofer recibió instrucciones (i.e. un trayecto con un cliente)
 * ESTADO3: cuando el chofer recogió al cliente
 * ESTADO0: vuelta al estado 0.
 */
public class DriverActivity extends MainActivity {
    private String TAG = "DRIVER_ACTIVITY";
    private String passengerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        on_message_received();
        startEstado0();
    }

    private void on_message_received() {
        this.mDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Message received");
                passengerID = intent.getExtras().getString("passengerID");
                LatLng from = processCoordinates(intent.getExtras().getString("from"));
                LatLng to = processCoordinates(intent.getExtras().getString("to"));
                if (originMarker == null) {
                    originMarker = mMap.addMarker(new MarkerOptions()
                            .position(from)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.recogida_pin)));
                }
                if (destinationMarker == null) {
                    destinationMarker = mMap.addMarker(new MarkerOptions()
                            .position(to)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destino_pin)));
                }
                setDb_chatID(passengerID);
                show_passenger_location(passengerID);
                startEstado2();
            }
        };

    }

    /**
     * setDB_chatID
     * Un chat se establece entre un chofer y un pasajero.
     * Por convención, estableceremos que la ID del chat en la database de firebase
     * será la suma de ID del chofer + ID del pasajero.
     */
    @Override
    final protected void setDb_chatID(String passengerID){
        this.db_chatID = this.user.getUid() + passengerID;
    }

    private void show_passenger_location(String passengerID){
        if(!this.peers.containsKey(passengerID)){
            LatLng latLng = new LatLng(0,0);
            Marker passengerMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.peer_location)));
            peers.put(passengerID,passengerMarker);
        }
        on_location_database_update();
    }

    public void startEstado0() {
        this.estado = ESTADO.ESTADO0;
        Log.d(TAG,"estado 0. Pantalla de espera del chofer con boton para indicar que está disponible");
        stateButton.setText("Estoy Disponible");
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disponibilizar_chofer();
            }
        });
    }

    private void startEstado1() {
        this.estado = ESTADO.ESTADO1;
        Log.d(TAG,"estado 1");
        stateButton.setText("Esperando Viaje...");
        stateButton.setClickable(false);
        //TODO: mostrar pantalla de espera
    }

    private void startEstado2() {
        this.estado = ESTADO.ESTADO2;
        Log.d(TAG, "estado 2");
        stateButton.setText("Viaje Realizado");
        stateButton.setClickable(true);
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEstado0();
            }
        });
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
