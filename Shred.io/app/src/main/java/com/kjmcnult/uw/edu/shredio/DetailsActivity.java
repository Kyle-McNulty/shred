package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by kyle on 5/23/17.
 */

public class DetailsActivity extends AppCompatActivity{

    private static final String TAG= "com.kjmcnult.uw.edu.shredio.DetailsFragment";
    private static final String NAME_PARAM_KEY = "name";
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
