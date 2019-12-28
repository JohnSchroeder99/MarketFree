package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;


// this fragment is to create a new published product that people can order from the publisher
//TODO update the layout for this so it can handle all functionality in landscape mode for all
// versions.

public class CreatePublishingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PublishingActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageButton imageButton = null;

    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public CreatePublishingFragment() {
    }

    public static CreatePublishingFragment newInstance(String param1, String param2) {
        CreatePublishingFragment fragment = new CreatePublishingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //this is the layout that the fragment inflates (fragment_create_publishing)
        View view = inflater.inflate(R.layout.fragment_create_publishing, container, false);


        // Getting references to the button layouts and creating the onlcick listeners for them
        // with there functionality.

        Button xButton = view.findViewById(R.id.CreatePubFragExitButton);
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        Button publishButton = view.findViewById(R.id.CreateProductFragCreateButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ready to publish product");
                Intent intent = new Intent(getActivity(), ManagePublishingActivity.class);
                startActivity(intent);
            }
        });

        imageButton = view.findViewById(R.id.ProductImageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take the picture and save it for the product image
                dispatchTakePictureIntent();
            }
        });

        return view;
    }

    // need to look into how or if we need to use this default method
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    //not exactly sure what this is doing but it is needed for creation
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // a method for having the fragment remove itself if the x button in the corner of the layout
    // is clicked.
    private void removeSelf() {
        Log.d(TAG, "Order fragment removing");
        try {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }

    //required interface, not sure why but it is so dont remove
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //start the activity to take the picture of the product which returns a bitmap
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // set the bit map to the size that we want for publishing and use it as the image for the
    // product
    //TODO need to save the image to a file so it can be retrieved or deleted in the future and
    // also to find a way to save it to a server or something bigger for sharing purposes to
    // other people who are subscribed right now it is being saved to a bundle

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            Bitmap scaled = Bitmap.createScaledBitmap(Objects.requireNonNull(imageBitmap), 300, 300, true);
            scaled.setHeight(300);
            scaled.setWidth(300);
            imageButton.setImageBitmap(scaled);
        }
    }
}
