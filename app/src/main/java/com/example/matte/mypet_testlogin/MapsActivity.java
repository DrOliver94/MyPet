package com.example.matte.mypet_testlogin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locManager;
    private LocationListener locListener;
    private LocationRequest locRequest;

    private LatLng latlng;

//    private boolean isChoosingLoc;

    private GoogleApiClient gApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        gApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        return;
                    }
                })
                .build();

        latlng = (LatLng) getIntent().getExtras().get("com.example.matte.mypet.latlng");

                //True se si sta scegliendo una posizione
        //False se si vuole visualizzare una posizione
//        isChoosingLoc = (boolean) getIntent().getExtras().get("com.example.matte.mypet.isChoosingLoc");

        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(latlng).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16)); //1 - molto lontano; ~20 molto vicino

//        if (isChoosingLoc) {
//            //Se si vuole scegliere una location
//
//            //Posizionare sulla posizione corrente, se disponibile
//            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locListener = new MyLocationListener();
//            locRequest = new LocationRequest();
//            //Rimanere in ascolto di una scelta di posizione
//            //Reinviare al fragment richiesto la posizione scelta
//        } else {


            //Se si vuole visualizzare una location
            //Mostrare la posizione passata con marker



    }

    @Override
    protected void onResume() {
        super.onResume();

//        //Al ripristino, richiede di aggiornare la view con la posizione corrente
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            LocationServices.FusedLocationApi.requestLocationUpdates(gApiClient, locRequest, locListener);
//            return;
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Mette in pausa la lettura della posizione corrente
//        locManager.removeUpdates(locListener);
//        LocationServices.FusedLocationApi.removeLocationUpdates(gApiClient, locListener);
    }

    private class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                //leggere location
            }
        }
    }

}