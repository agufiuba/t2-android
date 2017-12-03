package com.example.darius.taller_uber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by darius on 01/12/17.
 *  ESTADO0: cuando el usuario todavía no inició el proceso para pedir viaje
 *  ESTADO1: cuando el usuario puede indicar la posicion de recogida
 *  ESTADO2: cuando el usuario puede indicar el destino del trayecto
 *  ESTADO3: cuando el usuario recibe la hipotética ruta con el costo, distancia
 *           y duración asociados.
 *  ESTADO4: cuando el usuario puede elegir el chofer con el que realizar el trayecto.
 *  ESTADO5: cuando el usuario confirma el viaje y espera al chofer.
 */
public class PassengerActivity extends MainActivity implements GoogleMap.OnMarkerClickListener{

    final String TAG = "PASSENGER_ACTIVITY";
    /**Passenger**/
    private CardView search_card_view;
    private LinearLayout route_specs;
    private ScrollView car_specs;
    private String selected_driver;
    private TextView distancia, duracion, costo;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        car_specs = (ScrollView) findViewById(R.id.car_specs);
        search_card_view = (CardView) findViewById(R.id.search_card_view);
        route_specs = (LinearLayout) findViewById(R.id.routeSpecs);
        distancia = (TextView) findViewById(R.id.distancia);
        duracion = (TextView) findViewById(R.id.duracion);
        costo = (TextView) findViewById(R.id.costo);
        search_card_view = (CardView) findViewById(R.id.search_card_view);
        on_message_received();

        startEstado0();
    }

    private void on_message_received(){

        this.mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"Message received");
            }
        };
    }
    public void startEstado0() {
        this.estado = ESTADO.ESTADO0;
        Log.d(TAG,"start estado 0");

        route_specs.setVisibility(View.INVISIBLE);
        search_card_view.setVisibility(View.INVISIBLE);
        stateButton.setText("Indicar Recogida");
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Boton de " + stateButton.getText().toString() + " presionado. " +
                        "Llamado a startEstado1.");
                startEstado1();
            }
        });
    }

    /**
     * startEstado1
     * Estado 1: el usuario indica la posición de recogida. Esta posición puede ser
     * indicada mediante el ingreso de la dirección en campo de ingreso de texto que surgirá.
     * También puede droppear un pin sobre una posicion en el mapa.
     * También puede apretar un botón que dropea el pin en la posicion actual del usuario.
     */
    private void startEstado1() {
        estado = ESTADO.ESTADO1;
        route_specs.setVisibility(View.INVISIBLE);
        if (originMarker == null) {
            originMarker = mMap.addMarker(new MarkerOptions()
                    .position(mMap.getCameraPosition().target)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.recogida_pin)));
        }
        originMarker.setDraggable(true);
        configureAutocompleteFragment();
        stateButton.setVisibility(View.GONE);
        stateButton.setText("Indicar Destino");
        stateButton.setVisibility(View.VISIBLE);

        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEstado2();
            }
        });
        Animation slide_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
        search_card_view.setAnimation(slide_left);
        search_card_view.setVisibility(View.VISIBLE);
    }

    public void startEstado2() {
        estado = ESTADO.ESTADO2;
        route_specs.setVisibility(View.INVISIBLE);
        originMarker.setDraggable(false);
        if (destinationMarker == null) {
            destinationMarker = mMap.addMarker(new MarkerOptions()
                    .position(mMap.getCameraPosition().target)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destino_pin)));
        }
        destinationMarker.setDraggable(true);
        autocompleteFragment.setText("");
        configureAutocompleteFragment();
        stateButton.setVisibility(View.GONE);
        stateButton.setText("Previsualizar Viaje");
        stateButton.setVisibility(View.VISIBLE);

        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRoute();
            }
        });
    }

    /**
     * startEstado3
     * Habiendose recibido el json con la ruta y los detalles de la ruta,
     * dibujamos en el mapa el trayecto y exponemos los precios, duracion, distancia.
     * El boton pasa a ser boton para solicitar el viaje.
     *
     * @param routeDetails: respuesta al request hecho al APP en requestRoute
     */
    public void startEstado3(JSONObject routeDetails) {
        try {
            this.estado = ESTADO.ESTADO3;
            stateButton.setText("Solicitar Viaje");
            RouteDetails routeDetails1 = new RouteDetails(routeDetails.getString("distance"),
                    routeDetails.getString("time"),
                    routeDetails.getString("cost"));
            routes.put(drawRoute(routeDetails.getString("points")),
                    routeDetails1);
            showRoadDetails(routeDetails1);
            stateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startEstado4();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startEstado4() {
        this.estado = ESTADO.ESTADO4;

        String url;
        route_specs.setVisibility(View.GONE);
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            public void onPolylineClick(Polyline polyline) {
            }
        });

        class onRequestSuccess extends RequestHandler {
            @Override
            public void run() {
                displayAvailableDrivers(this.jsonRecv);
                on_database_update();
            }
        }

        class onRequestFailure extends RequestHandler {
            @Override
            public void run() {
                //TODO mostrar mensaje de error
                startEstado0();
            }
        }

        url = url_drivers + "?pos=" + originMarker.getPosition().toString().replace(" ", "%20");
        Comunicador comunicador = new Comunicador(this.user, this);
        comunicador.requestAuthenticated(new onRequestSuccess(), new onRequestFailure(), url, new JSONObject(), Request.Method.GET);
        mMap.setOnMarkerClickListener(this);
        stateButton.setText("Seleccionar Chofer");
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_driver != null) {
                    solicitar_chofer(selected_driver);
                }
            }
        });
    }

    protected void configureAutocompleteFragment() {
        switch (estado) {
            case ESTADO1:
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelection(originMarker));
                break;
            case ESTADO2:
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelection(destinationMarker));
                break;
        }
    }

    private void solicitar_chofer(String driver) {

        class onSolicitarChoferSuccess extends RequestHandler {
            @Override
            public void run() {
                notify_driver_is_comming();
            }
        }

        class onSolicitarChoferFailure extends RequestHandler {
            @Override
            public void run() {
                final String TAG = "Solicitar Chofer: ";
                Log.e(TAG, volleyError.getMessage());
            }
        }

        JSONObject params = new JSONObject();
        try {
            params.put("driverID",driver);
            params.put("from",originMarker.getPosition().toString());
            params.put("to",destinationMarker.getPosition().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Comunicador comunicador = new Comunicador(user,this);
        comunicador.requestAuthenticated(new onSolicitarChoferSuccess(),
                new onSolicitarChoferFailure(),
                url_trip_request, params, Request.Method.POST);
    }

    private void notify_driver_is_comming(){
        stateButton.setVisibility(View.GONE);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("¡El chofer está en camino!");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder1.create();
        alert.show();
    }

    /**
     * requestRoute
     * El usuario ya tiene determinado de adonde a adonde ir
     * Le hace el request al servidor para recibir los caminos y los precios
     * e inicia el estado 3.
     */
    public void requestRoute() {
        estado = ESTADO.ESTADO3;
        destinationMarker.setDraggable(false);
        search_card_view.setVisibility(View.GONE);
        Comunicador comunicador = new Comunicador(user, this);
        JSONObject from_to = new JSONObject();
        String from = originMarker.getPosition().toString();
        from = from.substring(10, from.length() - 1);
        String to = destinationMarker.getPosition().toString();
        to = to.substring(10, to.length() - 1);
        try {
            from_to.put("from", from);
            from_to.put("to", to);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        class onRequestSuccess extends RequestHandler {
            @Override
            public void run() {
                startEstado3(jsonRecv);
            }
        }

        class onRequestFailure extends RequestHandler {
            @Override
            public void run() {
                //TODO mostrar mensaje de error
                startEstado0();
            }
        }

        comunicador.requestAuthenticated(new onRequestSuccess(),
                new onRequestFailure(), url_trip, from_to, Request.Method.POST);
    }

    /**
     * displayAvailableDrivers
     * Muestra en pantalla los choferes disponibles alrededor del punto de recogida.
     * @param app_response: respuesta del app server con los id's de los choferes.
     */
    private void displayAvailableDrivers(final JSONObject app_response) {
        try {
            JSONArray json1;
            JSONObject json2;
            String id;
            String pos;
            json1 = app_response.getJSONArray("peers");
            for (int i = 0; i < json1.length(); i++) {
                json2 = json1.getJSONObject(i);
                id = json2.getString("id");
                pos = json2.getString("pos");
                if (!peers.containsKey(id)) {
                    String lat = pos.substring(7, 18);
                    String lng = pos.substring(25, 36);
                    LatLng latLng = new LatLng(
                            Double.parseDouble(lat),
                            Double.parseDouble(lng));
                    Marker driverMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.peer_location)));
                    driverMarker.setTag(id);
                    peers.put(id, driverMarker);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * showRoadDetails
     * Muestra los detalles de la ruta:
     *  - costo
     *  - duracion
     *  - distancia
     * @param details: detalles de la ruta
     */
    private void showRoadDetails(RouteDetails details) {
        distancia.setText(details.distancia);
        duracion.setText(details.duracion);
        costo.setText(details.costo);
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        route_specs.setAnimation(slide_up);
        route_specs.setVisibility(View.VISIBLE);
    }

    /**
     * onMarkerCLick
     * Accionar de la aplicacion cuando se presiona un marcador. Verifica
     * si el marcador apretado es uno de los conductores que se muestran en pantalla.
     * En caso de serlo, se le hace un request al servidor para recibir los detalles
     * del vehículo.
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (peers.containsValue(marker)) {
            class onCarSpecsRequestSuccess extends RequestHandler {
                @Override
                public void run() {
                    selected_driver = (String) marker.getTag();
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selected_peer_location));
                    show_car_specs(jsonRecv);
                }
            }

            class onCarSpecsRequestFailure extends RequestHandler {
                @Override
                public void run() {
                    final String TAG = "Car_Specs_Request:";
                    Log.e(TAG, volleyError.getMessage());
                }
            }

            String url = url_user + "/" + marker.getTag();
            Comunicador comunicador = new Comunicador(this.user, this);
            comunicador.requestAuthenticated(new onCarSpecsRequestSuccess(),
                    new onCarSpecsRequestFailure(), url,
                    new JSONObject(),Request.Method.GET);
        }

        return false;
    }

    /**
     * show_car_specs
     * Muestra en pantalla los detalles de un determinado auto.
     * @param user_info: respuesta del servidor con los detalles del chofer
     *                   y de su auto.
     */
    private void show_car_specs(JSONObject user_info) {
        try {
            JSONObject carSpecs = null;
            carSpecs = user_info.getJSONObject("car");

            TextView nombre = (TextView) findViewById(R.id.car_specs_nombre);
            TextView color = (TextView) findViewById(R.id.car_specs_color);
            TextView patente = (TextView) findViewById(R.id.car_specs_patente);
            TextView estado = (TextView) findViewById(R.id.car_specs_estado);
            TextView aire = (TextView) findViewById(R.id.car_specs_aire);
            TextView musica = (TextView) findViewById(R.id.car_specs_musica);
            TextView anio = (TextView) findViewById(R.id.car_specs_anio);
            TextView modelo = (TextView) findViewById(R.id.car_specs_modelo);

//            String name = user_info.getString("name") + " " + user_info.getString("last_name");
//            nombre.setText(name);
//            color.setText(carSpecs.getString("color"));
//            patente.setText(carSpecs.getString("patent"));
//            anio.setText(carSpecs.getString("year"));
//            estado.setText(carSpecs.getString("state"));
//            aire.setText(carSpecs.getString("air_conditioner"));
//            musica.setText(carSpecs.getString("music"));
//            modelo.setText(carSpecs.getString("model"));

            /** Harcoding!! **/
            nombre.setText("Fulano");
            color.setText("Rojo");
            patente.setText("AA2000");
            anio.setText("2018");
            estado.setText("Nuevo");
            aire.setText("Si");
            musica.setText("Clasica");
            modelo.setText("S10");

            car_specs.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
            switch (estado) {
                case ESTADO4:
                    break;
                case ESTADO3:
                    for (Map.Entry<Polyline, RouteDetails> entry : routes.entrySet()) {
                        entry.getKey().remove();
                    }
                    routes.clear();
                    route_specs.setVisibility(View.INVISIBLE);
                    startEstado2();
                    break;
                case ESTADO2:
                    destinationMarker.remove();
                    destinationMarker = null;
                    startEstado1();
                    break;
                case ESTADO1:
                    originMarker.remove();
                    originMarker = null;
                    startEstado0();
                    break;
                case ESTADO0:
                    break;
            }
    }
}
