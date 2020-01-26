package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


// this fragment is to create a new published product that people can order from the publisher
//TODO handle rules for firstorage so not everyone can read and write but only people that are
// authorized.

//TODO handle errors for screen rotation during upload or download of images.

public class CreatePublishingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PublishingActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;


    // Fields that we are using here.
    private ImageButton imageButton = null;
    private Bitmap scaled = null;
    private ProgressBar progressBar = null;
    private SimpleDateFormat dateFormat = null;
    private EditText prodTitleInput = null;
    private EditText prodquantityInput = null;
    private EditText prodDescriptionInput = null;
    private EditText prodcostInput = null;
    private String currentTimeStamp = null;
    private Toast toast = null;
    private final Product tempProduct = new Product();


    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public CreatePublishingFragment() {
    }

    // this is currently not used
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
        //checking if image was there before rotate.
        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("image");
            imageButton = view.findViewById(R.id.productImageButton);
            imageButton.setImageBitmap(bitmap);
        }
        //references for buttons, progressbar, product info and creating onlcick listeners.

        prodcostInput = view.findViewById(R.id.productcostInput);
        prodDescriptionInput = view.findViewById(R.id.productDescriptionInput);
        prodquantityInput = view.findViewById(R.id.productquantityInput);
        prodTitleInput = view.findViewById(R.id.productTitleInput);

        progressBar = view.findViewById(R.id.productPublishingProgressBar);
        progressBar.setVisibility(View.GONE);

        Button xButton = view.findViewById(R.id.productCreatePubFragExitButton);
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        Button publishButton = view.findViewById(R.id.productCreateProductFragCreateButton);
        publishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ready to publish product");
                publishProductImageToFirebase();
            }
        });

        imageButton = view.findViewById(R.id.productImageButton);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
        scaled = drawable.getBitmap();
        outState.putParcelable("image", scaled);
        super.onSaveInstanceState(outState);
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


    public void removeSelfAndPopulate() {
        Log.d(TAG, "Order fragment removing and starting activity");
        try {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
            Intent intent = new Intent(getContext(), ManagePublishingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            String customerKey =
                    Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                            "CustomerKey"));
            String userName = Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                    "UserName"));
            String photoURI =
                    Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                            "Photo"));
            intent.putExtra("CustomerKey", customerKey);
            intent.putExtra("UserName", userName);
            intent.putExtra("Photo", Objects.requireNonNull(photoURI));
            startActivity(intent);
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

    // sets the bit map to the size that we want for publishing and set it as the image for the
    // product to be saved
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) Objects.requireNonNull(extras).get("data");
            scaled = Bitmap.createScaledBitmap(Objects.requireNonNull(imageBitmap), 300, 300, true);
            scaled.setHeight(300);
            scaled.setWidth(300);
            imageButton.setImageBitmap(scaled);
        }
    }

    //publishes the product image to firebase  firestorage
    private void publishProductImageToFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        // check if any of the fields are null, if they are then return because we cannot upload
        // this product yet
        Log.d(TAG, "checking for null values");
        try {
            tempProduct.setCost(Double.valueOf(prodcostInput.getText().toString()));
            tempProduct.setQuantity(Integer.valueOf(prodquantityInput.getText().toString()));
            tempProduct.setProductDescription(prodDescriptionInput.getText().toString());
            tempProduct.setProductTitle(prodTitleInput.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), "You need to fill out all the information before this " +
                            "can be published",
                    Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            Log.d(TAG, "Product failed to publish because all the parameters are not filled out");
            return;
        }

        Log.d(TAG, "setting up the datetime and location");
        Locale current = getResources().getConfiguration().locale;
        dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", current);
        this.currentTimeStamp = dateFormat.format(new Date());

        Log.d(TAG, "Setting up firestorage to handle bringing in the data");
        final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        final StorageReference storageReference =
                storageRef.child("ProductImages/").child(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("UserName")
                        + getActivity().getIntent().getStringExtra("CustomerKey")
                ).child(currentTimeStamp);

        //TODO update metadata to reflect data from the producer with their customer key or
        // composite key that uniquely identifies them. We have some metadata already which may
        // be good enough
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("ProductID", tempProduct.getProductID())
                .setCustomMetadata("ProducerKey", tempProduct.getCustomerKey())
                .setCustomMetadata("UserName", getActivity().getIntent().getStringExtra("UserName"))
                .build();

        // converting image to byte array so it can be loaded to firestorage bucket
        Log.d(TAG, "Setting up imageButton and bitmap for compression and byte Array conversion");
        imageButton.setDrawingCacheEnabled(true);
        imageButton.buildDrawingCache();
        scaled = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteData = baos.toByteArray();

        //TODO handle uploading with screen rotation incase uploading fails
        //beginning the task of uploading to firebase firestorage
        Log.d(TAG, "uploading product Image to firebase firstorage");
        final UploadTask uploadTask = storageReference.putBytes(byteData, metadata);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                toast = Toast.makeText(getContext(),
                        "Your product is publishing",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                publishProductDataToFireStorage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG,
                        "failed to load to firestore:  " + exception.getMessage() + exception.getCause());
                Toast toast = Toast.makeText(getContext(),
                        "Your product failed to publish, try again in the future",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    //set up the product data will be published to firebase firestore firestore database
    public void publishProductDataToFireStorage() {
        //setting up temporary product with fields that are already available
        tempProduct.setProductID(prodDescriptionInput.getText().toString() + this.currentTimeStamp);
        tempProduct.setQuantity(Integer.valueOf(prodquantityInput.getText().toString()));
        tempProduct.setProductDescription(prodDescriptionInput.getText().toString());
        Date date = new Date();
        date.getTime();

        tempProduct.setDateCreated(date);
        tempProduct.setCustomerKey(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey"));
        tempProduct.setProductTitle(prodTitleInput.getText().toString());

        finishSettingTempProd();
    }

    // setting up the URI for the product so that the image can be referenced after it is
    // published to firestorage
    public void finishSettingTempProd() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        storageReference
                .child("ProductImages")
                .child(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("UserName")
                        + Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey"))
                .child(currentTimeStamp)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "Testing URL: " + uri);
                        tempProduct.setUri(uri.toString());
                        finalizeAndPublish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Failed to properly publish" + exception.getMessage() + exception.getLocalizedMessage());
                Log.d(TAG, "ProductImages/"
                        + Objects.requireNonNull(getActivity()).getIntent().getStringExtra("UserName")
                        + Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey"));
            }
        });
    }

    // after the uri for the temp product has been setup we need to finish publishing to firestore
    public void finalizeAndPublish() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "adding product to firestore collection");
        db.collection("Publishings").add(tempProduct)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(TAG, "Product was published to fire store " + tempProduct.getProductID());
                        toast = Toast.makeText(getContext(), "Great! Your publishing complete!",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        progressBar.setVisibility(View.GONE);
                        // remove the fragment and start the activity again so it can display
                        // what was just published.
                        removeSelfAndPopulate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                removeSelfAndPopulate();
                Log.d(TAG,
                        "Failed to publish the product with error: " + e.getLocalizedMessage() + e.getMessage() + e.getCause());
            }
        });
    }
}
