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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SendMessageFragment extends Fragment {
    private final static String TAG = "ViewPostedActivity";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Bundle bundle;
    private Product tempProduct = new Product();
    private OnFragmentInteractionListener mListener;
    Message message = new Message();

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

                message.setMessageID(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey")
                        + getActivity().getIntent().getStringExtra("UserName"));
                message.setMessageContent(Objects.requireNonNull(textInputEditText.getText()).toString());
                String date;
                SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);
                date = spf.format(new Date());
                message.setDateSent(date);
                message.setAssociatedProductDescription(tempProduct.getProductDescription());
                message.setAssociatedProductImageURL(tempProduct.getUri());
                message.setMessageFromCustomerKey(getActivity().getIntent().getStringExtra(
                        "CustomerKey"));
                message.setAssociatedProductID(tempProduct.getProductID());
                message.setAssociatedProductTitle(tempProduct.getProductTitle());
                message.setMessageToCustomerKey(tempProduct.getCustomerKey());
                getConversationListing();
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

    // see if the user has a thread already for this conversation. If they do then dont
    // populate more from this section and instead reference them to the messages tab
    public void getConversationListing() {

        String custkey = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey");
        String conversationkey =
                custkey + message.getAssociatedProductID() + message.getMessageToCustomerKey();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("People")
                .whereEqualTo("customerKey", custkey)
                .whereArrayContains("conversationsKeys", conversationkey)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();
                if (task.isComplete()) {
                    if (!isEmpty) {
                        Toast toast = Toast.makeText(getContext(), "You still have a conversation" +
                                        " open for this. Either delete it or go to messages " +
                                        "to continue this conversation",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    } else {
                        checkForConversationalready();
                    }
                }
            }
        });
    }

    // see if the conversation already exists. If it does then do not create a new conversation
    // and instead just add the message to the conversation that already exists.
    private void checkForConversationalready() {

        final Conversation conversation = new Conversation();
        conversation.setConversationKey(Objects.requireNonNull(getActivity()).getIntent().
                getStringExtra("CustomerKey") + message.getAssociatedProductID() + message.getMessageToCustomerKey());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ConversationReferences").whereEqualTo("conversationKey",
                message.getMessageID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                    addConversationToFirestore();
                } else {
                    final Conversation conversation = new Conversation();
                    conversation.setConversationKey(Objects.requireNonNull(getActivity()).getIntent().
                            getStringExtra("CustomerKey") + message.getAssociatedProductID() + message.getMessageToCustomerKey());
                    conversation.setAssociatedProductID(message.getAssociatedProductID());
                    conversation.setAssociatedProductTitle(message.getAssociatedProductTitle());
                    conversation.setConversationStartedDate(new Date());
                    conversation.setAssociatedProductImage(message.getAssociatedProductImageURL());
                    conversation.setInquiringCustomerUniqueKey(message.getMessageFromCustomerKey());
                    conversation.setProductOwnerUniqueKey(message.getMessageToCustomerKey());
                    addMessageToConversations(message, conversation);
                }
            }
        });
    }

    // adding the conversation details to firestore. This helps with population of data for the
    // recylcerview.
    private void addConversationToFirestore() {
        final Conversation conversation = new Conversation();
        conversation.setConversationKey(Objects.requireNonNull(getActivity()).getIntent().
                getStringExtra("CustomerKey") + message.getAssociatedProductID() + message.getMessageToCustomerKey());
        conversation.setAssociatedProductID(message.getAssociatedProductID());
        conversation.setAssociatedProductTitle(message.getAssociatedProductTitle());
        conversation.setConversationStartedDate(new Date());
        conversation.setAssociatedProductImage(message.getAssociatedProductImageURL());
        conversation.setInquiringCustomerUniqueKey(message.getMessageFromCustomerKey());
        conversation.setProductOwnerUniqueKey(message.getMessageToCustomerKey());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ConversationReferences").add(conversation).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isComplete() && task.isSuccessful()) {
                    addMessageToConversations(message, conversation);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //adding the message to the conversations collection then verify that the user has the
    // conversation added to their user profile.
    private void addMessageToConversations(final Message message, final Conversation conversation) {
        Log.d(TAG, "Adding to the users conversation keys to firestore");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Conversations");
        collectionReference.document(conversation.getConversationKey()).collection("Messages")
                .add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    addToPersonalUserConversations(Objects.requireNonNull(getActivity()).getIntent().getStringExtra(
                            "CustomerKey") + tempProduct.getProductID() + message.getMessageToCustomerKey(), message.getMessageToCustomerKey());
                } else {
                    Toast toast = Toast.makeText(getContext(), "Message failed to send",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    //adding the conversation key to who sent the message
    private void addToPersonalUserConversations(final String conversationKey,
                                                final String theirCustomerKey) {
        Log.d(TAG, "Adding to the users conversation keys");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("People");
        collectionReference.whereEqualTo("customerKey", Objects.requireNonNull(getActivity()).getIntent().getStringExtra(
                "CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        collectionReference.document(document.getId()).update("conversationsKeys",
                                FieldValue.arrayUnion(conversationKey));
                    }
                    addConversationKeyToTheirList(theirCustomerKey, conversationKey);
                    Log.d(TAG, "Message Conversation was started with the customer");
                } else {
                    Toast toast = Toast.makeText(getContext(), "Message failed to send",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        });
    }

    // adding the conversation key to who the message was sent to
    private void addConversationKeyToTheirList(final String theirCustomerKey,
                                               final String converSationKey) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("People");
        collectionReference.whereEqualTo("customerKey", theirCustomerKey).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        collectionReference.document(document.getId()).update("conversationsKeys",
                                FieldValue.arrayUnion(converSationKey));
                    }
                    Log.d(TAG, "New message Conversation was started with the customer");
                    removeSelf();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Message failed to send",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }

        });
    }
}
