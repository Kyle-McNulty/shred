package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;

import static android.R.attr.button;
import static java.security.AccessController.getContext;

/**
 * Created by kyle on 5/23/17.
 */

public class DetailsActivity extends AppCompatActivity{

    private static final String TAG= "DetailsActivity";
    private static final String NAME_PARAM_KEY = "name";
    private String markerLocation;
//    private static final String SUMMARY_PARAM_KEY = "summary";
//    private static final String IMAGE_PARAM_KEY = "image";
//    private static final String ARTICLE_PARAM_KEY = "article";
//    private String articleString;
//    private ImageView articleImage;

    //private OnButtonSelectedListener callback; //context that we use for event callbacks

//    //interface supported by anyone who can respond to this Fragment's clicks
//    public interface OnButtonSelectedListener {
//        void onButtonSelected(String urlString);
//    }


    public DetailsActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_fragment);


        markerLocation = getIntent().getExtras().getString("location");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.v(TAG,"SNAPSHOT: " + dataSnapshot.toString());
                Iterable<DataSnapshot> itr = dataSnapshot.getChildren();
                for (DataSnapshot obj : itr) {
                    //Spot spot = (Spot) obj;
                    //Log.v(TAG, obj.getValue() + " <---");
                    Object object = obj.getValue();
                    //Log.v(TAG, object.getClass() + "<---");
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) object;
                    Log.v(TAG, hashMap.keySet().toString());
                    if (hashMap.keySet().contains("location")) {
                        HashMap<String, Double> location = (HashMap<String, Double>) hashMap.get("location");
                        LatLng latLng = new LatLng(location.get("latitude"), location.get("longitude"));
                        String stringLocation = latLng.toString();
                        if(markerLocation.equals(stringLocation)){
                            //set the appropriate fields for this database point
                            TextView name = (TextView) findViewById(R.id.spot_name);
                            name.setText(hashMap.get("spotName").toString());

                            TextView description = (TextView) findViewById(R.id.spot_description);
                            description.setText(hashMap.get("description").toString());

                            TextView tags = (TextView) findViewById(R.id.spot_tags);
                            tags.setText(hashMap.get("tags").toString());

                            ImageView image = (ImageView) findViewById(R.id.spot_image);
                            //get the image from storage
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            Log.v(TAG, hashMap.get("imageResource").toString());
                            Log.v(TAG, hashMap.get("spotName").toString());
                            storageRef = storageRef.child("spots/" + hashMap.get("spotName").toString());

                            Glide.with(getApplicationContext())
                                    .using(new FirebaseImageLoader())
                                    .load(storageRef)
                                    .into(image);
                            Log.v(TAG, "gets to end");
                        }
                    }
                }
                //dataSnapshot.getChildren()
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button button = (Button) findViewById(R.id.spot_directions);
        //articleString = bundle.getString(ARTICLE_PARAM_KEY);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.v(TAG, articleString);
                //tell the activity to do stuff!
                //create a new intent to the user's maps application
                String latlng = ""; //get this value from a parameter
                Intent maps = new Intent(android.content.Intent.ACTION_VIEW,
                        //the uri should use the latlng we have in this fragmnet
                        Uri.parse("http://maps.google.com/maps?daddr=20.5666,45.345"));
                startActivity(maps);
            }
        });
    }
}
