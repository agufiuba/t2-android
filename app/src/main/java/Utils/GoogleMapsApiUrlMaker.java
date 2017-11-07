package Utils;

/**
 * Created by darius on 07/11/17.
 */

import android.content.Context;

import com.google.android.gms.maps.model.Marker;

/**
 * GoogleMapsApiUrlMaker
 * Permite generar una url propia de la api de google maps
 * para calcular la distancia efectiva entre un punto geográfico y otro.
 * Basado en el tutorial: https://developers.google.com/maps/documentation/directions/intro?hl=es-419
 */
public class GoogleMapsApiUrlMaker {

    private Context context;
    private String url = "https://maps.googleapis.com/maps/api/directions/json?";
    private String parameters;
    private String api = "AIzaSyDh9EMTub3aewSOKjv_oyrRs8sOZjUQwek";

    GoogleMapsApiUrlMaker(Context context, Marker origin, Marker destination) {
        parameters += "origin=" + origin.getPosition().toString();
        parameters += "&destination=" + destination.getPosition().toString();
        parameters += "&key=" + api;
        parameters += "&alternatives=true";

        /*
        Ejemplo:
        https://maps.googleapis.com/maps/api/directions/json?origin=-34.617952,-58.385983&destination=-34.617952,-58.385983&key=AIzaSyDh9EMTub3aewSOKjv_oyrRs8sOZjUQwek&alternatives=true/
        Con Postman funciona.
         */
    }

    /** ejemplo de devolucion:
     * {
     "geocoded_waypoints": [
     {
     "geocoder_status": "OK",
     "place_id": "ChIJ_xpVfdjKvJURvarjfWLR4Eo",
     "types": [
     "street_address"
     ]
     },
     {
     "geocoder_status": "OK",
     "place_id": "ChIJIZZec4O1vJURiSpMaich5vM",
     "types": [
     "street_address"
     ]
     }
     ],
     "routes": [
     {
     "bounds": {
     "northeast": {
     "lat": -34.5665729,
     "lng": -58.3797325
     },
     "southwest": {
     "lat": -34.6186717,
     "lng": -58.42174559999999
     }
     },
     "copyrights": "Map data ©2017 Google",
     "legs": [
     {
     "distance": {
     "text": "9.9 km",
     "value": 9918
     },
     "duration": {
     "text": "25 mins",
     "value": 1480
     },
     "end_address": "Av. Santa Fe 4200, Buenos Aires, Argentina",
     "end_location": {
     "lat": -34.5811736,
     "lng": -58.4217164
     },
     "start_address": "San José 807, C1076AAQ CABA, Argentina",
     "start_location": {
     "lat": -34.6179512,
     "lng": -58.3858729
     },
     "steps": [
     {
     "distance": {
     "text": "80 m",
     "value": 80
     },
     "duration": {
     "text": "1 min",
     "value": 18
     },
     "end_location": {
     "lat": -34.6186717,
     "lng": -58.3858656
     },
     "html_instructions": "Head <b>south</b> on <b>San José</b> toward <b>Estados Unidos</b>",
     "polyline": {
     "points": "dihrEtnjcJnC?"
     },
     "start_location": {
     "lat": -34.6179512,
     "lng": -58.3858729
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "0.5 km",
     "value": 478
     },
     "duration": {
     "text": "3 mins",
     "value": 161
     },
     "end_location": {
     "lat": -34.6185071,
     "lng": -58.3806458
     },
     "html_instructions": "Turn <b>left</b> at the 1st cross street onto <b>Estados Unidos</b>",
     "maneuver": "turn-left",
     "polyline": {
     "points": "tmhrEtnjcJ?YK_GAa@Au@A}DAG?IAM?QGmGAkA?k@?k@"
     },
     "start_location": {
     "lat": -34.6186717,
     "lng": -58.3858656
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "2.6 km",
     "value": 2620
     },
     "duration": {
     "text": "9 mins",
     "value": 553
     },
     "end_location": {
     "lat": -34.5950597,
     "lng": -58.3819761
     },
     "html_instructions": "Turn <b>left</b> onto <b>Av. 9 de Julio</b>",
     "maneuver": "turn-left",
     "polyline": {
     "points": "tlhrE`nicJsDDgFFyBDuBDu@JcBDi@@S@}EDsFBgBJeBF{EF{BHsBBI?wBFgBDuFLiDH{@FO?MCC?EAC?EAWK[MYKYEYAC?c@@q@Bc@@K?[BQDSDOFi@VOFG@QDkFDc@@{DHmFLmFLqA@[@K@I@e@F{@Jm@Bk@DoAF{@Da@@]@c@?"
    },
     "start_location": {
     "lat": -34.6185071,
     "lng": -58.3806458
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "0.4 km",
     "value": 388
     },
     "duration": {
     "text": "1 min",
     "value": 42
     },
     "end_location": {
     "lat": -34.5915756,
     "lng": -58.3818474
     },
     "html_instructions": "Continue straight to stay on <b>Av. 9 de Julio</b>",
     "maneuver": "straight",
     "polyline": {
     "points": "bzcrEjvicJaABe@Bw@?M?S@GAo@?aEIkESo@C"
     },
     "start_location": {
     "lat": -34.5950597,
     "lng": -58.3819761
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "3.8 km",
     "value": 3831
     },
     "duration": {
     "text": "4 mins",
     "value": 256
     },
     "end_location": {
     "lat": -34.5684749,
     "lng": -58.4055017
     },
     "html_instructions": "Continue onto <b>Au Pres. Arturo Umberto Illia</b><div style=\"font-size:0.9em\">Toll road</div>",
     "polyline": {
     "points": "jdcrEpuicJgCA_ACm@Ei@E{@I]E{@Om@Ma@IYIk@MwAc@w@Uy@Wg@QwAm@s@[a@SkAg@kAi@UKICSEICMCSEQC_@E]CW?c@@[BWBa@H_@J[Jc@T[R_@V]^c@f@[`@OVKPa@r@MVIPs@`Be@hAy@lBKRIR?@}@rBCHCFUf@w@lBm@~Aq@nBm@|A}@bC_@`Aw@bBYl@CDADGLUb@Yh@i@~@a@n@S\\g@t@i@t@q@`A}ArB}@`Aa@b@QJEDC@YReAl@QLYROLa@XWTk@h@YVKJKPOT[h@S`@Uh@g@vAOh@Mp@Ox@MrAE\\G^Gb@G^Qz@M`@GRQb@[x@e@fAkA|CMZOXO`@{@bBa@bAwB~Ee@hAc@hAy@jBqAtCaApBg@`AOTOT_@n@o@hAeDvFqAxB"
    },
     "start_location": {
     "lat": -34.5915756,
     "lng": -58.3818474
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "0.1 km",
     "value": 128
     },
     "duration": {
     "text": "1 min",
     "value": 17
     },
     "end_location": {
     "lat": -34.5673992,
     "lng": -58.4054247
     },
     "html_instructions": "Take the <b>Av. Sarmiento</b> exit toward <b>Av. Costanera</b>/<b>Aeroparque</b>",
     "maneuver": "ramp-right",
     "polyline": {
     "points": "|s~qEjincJOFOFMFIBG@G@I@G@O?OAOCOCMGICQMKIEE"
     },
     "start_location": {
     "lat": -34.5684749,
     "lng": -58.4055017
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "0.8 km",
     "value": 846
     },
     "duration": {
     "text": "2 mins",
     "value": 112
     },
     "end_location": {
     "lat": -34.5709779,
     "lng": -58.41134499999999
     },
     "html_instructions": "Turn <b>left</b> onto <b>Av. Sarmiento</b> (signs for <b>Centro</b>)",
     "maneuver": "turn-left",
     "polyline": {
     "points": "fm~qEzhncJWMGCICYGaAfCOd@ENEXCX?X?D?J@N@J@LDVFVJXNXFLLPLJTTZV|@r@NLLJrAhAdAz@~@t@JFDDBDDVHFXZr@p@l@l@bCxBzAtAd@b@LH"
     },
     "start_location": {
     "lat": -34.5673992,
     "lng": -58.4054247
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "0.5 km",
     "value": 465
     },
     "duration": {
     "text": "1 min",
     "value": 83
     },
     "end_location": {
     "lat": -34.5742564,
     "lng": -58.41438969999999
     },
     "html_instructions": "Slight <b>right</b> to stay on <b>Av. Sarmiento</b>",
     "maneuver": "turn-slight-right",
     "polyline": {
     "points": "rc_rEzmocJZj@Zj@JZNTFDDDHDNFNDJBD@HDFDl@b@j@b@FD@@FBPDd@b@pAjAzBrB|@x@n@l@"
     },
     "start_location": {
     "lat": -34.5709779,
     "lng": -58.41134499999999
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "77 m",
     "value": 77
     },
     "duration": {
     "text": "1 min",
     "value": 26
     },
     "end_location": {
     "lat": -34.5746417,
     "lng": -58.415079
     },
     "html_instructions": "Slight <b>right</b> to stay on <b>Av. Sarmiento</b>",
     "maneuver": "turn-slight-right",
     "polyline": {
     "points": "bx_rE|`pcJ@JFLTb@DHLVVh@"
     },
     "start_location": {
     "lat": -34.5742564,
     "lng": -58.41438969999999
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "0.9 km",
     "value": 875
     },
     "duration": {
     "text": "3 mins",
     "value": 162
     },
     "end_location": {
     "lat": -34.5807717,
     "lng": -58.42072229999999
     },
     "html_instructions": "At the roundabout, take the <b>2nd</b> exit and stay on <b>Av. Sarmiento</b>",
     "maneuver": "roundabout-right",
     "polyline": {
     "points": "nz_rEfepcJ?@?@?@?@?@?@?@?@?@@??@?@?@@FBD@DBDBDBBDBDBB@D@D?D?D?DA@?@A@?@A@?@A@AVHJBB@f@P`@PfGxFHTFHHJ`@`@BBFBzArAFDjCbCn@l@PNt@r@`@^vEjEr@n@HJDHFPBD@J"
     },
     "start_location": {
     "lat": -34.5746417,
     "lng": -58.415079
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "69 m",
     "value": 69
     },
     "duration": {
     "text": "1 min",
     "value": 21
     },
     "end_location": {
     "lat": -34.5807268,
     "lng": -58.42145379999999
     },
     "html_instructions": "Turn <b>right</b> onto <b>Calz Circular Plaza Italia</b>/<b>Av. Santa Fe</b>",
     "maneuver": "turn-right",
     "polyline": {
     "points": "x`arEnhqcJCNCHCPCRAP@NFn@"
     },
     "start_location": {
     "lat": -34.5807717,
     "lng": -58.42072229999999
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "58 m",
     "value": 58
     },
     "duration": {
     "text": "1 min",
     "value": 23
     },
     "end_location": {
     "lat": -34.5811576,
     "lng": -58.42174559999999
     },
     "html_instructions": "Slight <b>left</b> onto <b>Thames</b>",
     "maneuver": "turn-slight-left",
     "polyline": {
     "points": "p`arE`mqcJDJDFFJHJDBDBH@LBR@"
     },
     "start_location": {
     "lat": -34.5807268,
     "lng": -58.42145379999999
     },
     "travel_mode": "DRIVING"
     },
     {
     "distance": {
     "text": "3 m",
     "value": 3
     },
     "duration": {
     "text": "1 min",
     "value": 6
     },
     "end_location": {
     "lat": -34.5811736,
     "lng": -58.4217164
     },
     "html_instructions": "Turn <b>left</b> onto <b>Av. Santa Fe</b>",
     "maneuver": "turn-left",
     "polyline": {
     "points": "fcarE|nqcJ@E"
     },
     "start_location": {
     "lat": -34.5811576,
     "lng": -58.42174559999999
     },
     "travel_mode": "DRIVING"
     }
     ],
     "traffic_speed_entry": [],
     "via_waypoint": []
     }
     ],
     "overview_polyline": {
     "points": "dihrEtnjcJnC??YMaHEeGKyJ?wA{KLoFJu@JcBD}@BqMHmER{EF{BH}BB_FL_LVkAF[EsAg@s@Gg@@}BHe@Jy@^WHQDkFD_FJ{MZcCFaBRyAHkCL_ABcEFyA?aEIkESo@CgCAmBIeBOyAUoAWuEqAaBi@kCiAoEqBu@Qe@I}@I{@@s@FaAT_A`@{@j@aAfAk@x@m@dAqBtEkDdIeBlE_BlE}AdEqApCc@|@cAhBu@lAqAjBoCtD_BdBWPuBpAcBpAeA`AW\\k@~@i@jAw@`C]jBc@tDYzAUt@m@|AqBdF]t@kAdCcFvLkC`GiBrD_@j@oAxBwFpJ_@Ng@NQB_@A_@GWK{@k@QGYGaAfCUt@Ir@@z@BXLn@Zr@T^|BlBvD~CjA|@HJDVHFlAlArH`HLHZj@f@fAVZNJp@RPJxAfAHFXHpH|Gn@l@@J\\p@j@lA?B@J@LLVRLV@PGh@LhAb@fGxFHTPTd@d@vF`FxCpCjGzFNTJV@JCNGZEd@H~@JRPVJFVDR@@E"
    },
     "summary": "Av. 9 de Julio and Au Pres. Arturo Umberto Illia",
     "warnings": [],
     "waypoint_order": []
     }
     ],
     "status": "OK"
     }
     */
}
