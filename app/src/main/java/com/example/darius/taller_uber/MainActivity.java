package com.example.darius.taller_uber;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.Place;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, URL_local {

    private enum Estados {ESTADO0, ESTADO1, ESTADO2, ESTADO3}
    //ESTADO0: cuando el usuario todavía no inició el proceso para pedir viaje
    //ESTADO1: cuando el usuario puede indicar la posicion de recogida
    //ESTADO2: cuando el usuario puede indicar el destino del trayecto

    private Estados estado;
    private GoogleMap mMap;
    private Marker originMarker = null;
    private Marker destinationMarker = null;
    private CardView search_card_view;
    private Button buttonNext;
    private FirebaseUser user;
    private RequestQueue queue;
    private LinearLayout routeSpecs;
    private TextView distancia, duracion, costo;

    private Map<Polyline, RouteDetails> routes;

    PlaceAutocompleteFragment   autocompleteFragment;
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonNext = (Button) findViewById(R.id.nextButton);

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
        routeSpecs = (LinearLayout) findViewById(R.id.routeSpecs);
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        routeSpecs.setAnimation(slide_up);
        distancia = (TextView) findViewById(R.id.distancia);
        duracion = (TextView) findViewById(R.id.duracion);
        costo = (TextView) findViewById(R.id.costo);
        search_card_view = (CardView) findViewById(R.id.search_card_view);

        mapFragment.getMapAsync(this);
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.queue = Volley.newRequestQueue(this);

        startEstado0();
    }

    @Override
    public void onBackPressed() {
        switch (estado){
            case ESTADO3:
                for (Map.Entry<Polyline, RouteDetails> entry : routes.entrySet()){
                    entry.getKey().remove();
                }
                routes.clear();
                routeSpecs.setVisibility(View.INVISIBLE);
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

    private void configureAutocompleteFragment(){
        switch (estado) {
            case ESTADO1:
                autocompleteFragment.setOnPlaceSelectedListener( new PlaceSelection(originMarker));
                break;
            case ESTADO2:
                autocompleteFragment.setOnPlaceSelectedListener( new PlaceSelection(destinationMarker));
                break;
        }

    }

    private void setMarker(){
        switch (estado){
            case ESTADO1:
                originMarker.setPosition(place.getLatLng());
                break;
            case ESTADO2:
                destinationMarker.setPosition(place.getLatLng());
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Me posiciono sobre Buenos Aires
        LatLng bsas = new LatLng(-34.599722, -58.381944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bsas,12));
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener(){
            public void onPolylineClick(Polyline polyline) {
                showRoadDetails(polyline);
            }
        });
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

        } else if (id == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startEstado0(){
        this.estado = Estados.ESTADO0;
        routeSpecs.setVisibility(View.INVISIBLE);
        search_card_view.setVisibility(View.INVISIBLE);
        buttonNext.setText("Indicar Recogida");
        buttonNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
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
    private void startEstado1(){
        estado = Estados.ESTADO1;
        routeSpecs.setVisibility(View.INVISIBLE);
        if (originMarker == null){
            originMarker = mMap.addMarker(new MarkerOptions()
                .position(mMap.getCameraPosition().target)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.recogida_pin)));
        }
        originMarker.setDraggable(true);
        configureAutocompleteFragment();
        buttonNext.setVisibility(View.GONE);
        buttonNext.setText("Indicar Destino");
        buttonNext.setVisibility(View.VISIBLE);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEstado2();
            }
        });
        Animation slide_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
        search_card_view.setAnimation(slide_left);
        search_card_view.setVisibility(View.VISIBLE);
    }

    public void startEstado2(){
        estado = Estados.ESTADO2;
        routeSpecs.setVisibility(View.INVISIBLE);
        originMarker.setDraggable(false);
        if (destinationMarker == null){
            destinationMarker = mMap.addMarker(new MarkerOptions()
                .position(mMap.getCameraPosition().target)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destino_pin)));
        }
        destinationMarker.setDraggable(true);
        autocompleteFragment.setText("");
        configureAutocompleteFragment();
        buttonNext.setVisibility(View.GONE);
        buttonNext.setText("Previsualizar Viaje");
        buttonNext.setVisibility(View.VISIBLE);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRoute();
            }
        });
    }

    /**
     * requestRoute
     * El usuario ya tiene determinado de adonde a adonde ir
     * Le hace el request al servidor para recibir los caminos y los precios
     * e inicia el estado 3.
     */
    public void requestRoute(){
        estado = Estados.ESTADO3;
        destinationMarker.setDraggable(false);
        search_card_view.setVisibility(View.GONE);
        Comunicador comunicador = new Comunicador(user,this);
        JSONObject from_to = new JSONObject();
        String from = originMarker.getPosition().toString();
        from = from.substring(10,from.length()-1);
        String to = destinationMarker.getPosition().toString();
        to = to.substring(10,to.length()-1);
        try {
            from_to.put("from",from);
            from_to.put("to",to);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        class onRequestSuccess extends RequestHandler{
            @Override
            public void run(){
                startEstado3(jsonRecv);
            }
        }

        class onRequestFailure extends RequestHandler{
            @Override
            public void run(){
                //TODO mostrar mensaje de error
                startEstado0();
            }
        }

        comunicador.requestAuthenticated(new onRequestSuccess(),
            new onRequestFailure(), url_trip, from_to,Request.Method.POST);
    }

    /**
     * startEstado3
     * Habiendose recibido el json con la ruta y los detalles de la ruta,
     * dibujamos en el mapa el trayecto y exponemos los precios, duracion, distancia.
     * El boton pasa a ser boton para solicitar el viaje.
     * @param routeDetails: respuesta al request hecho al APP en requestRoute
     */
    public void startEstado3(JSONObject routeDetails){
        try {
            this.estado = Estados.ESTADO3;
            buttonNext.setText("Solicitar Viaje");
            routes.put(drawRoute(routeDetails.getString("points")),
                new RouteDetails(routeDetails.getString("distance"),
                    routeDetails.getString("time"),
                    routeDetails.getString("cost")));
            /*JSONArray jsonArray = jsonObject.getJSONArray("").getJSONArray(0);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                routes.put(drawRoute(json.getString("polyline")),
                    new RouteDetails(json.getString("km"),
                        json.getString("time"),
                        json.getString("costo")));

            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startEstado3prueba(){
        //TODO borrar esto
        this.estado = Estados.ESTADO3;
        String pllne = "dihrEtnjcJnC??YMaHEeGKyJ?wA{KLoFJu@JcBD}@BqMHmER{EF{BH}BB_FL_LVkAF[EsAg@s@Gg@@}BHe@Jy@^WHQDkFD_FJ{MZcCFaBRyAHkCL_ABcEFyA?aEIkESo@CgCAmBIeBOyAUoAWuEqAaBi@kCiAoEqBu@Qe@I}@I{@@s@FaAT_A`@{@j@aAfAk@x@m@dAqBtEkDdIeBlE_BlE}AdEqApCc@|@cAhBu@lAqAjBoCtD_BdBWPuBpAcBpAeA`AW\\k@~@i@jAw@`C]jBc@tDYzAUt@m@|AqBdF]t@kAdCcFvLkC`GiBrD_@j@oAxBwFpJ_@Ng@NQB_@A_@GWK{@k@QGYGaAfCUt@Ir@@z@BXLn@Zr@T^|BlBvD~CjA|@HJDVHFlAlArH`HLHZj@f@fAVZNJp@RPJxAfAHFXHpH|Gn@l@@J\\p@j@lA?B@J@LLVRLV@PGh@LhAb@fGxFHTPTd@d@vF`FxCpCjGzFNTJV@JCNGZEd@H~@JRPVJFVDR@@E";
        routes.put(drawRoute(pllne),new RouteDetails("5km","30min","100$"));

        routeSpecs.setVisibility(View.VISIBLE);
        buttonNext.setText("Solicitar Viaje");

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO logica de solicitud de viaje
            }
        });
    }

    private void showRoadDetails(Polyline polyline){
        RouteDetails details = routes.get(polyline);
        distancia.setText(details.distancia);
        duracion.setText(details.duracion);
        costo.setText(details.costo);
        routeSpecs.setVisibility(View.VISIBLE);
    }

    /**
     * drawRoute
     * Dibuja la ruta codificada recibida del app server. Devuelve la id de la ruta.
     * @param encodedPath: codigo de camino recibido del app server
     * @return polyLine.ID
     */
    private Polyline drawRoute(String encodedPath){
        List<LatLng> list = PolyUtil.decode(encodedPath);
        PolylineOptions ruta = new PolylineOptions();
        ruta.addAll(list);
        Polyline polyline = mMap.addPolyline(ruta);
        polyline.setClickable(true);
        return polyline;
    }

    private class RouteDetails{

        RouteDetails(String distancia, String duracion, String costo){
            this.distancia = distancia;
            this.duracion = duracion;
            this.costo = costo;
        }

        public String distancia;
        public String duracion;
        public String costo;
    }
}
