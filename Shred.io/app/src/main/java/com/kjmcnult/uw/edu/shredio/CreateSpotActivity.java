package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import static android.R.attr.bitmap;
import static android.R.attr.button;
import static android.app.Activity.RESULT_OK;
import static com.kjmcnult.uw.edu.shredio.R.id.container;

/**
 * Created by kyle on 5/23/17.
 */

public class CreateSpotActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG= "com.kjmcnult.uw.edu.shredio.CreateSpotFragment";
    private static final String NAME_PARAM_KEY = "name";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static Uri mLocationForPhotos;
    private DatabaseReference myRef;
    private Bitmap bitmap;
    private LatLng currentLocation;
    private GoogleApiClient mGoogleApiClient;
//    private static final String SUMMARY_PARAM_KEY = "summary";
//    private static final String IMAGE_PARAM_KEY = "image";
//    private static final String ARTICLE_PARAM_KEY = "article";
//    private String articleString;
//    private ImageView articleImage;

    //private OnButtonSelectedListener callback; //context that we use for event callbacks


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

        final EditText nameText = (EditText) findViewById(R.id.new_spot_name);
        //name.setText(bundle.getString(NAME_PARAM_KEY));

        final EditText descriptionText = (EditText)findViewById(R.id.new_spot_description);
        final EditText tagsText = (EditText)findViewById(R.id.new_spot_tags);


        Button chooseImageButton = (Button) findViewById(R.id.select_image);
        //articleString = bundle.getString(ARTICLE_PARAM_KEY);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an intent to take a picture
                Log.v("haHAA", "picture listener"); //doesn't get inside the listener, might be because it is in the maps activity
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    Log.v("haHAA", "picture");
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        Button uploadButton = (Button) findViewById(R.id.new_spot_upload);
        //articleString = bundle.getString(ARTICLE_PARAM_KEY);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask the user to take a picture before uploading, otherwise don't allow upload
                String name = nameText.getText().toString();

                //add the spot information as a new database entry
                //store the image first, then set the image string as a location/identifier in order to retrieve it
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                //storageRef = storageRef.child("spots");
                storageRef = storageRef.child("spots/" + name); //change to use variable for name

                //uploads with the image currently stored in the instance variable
                if(bitmap != null)
                    upload(storageRef);


                String description = descriptionText.getText().toString();
                String tags = tagsText.getText().toString();

                SkateSpot spot = new SkateSpot(name, description, "spots/" + name, tags, currentLocation);

                //clear the edit text fields
                nameText.setText("");
                descriptionText.setText("");
                tagsText.setText("");

                //child(spot.spotName).

                myRef.child(name).setValue(spot);



                //also need to add a marker to the map from here
                //this should send back to the maps activity

                Intent intent = new Intent(CreateSpotActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


            //articleImage = (ImageView) rootView.findViewById(R.id.previewImage);
            //String imageString = bundle.getString(IMAGE_PARAM_KEY);
            //Log.v(TAG, imageString);
            // if imagestring is blank/null, set the image to a default resource loaded with the app, else use the image loader
//            if(imageString.equals("")){
//                articleImage.setImageResource(R.drawable.no_image);
//            } else {
//                fetchArticlePoster(bundle.getString(IMAGE_PARAM_KEY));
//            }
        }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap)extras.get("data");

            //should probably display this photo as well to show a preview

            //ImageView imageView = (ImageView)findViewById(R.id.imgThumbnail);
            //imageView.setImageBitmap(imageBitmap);

            //store this photo with the item (instance variable?) so it can be added when the database entry is submitted
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
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.v("myTagHere", "success!");
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            this.currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        try {
//            callback = (OnButtonSelectedListener)context;
//        }catch(ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnButtonSelectedListend");
//        }
//    }

    public static class SkateSpot{
        public String spotName;
        public String description;
        public String imageResource;
        public String tags;
        public LatLng location;

        public SkateSpot(){}

        public SkateSpot(String spotName, String description, String imageResource, String tags, LatLng location){
            this.spotName = spotName;
            this.description = description;
            this.imageResource = imageResource;
            this.tags = tags;
            this.location = location;
        }

        public String getSpotName() {
            return spotName;
        }

        public String getDescription() {
            return description;
        }

        public String getImageResource() {
            return imageResource;
        }

        public String getTags() {
            return tags;
        }

        public LatLng getLocation() { return location; }
    }


}