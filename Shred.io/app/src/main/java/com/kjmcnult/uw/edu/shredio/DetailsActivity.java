package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.bitmap;
import static android.R.attr.button;
import static android.R.attr.description;
import static java.security.AccessController.getContext;

/**
 * Created by kyle on 5/23/17.
 */

public class DetailsActivity extends AppCompatActivity{

    private static final String TAG= "DetailsActivity";
    private static final String NAME_PARAM_KEY = "name";
    private String markerLocation;
    private ImageView image;
    private Button[] ids;
    private ArrayList idBools;
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

        image = (ImageView) findViewById(R.id.spot_image);

        markerLocation = getIntent().getExtras().getString("location");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        idBools = new ArrayList<>();

        ids = new Button[5];

        final Button button1 = (Button) findViewById(R.id.button1);
        ids[0] = button1;

        final Button button2 = (Button) findViewById(R.id.button2);
        ids[1] = button2;

        final Button button3 = (Button) findViewById(R.id.button3);
        ids[2] = button3;

        final Button button4 = (Button) findViewById(R.id.button4);
        ids[3] = button4;

        final Button button5 = (Button) findViewById(R.id.button5);
        ids[4] = button5;


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
                            Log.v(TAG, hashMap.get("spotName").toString());
                            TextView name = (TextView) findViewById(R.id.spot_name);
                            name.setText(hashMap.get("spotName").toString());

                            TextView description = (TextView) findViewById(R.id.spot_description);
                            description.setText(hashMap.get("description").toString());

                            TextView tags = (TextView) findViewById(R.id.spot_tags);
                            tags.setText(hashMap.get("tags").toString());

                            String list = hashMap.get("ids").toString();
                            list = list.substring(1, list.length() - 1);
                            Log.v("hahaa", list);
                            //list.replaceAll("[0-9]" + "=", "");
                            //Log.v("hahaa", list);
                            List<String> myList = new ArrayList<String>(Arrays.asList(list.split(", ")));
                            Log.v("hahaa", myList.toString());
                            for(String item : myList){
                                if(item.equals("false")){
                                    idBools.add(false);
                                } else{
                                    idBools.add(true);
                                }
                            }
                            Log.v("hahaa", idBools.toString());

                            for(int j = 0; j < idBools.size(); j++){
                                Log.v(TAG, idBools.get(j).toString());
                                if((boolean)idBools.get(j)){
                                    // if the button pressed was true
                                    // then set the button to be visible
                                    ids[j].setVisibility(View.VISIBLE);
                                }
                            }




                            //get the image from storage
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            Log.v(TAG, hashMap.get("imageResource").toString());
                            Log.v(TAG, hashMap.get("spotName").toString());
                            storageRef = storageRef.child("spots/" + hashMap.get("spotName").toString());

                            download(storageRef);
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
                        Uri.parse("http://maps.google.com/maps?daddr=" + markerLocation.substring(10, markerLocation.length() - 1)));
                startActivity(maps);
            }
        });
    }

    public void download(StorageReference ref){
        Log.v(TAG, "ref: " + ref.toString());
//        Glide.with(this)
//                .using(new FirebaseImageLoader())
//                .load(ref)
//                .dontAnimate()
//                .into(image);
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
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
