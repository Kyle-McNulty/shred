package com.kjmcnult.uw.edu.shredio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.kjmcnult.uw.edu.shredio.SkateSpot.getRating;

/*
 * Activity that shows by default when logged in
 * Shows a map of your surrounding area with skate spots as markers
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private String TAG = "MapsActivity";
    GoogleApiClient googleApiClient;
    private int LOC_REQUEST_CODE = 1;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private LatLng currentLocation;
    private ArrayList<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // MAP STUFF
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // LIST STUFF
        final ArrayList<SkateSpot> spotArrayList = new ArrayList<>();
        final SpotsAdapter spotsAdapter = new SpotsAdapter(this, spotArrayList);
        ListView lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(spotsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // PULL UP THE DETAIL FRAG
                Intent mIntent = new Intent(MapsActivity.this, DetailsActivity.class);
                Bundle mBundle = new Bundle();

                TextView keyView = (TextView) view.findViewById(R.id.spotKey);
                mBundle.putString("key", keyView.getText().toString());

                TextView locationView = (TextView) view.findViewById(R.id.spotLocation);

                String locationString = locationView.getText().toString();
                mBundle.putString("location", locationString);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean[] settingsData = new boolean[5];
        settingsData[0] = sharedPref.getBoolean("pref_rail", true);
        settingsData[1] = sharedPref.getBoolean("pref_stairs", true);
        settingsData[2] = sharedPref.getBoolean("pref_ledge", true);
        settingsData[3] = sharedPref.getBoolean("pref_gap", true);
        settingsData[4] = sharedPref.getBoolean("pref_ramp", true);

        DatabaseReference ref = database.getReference("Spots");
        // adds all the markers to the map from the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> skatespots = dataSnapshot.getChildren();
                for (DataSnapshot snap : skatespots) {
                    SkateSpot skatespot = snap.getValue(SkateSpot.class);
                    skatespot.setKey(snap.getKey());
//                    Log.v(TAG, skatespot.getLocation().toString());
                    ArrayList<Boolean> filterData = skatespot.getIds();
                    ArrayList<String> spotData = new ArrayList<String>();
                    spotData.add(snap.getKey());
                    boolean filtered = false;
                    for (int i = 0; i < filterData.size(); i++) {
                        spotData.add(filterData.get(i).toString());
                        // Log.v(TAG, "Settings: " + settingsData[i] + " vs. " + filterData.get(i));
                        if (!settingsData[i]) {
                            if (settingsData[i] != filterData.get(i)) {
                                filtered = true;
                            }
                        }
                    }

                    if (!filtered) {
                        spotsAdapter.add(skatespot);
                        markers.add(mMap.addMarker(new MarkerOptions()
                                .title(skatespot.getName())
                                .snippet(skatespot.getDescription())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shred_marker_small))
                                .position(skatespot.getLocation().getLatLng())));
                        markers.get(markers.size() - 1).setTag(spotData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        invalidateOptionsMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            menu.findItem(R.id.menu_item_account).setEnabled(true);
            menu.findItem(R.id.menu_item_account).setVisible(true);
            menu.findItem(R.id.menu_item_signout).setEnabled(false);
            menu.findItem(R.id.menu_item_signout).setVisible(false);
            fab.hide();
        } else {
            menu.findItem(R.id.menu_item_account).setEnabled(false);
            menu.findItem(R.id.menu_item_account).setVisible(false);
            menu.findItem(R.id.menu_item_signout).setEnabled(true);
            menu.findItem(R.id.menu_item_signout).setVisible(true);
            fab.show();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_account:
                Intent intent_login = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(intent_login);
            case R.id.menu_item_signout:
                FirebaseAuth.getInstance().signOut();
                invalidateOptionsMenu();
                Snackbar.make(findViewById(R.id.map_activity), "You just signed out!", Snackbar.LENGTH_SHORT).show();
                return true;
            case R.id.menu_item_settings:
                startActivity(new Intent(MapsActivity.this, SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the Spot will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the Spot has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newSpotIntent = new Intent(MapsActivity.this, CreateSpotActivity.class);
                startActivity(newSpotIntent);
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        FragmentManager fm = getSupportFragmentManager();
        Intent mIntent = new Intent(MapsActivity.this, DetailsActivity.class);
        Bundle mBundle = new Bundle();

        ArrayList<String> spotData = (ArrayList<String>) marker.getTag();
        mBundle.putString("key", spotData.get(0));
        LatLng location = marker.getPosition();
        String locationString = location.latitude + ", " + location.longitude;

        mBundle.putString("location", locationString);
        mIntent.putExtras(mBundle);
        // Log.v(TAG, marker.getPosition().toString());
        startActivity(mIntent);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
        invalidateOptionsMenu();
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public class SpotsAdapter extends ArrayAdapter<SkateSpot> {

        public SpotsAdapter(Context context, ArrayList<SkateSpot> spots) {
            super(context, 0, spots);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            SkateSpot spot = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            // Lookup view for data population
            TextView spotTitle = (TextView) convertView.findViewById(R.id.spotTitle);
            TextView spotDescription = (TextView) convertView.findViewById(R.id.spotDescription);
            TextView spotLocation = (TextView) convertView.findViewById(R.id.spotLocation);
            TextView spotKey = (TextView) convertView.findViewById(R.id.spotKey);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.list_rating);
            // Populate the data into the template view using the data object
            spotTitle.setText(spot.getName());
            spotDescription.setText(spot.getDescription());
            spotLocation.setText(spot.getLocation().toString());
            spotKey.setText(spot.getKey());
            Double averageRating = getRating(spot.getUserRatings());
            ratingBar.setRating(Float.parseFloat(averageRating.toString()));

            // Return the completed view to render on screen
            return convertView;
        }
    }


}
