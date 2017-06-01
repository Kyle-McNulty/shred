package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.media.Rating;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Activity for creating a new spot and posting it to firebase
 */

public class CreateSpotActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;
    private DatabaseReference myRef;
    private Bitmap bitmap;
    private LatLng currentLocation;
    private GoogleApiClient mGoogleApiClient;
    private int[] ids;
    private ArrayList<Boolean> idBools;

    public CreateSpotActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_spot_fragment);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();

        this.currentLocation = null;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        ids = new int[5];
        idBools = new ArrayList<>();
        //initialize array to all false
        for(int i = 0; i < 5; i++){
            idBools.add(i, false);
        }

        //set the on click listeners for each button and add ids to array to track
        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(this);
        ids[0] = button1.getId();

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        ids[1] = button2.getId();

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(this);
        ids[2] = button3.getId();

        Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(this);
        ids[3] = button4.getId();

        Button button5 = (Button) findViewById(R.id.button5);
        button5.setOnClickListener(this);
        ids[4] = button5.getId();
        
        final EditText nameText = (EditText) findViewById(R.id.new_spot_name);
        final EditText descriptionText = (EditText)findViewById(R.id.new_spot_description);

        ImageButton chooseImageButton = (ImageButton) findViewById(R.id.select_image);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to take a picture
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        Button uploadButton = (Button) findViewById(R.id.new_spot_upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String description = descriptionText.getText().toString();

                //add the spot information as a new database entry
                //store the image first, then set the image string as a location/identifier in order to retrieve it
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                //storageRef = storageRef.child("spots");
                String photoID = UUID.randomUUID().toString();
                storageRef = storageRef.child("spots/" + photoID); //change to use UUID for name

                //uploads with the image currently stored in the instance variable
                if(bitmap != null) {
                    upload(storageRef);
                }

                com.kjmcnult.uw.edu.shredio.LatLng location = new com.kjmcnult.uw.edu.shredio.LatLng(currentLocation.latitude, currentLocation.latitude);

                HashMap<String, Double> map = new HashMap<String, Double>();
                RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
                map.put(FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ""), (double)ratingBar.getRating());
                SkateSpot spot = new SkateSpot(name, description, photoID, location, idBools, map);

                //clear the edit text fields
                nameText.setText("");
                descriptionText.setText("");

                //child(spot.spotName).

                myRef.child("Spots").push().setValue(spot);

                //also need to add a marker to the map from here
                //this should send back to the maps activity

                Intent intent = new Intent(CreateSpotActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    // what happens when we receive the image back from the camera app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap)extras.get("data");
            ImageView image = (ImageView) findViewById(R.id.image_preview);
            image.setImageBitmap(bitmap);
        }
    }

    // uploads the image to Firebase storage
    public void upload(StorageReference storageRef){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            }
        });
    }

    // updates the current location through an instance variable
    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // check if we have permission, else ask for it
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //have permission, can go ahead and do stuff
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                this.currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        }
        else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // when one of the tag buttons is pressed, highlight it to make it apparent and set the corresponding value in the list accordingly
    @Override
    public void onClick(View v) {
        int vId = v.getId();
        for(int id = 0; id < ids.length; id++){
            if(ids[id] == vId){
                //set the boolean for the specific id to opposite
                if(idBools.get(id)){
                    //if button has already been pressed, chane background color back to normal
                    v.setBackgroundColor(Color.GRAY);
                } else {
                    //change color to show selected
                    v.setBackgroundColor(Color.YELLOW);
                }
                idBools.set(id, !idBools.get(id));
            }
        }

    }
}
