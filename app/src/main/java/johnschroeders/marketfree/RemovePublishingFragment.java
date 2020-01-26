package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Objects;

public class RemovePublishingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PublishingActivity";
    Bundle bundle;
    private Product tempProduct = new Product();

    private OnFragmentInteractionListener mListener;

    public RemovePublishingFragment() {
        // Required empty public constructor
    }


    public static RemovePublishingFragment newInstance(String param1, String param2) {
        RemovePublishingFragment fragment = new RemovePublishingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //Inflating View with proper items from saved bundle
            Log.d(TAG, "before bundle grab in RemovePublishing Fragment " + getArguments());
            bundle = this.getArguments();
            if (bundle != null) {
                tempProduct = bundle.getParcelable("RemovePublshing");
            }
            Log.d(TAG,
                    "Temp order ID = " + Objects.requireNonNull(tempProduct)
                            .getProductID() + "Temp Producer Key is = " + tempProduct.getCustomerKey());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_remove_publishing, container, false);

        Button editPubXbutton = view.findViewById(R.id.editPubXButton);
        editPubXbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        Button editPubRemoveButton = view.findViewById(R.id.editPubRemovePublishingButton);
        editPubRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProduct();
            }
        });


        // get the URI from the product that was passed in and use GLIDE to grab it and populate
        // the imgae view with it.
        ImageView imageView = view.findViewById(R.id.editPubProductImageView);
        Glide.with(Objects.requireNonNull(getContext())).asBitmap().
                load(tempProduct.getUri()).into(imageView);

        TextView productTitle = view.findViewById(R.id.editPubProductTitleLabel);
        productTitle.setText(tempProduct.getProductTitle());
        TextView productDescription = view.findViewById(R.id.editPubProductDescrInput);
        productDescription.setText(tempProduct.getProductDescription());
        TextView productID = view.findViewById(R.id.editPubProductIDInput);
        productID.setText(tempProduct.getProductID());
        TextView productCost = view.findViewById(R.id.editPubProductCostInput);
        productCost.setText(String.valueOf(tempProduct.getCost()));
        TextView productQuantity = view.findViewById(R.id.editPubProductQuantityInput);
        productQuantity.setText(String.valueOf(tempProduct.getQuantity()));
        TextView productCreatedDate = view.findViewById(R.id.editPubProductDateCreatedInput);
        productCreatedDate.setText(DateFormat.getDateTimeInstance().
                format(tempProduct.getDateCreated().getTime()));
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void removeProduct() {
        Log.d(TAG, "Removing product from firestore");

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Publishings");
        collectionReference.whereEqualTo("productID", tempProduct.getProductID()).
                whereEqualTo("customerKey", tempProduct.getCustomerKey()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        collectionReference.document(document.getId()).delete();
                    }
                    restartActivity();
                }
            }

        });
    }

    private void restartActivity() {
        removeSelf();
        Intent intent = new Intent(getContext(), ManagePublishingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("SavedTab", 1);
        String customerKey =
                Objects.requireNonNull(Objects.requireNonNull(getActivity()).getIntent().getStringExtra(
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
    }


    private void removeSelf() {
        Log.d(TAG, "Order fragment removing");
        try {
            bundle.clear();
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                    beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }
}
