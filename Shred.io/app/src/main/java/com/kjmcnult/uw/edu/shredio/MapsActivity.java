package com.kjmcnult.uw.edu.shredio;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private String TAG = "MapsActivity";
    GoogleApiClient googleApiClient;
    private int LOC_REQUEST_CODE = 1;
    private FirebaseDatabase database;
    private LatLng currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        database = FirebaseDatabase.getInstance();
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

        //mMap.getUiSettings().setZoomControlsEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = database.getReference("spot");
                //Bitmap bitmap = new
                Spot spot = new Spot("Steven's Rail", currentLocation, "Sick Rail over hear at my apartment");
                //Spot s = new Spot();
                mMap.addMarker(new MarkerOptions()
                    .title("Steven's Rail")
                    .snippet("Sick rail over here")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .position(currentLocation));
                myRef.setValue(spot);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Log.v(TAG, "Location changed to : " + currentLocation.toString());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOC_REQUEST_CODE);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        //View rootView = findViewById(R.id.map_activity);
        //FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //DatabaseReference myRef = database.getReference("spot");
//                //Bitmap bitmap = new
//                //Spot spot = new Spot("Steven's Rail", currentLocation, "Sick Rail over hear at my apartment");
//                mMap.addMarker(new MarkerOptions()
//                        .title("Steven's Rail")
//                        .snippet("Sick rail over here")
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name))
//                        .position(currentLocation));
//                //myRef.setValue("test spot");
//            }
//        });
        return super.onCreateView(name, context, attrs);
    }

    public static class Spot {

        public String name;
        public String description;
        public Bitmap picture;
        public LatLng location;

        private Spot() {}

        public Spot(String name, LatLng location, String description, Bitmap picture) {
            this.name = name;
            this.location = location;
            this.description = description;
            this.picture = picture;
        }

        public Spot(String name, LatLng location, String description) {
            this.name = name;
            this.location = location;
            this.description = description;
        }

        public Spot(String name, LatLng location) {
            this.name = name;
            this.location = location;
        }

//        public String getName() {
//            return  this.name;
//        }
//
//        public String getDescription() {
//            return  this.description;
//        }
//
//        public Bitmap getPicture() {
//            return  this.picture;
//        }
//
//        public LatLng getLocation() {
//            return location;
//        }
    }
}
