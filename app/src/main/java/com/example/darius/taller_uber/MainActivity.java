package com.example.darius.taller_uber;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, URL_local, USER_TYPE {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";

    protected enum ESTADO {ESTADO0, ESTADO1, ESTADO2, ESTADO3, ESTADO4;}
    protected enum DBREFERENCES {localizations, chats};
    /**Comun**/
    protected String client_type;
    protected ESTADO estado;
    protected GoogleMap mMap;
    protected Marker user_location_marker = null;
    protected Marker originMarker = null;
    protected Marker destinationMarker = null;
    protected FusedLocationProviderClient mFusedLocationClient;
    protected FirebaseUser user;
    protected Button stateButton;
    protected Map<Polyline, RouteDetails> routes;
    protected Map<String, Marker> peers = new HashMap<>();

        /**Firebase Database**/
    protected FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected DatabaseReference dbReference;
    protected PlaceAutocompleteFragment autocompleteFragment;
        /**Location**/
    protected boolean mRequestingLocationUpdates = false;
    protected LocationCallback mLocationCallback;
    protected LocationRequest mLocationRequest;
        /**Messaging Service**/
    protected BroadcastReceiver mDataReceiver;
    protected BroadcastReceiver mMessageReceiver;
    protected BroadcastReceiver mNotificationReceiver;

//    protected android.support.v4.app.FragmentManager manager;
//    protected android.support.v4.app.FragmentTransaction transaction;
//    protected ChatFragment chat = new ChatFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client_type = getIntent().getStringExtra("Client Type");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.stateButton = (Button) findViewById(R.id.stateButton);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        manager = getSupportFragmentManager();
//        transaction = manager.beginTransaction();
//        transaction.add(R.id.chat, chat,"Chat");
//        transaction.addToBackStack(null);
//        transaction.commit();

        verify_gps_settings();
        this.routes = new HashMap<>();
        dbReference = database.getReferenc                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          eFromUrl("https://t2t2-9753f.firebaseio.com/");
    }

    @Override
    protected void onStart() {
        super.onStart();
        listen_to_messages();
    }

    protected void listen_to_messages(){
        LocalBroadcastManager.getInstance(this).registerReceiver((mDataReceiver),
                new IntentFilter(FCM_MESSAGE_TYPE.FCM_DATA.name())
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter(FCM_MESSAGE_TYPE.FCM_MESSAGE.name())
        );
        LocalBroadcastManager.getInstance(this).registerReceiver((mNotificationReceiver),
                new IntentFilter(FCM_MESSAGE_TYPE.FCM_NOTIFICATION.name())
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop_listening_to_messages();
    }

    protected void stop_listening_to_messages(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationReceiver);
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

    /**
     * onNavigationItemSelected
     * Dispara la actividad o el fragmento asociado a uno de los ítems
     * del panel lateral.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.perfil) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.chat) {
//            transaction.replace(R.id.chat, chat, "Chat");
//            transaction.addToBackStack(null);
//            transaction.commit();
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

    /**
     * drawRoute
     * Dibuja la ruta codificada recibida del app server. Devuelve la id de la ruta.
     *
     * @param encodedPath: codigo de camino recibido del app server
     * @return polyLine.ID
     */
    protected Polyline drawRoute(String encodedPath) {
        List<LatLng> list = PolyUtil.decode(encodedPath);
        PolylineOptions ruta = new PolylineOptions();
        ruta.addAll(list);
        Polyline polyline = mMap.addPolyline(ruta);
        polyline.setClickable(true);
        return polyline;
    }


    protected class RouteDetails {
        /**
         * Detalles de la ruta
         * @param distancia: distancia de la ruta
         * @param duracion: duracion del trayecto
         * @param costo: costo del viaje
         */
        RouteDetails(String distancia, String duracion, String costo) {
            this.distancia = distancia;
            this.duracion = duracion;
            this.costo = costo;
        }

        public String distancia;
        public String duracion;
        public String costo;
    }

    /**
     * verify_gps_settings.
     * Verifica que la configuracion del servicio de geolocalización
     * del celular necesaria para la realización de esta tarea.
     * En caso de poder acceder a dicha configuracion, llama a
     * attempt_location_updates que solicita al usuario que adapte la configuracion
     * necesaria de forma tal de empezar a obtener la posicion del cliente.
     */
    protected void verify_gps_settings() {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.mLocationRequest = new LocationRequest();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                attempt_location_updates();
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

    /**
     * attempt_location_updates
     * Solicita al usuario que active el servicio de geolocalizacion.
     * En caso de exito inicia el proceso de escucha para recibir permanentemente
     * actualizaciones de posición. En caso contrario, retorna.
     * Invicado en verify_gps_settings.
     */
    private void attempt_location_updates() {
        final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 99;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            onResume();

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String TAG = "START_LOCATION_UPDATES";
            Log.d(TAG, "GPS_permission_update");
            return;
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    draw_client_position(location);
                    push_user_position_to_database();
                }
            }
        };

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, null);

    }

    /**
     * draw_client_position
     * Dibuja en el mapa con un marker azul la posición del cliente.
     * @param location: localizacion del cliente
     */
    protected void draw_client_position(Location location) {
        if (location != null) {
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            if (user_location_marker == null) {
                user_location_marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location)));
            } else {
                user_location_marker.setPosition(pos);
            }
        }
    }

    /**
     * stop_location_updates
     * Finaliza la escucha de posiciones del cliente.
     */
    private void stop_location_updates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * push_user_position_to_database
     * Actualiza en la database de Firebase la posicion del cliente.
     */
    private void push_user_position_to_database() {
        DatabaseReference ref = database.getReference(DBREFERENCES.localizations.name()).child(this.user.getUid());
        ref.setValue(user_location_marker.getPosition().toString());
    }

    LatLng processCoordinates(String coords){
        coords = coords.substring(10, coords.length() - 1);
        LatLng latLng = new LatLng(
                Double.parseDouble(coords.substring(0, coords.indexOf(","))),
                Double.parseDouble(coords.substring(coords.indexOf(",") + 1, 21)));
        return latLng;
    }

    /**
     * on_location_database_update
     * Actualiza las posiciones de los choferes. Esto ocurre cuando la database
     * que contiene las coordenadas de la ubicacion de los choferes se actualiza.
     */
    protected void on_location_database_update() {
        final String TAG = "LOCATION_DB_UPDATE";
        ValueEventListener posListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "new update.");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    Log.d(TAG, "ID: " + id + " LAT/LNG: " + child.getValue(String.class));
                    if (peers.containsKey(id)) {
                        show_peer_position(id, child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        };
        dbReference.child(DBREFERENCES.localizations.name()).addValueEventListener(posListener);
    }

    /**
     * show_peer_position
     * @param key:   userID de la database
     * @param value: valor de la posicion asociada.
     *               Ej: lat/lng: (-34.6231222,-58.3836347)
     */
    protected void show_peer_position(String key, String value) {
        value = value.substring(10, value.length() - 1);
        LatLng latLng = new LatLng(
                Double.parseDouble(value.substring(0, value.indexOf(","))),
                Double.parseDouble(value.substring(value.indexOf(",") + 1, 21)));
        if (peers.containsKey(key)) {
            peers.get(key).setPosition(latLng);
        } else {
            Marker driverMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.peer_location)));
            peers.put(key, driverMarker);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mRequestingLocationUpdates) {
            attempt_location_updates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop_location_updates();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

}
