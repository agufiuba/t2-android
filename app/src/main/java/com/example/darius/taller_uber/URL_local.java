package com.example.darius.taller_uber;

/**
 * Created by darius on 06/11/17.
 */

public interface URL_local {
    String url = "http://192.168.43.137:3000/";
//    String url = "http://t2-appserver.herokuapp.com/";
    String url_login = url + "login";
    String url_user = url + "user";
    String url_trip = url + "availableTrip";
    String url_drivers = url + "drivers";
}
