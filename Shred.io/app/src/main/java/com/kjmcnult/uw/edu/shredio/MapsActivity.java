package com.kjmcnult.uw.edu.shredio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
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
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

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
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        // MAP STUFF
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        database = FirebaseDatabase.getInstance();

        // LIST STUFF
//        ArrayList<Spot> testlist = new ArrayList<>();
//        testlist.add();
        final LatLng loc = new LatLng(47.6553, 122.3035);
        final Spot testSpot = new Spot("name1", loc, "desc");

//        final ArrayAdapter<Spot> adapter = new ArrayAdapter<Spot>(
//                this,
//                R.layout.list_item,
//                R.id.txtItem );
        ArrayList<Spot> spotArrayList = new ArrayList<>();
        final SpotsAdapter spotsAdapter = new SpotsAdapter(this, spotArrayList);
        ListView lv = (ListView)findViewById(R.id.list);
        //lv.setAdapter(adapter);
        lv.setAdapter(spotsAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // PULL UP THE DETAIL FRAG
                // pass the name, date, description, image
                //DetailsFragment detailsFragment = DetailsFragment.newInstance(testSpot.name);

//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.dragView, detailsFragment, "DetailsFragment")
//                        .addToBackStack(null)
//                        .commit();
                Intent mIntent = new Intent(MapsActivity.this, DetailsActivity.class);
                Bundle mBundle = new Bundle();
                TextView locationView = (TextView) view.findViewById(R.id.spotLocation);
                String locationString = locationView.getText().toString().substring(10);
                locationString = locationString.substring(0, locationString.length() - 1);
                String[] locations = locationString.split(",");
                Log.v(TAG, "spotLocation: " + Double.parseDouble(locations[0]) + ", " + locations[1]);
                LatLng location = new LatLng(Double.parseDouble(locations[0]), Double.parseDouble(locations[1]));
                mBundle.putString("location", location.toString());
                mIntent.putExtras(mBundle);
                Log.v(TAG, location.toString());
                //extras.putString();
                startActivity(mIntent);
                //startActivity(new Intent(MapsActivity.this, DetailsActivity.class));
            }
        });

        DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG,"SNAPSHOT: " + dataSnapshot.toString());
                Iterable<DataSnapshot> itr = dataSnapshot.getChildren();
                for (DataSnapshot obj : itr) {
                    //Spot spot = (Spot) obj;
                    //Log.v(TAG, obj.getValue() + " <---");
                    Object object = obj.getValue();
                    //Log.v(TAG, object.getClass() + "<---");
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
                    Log.v(TAG, hashMap.keySet().toString());
                    if (hashMap.keySet().contains("location") && hashMap.keySet().contains("spotName")) {
                        HashMap<String, Double> location = (HashMap<String, Double>) hashMap.get("location");
                        LatLng latLng = new LatLng(location.get("latitude"), location.get("longitude"));
                        Spot spot = new Spot();
                        spot.setName(hashMap.get("spotName").toString());
                        spot.setDescription(hashMap.get("description").toString());
                        spot.setLocation(latLng);
                        //adapter.add(spot);
                        spotsAdapter.add(spot);
                        mMap.addMarker(new MarkerOptions()
                                .title(obj.getKey())
                                .snippet(hashMap.get("description").toString())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_filled_smaller))
                                .position(latLng));
                    }
                }
                View dragView = findViewById(R.id.dragView);
//                dragView.setOnDragListener(new View.OnDragListener() {
//                    @Override
//                    public boolean onDrag(View v, DragEvent event) {
//                        Log.v(TAG, event.toString());
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                            ImageView listIcon = (ImageView) findViewById(R.id.list_icon);
//                            Drawable icon = null;
//                            if (event.getAction() == DragEvent.ACTION_DRAG_ENTERED) {
//                                Log.v(TAG, "SET ICON TO DOWN ARROW");
//                                icon = (Drawable) getDrawable(R.drawable.ic_list_away);
//                            } else if ((event.getAction() == DragEvent.ACTION_DRAG_EXITED)) {
//                                Log.v(TAG, "SET ICON TO UP ARROW");
//                                icon = (Drawable) getDrawable(R.drawable.ic_list_drag);
//                            }
//                            listIcon.setImageDrawable(icon);
//                            return true;
//                        }
//                        return false;
//                    }
//                });
                // onclicklistener for changing of listview icon
//                final ImageView listIcon = (ImageView) findViewById(R.id.list_icon);
//                listIcon.setOnClickListener(new View.OnClickListener() {
//
//                    private Boolean listShown = false;
//                    @Override
//                    public void onClick(View v) {
//                        this.listShown = !this.listShown;
//                        Drawable icon = null;
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                            if (listShown) {
//                                icon = (Drawable) getDrawable(R.drawable.ic_list_drag);
//                            } else {
//                                icon = (Drawable) getDrawable(R.drawable.ic_list_away);
//                            }
//                        }
//                        listIcon.setImageDrawable(icon);
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //adapter.add(testSpot);
        //adapter.add(testSpot);

        //initialize
        //initializeMap();

    }

//    private void initializeMap() {
//
//        mMapFragment = SupportMapFragment.newInstance();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.fragment_container, mMapFragment, "map");
//        fragmentTransaction.commit();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                mMap = mMapFragment.getMap();
//
//                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//                    @Override
//                    public void onCameraChange(CameraPosition cameraPosition) {
//                        // Do something here
//                    }
//                });
//            }
//        });
//    }


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
//                Intent intent_signout = new Intent(MapsActivity.this, LoginActivity.class);
//                intent_signout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent_signout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent_signout);
//                finish();
                return true;
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

        //mMap.getUiSettings().setZoomControlsEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = database.getReference("spot");
                //Bitmap bitmap = new
                //Spot spot = new Spot("Steven's Rail", currentLocation, "Sick Rail over here at my apartment");
                //this should send the Spot to the create spot fragment
//                FragmentManager fm = getSupportFragmentManager();
//                fm.beginTransaction().replace(R.id.container, CreateSpotFragment.newInstance("rail")).addToBackStack(null).commit();
                Intent newSpotIntent = new Intent(MapsActivity.this, CreateSpotActivity.class);
                startActivity(newSpotIntent);
                // the marker should be created after the Spot submits it, and the location should be when the Spot first clicks new


                //Spot s = new Spot();
//                if(currentLocation != null) {
//                    Log.v(TAG, currentLocation.toString());
//                    mMap.addMarker(new MarkerOptions()
//                            .title("Steven's Rail")
//                            .snippet("Sick rail over here")
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
//                            .position(currentLocation));
//                    myRef.setValue(spot);
//                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        FragmentManager fm = getSupportFragmentManager();
        //need to set this up so the fragment created is based off of the marker clicked
        //potentially a for loop checking each value's latlng to see if it matches the marker's
        //fm.beginTransaction().replace(R.id.mainLayout, DetailsFragment.newInstance("Terwilliger Rail")).addToBackStack(null).commit();
        Intent mIntent = new Intent(MapsActivity.this, DetailsActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("location", marker.getPosition().toString());
        mIntent.putExtras(mBundle);
        Log.v(TAG, marker.getPosition().toString());
        //extras.putString();
        startActivity(mIntent);
        return true;
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
        Log.v(TAG, "Starting");
        invalidateOptionsMenu();
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "Resuming");
        invalidateOptionsMenu();
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

    public class SpotsAdapter extends ArrayAdapter<Spot> {
        public SpotsAdapter(Context context, ArrayList<Spot> spots) {
            super(context, 0, spots);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Spot spot = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }
            // Lookup view for data population
            TextView spotTitle = (TextView) convertView.findViewById(R.id.spotTitle);
            TextView spotDescription = (TextView) convertView.findViewById(R.id.spotDescription);
            TextView spotLocation = (TextView) convertView.findViewById(R.id.spotLocation);
            // Populate the data into the template view using the data object
            spotTitle.setText(spot.getName());
            spotDescription.setText(spot.getDescription());
            spotLocation.setText(spot.getLocation().toString());
            // Return the completed view to render on screen
            return convertView;
        }
    }


}
