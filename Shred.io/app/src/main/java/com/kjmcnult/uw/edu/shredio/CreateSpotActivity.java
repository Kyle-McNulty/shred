package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by kyle on 5/23/17.
 */

public class CreateSpotActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG= "com.kjmcnult.uw.edu.shredio.CreateSpotFragment";
    private static final String NAME_PARAM_KEY = "name";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_REQUEST_CODE = 2;
    private static Uri mLocationForPhotos;
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
        final EditText tagsText = (EditText)findViewById(R.id.new_spot_tags);

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
                storageRef = storageRef.child("spots/" + name); //change to use variable for name

                // check if anything has been left blank
                if(description.equals("") || name.equals("") || bitmap == null){
                    // don't let the user post the spot
                    Toast.makeText(getApplicationContext(), "Please make sure you fill out all fields", Toast.LENGTH_LONG).show();
                } else {

                    SkateSpot spot = new SkateSpot(name, description, "spots/" + name, currentLocation, idBools);

                    //clear the edit text fields
                    nameText.setText("");
                    descriptionText.setText("");
                    tagsText.setText("");

                    //uploads with the image currently stored in the instance variable
                    if (bitmap != null)
                        upload(storageRef);
                    //uploads entry to database
                    myRef.child(name).setValue(spot);

                    // send user back to maps activity after creating the spot
                    Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateSpotActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap)extras.get("data");
            ImageView image = (ImageView) findViewById(R.id.image_preview);
            image.setImageBitmap(bitmap);
        }
    }

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

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //have permission, can go ahead and do stuff

            //assumes location settings enabled
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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

    @Override
    public void onClick(View v) {
        Log.v("hahAA", idBools.toString());
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

    public static class SkateSpot{
        public String spotName;
        public String description;
        public String imageResource;
        public LatLng location;
        public ArrayList<Boolean> ids;

        public SkateSpot(){}

        public SkateSpot(String spotName, String description, String imageResource, LatLng location, ArrayList<Boolean> ids){
            this.spotName = spotName;
            this.description = description;
            this.imageResource = imageResource;
            this.location = location;
            this.ids = ids;
        }
    }

}
