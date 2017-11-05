package com.example.darius.taller_uber;

import android.content.Intent;
import android.location.Geocoder;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

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
    implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private enum Estados {ESTADO0, ESTADO1, ESTADO2}
    //ESTADO0: cuando el usuario todavía no inició el proceso para pedir viaje
    //ESTADO1: cuando el usuario puede indicar la posicion de recogida
    //ESTADO2: cuando el usuario puede indicar el destino del trayecto

    private Estados estado;
    private GoogleMap mMap;
    private Marker originMarker;
    private Marker destinationMarker;
    private FloatingActionButton requestTravelfab;
    private FloatingActionButton nextfab;
    private CardView search_card_view;

    PlaceAutocompleteFragment   autocompleteFragment;
    Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestTravelfab = (FloatingActionButton) findViewById(R.id.fab);
        requestTravelfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEstado1();
            }
        });
        nextfab = (FloatingActionButton) findViewById(R.id.next_fab);
        nextfab.setVisibility(View.INVISIBLE);

        search_card_view = (CardView) findViewById(R.id.search_card_view);
        search_card_view.setVisibility(View.INVISIBLE);

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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

    /**
     * startEstado1
     * Estado 1: el usuario indica la posición de recogida. Esta posición puede ser
     * indicada mediante el ingreso de la dirección en campo de ingreso de texto que surgirá.
     * También puede droppear un pin sobre una posicion en el mapa.
     * También puede apretar un botón que dropea el pin en la posicion actual del usuario.
     */
    private void startEstado1(){
        estado = Estados.ESTADO1;
        originMarker = mMap.addMarker(new MarkerOptions()
            .position(mMap.getCameraPosition().target)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.recogida_pin)));
        originMarker.setDraggable(true);
        configureAutocompleteFragment();
        requestTravelfab.setVisibility(View.INVISIBLE);
        nextfab.setVisibility(View.VISIBLE);
        nextfab.setOnClickListener(new View.OnClickListener() {
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
        originMarker.setDraggable(false);
        destinationMarker = mMap.addMarker(new MarkerOptions()
            .position(mMap.getCameraPosition().target)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destino_pin)));
        destinationMarker.setDraggable(true);
        autocompleteFragment.setText("");
        configureAutocompleteFragment();
    }
}
