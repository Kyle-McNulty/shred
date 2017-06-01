package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Details about a specific spot displayed when the spot is clicked on
 */

public class DetailsActivity extends AppCompatActivity{

    private static final String TAG= "DetailsActivity";
    private String markerLocation;
    private String markerKey;
    private ImageView image;
    private TextView[] ids;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_fragment);

        image = (ImageView) findViewById(R.id.spot_image);

        markerLocation = getIntent().getExtras().getString("location");
        markerKey = getIntent().getExtras().getString("key");

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // initialize the array of textviews with appropriate values
        ids = new TextView[5];
        final TextView tag1 = (TextView) findViewById(R.id.tag1);
        ids[0] = tag1;

        final TextView tag2 = (TextView) findViewById(R.id.tag2);
        ids[1] = tag2;

        final TextView tag3 = (TextView) findViewById(R.id.tag3);
        ids[2] = tag3;

        final TextView tag4 = (TextView) findViewById(R.id.tag4);
        ids[3] = tag4;

        final TextView tag5 = (TextView) findViewById(R.id.tag5);
        ids[4] = tag5;

        DatabaseReference ref = database.getReference("Spots");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> skatespots = dataSnapshot.getChildren();
                for (DataSnapshot snap : skatespots) {
//                    Log.v(TAG, "Current Key string " + snap.getKey());
//                    Log.v(TAG, "Expected Key string " + markerKey);
                    if (snap.getKey().equals(markerKey)) {
                        // Log.v(TAG, "Skatespot: " + snap.toString());
                        SkateSpot skatespot = snap.getValue(SkateSpot.class);

                        TextView name = (TextView) findViewById(R.id.spot_name);
                        name.setText(skatespot.getName());

                        TextView description = (TextView) findViewById(R.id.spot_description);
                        description.setText(skatespot.getDescription());

                        ArrayList<Boolean> idBools = skatespot.getIds();

                        for (int j = 0; j < idBools.size(); j++) {
                            if (idBools.get(j)) {
                                // if the button pressed was true
                                // then set the button to be visible
                                ids[j].setVisibility(View.VISIBLE);
                            }
                        }

                        //get the image from storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        // Log.v(TAG, skatespot.getImageResource());
                        storageRef = storageRef.child(skatespot.getImageResource());

                        download(storageRef);
                    }
                }
//                Log.v(TAG, "Skatespot: " + dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "The read failed: " + databaseError.getCode());
            }
        });



        // iterate through the database to show appropriate information for the marker that was clicked on
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> itr = dataSnapshot.getChildren();
//                for (DataSnapshot obj : itr) {
//                    Object object = obj.getValue();
//                    HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
//                    if (hashMap.keySet().contains("location")) {
//                        HashMap<String, Double> location = (HashMap<String, Double>) hashMap.get("location");
//                        LatLng latLng = new LatLng(location.get("latitude"), location.get("longitude"));
//                        String stringLocation = latLng.toString();
//                        // if the locations match, we found the correct object, so retrieve it and update the view
//                        if (markerLocation.equals(stringLocation)) {
//                            //set the appropriate fields for this database point
//                            TextView name = (TextView) findViewById(R.id.spot_name);
//                            name.setText(hashMap.get("spotName").toString());
//
//                            TextView description = (TextView) findViewById(R.id.spot_description);
//                            description.setText(hashMap.get("description").toString());
//
//                            TextView tags = (TextView) findViewById(R.id.spot_tags);
//                            tags.setText(hashMap.get("tags").toString());
//
//                            // converts the object stored in the database into a list of booleans
//                            String list = hashMap.get("ids").toString();
//                            list = list.substring(1, list.length() - 1);
//                            List<String> myList = new ArrayList<String>(Arrays.asList(list.split(", ")));
//                            for (String item : myList) {
//                                if (item.equals("false")) {
//                                    idBools.add(false);
//                                } else {
//                                    idBools.add(true);
//                                }
//                            }
//
//                            for (int j = 0; j < idBools.size(); j++) {
//                                Log.v(TAG, idBools.get(j).toString());
//                                if ((boolean) idBools.get(j)) {
//                                    // if the button was pressed for a specific tag, display it
//                                    ids[j].setVisibility(View.VISIBLE);
//                                }
//                            }
//
//                            //get the image from storage
//                            FirebaseStorage storage = FirebaseStorage.getInstance();
//                            StorageReference storageRef = storage.getReference();
//                            Log.v(TAG, hashMap.get("imageResource").toString());
//                            Log.v(TAG, hashMap.get("spotName").toString());
//                            storageRef = storageRef.child("spots/" + hashMap.get("spotName").toString());
//                            download(storageRef);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        Button button = (Button) findViewById(R.id.spot_directions);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a new intent to the user's maps application
                Intent maps = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + markerLocation.substring(10, markerLocation.length() - 1)));
                startActivity(maps);
            }
        });
    }

    // method for downloading a bitmap image from firebase storage and updating an imageview on the screen
    public void download(StorageReference ref){
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                image.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
