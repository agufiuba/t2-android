package com.example.darius.taller_uber;

import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by darius on 04/11/17.
 */

public class PlaceSelection implements PlaceSelectionListener {
    private Marker marker;
    final String TAG = "PlaceSelection";

    PlaceSelection(Marker marker){
        this.marker = marker;
    }

    @Override
    public void onPlaceSelected(Place place) {
        marker.setPosition(place.getLatLng());
        Log.i(TAG, "Place: " + place.getName());
    }

    @Override
    public void onError(Status status) {
        Log.i(TAG, "An error occurred: " + status);
    }

}
