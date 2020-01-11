package johnschroeders.marketfree;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Objects;

public class SendMessageFragment extends Fragment {
    private final static String TAG = "ViewPostedActivity";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Bundle bundle;
    private Product tempProduct = new Product();
    private OnFragmentInteractionListener mListener;

    public SendMessageFragment() {

    }

    public static SendMessageFragment newInstance(String param1, String param2) {
        SendMessageFragment fragment = new SendMessageFragment();
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
            Log.d(TAG, "before bundle grab in Send Message Fragment " + getArguments());
            bundle = this.getArguments();
            if (bundle != null) {
                tempProduct = bundle.getParcelable("PassedInFromViewPostedProductFragment");
            }
            Log.d(TAG,
                    "Temp order ID = " + Objects.requireNonNull(tempProduct)
                            .getProductID() + "Temp Producer Key is = " + tempProduct.getCustomerKey());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_message, container, false);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Setting up the references to the items in the layout
        Button xButton = view.findViewById(R.id.sendMessageFragmentXbutton);
        Button sendButton = view.findViewById(R.id.sendMessageFrgamentSendButton);
        TextView productTitle = view.findViewById(R.id.sendMessageProductTitle);
        final TextView productDescription = view.findViewById(R.id.sendMessageFragmentProductDesc);
        final TextInputEditText textInputEditText =
                view.findViewById(R.id.sendMessageFragmentCreateMessageInput);
        textInputEditText.setHint("Create text input");
        final ImageView imageView = view.findViewById(R.id.sendMessageFragmentProductImage);
        Glide.with(Objects.requireNonNull(getContext())).asBitmap().
                load(tempProduct.getUri()).into(imageView);

        // setup onlcick listeners for
        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setMessageID(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey")
                        + getActivity().getIntent().getStringExtra("UserName"));
                message.setMessageContent(Objects.requireNonNull(textInputEditText.getText()).toString());
                Date date = new Date();
                message.setDateSent(date);
                message.setAssociatedProductDescription(tempProduct.getProductDescription());
                message.setAssociatedProductImageURL(tempProduct.getUri());
                message.setMessageFromCustomerKey(getActivity().getIntent().getStringExtra(
                        "CustomerKey"));
                message.setAssociatedProductID(tempProduct.getProductID());
                message.setAssociatedProductTitle(tempProduct.getProductTitle());
                message.setMessageToCustomerKey(tempProduct.getCustomerKey());
                sendMessageToFirestore(message);
            }
        });

        try {
            productDescription.setText(tempProduct.getProductDescription());
            productTitle.setText(tempProduct.getProductTitle());
        } catch (Exception e) {
            productDescription.setText("N/A");
            productTitle.setText("N/A");
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
        Log.d(TAG, "Send Message fragment removing");
        try {
            bundle.clear();
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                    beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }


    private void sendMessageToFirestore(final Message messageToSend) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "adding Message to firestore message collection");
        db.collection("Messages").add(messageToSend)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(TAG,
                                "Message was sent: " + messageToSend.getMessageID());
                        Toast toast = Toast.makeText(getContext(), "Your message has been sent!",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        removeSelf();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                removeSelf();
                Log.d(TAG,
                        "Failed to publish the product with error: " + e.getLocalizedMessage() + e.getMessage() + e.getCause());
            }
        });
    }


}
