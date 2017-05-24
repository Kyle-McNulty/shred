package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static android.R.attr.button;
import static android.app.Activity.RESULT_OK;

/**
 * Created by kyle on 5/23/17.
 */

public class CreateSpotFragment extends Fragment {

    private static final String TAG= "com.kjmcnult.uw.edu.shredio.CreateSpotFragment";
    private static final String NAME_PARAM_KEY = "name";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static Uri mLocationForPhotos;
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


    public CreateSpotFragment() {
        // Required empty public constructor
    }

    //factory method for creating the Fragment
    // other params , String summary, String image, String urlString
    public static CreateSpotFragment newInstance(String name) {
        CreateSpotFragment fragment = new CreateSpotFragment();
        Bundle args = new Bundle();
        args.putString(NAME_PARAM_KEY, name);
//        args.putString(SUMMARY_PARAM_KEY, summary);
//        args.putString(IMAGE_PARAM_KEY, image);
//        args.putString(ARTICLE_PARAM_KEY, urlString);
        fragment.setArguments(args);
        mLocationForPhotos = Uri.parse("");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.create_spot_fragment, container, false);

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            TextView name = (TextView) rootView.findViewById(R.id.new_spot_name);
            name.setText(bundle.getString(NAME_PARAM_KEY));

            Button chooseImageButton = (Button) rootView.findViewById(R.id.new_spot_upload);
            //articleString = bundle.getString(ARTICLE_PARAM_KEY);

            chooseImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // create an intent to take a picture
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });



            Button uploadButton = (Button) rootView.findViewById(R.id.new_spot_upload);
            //articleString = bundle.getString(ARTICLE_PARAM_KEY);

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add the spot information as a new database entry
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

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            //ImageView imageView = (ImageView)findViewById(R.id.imgThumbnail);
            //imageView.setImageBitmap(imageBitmap);

            //store this photo with the item (instance variable?) so it can be added when the database entry is submitted
        }
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

}
