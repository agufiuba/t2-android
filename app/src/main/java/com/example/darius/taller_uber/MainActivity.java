package com.example.darius.taller_uber;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Solicitud de viaje:
 * Estado 0: la aplicación muestra el mapa de buenos aires con un botón de inicio
 * al Estado 1.
 * Estado 1: el usuario indica la posición de recogida. Esta posición puede ser
 * indicada mediante el ingreso de la dirección en campo de ingreso de texto que surgirá.
 * También puede droppear un pin sobre una posicion en el mapa.
 * También puede apretar un botón que dropea el pin en la posicion actual del usuario.
 * Estado 2: el usuario indica la posición de destino por texto o por droppeo de pin.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, URL_local, USER_TYPE {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";


    private enum ESTADO {ESTADO0, ESTADO1, ESTADO2, ESTADO3, ESTADO4;}

    //ESTADO0: cuando el usuario todavía no inició el proceso para pedir viaje
    //ESTADO1: cuando el usuario puede indicar la posicion de recogida
    //ESTADO2: cuando el usuario puede indicar el destino del trayecto

    /**Comun**/
    private String client_type;
    private ESTADO estado;
    private GoogleMap mMap;
    private Marker user_location_marker = null;
    private Marker originMarker = null;
    private Marker destinationMarker = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseUser user;
    private Button stateButton;
    private Map<Polyline, RouteDetails> routes;
        /**Firebase Database**/
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    PlaceAutocompleteFragment autocompleteFragment;
        /**Location**/
    boolean mRequestingLocationUpdates = false;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    /**Passenger**/
    private CardView search_card_view;
    private LinearLayout route_specs;
    private ScrollView car_specs;
    private String selected_driver;
    private TextView distancia, duracion, costo;
    private Map<String, Marker> drivers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client_type = getIntent().getStringExtra("Client Type");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stateButton = (Button) findViewById(R.id.stateButton);
        car_specs = (ScrollView) findViewById(R.id.car_specs);

        search_card_view = (CardView) findViewById(R.id.search_card_view);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        routes = new HashMap<Polyline, RouteDetails>();
        route_specs = (LinearLayout) findViewById(R.id.routeSpecs);

        distancia = (TextView) findViewById(R.id.distancia);
        duracion = (TextView) findViewById(R.id.duracion);
        costo = (TextView) findViewById(R.id.costo);
        search_card_view = (CardView) findViewById(R.id.search_card_view);

        mapFragment.getMapAsync(this);
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        //Location
        configure_location_settings();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    draw_client_position(location);
                    push_user_position_to_database();
                }
            }
        };
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (client_type.equals(USER_TYPE.PASSENGER)) {
            startEstado0();

//            prueba_listener();
        } else {
            startEstado0_Driver();
        }
    }

    private void prueba_listener() {
        final String TAG = "prueba_listener";
        myRef = database.getReferenceFromUrl("https://t2t2-9753f.firebaseio.com/");
        ValueEventListener posListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    Log.d(TAG, "dataChanged " + id + " " + child.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        };
        myRef.addValueEventListener(posListener);
    }

    @Override
    public void onBackPressed() {
        if (client_type == USER_TYPE.PASSENGER) {
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

    private void configureAutocompleteFragment() {
        switch (estado) {
            case ESTADO1:
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelection(originMarker));
                break;
            case ESTADO2:
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelection(destinationMarker));
                break;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Me posiciono sobre Buenos Aires
        LatLng bsas = new LatLng(-34.599722, -58.381944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bsas, 12));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.perfil) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.chat) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startEstado0() {
        this.estado = ESTADO.ESTADO0;
        route_specs.setVisibility(View.INVISIBLE);
        search_card_view.setVisibility(View.INVISIBLE);
        stateButton.setText("Indicar Recogida");
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEstado1();
            }
        });
    }

    public void startEstado0_Driver() {
        route_specs.setVisibility(View.GONE);
        search_card_view.setVisibility(View.GONE);
        stateButton.setText("Estoy Disponible");
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disponibilizar_chofer();
            }
        });
    }

    private void disponibilizar_chofer() {
        String url = url_drivers;
        class onDisponibleDriverRequestSuccess extends RequestHandler {
            @Override
            public void run() {
                startEstado1_Driver();
            }
        }

        class onDisponibleDriverRequestFailure extends RequestHandler {
            @Override
            public void run() {
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

    private void startEstado1_Driver() {

        //TODO recibir notificaciones mediante Firebase
        //en caso de recibir notificacion... marcar posicion de recogida
        ////show_pickup_coords(latLng);
        startEstado2_Driver();
    }

    private void startEstado2_Driver() {
        //Dar de baja como chofer disponible
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

    private void displayAvailableDrivers(final JSONObject app_response) {
        try {
            JSONArray json1;
            JSONObject json2;
            String id = null;
            String pos = null;
            json1 = app_response.getJSONArray("drivers");
            for (int i = 0; i < json1.length(); i++) {
                json2 = json1.getJSONObject(i);
                id = json2.getString("id");
                pos = json2.getString("pos");
                if (!drivers.containsKey(id)) {
                    String lat = pos.substring(7, 18);
                    String lng = pos.substring(25, 36);
                    LatLng latLng = new LatLng(
                            Double.parseDouble(lat),
                            Double.parseDouble(lng));
                    Marker driverMarker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.peer_location)));
                    driverMarker.setTag(id);
                    drivers.put(id, driverMarker);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void on_database_update() {
        final String TAG = "DATABASE_UPDATE";
        myRef = database.getReferenceFromUrl("https://t2t2-9753f.firebaseio.com/");
        ValueEventListener posListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "new update.");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    Log.d(TAG, "ID: " + id + " LAT/LNG: " + child.getValue(String.class));
                    if (drivers.containsKey(id)) {
                        show_driver_position(id, child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        };
        myRef.addValueEventListener(posListener);
    }

    /**
     * show_driver_position
     *
     * @param key:   userID de la database
     * @param value: valor de la posicion asociada.
     *               Ej: lat/lng: (-34.6231222,-58.3836347)
     */
    private void show_driver_position(String key, String value) {
        value = value.substring(10, value.length() - 1);
        LatLng latLng = new LatLng(
                Double.parseDouble(value.substring(0, value.indexOf(","))),
                Double.parseDouble(value.substring(value.indexOf(",") + 1, 21)));
        if (drivers.containsKey(key)) {
            drivers.get(key).setPosition(latLng);
        } else {
            Marker driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.peer_location)));
            drivers.put(key, driverMarker);
        }
    }

    private void showRoadDetails(RouteDetails details) {

        distancia.setText(details.distancia);
        duracion.setText(details.duracion);
        costo.setText(details.costo);
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        route_specs.setAnimation(slide_up);
        route_specs.setVisibility(View.VISIBLE);
    }


    /**
     * drawRoute
     * Dibuja la ruta codificada recibida del app server. Devuelve la id de la ruta.
     *
     * @param encodedPath: codigo de camino recibido del app server
     * @return polyLine.ID
     */
    private Polyline drawRoute(String encodedPath) {
        List<LatLng> list = PolyUtil.decode(encodedPath);
        PolylineOptions ruta = new PolylineOptions();
        ruta.addAll(list);
        Polyline polyline = mMap.addPolyline(ruta);
        polyline.setClickable(true);
        return polyline;
    }

    private class RouteDetails {

        RouteDetails(String distancia, String duracion, String costo) {
            this.distancia = distancia;
            this.duracion = duracion;
            this.costo = costo;
        }

        public String distancia;
        public String duracion;
        public String costo;
    }

    public void configure_location_settings() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = new LocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public void draw_client_position(Location location) {
        if (location != null) {
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            if (user_location_marker == null) {
                user_location_marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location)));
            } else {
                user_location_marker.setPosition(pos);
            }
            // Logic to handle location object
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }

    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void startLocationUpdates() {
        boolean primero = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED;
        boolean segundo = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED;
        if (primero && segundo) {
            final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
            final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 100;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            onResume();

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String TAG = "startLocationUpdates";
            Log.d(TAG, "GPS_permission_update");
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, null);

    }

    private void push_user_position_to_database() {
        DatabaseReference ref = database.getReference(this.user.getUid());
        ref.setValue(user_location_marker.getPosition().toString());
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (drivers.containsValue(marker)) {
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
//            String url = url_user + "/cc@cc.com";
            Comunicador comunicador = new Comunicador(this.user, this);
            comunicador.requestAuthenticated(new onCarSpecsRequestSuccess(),
                    new onCarSpecsRequestFailure(), url,
                    new JSONObject(),Request.Method.GET);

        }

        return false;
    }

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

            String name = user_info.getString("name") + " " + user_info.getString("last_name");
            nombre.setText(name);
            color.setText(carSpecs.getString("color"));
            patente.setText(carSpecs.getString("patent"));
            anio.setText(carSpecs.getString("year"));
            estado.setText(carSpecs.getString("state"));
            aire.setText(carSpecs.getString("air_conditioner"));
            musica.setText(carSpecs.getString("music"));
            modelo.setText(carSpecs.getString("model"));

//            /** Harcoding!! **/
//            nombre.setText("Fulano");
//            color.setText("Rojo");
//            patente.setText("AA2000");
//            anio.setText("2018");
//            estado.setText("Nuevo");
//            aire.setText("Si");
//            musica.setText("Clasica");
//            modelo.setText("S10");

            car_specs.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
