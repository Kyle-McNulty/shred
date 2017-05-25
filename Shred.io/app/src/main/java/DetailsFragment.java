import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kjmcnult.uw.edu.shredio.R;

/**
 * Created by kyle on 5/23/17.
 */

public class DetailsFragment extends Fragment {

    private static final String TAG= "DetailsFragment";
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


    public DetailsFragment() {
        // Required empty public constructor
    }

    //factory method for creating the Fragment
    // other params , String summary, String image, String urlString
    public static DetailsFragment newInstance(String name) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(NAME_PARAM_KEY, name);
//        args.putString(SUMMARY_PARAM_KEY, summary);
//        args.putString(IMAGE_PARAM_KEY, image);
//        args.putString(ARTICLE_PARAM_KEY, urlString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.details_fragment, container, false);

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            TextView name = (TextView) rootView.findViewById(R.id.spot_name);
            name.setText(bundle.getString(NAME_PARAM_KEY));

            Button button = (Button) rootView.findViewById(R.id.spot_directions);
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
