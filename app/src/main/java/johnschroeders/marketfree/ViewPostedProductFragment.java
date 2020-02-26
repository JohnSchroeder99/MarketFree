package johnschroeders.marketfree;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ViewPostedProductFragment extends Fragment {
    private static final String TAG = "ViewPostedActivty";
    private Bundle bundle;
    private Product tempProduct = new Product();
    private OnFragmentInteractionListener mListener;
    private ProgressBar progressBar;
    private Order tempOrder = new Order();
    private Button orderButton;

    public ViewPostedProductFragment() {
        // Required empty public constructor
    }

    public static ViewPostedProductFragment newInstance(String param1, String param2) {
        ViewPostedProductFragment fragment = new ViewPostedProductFragment();
        Bundle args = new Bundle();

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
                tempProduct = bundle.getParcelable("PassedInProduct");
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
        View view = inflater.inflate(R.layout.fragment_view_posted_product, container, false);
        progressBar = view.findViewById(R.id.viewProductProgressBar);
        setupProgressar(false);


        Button inquireButton = view.findViewById(R.id.viewProductInquireButton);
        orderButton = view.findViewById(R.id.viewProductOrderButton);

        Button xButton = view.findViewById(R.id.viewProductXbutton);


        inquireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSendMessageFrag(tempProduct);
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderButton.setVisibility(View.INVISIBLE);
                //set up the progress bar
                setupProgressar(true);
                // assign values to the order
                createAndAssignValuesToOrder();
                orderButton.setVisibility(View.INVISIBLE);

                // publish the order to firestore
                publishTheOrderToFirestore();
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        ImageView imageView = view.findViewById(R.id.viewProductImageView);
        imageView.setMaxHeight(100);
        imageView.setMaxWidth(100);
        Glide.with(Objects.requireNonNull(getContext())).asBitmap().
                load(tempProduct.getUri()).into(imageView);

        TextView productID = view.findViewById(R.id.viewProductIDInput);
        TextView price = view.findViewById(R.id.viewProductPriceInput);
        TextView dateCreated = view.findViewById(R.id.viewProductDateCreatedInput);
        TextView quantity = view.findViewById(R.id.viewProductQuantityInput);
        TextView prodTitle = view.findViewById(R.id.viewProductTitleInput);
        TextView prodDescription = view.findViewById(R.id.viewProductDescriptionInput);

        try {

            productID.setText(tempProduct.getProductID());
            price.setText(String.valueOf(tempProduct.getCost()));
            dateCreated.setText(tempProduct.getDateCreated().toString());
            quantity.setText(String.valueOf(tempProduct.getQuantity()));
            prodTitle.setText(tempProduct.getProductTitle());
            prodDescription.setText(tempProduct.getProductDescription());
        } catch (Exception e) {

            Log.d(TAG, "Could not set the textviews to the data passed in");
        }

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


    private void removeSelf() {
        Log.d(TAG, "ViewPostedProduct fragment removing");
        try {
            bundle.clear();
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                    beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }

    private void addSendMessageFrag(Product passedProduct) {
        Fragment sendMessageFragment = new SendMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("PassedInFromViewPostedProductFragment", passedProduct);
        sendMessageFragment.setArguments(bundle);
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                beginTransaction().replace(R.id.viewProductActivityFrame, sendMessageFragment).commit();
    }


    private void setupProgressar(boolean status) {
        if (status) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void createAndAssignValuesToOrder() {
        Locale current = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
        Date date = new Date();
        dateFormat.format(date);
        tempOrder.setOrderID(tempProduct.getProductID() + date);
        tempOrder.setProductDescription(tempProduct.getProductDescription());
        tempOrder.setOrderStatus("Pending");
        tempOrder.setDateOrdered(date);
        tempOrder.setProductQuantity(tempProduct.getQuantity());
        tempOrder.setProducerKey(tempProduct.getCustomerKey());
        tempOrder.setCustomerKey(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey"));
        tempOrder.setProductDescription(tempProduct.getProductDescription());
        tempOrder.setOrderTitle(tempProduct.getProductTitle());
        tempOrder.setProductURI(tempProduct.getUri());
    }

    private void publishTheOrderToFirestore() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Orders").add(tempOrder).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful() && task.isComplete()) {
                    toastShow("Great! Your order was made! Go to your orders to manage it if " +
                            "needed");
                    setupProgressar(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastShow("Something happened! Your order failed to publish. Try again.");
                setupProgressar(false);
                Log.d(TAG,
                        "Failed to create and publish the order" + e.getLocalizedMessage() + e.getCause() + e.getMessage());
            }
        });
    }

    private void toastShow(String whatToSay) {
        Toast toast = Toast.makeText(getContext(),
                whatToSay,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

}
